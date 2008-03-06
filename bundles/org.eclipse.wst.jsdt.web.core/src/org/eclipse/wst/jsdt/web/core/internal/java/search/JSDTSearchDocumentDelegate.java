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
package org.eclipse.wst.jsdt.web.core.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.internal.core.search.JavaSearchDocument;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;


/**
 * Wrapper method to set getPath() path to be the path of the compilation unit
 * for the jsp file. (since it's a final method, it needs to be set via
 * constructor)
 * 
 * @author pavery
 */
/* Used to extend SearchDocument */
public class JSDTSearchDocumentDelegate extends JavaSearchDocument {
	private JsSearchDocument fJSPSearchDoc = null;
	
	public JSDTSearchDocumentDelegate(JsSearchDocument jspSearchDoc) {
		super(jspSearchDoc.getPath(), jspSearchDoc.getParticipant());
		this.fJSPSearchDoc = jspSearchDoc;
	}
	
	
	public byte[] getByteContents() {
		return this.fJSPSearchDoc.getByteContents();
	}
	
	
	public char[] getCharContents() {
		return this.fJSPSearchDoc.getCharContents();
	}
	
	
	public String getEncoding() {
		return this.fJSPSearchDoc.getEncoding();
	}
	
	public IFile getFile() {
		return this.fJSPSearchDoc.getFile();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getJavaElement()
	 */
	
	public IJavaElement getJavaElement() {
		return getJspTranslation().getCompilationUnit();
	}
	
	public String getJavaText() {
		return this.fJSPSearchDoc.getJavaText();
	}
	
	public IJsTranslation getJspTranslation() {
		return this.fJSPSearchDoc.getJSPTranslation();
	}
	
	
	public boolean isVirtual() {
		return true;
	}
	
	public void release() {
		this.fJSPSearchDoc.release();
	}
}
