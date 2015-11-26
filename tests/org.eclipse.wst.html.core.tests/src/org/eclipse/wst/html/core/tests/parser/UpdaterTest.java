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

public class UpdaterTest extends ModelTest {
	/**
	 * Constructor for UpdaterTest.
	 * 
	 * @param name
	 */
	public UpdaterTest(String name) {
		super(name);
	}

	public UpdaterTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest().testModel();
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

			Element p = document.createElement("P");
			body.appendChild(p);

			printSource(model);
			printTree(model);

			Text text = document.createTextNode("text");
			p.appendChild(text);

			printSource(model);
			printTree(model);

			Element br = document.createElement("BR");
			p.insertBefore(br, text);

			printSource(model);
			printTree(model);

			Element img = document.createElement("IMG");
			p.appendChild(img);

			printSource(model);
			printTree(model);

			p.removeChild(text);

			printSource(model);
			printTree(model);

			p.insertBefore(text, img);

			printSource(model);
			printTree(model);

			body.removeChild(p);

			printSource(model);
			printTree(model);

			body.appendChild(p);

			printSource(model);
			printTree(model);


			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
