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
package org.eclipse.jst.jsp.core.internal.java.search;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jst.jsp.core.Logger;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.wst.common.encoding.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.osgi.framework.Bundle;

/**
 * Created with a .jsp file, but should appear to be a .java file for indexing
 * and searching purposes
 * 
 * @author pavery
 */
public class JSPSearchDocument {

	private String UNKNOWN_PATH = "**path unknown**"; //$NON-NLS-1$
	private String jspPath = UNKNOWN_PATH; //for debugging
	private IFile fJSPFile = null;
	private JSPTranslationExtension fJSPTranslation = null;
	private SearchParticipant fParticipant = null;
	private String fCUText = null;
	private String fCUPath = UNKNOWN_PATH;

	/**
	 * @param file
	 * @param participant
	 * @throws CoreException
	 */
	public JSPSearchDocument(IFile file, SearchParticipant participant) throws CoreException {

		this.fJSPFile = file;
		this.jspPath = file.getFullPath().toString();
		
		updateJavaInfo(file);
		
		this.fParticipant = participant;
	}

	public SearchParticipant getParticipant() {
		return this.fParticipant;
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchDocument#getCharContents()
	 */
	public char[] getCharContents() {
		return this.fCUText.toCharArray();
	}

	public String getJavaText() {
		return new String(getCharContents());
	}
	
	private IModelManager getModelManager() {
		IModelManager modelManager = null;
		IModelManagerPlugin plugin = null;
		Bundle mmBundle = Platform.getBundle(IModelManagerPlugin.ID);
		int state = mmBundle.getState();
		// I put in check to active bundle to avoid so many
		// exceptions during unit tests ... apparently the search
		// indexing continues, even when being shutdown. We should
		// fix that "root" problem, but I'm not sure where right now.
		// Note: the "active" state assume's we're never called early
		// before already active once.
		if (state == Bundle.ACTIVE) {
			//System.out.println("state: " + state);
			plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		}
		// occassionally, during unit tests, I've seen this return null, I
		// suspect because
		// the workbench is shutting down. So, if we get 'null' back,
		// we'll though relatively CoreException, just to get it logged.
		// and cease processing.

		if (plugin == null) {
			Logger.log(Logger.INFO, "ModelManager not available, probably due to shutting down"); //$NON-NLS-1$
			modelManager = new NullModelManager();
		}
		else {
			modelManager = plugin.getModelManager();
		}
		return modelManager;
	}
	
	/**
	 * @param file
	 * @throws CoreException
	 */
	private void updateJavaInfo(IFile file) throws CoreException {
		
		if(!JSPSearchSupport.isJsp(file))
			return;
		
		XMLModel xmlModel = null;
		try {
			// get existing model for read, then get document from it
			xmlModel = (XMLModel) getModelManager().getModelForRead(file);
			// handle unsupported
			if (xmlModel != null) {
				setupAdapterFactory(xmlModel);
				XMLDocument doc = xmlModel.getDocument();
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) doc.getAdapterFor(IJSPTranslation.class);
				this.fJSPTranslation = adapter.getJSPTranslation();

				if (this.fJSPTranslation != null) {
					this.fCUText = this.fJSPTranslation.getJavaText();
					this.fCUPath = this.fJSPTranslation.getJavaPath();
				}
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (UnsupportedCharsetExceptionWithDetail e) {
			// no need to log this. Just consider it an invalid file for our
			// purposes.
			//Logger.logException(e);
		}
		finally {
			if (xmlModel != null)
				xmlModel.releaseFromRead();
		}
	}

	/**
	 * add the factory for JSPTranslationAdapter here
	 * 
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
		sm.getFactoryRegistry().addFactory(factory);
	}

	/**
	 * the path to the Java compilation unit
	 * 
	 * @see org.eclipse.jdt.core.search.SearchDocument#getPath()
	 */
	public String getPath() {
		return this.fCUPath != null ? this.fCUPath : UNKNOWN_PATH;
	}

	public int getJspOffset(int javaOffset) {
		return this.fJSPTranslation != null ? this.fJSPTranslation.getJspOffset(javaOffset) : 0;
	}

	public IFile getFile() {
		return this.fJSPFile;
	}
	
	public void release() {
		
		if (this.fJSPTranslation != null) {
			this.fJSPTranslation.release();
		}
		this.fJSPTranslation = null;
	}

	public JSPTranslationExtension getJspTranslation() {
		return this.fJSPTranslation;
	}

	/**
	 * for debugging
	 */
	public String toString() {
		return "[JSPSearchDocument:" + this.jspPath + "(CU:" + this.fCUPath + ")]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.SearchDocument#getEncoding()
	 */
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.SearchDocument#getByteContents()
	 */
	public byte[] getByteContents() {
		// TODO Auto-generated method stub
		return null;
	}
}