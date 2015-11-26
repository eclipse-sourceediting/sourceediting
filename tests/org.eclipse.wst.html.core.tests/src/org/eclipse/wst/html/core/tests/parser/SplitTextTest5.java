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

public class SplitTextTest5 extends ModelTest {
	/**
	 * Constructor for SplitTextTest5.
	 * 
	 * @param name
	 */
	public SplitTextTest5(String name) {
		super(name);
	}

	public SplitTextTest5() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new SplitTextTest5().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element p = document.createElement("P");
			document.appendChild(p);
			Text text = document.createTextNode("aabbbbccc");
			p.appendChild(text);

			printSource(model);
			printTree(model);

			Text text2 = text.splitText(6);

			printSource(model);
			printTree(model);

			Element br = document.createElement("BR");
			p.insertBefore(br, text2);

			printSource(model);
			printTree(model);

			text2.setData("cccd");

			printSource(model);
			printTree(model);

			text2.setData("cccde");

			printSource(model);
			printTree(model);

			Text text3 = text.splitText(2);
			Element b = document.createElement("B");
			b.appendChild(text3);
			p.insertBefore(b, br);

			printSource(model);
			printTree(model);

			Text text4 = text2.splitText(3);
			Element i = document.createElement("I");
			i.appendChild(text2);
			p.insertBefore(i, text4);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();


		}
		finally {
			model.releaseFromEdit();
		}
	}
}
