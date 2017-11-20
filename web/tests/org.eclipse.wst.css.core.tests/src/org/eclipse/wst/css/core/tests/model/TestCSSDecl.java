/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.css.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.util.CSSStyleDeclarationFactory;
import org.eclipse.wst.css.core.internal.util.declaration.CSSPropertyContext;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class TestCSSDecl extends TestCase {
	// commenting out this test because decl.setCssText() is not an implemented method
//	public void testDecl() {
//		CSSPropertyContext context = new CSSPropertyContext();
//		ICSSStyleDeclaration decl = CSSStyleDeclarationFactory.getInstance().createStyleDeclaration();
//		context.initialize(decl);
//		decl.setCssText(getString() != null ? getString() : "");//$NON-NLS-1$
//	}
//	private String getString() {
//		return "body {}";
//	}
	
	public void testStandaloneCSSDecl() {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=202615
		CSSPropertyContext context = new CSSPropertyContext();
		ICSSStyleDeclaration decl = CSSStyleDeclarationFactory.getInstance().createStyleDeclaration();
		context.initialize(decl);
		String cssText = decl.getCssText();
		assertEquals("standalone css node was not initialized", "", cssText); //$NON-NLS-1$ //$NON-NLS-2$

		context.setMargin("auto"); //$NON-NLS-1$
		context.setColor("red"); //$NON-NLS-1$
		context.setBorder("thick"); //$NON-NLS-1$
		context.applyFull(decl);
		cssText = decl.getCssText();
		String expected = "color: red; border: thick; margin: auto"; //$NON-NLS-1$
		assertEquals("standalone css node's properties were not set as expected", expected, cssText);  //$NON-NLS-1$
	}

	public void testCSSStyleDeclItem() {
		String value = "color: blue;";
		IDocumentLoader loader = new CSSDocumentLoader();
		IStructuredDocument sharedStructuredDocument = (IStructuredDocument) loader.createNewStructuredDocument();
		((CSSSourceParser) sharedStructuredDocument.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION_VALUE);

		sharedStructuredDocument.set(value);

		IStructuredDocumentRegion region = sharedStructuredDocument.getFirstStructuredDocumentRegion();
		assertNotNull(region);

	}

	public void testCSSStyleDeclItemMODE_DECLARATION() {
		String value = "color: blue;";
		IDocumentLoader loader = new CSSDocumentLoader();
		IStructuredDocument sharedStructuredDocument = (IStructuredDocument) loader.createNewStructuredDocument();
		((CSSSourceParser) sharedStructuredDocument.getParser()).setParserMode(CSSSourceParser.MODE_DECLARATION);

		sharedStructuredDocument.set(value);

		IStructuredDocumentRegion region = sharedStructuredDocument.getFirstStructuredDocumentRegion();
		assertNotNull(region);

	}

	public void testCSSStyleDeclItemSTYLE_SHEET() {
		String value = "color: blue;";
		IDocumentLoader loader = new CSSDocumentLoader();
		IStructuredDocument sharedStructuredDocument = (IStructuredDocument) loader.createNewStructuredDocument();
		((CSSSourceParser) sharedStructuredDocument.getParser()).setParserMode(CSSSourceParser.MODE_STYLESHEET);

		sharedStructuredDocument.set(value);

		IStructuredDocumentRegion region = sharedStructuredDocument.getFirstStructuredDocumentRegion();
		assertNotNull(region);

	}

	public void testCSSModel() {

		ICSSModel model = FileUtil.createModel();

		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.set(getHTMLDocumentText());

			IStructuredDocumentRegion region = structuredDocument.getFirstStructuredDocumentRegion();
			assertNotNull(region);
		} finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}

	}
	
	/**
	 * To test https://bugs.eclipse.org/298111
	 */
	public void testInsertPropertyInExistingRule() {
		ICSSModel model = FileUtil.createModel();
		try {
			String originalDocument = 
				"@CHARSET \"ISO-8859-1\";\r\n" +
				".test {\r\n" +
				"}";
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.set(originalDocument);

			IndexedRegion indexedRegion = model.getIndexedRegion(28);
			assertTrue("Indexed region should be an ICSSStyleRule", indexedRegion instanceof ICSSStyleRule);
			ICSSStyleRule rule = (ICSSStyleRule)indexedRegion;
			ICSSStyleDeclaration declaration = (ICSSStyleDeclaration)rule.getStyle();
			declaration.setProperty("color", "#008040", null);
			CSSPropertyContext context = new CSSPropertyContext(declaration);
			context.applyModified(declaration);
			String newDocument = structuredDocument.get();
			String expectedNewDocument = 
				"@CHARSET \"ISO-8859-1\";\r\n" +
				".test {\r\n" +
				"\tcolor: #008040\r\n" +
				"}";
			
			assertEquals("The updated CSS document does not equal the expected", expectedNewDocument, newDocument);
		} finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	private String getHTMLDocumentText() {
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n" + 
				"<HTML><HEAD>\r\n" + 
				"<META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n" + 
				"<META http-equiv=\"Content-Style-Type\" content=\"text/css\">\r\n" + 
				"<TITLE></TITLE>\r\n" + 
				"<STYLE type=\"text/css\">\r\n" + 
				"<!--\r\n" + 
				"BODY{\r\n" + 
				"  color : teal;\r\n" + 
				"  background-color: #FFFF99;\r\n" + 
				"}\r\n" + 
				"H1{\r\n" + 
				"  color : teal;\r\n" + 
				"  text-align: center;\r\n" + 
				"  padding-top: 10px;\r\n" + 
				"  padding-right: 0px;\r\n" + 
				"  padding-bottom: 10px;\r\n" + 
				"  padding-left: 0px;\r\n" + 
				"  border-top-style : solid;\r\n" + 
				"  border-top-color : teal;\r\n" + 
				"}\r\n" + 
				"H2{\r\n" + 
				"  color : teal;\r\n" + 
				"  text-align: center;\r\n" + 
				"  padding-top: 7px;\r\n" + 
				"  padding-right: 0px;\r\n" + 
				"  padding-bottom: 7px;\r\n" + 
				"  padding-left: 0px;\r\n" + 
				"  border-top-style : solid;\r\n" + 
				"  border-top-color : teal;\r\n" + 
				"}\r\n" + 
				"H3{\r\n" + 
				"  color : teal;\r\n" + 
				"  text-align: center;\r\n" + 
				"  padding-top: 4px;\r\n" + 
				"  padding-right: 0px;\r\n" + 
				"  padding-bottom: 4px;\r\n" + 
				"  padding-left: 0px;\r\n" + 
				"  border-top-style : solid;\r\n" + 
				"  border-top-color : teal;\r\n" + 
				"}\r\n" + 
				"HR{\r\n" + 
				"  color : teal;\r\n" + 
				"  height: 3px;\r\n" + 
				"}\r\n" + 
				"P{\r\n" + 
				"  color : teal;\r\n" + 
				"}\r\n" + 
				"TH{\r\n" + 
				"  color: #FFFFCC;\r\n" + 
				"  background-color : teal;\r\n" + 
				"}\r\n" + 
				"TD{\r\n" + 
				"  color : black;\r\n" + 
				"  background-color : white;\r\n" + 
				"}\r\n" + 
				"-->\r\n" + 
				"</STYLE>\r\n" + 
				"</HEAD><BODY>\r\n" + 
				"<H1>Heading 1 Heading 1</H1>\r\n" + 
				"<H2>Heading 2 Heading 2</H2>\r\n" + 
				"<H3>Heading 3 Heading 3</H3>\r\n" + 
				"<HR>\r\n" + 
				"<P>Normal Text</P>\r\n" + 
				"<P style=\"color : white; background-color : teal;\">Normal Text with inline style</P>\r\n" + 
				"<HR>\r\n" + 
				"<TABLE border=\"1\">\r\n" + 
				"  <TBODY>\r\n" + 
				"    <TR>\r\n" + 
				"      <TH colspan=\"3\">Header Cell of Table</TH>\r\n" + 
				"    </TR>\r\n" + 
				"    <TR>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"    </TR>\r\n" + 
				"    <TR>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"      <TD>Data Cell of Table</TD>\r\n" + 
				"    </TR>\r\n" + 
				"  </TBODY>\r\n" + 
				"</TABLE>\r\n" + 
				"</BODY></HTML>";
		return text;
	}
}
