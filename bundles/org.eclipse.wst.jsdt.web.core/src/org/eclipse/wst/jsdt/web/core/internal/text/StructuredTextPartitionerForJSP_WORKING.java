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
package org.eclipse.wst.jsdt.web.core.internal.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.jsdt.web.core.internal.encoding.JSPDocumentHeadContentDetector;

import org.eclipse.wst.jsdt.web.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.jsdt.web.core.text.IJSPPartitions;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;

public class StructuredTextPartitionerForJSP_WORKING extends
		StructuredTextPartitioner {
	// for compatibility with v5.1.0, we'll reuse ST_JSP_DIRECTIVE for action
	// tags
	private final static boolean fEnableJSPActionPartitions = true;
	// list of valid JSP 1.2 tag and action names
	// private static List fJSPActionTagNames = null;
	private static final String HTML_MIME_TYPE = "text/html"; //$NON-NLS-1$
	private static final String XHTML_MIME_TYPE = "text/xhtml"; //$NON-NLS-1$
	private static final String XML_MIME_TYPE = "text/xml"; //$NON-NLS-1$
	private static final String VND_WAP_WML = "text/vnd.wap.wml"; //$NON-NLS-1$

	// private final static String[] fConfiguredContentTypes = new
	// String[]{IJSPPartitions.JSP_DEFAULT, IJSPPartitions.JSP_DEFAULT_EL,
	// IJSPPartitions.JSP_DEFAULT_EL2, IJSPPartitions.JSP_DIRECTIVE,
	// IJSPPartitions.JSP_CONTENT_DELIMITER, IJSPPartitions.JSP_CONTENT_JAVA,
	// IJSPPartitions.JSP_CONTENT_JAVASCRIPT, IJSPPartitions.JSP_COMMENT};
	// No additional JS Content types (handled in HTML types)
	private final static String[] fConfiguredContentTypes = new String[] { "" };
	/**
	 * @return
	 */

	private IStructuredTextPartitioner fEmbeddedPartitioner = null;
	/*
	 * Save last taglib prefix that was checked (see isAction()) for better
	 * performance
	 */
	private String fLastCheckedPrefix = null;

	/**
	 * Assume language=java by default ... client, such as PageDirectiveAdapter,
	 * must set language of document partitioner, if/when it changes.
	 */
	private String fLanguage = "java"; //$NON-NLS-1$

	/**
	 * Constructor for JSPDocumentPartioner.
	 */
	public StructuredTextPartitionerForJSP_WORKING() {
		super();
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#connect(org.eclipse.jface.text.IDocument)
	 */
	public static String[] getConfiguredContentTypes() {
		return fConfiguredContentTypes;
	}

	@Override
	public void connect(IDocument document) {
		super.connect(document);
		// fScriptPartitionHelper.connect(document);
		fSupportedTypes = null;
	}

	private IStructuredTextPartitioner createStructuredTextPartitioner(
			IStructuredDocument structuredDocument) {
		IStructuredTextPartitioner result = null;
		// this same detector should underly content describer, to have
		// consistent results
		JSPDocumentHeadContentDetector jspHeadContentDetector = new JSPDocumentHeadContentDetector();
		jspHeadContentDetector.set(structuredDocument);
		String contentType = null;
		try {
			contentType = jspHeadContentDetector.getContentType();
			// if XHTML or WML, that trumps what is in the page directive
			if (jspHeadContentDetector.isXHTML()
					|| jspHeadContentDetector.isWML()) {
				contentType = "text/html";
			}
		} catch (IOException e) {
			// impossible in this context, since working with document stream
			throw new Error(e);
		}
		// null or empty, treat as "default"
		if (contentType == null || contentType.length() == 0) {
			contentType = "text/html"; //$NON-NLS-1$
		}
		// we currently only have two ... eventually should
		// make or tie-in to existing registry.
		if (contentType.equalsIgnoreCase(HTML_MIME_TYPE)
				|| contentType.equalsIgnoreCase(VND_WAP_WML)) {
			result = new StructuredTextPartitionerForHTML();
			result.connect(structuredDocument);
		} else if (contentType.equalsIgnoreCase(XHTML_MIME_TYPE)) {
			result = new StructuredTextPartitionerForHTML();
			result.connect(structuredDocument);
		} else if (contentType.equalsIgnoreCase(XML_MIME_TYPE)
				|| contentType.endsWith("+xml")) { //$NON-NLS-1$
			result = new StructuredTextPartitionerForXML();
			result.connect(structuredDocument);
		} else {
			result = new StructuredTextPartitioner();
			result.connect(structuredDocument);
		}
		return result;

	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	@Override
	public void disconnect() {
		if (fEmbeddedPartitioner != null) {
			fEmbeddedPartitioner.disconnect();
			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4909
			/**
			 * force recreation when reconnected
			 */
			fEmbeddedPartitioner = null;
		}
		// super.disconnect should come at end, since it (may) set
		// structuredDocument to null
		super.disconnect();
	}

	@Override
	public String getDefaultPartitionType() {
		return getEmbeddedPartitioner().getDefaultPartitionType();
	}

	/**
	 * Returns the embeddedPartitioner.
	 * 
	 * @return IStructuredTextPartitioner
	 */
	public IStructuredTextPartitioner getEmbeddedPartitioner() {
		if (fEmbeddedPartitioner == null) {
			fEmbeddedPartitioner = createStructuredTextPartitioner(fStructuredDocument);
			fEmbeddedPartitioner.connect(fStructuredDocument);
		}

		return fEmbeddedPartitioner;
	}

	/**
	 * Returns the language.
	 * 
	 * @return String
	 */
	public String getLanguage() {
		return fLanguage;
	}

	private List getLocalLegalContentTypes() {
		List types = new ArrayList();
		Object[] configuredTypes = getConfiguredContentTypes();
		for (int i = 0; i < configuredTypes.length; i++) {
			types.add(configuredTypes[i]);
		}
		return types;
	}

	@Override
	protected String getPartitionType(ForeignRegion region, int offset) {
		return getEmbeddedPartitioner().getPartitionType(region, offset);
	}

	@Override
	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		final String region_type = region.getType();

		if (region_type == IHTMLPartitions.SCRIPT) {
			result = getPartitionTypeForDocumentLanguage(region, offset);
		}

		else if (region_type == DOMRegionContext.XML_CDATA_TEXT) {

			result = getEmbeddedPartitioner().getPartitionType(region, offset);
		} else if (region_type == DOMRegionContext.XML_CONTENT) {

			result = getDefaultPartitionType();
		}

		else {
			result = getEmbeddedPartitioner().getPartitionType(region, offset);
		}
		return result;
	}

	@Override
	public String getPartitionTypeBetween(
			IStructuredDocumentRegion previousNode,
			IStructuredDocumentRegion nextNode) {
		return getEmbeddedPartitioner().getPartitionTypeBetween(previousNode,
				nextNode);
	}

	private String getPartitionTypeForDocumentLanguage(ITextRegion region,
			int offset) {
		String result;
		if (fLanguage == null || fLanguage.equalsIgnoreCase("java")) { //$NON-NLS-1$
			result = IJSPPartitions.JSP_CONTENT_JAVA;
		} else {
			result = IJSPPartitions.JSP_SCRIPT_PREFIX
					+ getLanguage().toUpperCase(Locale.ENGLISH);
		}
		return result;
	}

	@Override
	protected void initLegalContentTypes() {
		List combinedTypes = getLocalLegalContentTypes();
		if (getEmbeddedPartitioner() != null) {
			String[] moreTypes = getEmbeddedPartitioner()
					.getLegalContentTypes();
			for (int i = 0; i < moreTypes.length; i++) {
				combinedTypes.add(moreTypes[i]);
			}
		}
		fSupportedTypes = new String[0];
		combinedTypes.toArray(fSupportedTypes);
	}

	/**
	 * @param sdRegion
	 * @param offset
	 * @return
	 */
//	private boolean isAction(IStructuredDocumentRegion sdRegion, int offset) {
//		if (!sdRegion.getType().equals(DOMRegionContext.XML_TAG_NAME)) {
//			return false;
//		}
//		/*
//		 * shouldn't get a tag name region type unless a tag name region exists
//		 * at [1]
//		 */
//		ITextRegion tagNameRegion = sdRegion.getRegions().get(1);
//		String tagName = sdRegion.getText(tagNameRegion);
//
//		RegionParser parser = fStructuredDocument.getParser();
//		if (parser instanceof JSPSourceParser) {
//			if (tagName.equals(fLastCheckedPrefix)) {
//				return true;
//			}
//			List fCustomActionPrefixes = ((JSPSourceParser) parser)
//					.getNestablePrefixes();
//			for (int i = 0; i < fCustomActionPrefixes.size(); i++) {
//				if (tagName.startsWith(((TagMarker) fCustomActionPrefixes
//						.get(i)).getTagName())) {
//					fLastCheckedPrefix = tagName;
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	@Override
	protected boolean isDocumentRegionBasedPartition(
			IStructuredDocumentRegion sdRegion,
			ITextRegion containedChildRegion, int offset) {
		String documentRegionContext = sdRegion.getType();
		if (containedChildRegion != null) {
			if (documentRegionContext
					.equals(DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)
					|| documentRegionContext
							.equals(DOMJSPRegionContexts.JSP_ROOT_TAG_NAME)) {
				setInternalPartition(offset, containedChildRegion.getLength(),
						IJSPPartitions.JSP_DIRECTIVE);
				return true;
			}
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=113346
//			if (fEnableJSPActionPartitions && isAction(sdRegion, offset)
//					&& !(containedChildRegion instanceof ITextRegionContainer)) {
//				// if (fEnableJSPActionPartitions && isAction(sdRegion,
//				// offset)) {
//				setInternalPartition(offset, containedChildRegion.getLength(),
//						IJSPPartitions.JSP_DIRECTIVE);
//				return true;
//			}
		}
		return super.isDocumentRegionBasedPartition(sdRegion,
				containedChildRegion, offset);
	}

	private boolean isValidJspActionRegionType(String type) {
		// true for anything that can be within <jsp:scriptlet>,
		// <jsp:expression>, <jsp:declaration>
		return type == DOMRegionContext.XML_CONTENT
				|| type == DOMRegionContext.BLOCK_TEXT
				|| type == DOMRegionContext.XML_CDATA_OPEN
				|| type == DOMRegionContext.XML_CDATA_TEXT
				|| type == DOMRegionContext.XML_CDATA_CLOSE;
	}

	@Override
	public IDocumentPartitioner newInstance() {
		StructuredTextPartitionerForJSP instance = new StructuredTextPartitionerForJSP();
		instance
				.setEmbeddedPartitioner(createStructuredTextPartitioner(fStructuredDocument));
		instance.setLanguage(fLanguage);
		return instance;
	}

	/**
	 * Sets the embeddedPartitioner.
	 * 
	 * @param embeddedPartitioner
	 *            The embeddedPartitioner to set
	 */
	public void setEmbeddedPartitioner(
			IStructuredTextPartitioner embeddedPartitioner) {
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4909
		/**
		 * manage connected state of embedded partitioner
		 */
		if (fEmbeddedPartitioner != null && fStructuredDocument != null) {
			fEmbeddedPartitioner.disconnect();
		}

		this.fEmbeddedPartitioner = embeddedPartitioner;

		if (fEmbeddedPartitioner != null && fStructuredDocument != null) {
			fEmbeddedPartitioner.connect(fStructuredDocument);
		}
	}

	@Override
	protected void setInternalPartition(int offset, int length, String type) {
		// TODO: need to carry this single instance idea further to be
		// complete,
		// but hopefully this will be less garbage than before (especially for
		// HTML, XML,
		// naturally!)
		internalReusedTempInstance = getEmbeddedPartitioner().createPartition(
				offset, length, type);

	}

	/**
	 * Sets the language.
	 * 
	 * @param language
	 *            The language to set
	 */
	public void setLanguage(String language) {

		System.out.println("StructuredTextPartitionerForJSP.setLanguage("
				+ language);
		this.fLanguage = language;
	}

}