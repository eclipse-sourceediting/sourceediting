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
package org.eclipse.wst.dtd.core.encoding;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.dtd.core.internal.text.DTDStructuredDocumentReParser;
import org.eclipse.wst.dtd.core.parser.DTDRegionParser;
import org.eclipse.wst.dtd.core.text.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.parser.RegionParser;


public final class DTDDocumentLoader extends AbstractDocumentLoader {

	public DTDDocumentLoader() {
		super();
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForDTD();
	}

	protected String getSpecDefaultEncoding() {
		String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
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

	public IEncodedDocument newEncodedDocument() {
		BasicStructuredDocument flatModel = new BasicStructuredDocument(getParser());
		DTDStructuredDocumentReParser reParser = new DTDStructuredDocumentReParser();
		reParser.setStructuredDocument(flatModel);
		flatModel.setReParser(reParser);
		return flatModel;
	}

}
