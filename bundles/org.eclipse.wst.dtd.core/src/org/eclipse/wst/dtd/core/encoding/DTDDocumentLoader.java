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
package org.eclipse.wst.dtd.core.encoding;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.dtd.core.internal.text.DTDStructuredDocumentReParser;
import org.eclipse.wst.dtd.core.parser.DTDRegionParser;
import org.eclipse.wst.dtd.core.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


public final class DTDDocumentLoader extends AbstractDocumentLoader {

	public DTDDocumentLoader() {
		super();
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForDTD();
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new DTDDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	public RegionParser getParser() {
		return new DTDRegionParser();
	}

	protected String getSpecDefaultEncoding() {
		String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument document = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		DTDStructuredDocumentReParser reParser = new DTDStructuredDocumentReParser();
		reParser.setStructuredDocument(document);
		if (document instanceof BasicStructuredDocument) {
			((BasicStructuredDocument) document).setReParser(reParser);
		}
		return document;
	}

}
