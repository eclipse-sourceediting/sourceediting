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

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;


/**
 * This class reads an XML file and creates an XML Structured Model.
 *  
 */
public class XMLDocumentLoader extends AbstractDocumentLoader {

	public XMLDocumentLoader() {
		super();
	}

	public RegionParser getParser() {
		return new XMLSourceParser();
	}

	public IEncodedDocument newEncodedDocument() {
		BasicStructuredDocument structuredDocument = new BasicStructuredDocument(getParser());
		structuredDocument.setReParser(new XMLStructuredDocumentReParser());
		
		return structuredDocument;
	}

	protected String getPreferredNewLineDelimiter() {
		return ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(IContentTypeIdentifier.ContentTypeID_SSEXML);
	}

	protected String getSpecDefaultEncoding() {
		// by default, UTF-8 as per XML spec
		final String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new XMLDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForXML();
	}

	public IDocumentLoader newInstance() {
		return new XMLDocumentLoader();
	}

}
