/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * An adapter for getting a JSPTranslation of the document.
 * 
 * @author pavery
 */
public class JSPTranslationAdapter implements INodeAdapter, IDocumentListener {

	// for debugging
	private static final boolean DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation")); //$NON-NLS-1$  //$NON-NLS-2$

	private IDocument fJspDocument = null;
	private IDocument fJavaDocument = null;
	private JSPTranslationExtension fJSPTranslation = null;
	private boolean fDocumentIsDirty = true;
	private IDOMModel fXMLModel;
	private JSPTranslator fTranslator = null;
	private NullProgressMonitor fTranslationMonitor = null;

	/**
	 * <p>Constructs a {@link JSPTranslationAdapter} that will create a new {@link JSPTranslator}<p>
	 * 
	 * @param xmlModel {@link IDOMModel} this {@link JSPTranslationAdapter} is for
	 */
	public JSPTranslationAdapter(IDOMModel xmlModel) {
		setXMLModel(xmlModel);
		initializeJavaPlugins();
	}
	
	/**
	 * <p>Constructs a {@link JSPTranslationAdapter} using an existing {@link JSPTranslator}</p>
	 * 
	 * @param xmlModel {@link IDOMModel} this {@link JSPTranslationAdapter} is for
	 * @param translator existing {@link JSPTranslator} this {@link JSPTranslationAdapter} will use
	 */
	public JSPTranslationAdapter(IDOMModel xmlModel, JSPTranslator translator) {
		this(xmlModel);
		this.fTranslator = translator;
		this.fJavaDocument = new Document(translator.getTranslation().toString());
		this.fJSPTranslation = new JSPTranslationExtension(getXMLModel().getStructuredDocument(), fJavaDocument, getJavaProject(), this.fTranslator);
		this.fDocumentIsDirty = false;
	}

	/**
	 * Initialize the required Java Plugins
	 */
	protected void initializeJavaPlugins() {
		JavaCore.getPlugin();
	}

	public boolean isAdapterForType(Object type) {
		return type.equals(IJSPTranslation.class);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// nothing to do
	}

	/**
	 * Automatically set through the setXMLModel(XMLModel)
	 * 
	 * @param doc
	 */
	private void setDocument(IDocument doc) {
		if (fJspDocument != null)
			fJspDocument.removeDocumentListener(this);
		if (doc != null) {
			doc.addDocumentListener(this);
			fJspDocument = doc;
		}
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		// do nothing
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentChanged(DocumentEvent event) {
		// mark translation for rebuilding
		fDocumentIsDirty = true;
	}

	public void release() {

		if (fJspDocument != null)
			fJspDocument.removeDocumentListener(this);

		if (fTranslationMonitor != null)
			fTranslationMonitor.setCanceled(true);

		if (fJSPTranslation != null) {

			if (DEBUG)
				System.out.println("JSPTranslationAdapter releasing:" + fJSPTranslation); //$NON-NLS-1$

			fJSPTranslation.release();
		}
	}

	/**
	 * <p>Returns the JSPTranslation for this adapter.</p>
	 * 
	 * <p><b>IMPORTANT: </b><i>This will force translation of the
	 * document if it has not already been called.  To avoid
	 * accidental translation before calling this method call
	 * {@link #hasTranslation()} to verify a translation
	 * has already been forced by this adapter.</i></p>
	 * 
	 * @return a JSPTranslationExtension
	 */
	public synchronized JSPTranslationExtension getJSPTranslation() {

		if (fJSPTranslation == null || fDocumentIsDirty) {
			JSPTranslator translator = null;
			if (getXMLModel() != null && getXMLModel().getIndexedRegion(0) != null) {
				translator = getTranslator((IDOMNode) getXMLModel().getIndexedRegion(0));
				translator.translate();
				StringBuffer javaContents = translator.getTranslation();
				fJavaDocument = new Document(javaContents.toString());
			}
			else {
				// empty document case
				translator = createTranslator();
				StringBuffer emptyContents = translator.getEmptyTranslation();
				fJavaDocument = new Document(emptyContents.toString());
			}
			// Just a dirty translation, re-translate
			if (fJSPTranslation != null) {
				if (DEBUG)
					System.out.println("JSPTranslationAdapter retranslating:" + fJSPTranslation); //$NON-NLS-1$
				fJSPTranslation.retranslate(fJavaDocument, translator);
				fDocumentIsDirty = false;
				return fJSPTranslation;
			}
			fJSPTranslation = new JSPTranslationExtension(getXMLModel().getStructuredDocument(), fJavaDocument, getJavaProject(), translator);
			fDocumentIsDirty = false;
		}
		return fJSPTranslation;
	}
	
	/**
	 * <p>Knowing weather the translation has already been retrieved
	 * from this adapter is important if you do not wan't to force
	 * the translation of a document that has not yet been translated</p>
	 * 
	 * @return <code>true</code> if {@link #getJSPTranslation()} has
	 * been called on this adapter already, <code>false</code> otherwise
	 */
	public boolean hasTranslation() {
		return fJSPTranslation != null;
	}

	JSPTranslator createTranslator() {
		return new JSPTranslator();
	}

	/**
	 * Returns the JSPTranslator for this adapter. If it's null, a new
	 * translator is created with the xmlNode. Otherwise the
	 * translator.reset(xmlNode) is called to reset the current local
	 * translator.
	 * 
	 * @param xmlNode
	 *            the first node of the JSP document to be translated
	 * @return the JSPTranslator for this adapter (creates if null)
	 */
	private JSPTranslator getTranslator(IDOMNode xmlNode) {
		if (fTranslator == null) {
			fTranslationMonitor = new NullProgressMonitor();
			fTranslator = createTranslator();
			fTranslator.reset(xmlNode, fTranslationMonitor);
		}
		else
			fTranslator.reset(xmlNode, fTranslationMonitor);
		return fTranslator;
	}

	/**
	 * set the XMLModel for this adapter. Must be called.
	 * 
	 * @param xmlModel
	 */
	public void setXMLModel(IDOMModel xmlModel) {
		fXMLModel = xmlModel;
		setDocument(fXMLModel.getStructuredDocument());
	}

	/**
	 * @return the XMLModel for this adapter.
	 */
	private IDOMModel getXMLModel() {
		return fXMLModel;
	}

	/**
	 * Gets (or creates via JavaCore) a JavaProject based on the location of
	 * this adapter's XMLModel. Returns null for non IFile based models.
	 * 
	 * @return the java project where
	 */
	public IJavaProject getJavaProject() {

		IJavaProject javaProject = null;
		try {
			String baseLocation = getXMLModel().getBaseLocation();
			// 20041129 (pa) the base location changed for XML model
			// because of FileBuffers, so this code had to be updated
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=79686
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
//			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(baseLocation));
//			for (int i = 0; project == null && i < files.length; i++) {
//				if (files[i].getType() != IResource.PROJECT) {
//					project = files[i].getProject();
//					break;
//				}
//			}
			if (project != null) {
				javaProject = JavaCore.create(project);
			}
		}
		catch (Exception ex) {
			if (getXMLModel() != null)
				Logger.logException("(JSPTranslationAdapter) problem getting java project from the XMLModel's baseLocation > " + getXMLModel().getBaseLocation(), ex); //$NON-NLS-1$
			else
				Logger.logException("(JSPTranslationAdapter) problem getting java project", ex); //$NON-NLS-1$
		}
		return javaProject;
	}
}
