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

import org.eclipse.wst.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslator;

/**
 * An empty servlet, safe for Java search participation
 * 
 * @author pavery
 */
public class NullSearchDocument extends SearchDocument {
	
	StringBuffer fEmptyServletBuffer = null;
	
	public NullSearchDocument(String documentPath) {
		super(documentPath, new JSPSearchParticipant());
		this.fEmptyServletBuffer = new JSPTranslator().getEmptyTranslation();
	}
	
	@Override
	public byte[] getByteContents() {
		return this.fEmptyServletBuffer.toString().getBytes();
	}
	
	@Override
	public char[] getCharContents() {
		return this.fEmptyServletBuffer.toString().toCharArray();
	}
	
	@Override
	public String getEncoding() {
		return null;
	}
	
}
