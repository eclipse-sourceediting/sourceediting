/*******************************************************************************
 * Copyright (c) 2010, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.NodeList;

public class OmissibleTest extends ModelTest {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.tests.parser.ModelTest#testModel()
	 */
	public void testModel() {
		IDOMModel model = createHTMLModel();
		model.getStructuredDocument().set("<html><body><ul><li>First<li>Second<li>Third</ul></body></html>");
		NodeList list = model.getDocument().getElementsByTagName("ul");
		assertEquals("There may only be one list", 1, list.getLength());
		NodeList items = list.item(0).getChildNodes();
		assertEquals("The list must contain 3 items", 3, items.getLength());
	}

}
