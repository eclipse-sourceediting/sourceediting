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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListTest extends ModelTest {
	/**
	 * Constructor for PITest.
	 * 
	 * @param name
	 */
	public NodeListTest(String name) {
		super(name);
	}

	public NodeListTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new NodeListTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<p></p>");
			Node parent = document.getFirstChild();

			for (int i = 0; i < 1; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 1; i < 5; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 5; i < 10; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 10; i < 20; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 20; i < 50; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 50; i < 100; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 100; i < 200; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 200; i < 500; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 500; i < 1000; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 1000; i < 2000; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 2000; i < 5000; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);

			for (int i = 5000; i < 10000; i++) {
				parent.appendChild(document.createElement("c"));
			}

			iterate(parent);
		}
		finally {
			model.releaseFromEdit();
		}

	}

	private void iterate(Node parent) {
		long start = System.currentTimeMillis();
		int i = 0;
		int t = 0;
		while (t < 1000) {
			NodeList childNodes = parent.getChildNodes();
			int length = childNodes.getLength();
			for (i = 0; i < length; i++) {
				childNodes.item(i);
			}
			// these 2 lines are required to invalidate the cache
			parent.appendChild(parent.getOwnerDocument().createTextNode(""));
			parent.removeChild(parent.getLastChild());
			t++;
		}
		float d = ((float) (System.currentTimeMillis() - start)) / t;
		System.out.println(d + " ms for " + i + " child nodes");
	}
}
