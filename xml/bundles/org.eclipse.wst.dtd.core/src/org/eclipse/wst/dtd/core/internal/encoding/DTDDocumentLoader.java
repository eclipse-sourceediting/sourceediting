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
package org.eclipse.wst.dtd.core.internal.encoding;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.dtd.core.internal.parser.DTDRegionParser;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.core.internal.text.DTDStructuredDocumentReParser;
import org.eclipse.wst.dtd.core.internal.text.StructuredTextPartitionerForDTD;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;


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
	
	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForDTD.ContentTypeID_DTD);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
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
