/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;

/**
 * An adapter for getting a JSPTranslation of the document.
 * 
 * @author pavery
 */
public class JSPTranslationAdapter implements INodeAdapter, IDocumentListener {
	
	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	private IDocument fJspDocument = null;
	private IDocument fJavaDocument = null;
	private JSPTranslationExtension fJSPTranslation = null;
	private boolean fDocumentIsDirty = true;
	private XMLModel fXMLModel;
	private JSPTranslator fTranslator = null;
	private NullProgressMonitor fTranslationMonitor = null;

	public JSPTranslationAdapter(XMLModel xmlModel) {
		setXMLModel(xmlModel);
		initializeJavaPlugins();
	}

	/**
	 * Initialize the required Java Plugins
	 */
	protected void initializeJavaPlugins() {
		JavaCore.getPlugin();
	}

	/**
	 * @see com.ibm.sse.model.INodeAdapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(IJSPTranslation.class);
	}

	/**
	 * @see com.ibm.sse.model.INodeAdapter#notifyChanged(com.ibm.sse.model.INodeNotifier,
	 *      int, java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
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

	/**
	 * @see com.ibm.sse.editor.IReleasable#release()
	 */
	public void release() {
		
		if(fJspDocument != null)
			fJspDocument.removeDocumentListener(this);
		
		if(fTranslationMonitor != null)
			fTranslationMonitor.setCanceled(true);
		
		if(fJSPTranslation != null) {
			
			if(DEBUG)
				System.out.println("JSPTranslationAdapter releasing:" + fJSPTranslation);
			
			fJSPTranslation.release();
		}
	}

	/**
	 * Returns the JSPTranslation for this adapter.
	 * 
	 * @see com.ibm.sse.editor.jsp.java.JSPTranslation
	 * @return a JSPTranslationExtension
	 */
	public synchronized JSPTranslationExtension getJSPTranslation() {
		
		if (fJSPTranslation == null || fDocumentIsDirty) {
			JSPTranslator translator = null;
			if (getXMLModel() != null && getXMLModel().getIndexedRegion(0) != null) {
				translator = getTranslator((XMLNode) getXMLModel().getIndexedRegion(0));
				translator.translate();
				StringBuffer javaContents = translator.getTranslation();
				fJavaDocument = new Document(javaContents.toString());
				
			}
			else {
				// empty document case
				translator = new JSPTranslator();
				StringBuffer emptyContents = translator.getEmptyTranslation();
				fJavaDocument = new Document(emptyContents.toString());
			}
			// it's going to be rebuilt, so we release it here
			if(fJSPTranslation != null) {
				if(DEBUG)
					System.out.println("JSPTranslationAdapter releasing:" + fJSPTranslation);
				fJSPTranslation.release();
			}
			fJSPTranslation = new JSPTranslationExtension(getXMLModel().getStructuredDocument(), fJavaDocument, getJavaProject(), translator);
			fDocumentIsDirty = false;
		}
		return fJSPTranslation;
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
	private JSPTranslator getTranslator(XMLNode xmlNode) {
		if (fTranslator == null) {
			fTranslationMonitor = new NullProgressMonitor();
			fTranslator = new JSPTranslator();
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
	public void setXMLModel(XMLModel xmlModel) {
		fXMLModel = xmlModel;
		setDocument(fXMLModel.getStructuredDocument());
	}

	/**
	 * @return the XMLModel for this adapter.
	 */
	public XMLModel getXMLModel() {
		return fXMLModel;
	}

	/**
	 * Gets (or creates via JavaCore) a JavaProject based on the location of
	 * this adapter's XMLModel.  Returns null for non IFile based models.
	 * 
	 * @return the java project where
	 */
	public IJavaProject getJavaProject() {

		IJavaProject javaProject = null;
		try {
			String baseLocation = getXMLModel().getBaseLocation();
			// 20041129 (pa) the base location changed for xml model 
			// because of FileBuffers, so this code had to be updated
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=79686
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IFile file = null;
			IProject project = null;
			if (filePath.segmentCount() > 1) {
				file = root.getFile(filePath);
			}
			if (file != null) {
				project = file.getProject();
			}
//			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(baseLocation));
//			for (int i = 0; project == null && i < files.length; i++) {
//				if (files[i].getType() != IResource.PROJECT) {
//					project = files[i].getProject();
//					break;
//				}
//			}
			if(project != null) {
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