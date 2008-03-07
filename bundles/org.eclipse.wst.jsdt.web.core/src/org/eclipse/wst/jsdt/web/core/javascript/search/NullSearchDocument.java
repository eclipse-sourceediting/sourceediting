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
package org.eclipse.wst.jsdt.web.core.javascript.search;

import org.eclipse.wst.jsdt.core.search.SearchDocument;

/**
 * An empty servlet, safe for Java search participation
 * 
 * @author pavery
 */
public class NullSearchDocument extends SearchDocument {
	StringBuffer fEmptyServletBuffer = null;
	
	public NullSearchDocument(String documentPath) {
		super(documentPath, new JsSearchParticipant());
		this.fEmptyServletBuffer = new StringBuffer();
	}
	
	
	public byte[] getByteContents() {
		return this.fEmptyServletBuffer.toString().getBytes();
	}
	
	
	public char[] getCharContents() {
		return this.fEmptyServletBuffer.toString().toCharArray();
	}
	
	
	public String getEncoding() {
		return null;
	}
}
