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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ParserTest2 extends ModelTest {
	/**
	 * Constructor for ParserTest2.
	 * 
	 * @param name
	 */
	public ParserTest2(String name) {
		super(name);
	}

	public ParserTest2() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new ParserTest2().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<a b=\"\"  c=\"c\">");

			printSource(model);
			printTree(model);

			Element a = (Element) document.getFirstChild();
			NamedNodeMap attributes = a.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attr = attributes.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				fOutputWriter.writeln(name + "=[" + value + "]");
			}

			structuredDocument.replaceText(null, 8, 0, "d");

			printSource(model);
			printTree(model);

			a = (Element) document.getFirstChild();
			attributes = a.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attr = attributes.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				fOutputWriter.writeln(name + "=[" + value + "]");
			}

			structuredDocument.replaceText(null, 8, 1, "");

			printSource(model);
			printTree(model);

			a = (Element) document.getFirstChild();
			attributes = a.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attr = attributes.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				fOutputWriter.writeln(name + "=[" + value + "]");
			}

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
