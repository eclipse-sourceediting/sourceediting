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

public class TableTest extends ModelTest {
	/**
	 * Constructor for TableTest.
	 * @param name
	 */
	public TableTest(String name) {
		super(name);
	}

	public TableTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new TableTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
		Document document = model.getDocument();

		Element html = document.createElement("HTML");
		document.appendChild(html);

		printSource(model);
		printTree(model);

		Element body = document.createElement("BODY");
		html.appendChild(body);

		printSource(model);
		printTree(model);

		Element table = document.createElement("TABLE");
		table.setAttribute("border", "1");
		Element td = null;
		for (int row = 0; row < 2; row++) {
			Element tr = document.createElement("TR");
			table.appendChild(tr);
			for (int col = 0; col < 2; col++) {
				td = document.createElement("TD");
				tr.appendChild(td);
			}
		}
		body.appendChild(table);

		printSource(model);
		printTree(model);

		Element font = document.createElement("FONT");
		font.setAttribute("color", "red");
		Text text = document.createTextNode("text");
		font.appendChild(text);
		td.appendChild(font);

		printSource(model);
		printTree(model);

		saveAndCompareTestResults();

		}
		finally {
			model.releaseFromEdit();
		}

	}
}
