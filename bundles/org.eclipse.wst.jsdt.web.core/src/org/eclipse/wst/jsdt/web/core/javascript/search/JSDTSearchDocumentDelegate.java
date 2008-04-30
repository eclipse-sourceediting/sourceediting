/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
*
 * Wrapper method to set getPath() path to be the path of the compilation unit 
 * for the jsp file. (since it's a final method, it needs to be set via constructor)
 * 
 * @author pavery
 */
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
	
	public String getJavaText() {
		return this.fJSPSearchDoc.getJavaText();
	}

	public String getEncoding() {
		
		return this.fJSPSearchDoc.getEncoding();
	}
	
	public IFile getFile() {
		
		return this.fJSPSearchDoc.getFile();
	}
	
	public IJsTranslation getJspTranslation() {
		
		return this.fJSPSearchDoc.getJSPTranslation();
	}
	
//	public int getJspOffset(int javaOffset) {
//		
//		return this.fJSPSearchDoc.getJspOffset(javaOffset);
//	}
	
	public void release() {
		this.fJSPSearchDoc.release();
	}
	public IJavaScriptElement getJavaElement() {
		return getJspTranslation().getCompilationUnit();
	}
	public boolean isVirtual() {
		return true;
	}
}
