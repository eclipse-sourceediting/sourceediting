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

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class UpdaterTest13 extends ModelTest {
	/**
	 * Constructor for UpdaterTest13.
	 * 
	 * @param name
	 */
	public UpdaterTest13(String name) {
		super(name);
	}

	public UpdaterTest13() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest13().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<a>\r\n<b>\r\n</a>");

			Element a = (Element) document.getFirstChild();
			Element b = (Element) a.getLastChild();
			Text text = document.createTextNode("  ");
			a.appendChild(text);

			printSource(model);
			printTree(model);

			a.removeChild(b);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
