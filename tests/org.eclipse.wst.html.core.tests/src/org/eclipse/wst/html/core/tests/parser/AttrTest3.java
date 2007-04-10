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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AttrTest3 extends ModelTest {
	/**
	 * Constructor for AttrTest3.
	 * 
	 * @param name
	 */
	public AttrTest3(String name) {
		super(name);
	}

	public AttrTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new AttrTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.setText(this, "<a xmlns='default-uri' xmlns:b='b-uri'><c d='d-value' b:e='e-value'/></a>");

			printSource(model);
			printTree(model);

			Element a = (Element) document.getFirstChild();
			Element c = (Element) a.getFirstChild();

			Attr xmlns = a.getAttributeNode("xmlns");
			fOutputWriter.writeln("xmlns [" + xmlns.getNamespaceURI() + "]");
			Attr xmlns_b = a.getAttributeNode("xmlns:b");
			fOutputWriter.writeln("xmlns:b [" + xmlns_b.getNamespaceURI() + "]");
			Attr d = c.getAttributeNode("d");
			fOutputWriter.writeln("d [" + d.getNamespaceURI() + "]");
			Attr b_e = c.getAttributeNode("b:e");
			fOutputWriter.writeln("b:e [" + b_e.getNamespaceURI() + "]");

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
