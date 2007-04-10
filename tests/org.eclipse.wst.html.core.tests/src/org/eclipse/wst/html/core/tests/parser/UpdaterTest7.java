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

public class UpdaterTest7 extends ModelTest {
	/**
	 * Constructor for UpdaterTest7.
	 * 
	 * @param name
	 */
	public UpdaterTest7(String name) {
		super(name);
	}

	public UpdaterTest7() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest7().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element p = document.createElement("P");
			document.appendChild(p);
			Element b = document.createElement("B");
			p.appendChild(b);

			printSource(model);
			printTree(model);

			Element b2 = document.createElement("B");
			p.insertBefore(b2, b);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();

		}
		finally {
			model.releaseFromEdit();
		}

	}
}
