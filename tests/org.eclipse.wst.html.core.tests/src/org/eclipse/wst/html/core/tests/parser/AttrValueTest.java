/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttrValueTest extends ModelTest {

	private static final String[] VALUES = {"<<Previous", "<page>", "Next>>"};
	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.tests.parser.ModelTest#testModel()
	 */
	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			assertNotNull(model);
			IStructuredDocument document = model.getStructuredDocument();
			assertNotNull(document);

			document.setText(this, "<button value=\""+ VALUES[0] +"\"></button><button value=\"" + VALUES[1] + "\"></button><button value=\"" + VALUES[2] + "\"></button>");

			IDOMDocument dom = model.getDocument();
			NodeList nodes = dom.getElementsByTagName("button");
			assertTrue("Must be 3 button elements in the document.", nodes.getLength() == 3);

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				Node attr = node.getAttributes().getNamedItem("value");
				assertTrue("Attribute 'value' not present.", attr != null && attr.getNodeValue().length() > 0);
				assertEquals("Attribute values are not equal", VALUES[i], attr.getNodeValue()); 
			}
		} finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

}