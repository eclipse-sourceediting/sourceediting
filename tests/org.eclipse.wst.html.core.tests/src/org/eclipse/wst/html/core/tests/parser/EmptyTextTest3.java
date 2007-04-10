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

public class EmptyTextTest3 extends ModelTest {
	/**
	 * Constructor for EmptyTextTest3.
	 * 
	 * @param name
	 */
	public EmptyTextTest3(String name) {
		super(name);
	}

	public EmptyTextTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new EmptyTextTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Text text = document.createTextNode("");
			document.appendChild(text);

			printSource(model);
			printTree(model);

			Element a = document.createElement("a");
			document.insertBefore(a, text);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
