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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AttrTest2 extends ModelTest {
	/**
	 * Constructor for AttrTest2.
	 * 
	 * @param name
	 */
	public AttrTest2(String name) {
		super(name);
	}

	public AttrTest2() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new AttrTest2().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			a.setAttribute("b", "c");
			a.setAttribute("d", "e");
			Text t = document.createTextNode("f");
			document.appendChild(a);
			a.appendChild(t);

			printSource(model);
			printTree(model);

			Attr b = a.getAttributeNode("b");
			b.setPrefix("x");

			fOutputWriter.writeln("b.name=" + b.getName());
			fOutputWriter.writeln("b.value=" + b.getValue());
			String ab = a.getAttribute("b");
			fOutputWriter.writeln("a b=" + ab);
			String axb = a.getAttribute("x:b");
			fOutputWriter.writeln("a x:b=" + axb);

			printSource(model);
			printTree(model);

			a.getAttributeNode("d").setPrefix("y");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
