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
package org.eclipse.wst.html.core.tests.model;


import junit.framework.TestCase;

import org.eclipse.wst.html.core.tests.utils.FileUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.DocumentCSS;

public class GetOverrideStyleTest extends TestCase {
	/**
	 * Constructor for StyleTest.
	 * 
	 * @param name
	 */
	public GetOverrideStyleTest(String name) {
		super(name);
	}

	public GetOverrideStyleTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new GetOverrideStyleTest().testModel();
	}

	public void testModel() {
		IDOMModel model = FileUtil.createHTMLModel(); 
		try {
			model.getStructuredDocument().setText(null, "<style>p {	border-color: blue; margin: 0; } </style> <p style=\"border-color: red; margin: 1;\">");
			Document document = model.getDocument();

			DocumentCSS ddd = (DocumentCSS) document;
			NodeList n = document.getElementsByTagName("p");
			ddd.getOverrideStyle((Element) n.item(0), "");
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}
}
