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
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class EmptyTextTest extends ModelTest {
	/**
	 * Constructor for EmptyTextTest.
	 * 
	 * @param name
	 */
	public EmptyTextTest(String name) {
		super(name);
	}

	public EmptyTextTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new EmptyTextTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element p = document.createElement("P");
			document.appendChild(p);
			Text text = document.createTextNode("a");
			p.appendChild(text);
			Element br = document.createElement("BR");
			p.appendChild(br);
			Text text2 = document.createTextNode("");
			p.appendChild(text2);

			printSource(model);
			printTree(model);

			Text text3 = document.createTextNode("");
			p.insertBefore(text3, text2);

			printSource(model);
			printTree(model);

			text3.setData("b");

			printSource(model);
			printTree(model);

			p.removeChild(text2);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
