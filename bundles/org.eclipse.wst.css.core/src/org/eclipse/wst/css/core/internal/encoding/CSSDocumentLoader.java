/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.encoding;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.css.core.internal.contenttype.EncodingGuesser;
import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.core.internal.text.CSSStructuredDocumentReParser;
import org.eclipse.wst.css.core.internal.text.StructuredTextPartitionerForCSS;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;


public class CSSDocumentLoader extends AbstractDocumentLoader {
	private final static String CSS_ID = ContentTypeIdForCSS.ContentTypeID_CSS;
	private IDocumentCharsetDetector documentEncodingDetector;


	public CSSDocumentLoader() {
		super();
	}

	protected String getEncodingNameByGuess(byte[] string, int length) {
		String ianaEnc = null;
		ianaEnc = EncodingGuesser.guessEncoding(string, length);
		return ianaEnc;
	}

	/**
	 * Default encoding. For CSS there is no spec'd default.
	 */
	protected String getSpecDefaultEncoding() {
		return null;
	}

	protected IEncodedDocument newEncodedDocument() {
		// DMW: I copied this from CSSModelImple ...
		// but am not sure its right
		IStructuredDocument structuredDocument = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		CSSStructuredDocumentReParser reParser = new CSSStructuredDocumentReParser();
		reParser.setStructuredDocument(structuredDocument);
		((BasicStructuredDocument) structuredDocument).setReParser(reParser);
		return structuredDocument;
	}

	public RegionParser getParser() {
		// return new CSSRegionParser();
		return new CSSSourceParser();
	}

	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(CSS_ID);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (documentEncodingDetector == null) {
			documentEncodingDetector = new CSSDocumentCharsetDetector();
		}
		return documentEncodingDetector;
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForCSS();
	}

	public IDocumentLoader newInstance() {
		return new CSSDocumentLoader();
	}

}
