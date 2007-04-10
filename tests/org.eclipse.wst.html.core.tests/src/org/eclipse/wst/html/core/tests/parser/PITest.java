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
import org.w3c.dom.ProcessingInstruction;

public class PITest extends ModelTest {
	/**
	 * Constructor for PITest.
	 * 
	 * @param name
	 */
	public PITest(String name) {
		super(name);
	}

	public PITest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new PITest().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a><?echo $PHP_SELF?></a>");

			printSource(model);
			printTree(model);

			Node a = document.getFirstChild();
			for (Node child = a.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)
					continue;
				ProcessingInstruction pi = (ProcessingInstruction) child;
				String target = pi.getTarget();
				if (target == null)
					target = "null";
				String data = pi.getData();
				if (data == null)
					data = "null";
				fOutputWriter.writeln("target(" + target + ") data (" + data + ")");
			}

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
