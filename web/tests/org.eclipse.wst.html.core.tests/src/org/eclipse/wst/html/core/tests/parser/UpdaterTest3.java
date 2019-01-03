/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class UpdaterTest3 extends ModelTest {
	/**
	 * Constructor for UpdaterTest3.
	 * 
	 * @param name
	 */
	public UpdaterTest3(String name) {
		super(name);
	}

	public UpdaterTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			Element b = document.createElement("b");
			a.appendChild(b);

			printSource(model);
			printTree(model);

			Text t1 = document.createTextNode("\n");
			a.insertBefore(t1, b);

			printSource(model);
			printTree(model);

			Text t2 = document.createTextNode("\n");
			a.appendChild(t2);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
