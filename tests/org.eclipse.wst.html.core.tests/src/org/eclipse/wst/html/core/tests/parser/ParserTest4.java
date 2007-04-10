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

public class ParserTest4 extends ModelTest {
	/**
	 * Constructor for ParserTest4.
	 * 
	 * @param name
	 */
	public ParserTest4(String name) {
		super(name);
	}

	public ParserTest4() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new ParserTest4().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {

			IStructuredDocument structuredDocument = model.getStructuredDocument();
			//Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<a>\n\n<% a %>\n<% b %>\n</a>");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 4, 0, "<");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 5, 0, "b");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 6, 0, "c");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 7, 0, "d");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 8, 0, "e");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 9, 0, ">");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
