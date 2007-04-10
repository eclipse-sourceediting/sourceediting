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

public class ElementTest3 extends ModelTest {
	/**
	 * Constructor for ElementTest3.
	 * 
	 * @param name
	 */
	public ElementTest3(String name) {
		super(name);
	}

	public ElementTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new ElementTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("x:a");
			Element b = document.createElement("b");
			Text t = document.createTextNode("c");
			document.appendChild(a);
			a.appendChild(b);
			b.appendChild(t);

			printSource(model);
			printTree(model);

			a.setPrefix("");

			fOutputWriter.writeln("a.tagName=" + a.getTagName());

			printSource(model);
			printTree(model);

			a.setPrefix("y");

			fOutputWriter.writeln("a.tagName=" + a.getTagName());

			printSource(model);
			printTree(model);

			a.setPrefix("y");

			printSource(model);
			printTree(model);


			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
