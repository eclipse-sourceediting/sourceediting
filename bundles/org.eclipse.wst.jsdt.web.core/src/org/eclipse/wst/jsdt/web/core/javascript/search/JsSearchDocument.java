/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.javascript.search;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.search.SearchParticipant;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*(copied from JSP)
 * Created with a .jsp file, but should appear to be a .java file for indexing
 * and searching purposes. There are purposely few fields in this class, and
 * those fields are lightweight since it's possible for many JSP search
 * documents to exist in memory at one time (eg. after importing a project
 * with a large number of JSP files)
 * 
 * @author pavery
 */
public class JsSearchDocument {

	private String UNKNOWN_PATH = "**path unknown**"; //$NON-NLS-1$
	private String fJSPPathString = UNKNOWN_PATH;
	private String fCUPath = UNKNOWN_PATH;
	private SearchParticipant fParticipant = null;
	private long fLastModifiedStamp;
	private char[] fCachedCharContents;
	
	/**
	 * @param file
	 * @param participant
	 * @throws CoreException
	 */
	public JsSearchDocument(String filePath, SearchParticipant participant) {

		this.fJSPPathString = filePath;
		this.fParticipant = participant;
	}

	public SearchParticipant getParticipant() {
		return this.fParticipant;
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchDocument#getCharContents()
	 */
	public char[] getCharContents() {
		
		if((fCachedCharContents == null) || isDirty()) {
		    IJsTranslation trans = getJSTranslation();    
		    fCachedCharContents = trans != null ? trans.getJsText().toCharArray() : new char[0];
		    fCUPath = trans.getJavaPath();
		}
		return fCachedCharContents;
	}

	public String getJavaText() {
		return new String(getCharContents());
	}

	private IModelManager getModelManager() {
		return StructuredModelManager.getModelManager();
	}

	/**
	 * It's not recommended for clients to hold on to this JSPTranslation
	 * since it's kind of large. If possible, hold on to the
	 * JSPSearchDocument, which is more of a lightweight proxy.
	 * 
	 * @return the JSPTranslation for the jsp file, or null if it's an
	 *         unsupported file.
	 */
	public final IJsTranslation getJSTranslation() {
		IJsTranslation translation = null;
		IFile jspFile = getFile();
		if (!JsSearchSupport.isJsp(jspFile)) {
			return translation;
		}

		IStructuredModel model = null;
		try {
			// get existing model for read, then get document from it
			IModelManager modelManager = getModelManager();
			if (modelManager != null) {
				model = modelManager.getModelForRead(jspFile);
			}
			// handle unsupported
			if (model instanceof IDOMModel) {
				IDOMModel xmlModel = (IDOMModel)model;
				JsTranslationAdapterFactory.setupAdapterFactory(xmlModel);
				IDOMDocument doc = xmlModel.getDocument();
				JsTranslationAdapter adapter = (JsTranslationAdapter) doc.getAdapterFor(IJsTranslation.class);
				translation = adapter.getJsTranslation(false);
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
			// Logger.logException(e);
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return translation;
	}

	/**
	 * the path to the Java compilation unit
	 * 
	 * @see org.eclipse.jdt.core.search.SearchDocument#getPath()
	 */
	public String getPath() {
	    // caching the path since it's expensive to get translation
		// important that isDirty() check is second to cache modification stamp
	    if((this.fCUPath == null) || isDirty() || (this.fCUPath == UNKNOWN_PATH)) {
	        IJsTranslation trans = getJSTranslation();
	        if(trans != null) {
	            this.fCUPath = trans.getJavaPath();
	            // save since it's expensive to calculate again later
	            fCachedCharContents = trans.getJsText().toCharArray();
	        }
	    }
		return fCUPath != null ? fCUPath : UNKNOWN_PATH;
	}

//	public int getJspOffset(int javaOffset) {
//		// copied from JSPTranslation
//		int result = -1;
//		int offsetInRange = 0;
//		Position jspPos, javaPos = null;
//		IJsTranslation trans = getJSPTranslation();
//		if (trans != null) {
//			HashMap java2jspMap = trans.getJava2JspMap();
//
//			// iterate all mapped java ranges
//			Iterator it = java2jspMap.keySet().iterator();
//			while (it.hasNext()) {
//				javaPos = (Position) it.next();
//				// need to count the last position as included
//				if (!javaPos.includes(javaOffset) && !(javaPos.offset + javaPos.length == javaOffset))
//					continue;
//
//				offsetInRange = javaOffset - javaPos.offset;
//				jspPos = (Position) java2jspMap.get(javaPos);
//
//				if (jspPos != null)
//					result = jspPos.offset + offsetInRange;
//				else {
//					Logger.log(Logger.ERROR, "jspPosition was null!" + javaOffset); //$NON-NLS-1$
//				}
//				break;
//			}
//		}
//		return result;
//	}

	public IFile getFile() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath jspPath = new Path(this.fJSPPathString);
		IFile jspFile = root.getFile(jspPath);
		if (!jspFile.exists()) {
			// possibly outside workspace
			jspFile = root.getFileForLocation(jspPath);
		}
		return jspFile;
	}

	
	private boolean isDirty() {
		boolean modified = false;
		IFile f = getFile();
		if(f != null) {
			long currentStamp = f.getModificationStamp();
			if(currentStamp != fLastModifiedStamp) {
				modified = true;
			}
			fLastModifiedStamp = currentStamp;
		}
		return modified;
	}
	
	public void release() {
		// nothing to do now since JSPTranslation is created on the fly
	}

	/**
	 * for debugging
	 */
	public String toString() {
		return "[JSPSearchDocument:" + this.fJSPPathString + "]"; //$NON-NLS-1$ //$NON-NLS-2$ 
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
