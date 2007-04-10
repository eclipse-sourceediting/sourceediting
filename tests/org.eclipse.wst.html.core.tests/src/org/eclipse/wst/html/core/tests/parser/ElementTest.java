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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementTest extends ModelTest {
	/**
	 * Constructor for ElementTest.
	 * 
	 * @param name
	 */
	public ElementTest(String name) {
		super(name);
	}

	public ElementTest() {
		super();
	}

	public Element changeTagName(Element element, String tagName) {
		Document document = element.getOwnerDocument();
		Node parent = element.getParentNode();
		Element newElement = document.createElement(tagName);
		NamedNodeMap attributes = element.getAttributes();
		while (attributes.getLength() > 0) {
			Attr attr = (Attr) attributes.item(0);
			attr = element.removeAttributeNode(attr);
			newElement.setAttributeNode(attr);
		}
		while (element.hasChildNodes()) {
			Node child = element.getFirstChild();
			child = element.removeChild(child);
			newElement.appendChild(child);
		}
		parent.insertBefore(newElement, element);
		parent.removeChild(element);
		return newElement;
	}

	public static void main(java.lang.String[] args) {
		new ElementTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<a><b e=\"f\" c=\"d\"><g /><h /></b></a>");
			Node a = document.getFirstChild();
			Element b = (Element) a.getFirstChild();

			printSource(model);
			printTree(model);

			//Element i =
			changeTagName(b, "i");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}

	}
}
