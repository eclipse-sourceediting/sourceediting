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

public class UpdaterTest6 extends ModelTest {
	/**
	 * Constructor for UpdaterTest6.
	 * 
	 * @param name
	 */
	public UpdaterTest6(String name) {
		super(name);
	}

	public UpdaterTest6() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest6().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			Text t = document.createTextNode("b");
			a.appendChild(t);

			printSource(model);
			printTree(model);

			a.removeChild(t);

			printSource(model);
			printTree(model);

			a.appendChild(t);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
