/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.encoding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.common.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.html.core.document.HTMLDocumentTypeAdapterFactory;
import org.eclipse.wst.html.core.document.HTMLModelParserAdapterFactory;
import org.eclipse.wst.html.core.htmlcss.HTMLStyleSelectorAdapterFactory;
import org.eclipse.wst.html.core.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.internal.contenttype.EncodingGuesser;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.core.modelquery.ModelQueryAdapterFactoryForHTML;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.parser.BlockMarker;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

public class HTMLDocumentLoader extends AbstractDocumentLoader {

	public HTMLDocumentLoader() {
		super();
	}

	/**
	 * Convenience method to add tag names using BlockMarker object
	 */
	protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
		//DMW: Nitin, perhaps we should provide some convenience methods
		// in the parser itself? e.g. addCaseInsensitiveBlockedTag(tagname)
		BlockMarker bm = new BlockMarker(tagname, null, XMLRegionContext.BLOCK_TEXT, false);
		parser.addBlockMarker(bm);
	}

	/**
	 * Default encoding. For HTML, there is no spec.
	 */
	protected String getSpecDefaultEncoding() {
		return null;
	}

	/**
	 * @return java.lang.String
	 * @param string
	 *            byte[]
	 * @param length
	 *            int
	 * 
	 * Do automatic encoding detection by guess
	 */
	protected String getEncodingNameByGuess(byte[] string, int length) {
		final String ianaEnc = EncodingGuesser.guessEncoding(string, length);
		return ianaEnc;
	}

	/*
	 * @see AbstractLoader#createNewStructuredDocument()
	 */
	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		((BasicStructuredDocument) structuredDocument).setReParser(new XMLStructuredDocumentReParser());

		return structuredDocument;
	}

	/*
	 * @see ModelLoader#getParser()
	 */
	public RegionParser getParser() {
		XMLSourceParser parser = new XMLSourceParser();
		// for the "static HTML" case, we need to initialize
		// Blocktags here.
		addHTMLishTag(parser, "script"); //$NON-NLS-1$
		addHTMLishTag(parser, "style"); //$NON-NLS-1$
		return parser;
	}

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	public List getAdapterFactories() {
		List result = new ArrayList();
		AdapterFactory factory = null;
		factory = StyleAdapterFactory.getInstance();
		result.add(factory);
		factory = HTMLStyleSelectorAdapterFactory.getInstance();
		result.add(factory);
		factory = HTMLDocumentTypeAdapterFactory.getInstance();
		result.add(factory);
		factory = HTMLModelParserAdapterFactory.getInstance();
		result.add(factory);
		//
		factory = new ModelQueryAdapterFactoryForHTML();
		result.add(factory);

		factory = new PropagatingAdapterFactoryImpl();
		result.add(factory);


		return result;
	}

	protected String getPreferredNewLineDelimiter() {
		return ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(IContentTypeIdentifier.ContentTypeID_HTML);
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new HTMLDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		//		* TODO - how to best handle model requirements
		//		* if/when document already loaded?
		// DMW: just added this preload on 8/16/2002
		// I noticed the ProagatingAdapterFactory was being added,
		// that that the ProagatingAdapterAdapter was not being
		// preload adapted -- I'm assuing it ALWAYS has to be.
		XMLModel domModel = (XMLModel) structuredModel;
		// if there is a model in the adapter, this will adapt it to
		// first node. After that the PropagatingAdater spreads over the
		// children being
		// created. Each time that happends, a side effect is to
		// also "spread" sprecific registered adapters,
		// they two can propigate is needed.
		((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);
	}

	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForHTML();
	}

	public IDocumentLoader newInstance() {
		return new HTMLDocumentLoader();
	}

}