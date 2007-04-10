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

public class UpdaterTest9 extends ModelTest {
	/**
	 * Constructor for UpdaterTest9.
	 * 
	 * @param name
	 */
	public UpdaterTest9(String name) {
		super(name);
	}

	public UpdaterTest9() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest9().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element style = document.createElement("STYLE");
			document.appendChild(style);

			printSource(model);
			printTree(model);

			Text text2 = document.createTextNode("p");
			style.appendChild(text2);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
