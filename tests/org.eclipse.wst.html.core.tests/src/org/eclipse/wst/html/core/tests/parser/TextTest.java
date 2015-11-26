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
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TextTest extends ModelTest {
	/**
	 * Constructor for TextTest.
	 * 
	 * @param name
	 */
	public TextTest(String name) {
		super(name);
	}

	public TextTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new TextTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			CharacterData text = document.createTextNode("text");
			a.appendChild(text);

			text.setData("hello &lt;");

			printSource(model);
			printTree(model);

			fOutputWriter.writeln(text.getData());

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
