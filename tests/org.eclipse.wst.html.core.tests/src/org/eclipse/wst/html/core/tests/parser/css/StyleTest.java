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
package org.eclipse.wst.html.core.tests.parser.css;

import org.eclipse.wst.html.core.tests.parser.ModelTest;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheetList;

public class StyleTest extends ModelTest {
	/**
	 * Constructor for StyleTest.
	 * @param name
	 */
	public StyleTest(String name) {
		super(name);
	}

	public StyleTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new StyleTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel(); //new HTMLModelImpl();
		Document document = model.getDocument();

		Element style = document.createElement("STYLE");
		Text text = document.createTextNode("BODY { color : red; } P { color : green; } B { color : blue; }");
		style.appendChild(text);
		document.appendChild(style);

		printSource(model);
		printTree(model);

		DocumentStyle ds = (DocumentStyle) document;
		StyleSheetList ssl = ds.getStyleSheets();
		if (ssl.getLength() > 0) {
			CSSStyleSheet ss = (CSSStyleSheet) ssl.item(0);
			ss.deleteRule(1);
		}

		printSource(model);
		printTree(model);

		LinkStyle ls = (LinkStyle) style;
		CSSStyleSheet ss2 = (CSSStyleSheet) ls.getSheet();
		if (ss2 != null) {
			ss2.deleteRule(0);
		}

		printSource(model);
		printTree(model);

		saveAndCompareTestResults();


	}
}
