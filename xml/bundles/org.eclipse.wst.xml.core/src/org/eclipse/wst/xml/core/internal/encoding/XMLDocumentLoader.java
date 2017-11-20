/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.encoding;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
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

	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForXML.ContentTypeID_XML);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
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
