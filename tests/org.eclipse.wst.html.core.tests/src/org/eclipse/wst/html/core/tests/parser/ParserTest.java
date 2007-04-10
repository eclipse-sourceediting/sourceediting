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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ParserTest extends ModelTest {
	/**
	 * Constructor for ParserTest.
	 * 
	 * @param name
	 */
	public ParserTest(String name) {
		super(name);
	}

	public ParserTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new ParserTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<P><B></B><B></B></P><P></P>");
			Node p = document.getFirstChild();
			Node b = p.getFirstChild();
			Node b2 = b.getNextSibling();
			Node p2 = p.getNextSibling();
			/*
			 * Element p = document.createElement("P");
			 * document.appendChild(p); Element b =
			 * document.createElement("B"); p.appendChild(b); Element b2 =
			 * document.createElement("B"); p.appendChild(b2); Element p2 =
			 * document.createElement("P"); document.appendChild(p2);
			 */

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 0, 0, "a");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) b).getStartOffset(), 0, "b");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) b2).getStartOffset(), 0, "c");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) b2).getEndOffset(), 0, "d");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) p2).getStartOffset(), 0, "e");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) p2).getStartOffset() + 3, 0, "f");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, ((IDOMNode) p2).getEndOffset(), 0, "g");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
