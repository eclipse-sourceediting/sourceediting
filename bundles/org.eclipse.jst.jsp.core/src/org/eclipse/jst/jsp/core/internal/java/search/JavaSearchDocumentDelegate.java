/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;


/**
 * Wrapper method to set getPath() path to be the path of the compilation unit 
 * for the jsp file. (since it's a final method, it needs to be set via constructor)
 * 
 * @author pavery
 */
public class JavaSearchDocumentDelegate extends SearchDocument {
	
	private JSPSearchDocument fJSPSearchDoc = null;
	
	public JavaSearchDocumentDelegate(JSPSearchDocument jspSearchDoc) {
		
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
	
	public JSPTranslationExtension getJspTranslation() {
		
		return this.fJSPSearchDoc.getJSPTranslation();
	}
	
	public int getJspOffset(int javaOffset) {
		
		return this.fJSPSearchDoc.getJspOffset(javaOffset);
	}
	
	public void release() {
		this.fJSPSearchDoc.release();
	}
}
