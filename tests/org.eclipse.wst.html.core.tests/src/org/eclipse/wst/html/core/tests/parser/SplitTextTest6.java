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

public class SplitTextTest6 extends ModelTest {
	/**
	 * Constructor for SplitTextTest6.
	 * 
	 * @param name
	 */
	public SplitTextTest6(String name) {
		super(name);
	}

	public SplitTextTest6() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new SplitTextTest6().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element p = document.createElement("P");
			document.appendChild(p);
			Text text = document.createTextNode("aabbbbccc");
			p.appendChild(text);
			Text text2 = text.splitText(2);
			// ==> // Text text3 =
			text2.splitText(4);

			printSource(model);
			printTree(model);

			text2.setData("bbbbd");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
