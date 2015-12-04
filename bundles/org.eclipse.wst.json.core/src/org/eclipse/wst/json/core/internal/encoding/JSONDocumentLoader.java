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
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader
 *                                           modified in order to process JSON Objects.               
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.encoding;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.json.core.internal.parser.JSONSourceParser;
import org.eclipse.wst.json.core.internal.text.JSONStructuredDocumentReParser;
import org.eclipse.wst.json.core.internal.text.StructuredTextPartitionerForJSON;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;

public class JSONDocumentLoader extends AbstractDocumentLoader {

	private final static String JSON_ID = ContentTypeIdForJSON.ContentTypeID_JSON;

	private IDocumentCharsetDetector documentEncodingDetector;

	public JSONDocumentLoader() {
		super();
	}

	/*
	 * protected String getEncodingNameByGuess(byte[] string, int length) {
	 * String ianaEnc = null; ianaEnc = EncodingGuesser.guessEncoding(string,
	 * length); return ianaEnc; }
	 */

	/**
	 * Default encoding. For JSON there is no spec'd default.
	 */
	protected String getSpecDefaultEncoding() {
		return null;
	}

	@Override
	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory
				.getNewStructuredDocumentInstance(getParser());
		if (structuredDocument instanceof BasicStructuredDocument) {
			((BasicStructuredDocument) structuredDocument)
					.setReParser(new JSONStructuredDocumentReParser());
		}
		return structuredDocument;
	}

	public RegionParser getParser() {
		// return new JSONRegionParser();
		return new JSONSourceParser();
	}

	@Override
	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences
				.getPreferredNewLineDelimiter(JSON_ID);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
	}

	@Override
	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (documentEncodingDetector == null) {
			documentEncodingDetector = new JSONDocumentCharsetDetector();
		}
		return documentEncodingDetector;
	}

	@Override
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForJSON();
	}

	public IDocumentLoader newInstance() {
		return new JSONDocumentLoader();
	}

}
