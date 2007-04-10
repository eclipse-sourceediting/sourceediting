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

public class UpdaterTest2 extends ModelTest {
	/**
	 * Constructor for UpdaterTest2.
	 * 
	 * @param name
	 */
	public UpdaterTest2(String name) {
		super(name);
	}

	public UpdaterTest2() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest2().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.setText(this, "<HTML>\r\n<HEAD></HEAD>\r\n<BODY></BODY>\r\n</HTML>\r\n");
			Element html = (Element) document.getFirstChild();
			Element head = (Element) html.getFirstChild().getNextSibling();

			printSource(model);
			printTree(model);

			Element meta = document.createElement("META");
			meta.setAttribute("name", "GENERATOR");
			meta.setAttribute("content", "Updater Test 2");
			head.appendChild(meta);
			Text text4 = document.createTextNode("\r\n");
			head.insertBefore(text4, meta);
			Text text5 = document.createTextNode("\r\n");
			head.appendChild(text5);

			printSource(model);
			printTree(model);

			Element title = document.createElement("TITLE");
			Text text6 = document.createTextNode("");
			title.appendChild(text6);
			head.appendChild(title);

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
