/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.source;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

public class CSSUrlTest extends TestCase {

	/**
	 * Declaration: without quotes
	 */
	public void testDeclaration1() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		document.set("background-image: url(white space.gif);"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Declaration: without quotes, with surrounded space
	 */
	public void testDeclaration2() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		document.set("background-image: url(  white space.gif  );"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Declaration: with single quote
	 */
	public void testDeclaration3() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		document.set("background-image: url(\'white space.gif\');"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Declaration: with double quote
	 */
	public void testDeclaration4() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		document.set("background-image: url(\"white space.gif\");"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Declaration: two urls
	 */
	public void testDeclaration5() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		document.set("background-image: url(white space.gif); list-style-image: url(style image.gif);"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(2, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[1].getType());
		assertEquals("style image.gif", urls[1].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Style rule: two urls
	 */
	public void testStyleRule1() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		document.set("LI { background-image: url(white space.gif);\r\nlist-style-image: url(style image.gif); }"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(2, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[1].getType());
		assertEquals("style image.gif", urls[1].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Import rule and style rule: without quotes
	 */
	public void testSomeRules1() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		document.set("@import url(white space.css);LI { background-image: url(white space.gif);list-style-image: url(style image.gif); }"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);

		assertEquals(3, urls.length);
		assertEquals(CSSRegionContexts.CSS_URI, urls[0].getType());
		assertEquals("white space.css", urls[0].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[1].getType());
		assertEquals("white space.gif", urls[1].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[2].getType());
		assertEquals("style image.gif", urls[2].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Declaration: Two urls but parenthesis are missed, so concatenated url
	 * is identified. By adding parenthesis, urls are corrected.
	 */
	public void testFixError1() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		((CSSSourceParser) document.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);
		// ____________01234567890123456789012345678901234567890123456789012345678901234567890123456789
		document.set("background-image: url(white space.gif; list-style-image: urlstyle image.gif);"); //$NON-NLS-1$
		UrlInfo[] urls = pickupUrl(document);
		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif; list-style-image: urlstyle image.gif", urls[0].getUrl()); //$NON-NLS-1$

		// correct first url
		document.replaceText(null, 37, 0, ")"); //$NON-NLS-1$
		urls = pickupUrl(document);
		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$

		// correct second url
		document.replaceText(null, 61, 0, "("); //$NON-NLS-1$
		urls = pickupUrl(document);
		assertEquals(2, urls.length);
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[0].getType());
		assertEquals("white space.gif", urls[0].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[1].getType());
		assertEquals("style image.gif", urls[1].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Import rule and style rule: Two urls but parenthesis are missed, so
	 * concatenated url is identified. By adding parenthesis, urls are
	 * corrected.
	 */
	public void testFixError2() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		// ____________01234567890123456789012345678901234567890123456789012345678901234567890123456789
		document.set("@import url(white space.css;LI { background-image: urlwhite space.gif); }"); //$NON-NLS-1$		
		UrlInfo[] urls = pickupUrl(document);
		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_URI, urls[0].getType());
		assertEquals("white space.css;LI { background-image: urlwhite space.gif", urls[0].getUrl()); //$NON-NLS-1$

		// correct first url
		document.replaceText(null, 27, 0, ")"); //$NON-NLS-1$
		urls = pickupUrl(document);
		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_URI, urls[0].getType());
		assertEquals("white space.css", urls[0].getUrl()); //$NON-NLS-1$

		// correct second url
		document.replaceText(null, 55, 0, "("); //$NON-NLS-1$
		urls = pickupUrl(document);
		assertEquals(2, urls.length);
		assertEquals(CSSRegionContexts.CSS_URI, urls[0].getType());
		assertEquals("white space.css", urls[0].getUrl()); //$NON-NLS-1$
		assertEquals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI, urls[1].getType());
		assertEquals("white space.gif", urls[1].getUrl()); //$NON-NLS-1$
	}

	/**
	 * Import rule: Url has only one quote, so it is not identified as url. By
	 * removing quote, url is corrected.
	 */
	public void testFixError3() {
		ICSSModel model = FileUtil.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		// ____________0123456789012345678901234567890
		document.set("@import url(white space.css\");"); //$NON-NLS-1$		
		UrlInfo[] urls = pickupUrl(document);
		assertEquals(0, urls.length);

		// correct url
		document.replaceText(null, 27, 1, ""); //$NON-NLS-1$
		urls = pickupUrl(document);
		assertEquals(1, urls.length);
		assertEquals(CSSRegionContexts.CSS_URI, urls[0].getType());
		assertEquals("white space.css", urls[0].getUrl()); //$NON-NLS-1$
	}


	private UrlInfo[] pickupUrl(IStructuredDocument document) {
		List urls = new ArrayList();
		IStructuredDocumentRegion documentRegion = document.getFirstStructuredDocumentRegion();
		while (documentRegion != null) {
			ITextRegionList regionList = documentRegion.getRegions();
			Iterator i = regionList.iterator();
			while (i.hasNext()) {
				ITextRegion textRegion = (ITextRegion) i.next();
				String type = textRegion.getType();
				if (type.equals(CSSRegionContexts.CSS_URI) || type.equals(CSSRegionContexts.CSS_DECLARATION_VALUE_URI)) {
					urls.add(new UrlInfo(type, documentRegion.getFullText(textRegion)));
				}
			}
			documentRegion = documentRegion.getNext();
		}
		return (UrlInfo[]) urls.toArray(new UrlInfo[urls.size()]);
	}

	private class UrlInfo {
		UrlInfo(String type, String url) {
			fType = type;
			fUrl = CSSUtil.extractUriContents(url);
		}

		String getType() {
			return fType;
		}

		String getUrl() {
			return fUrl;
		}

		private String fType;
		private String fUrl;

	}
}
