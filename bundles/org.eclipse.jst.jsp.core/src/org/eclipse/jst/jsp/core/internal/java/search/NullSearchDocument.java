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

import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;



/**
 * An empty servlet, safe for Java search participation
 * 
 * @author pavery
 */
public class NullSearchDocument extends SearchDocument {
	
	StringBuffer fEmptyServletBuffer = null;
	
	public NullSearchDocument(String documentPath) {
		super(documentPath, new JSPSearchParticipant()); //$NON-NLS-1$
		this.fEmptyServletBuffer = new JSPTranslator().getEmptyTranslation();
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
