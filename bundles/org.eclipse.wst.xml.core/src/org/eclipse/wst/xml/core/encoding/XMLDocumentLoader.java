/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.encoding;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;


/**
 * This class reads an XML file and creates an XML Structured Model.
 * 
 */
public class XMLDocumentLoader extends AbstractDocumentLoader {

	public XMLDocumentLoader() {
		super();
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForXML();
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new XMLDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	public RegionParser getParser() {
		return new XMLSourceParser();
	}

	protected String getPreferredNewLineDelimiter() {
		return ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForXML.ContentTypeID_XML);
	}

	protected String getSpecDefaultEncoding() {
		// by default, UTF-8 as per XML spec
		final String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		if (structuredDocument instanceof BasicStructuredDocument) {
			((BasicStructuredDocument) structuredDocument).setReParser(new XMLStructuredDocumentReParser());
		}
		return structuredDocument;
	}

	public IDocumentLoader newInstance() {
		return new XMLDocumentLoader();
	}

}
