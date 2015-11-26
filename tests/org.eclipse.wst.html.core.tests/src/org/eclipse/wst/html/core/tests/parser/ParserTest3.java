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

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ParserTest3 extends ModelTest {
	/**
	 * Constructor for ParserTest3.
	 * 
	 * @param name
	 */
	public ParserTest3(String name) {
		super(name);
	}

	public ParserTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new ParserTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			//StructuredDocumentEvent event =
			structuredDocument.replaceText(null, 0, 0, "<a b  >");
			// dont's print instance of event, or many tests will fail when
			// event class is changed (exactly what is not needed for unit
			// tests!)
			// fOutputWriter.writeln(event.toString());

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

			StructuredDocumentEvent event2 = structuredDocument.replaceText(null, 4, 1, "");
			// I removed this part of output, since renaming class or package
			// will cause test to fail!
			fOutputWriter.writeln(event2.toString());

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
