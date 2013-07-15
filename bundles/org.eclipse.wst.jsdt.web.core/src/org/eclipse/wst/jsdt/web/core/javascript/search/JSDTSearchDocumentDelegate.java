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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.internal.core.search.JavaSearchDocument;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*(copied from JSP)
 * Wrapper method to set getPath() path to be the path of the compilation unit 
 * for the jsp file. (since it's a final method, it needs to be set via constructor)
 * 
 * @author pavery
 */
public class JSDTSearchDocumentDelegate extends JavaSearchDocument {
	
	private JsSearchDocument fJSSearchDoc = null;
	
	public JSDTSearchDocumentDelegate(JsSearchDocument jsSearchDoc) {
		
		super(jsSearchDoc.getPath(), jsSearchDoc.getParticipant());
		this.fJSSearchDoc = jsSearchDoc;
	}

	public byte[] getByteContents() {
		
		return this.fJSSearchDoc.getByteContents();
	}

	public char[] getCharContents() {
		
		return this.fJSSearchDoc.getCharContents();
	}
	
	public String getJavaText() {
		return this.fJSSearchDoc.getJavaText();
	}

	public String getEncoding() {
		
		return this.fJSSearchDoc.getEncoding();
	}
	
	public IFile getFile() {
		
		return this.fJSSearchDoc.getFile();
	}
	
	public IJsTranslation getJspTranslation() {
		
		return this.fJSSearchDoc.getJSTranslation();
	}
	
//	public int getJspOffset(int javaOffset) {
//		
//		return this.fJSPSearchDoc.getJspOffset(javaOffset);
//	}
	
	public void release() {
		this.fJSSearchDoc.release();
	}
	public IJavaScriptElement getJavaElement() {
		return getJspTranslation().getCompilationUnit();
	}
	public boolean isVirtual() {
		return true;
	}
}
