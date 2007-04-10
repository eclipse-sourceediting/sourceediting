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
package org.eclipse.wst.html.core.internal.encoding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.html.core.internal.contenttype.EncodingGuesser;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeAdapterFactory;
import org.eclipse.wst.html.core.internal.document.HTMLModelParserAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.HTMLStyleSelectorAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.internal.modelquery.ModelQueryAdapterFactoryForHTML;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class HTMLDocumentLoader extends AbstractDocumentLoader {

	public HTMLDocumentLoader() {
		super();
	}

	/**
	 * Convenience method to add tag names using BlockMarker object
	 */
	protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
		BlockMarker bm = new BlockMarker(tagname, null, DOMRegionContext.BLOCK_TEXT, false);
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
	 * @see IModelLoader#getParser()
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
		INodeAdapterFactory factory = null;
		factory = StyleAdapterFactory.getInstance();
		result.add(factory);
		factory = HTMLStyleSelectorAdapterFactory.getInstance();
		result.add(factory);
		factory = new HTMLDocumentTypeAdapterFactory();
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

	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForHTML.ContentTypeID_HTML);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
	}

	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new HTMLDocumentCharsetDetector();
		}
		return fDocumentEncodingDetector;
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		// DMW: just added this preload on 8/16/2002
		// I noticed the ProagatingAdapterFactory was being added,
		// that that the ProagatingAdapterAdapter was not being
		// preload adapted -- I'm assuing it ALWAYS has to be.
		IDOMModel domModel = (IDOMModel) structuredModel;
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
