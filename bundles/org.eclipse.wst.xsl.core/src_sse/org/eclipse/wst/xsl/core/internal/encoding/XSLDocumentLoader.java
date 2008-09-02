/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.encoding;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xsl.core.internal.parser.XSLSourceParser;
import org.eclipse.wst.xsl.core.internal.text.rules.StructuredTextPartitionerForXSL;


/**
 * This class reads an XSL file and creates an XML Structured Model.
 * 
 */
public class XSLDocumentLoader extends AbstractDocumentLoader {

	public XSLDocumentLoader() {
		super();
	}

	@Override
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForXSL();
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new XMLDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	public RegionParser getParser() {
		return new XSLSourceParser();
	}

	protected String getSpecDefaultEncoding() {
		// by default, UTF-8 as per XML spec
		final String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	@Override
	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		if (structuredDocument instanceof BasicStructuredDocument) {
			((BasicStructuredDocument) structuredDocument).setReParser(new XMLStructuredDocumentReParser());
		}
		return structuredDocument;
	}

	public IDocumentLoader newInstance() {
		return new XSLDocumentLoader();
	}

}
