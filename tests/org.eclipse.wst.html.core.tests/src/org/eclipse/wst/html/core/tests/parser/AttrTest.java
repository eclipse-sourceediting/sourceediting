/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

public class AttrTest extends ModelTest {
	/**
	 * Constructor for AttrTest.
	 * 
	 * @param name
	 */
	public AttrTest(String name) {
		super(name);
	}

	public AttrTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new AttrTest().testModel();
	}

	public void testModel() {
		XMLModel model = createHTMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			//Document document = model.getDocument();

			structuredDocument.setText(this, "<a href=\"<%=c%>\">a</a>");

			printSource(model);
			printTree(model);
			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}
	}
}