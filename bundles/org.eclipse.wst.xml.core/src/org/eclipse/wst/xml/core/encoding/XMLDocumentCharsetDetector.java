/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.encoding;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.document.DocumentReader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.xml.core.contenttype.XMLResourceEncodingDetector;


/**
 * This class reads and parses first of XML file to get encoding.
 *  
 */
public class XMLDocumentCharsetDetector extends XMLResourceEncodingDetector implements IDocumentCharsetDetector {

	/**
	 * XMLLoader constructor comment.
	 */
	public XMLDocumentCharsetDetector() {
		super();
	}

	public void set(IDocument document) {
		set(new DocumentReader(document, 0));
	}
}
