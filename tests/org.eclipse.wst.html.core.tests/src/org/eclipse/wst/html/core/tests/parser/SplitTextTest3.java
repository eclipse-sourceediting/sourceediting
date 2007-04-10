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

public class SplitTextTest3 extends ModelTest {
	/**
	 * Constructor for SplitTextTest3.
	 * 
	 * @param name
	 */
	public SplitTextTest3(String name) {
		super(name);
	}

	public SplitTextTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new SplitTextTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element p = document.createElement("P");
			document.appendChild(p);
			Text text = document.createTextNode("aa");
			p.appendChild(text);

			printSource(model);
			printTree(model);

			Element b = document.createElement("B");
			Text text2 = document.createTextNode("bbbb");
			b.appendChild(text2);
			p.appendChild(b);

			printSource(model);
			printTree(model);

			Text text3 = document.createTextNode("ccc");
			p.appendChild(text3);

			printSource(model);
			printTree(model);

			p.removeChild(b);

			printSource(model);
			printTree(model);

			p.insertBefore(b, text3);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();


		}
		finally {
			model.releaseFromEdit();
		}
	}
}
