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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CDATASectionTest extends ModelTest {
	/**
	 * Constructor for CDATASectionTest.
	 * 
	 * @param name
	 */
	public CDATASectionTest(String name) {
		super(name);
	}

	public CDATASectionTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new CDATASectionTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a><![CDATA[contentOfCDATASection]]></a>");

			printSource(model);
			printTree(model);

			Node a = document.getFirstChild();
			for (Node child = a.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.CDATA_SECTION_NODE)
					continue;
				CDATASection cdata = (CDATASection) child;
				fOutputWriter.writeln(cdata.getData());
			}

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
