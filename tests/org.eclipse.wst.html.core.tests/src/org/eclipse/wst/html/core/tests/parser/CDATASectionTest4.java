/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

public class CDATASectionTest4 extends ModelTest {
	
	/**
	 * 
	 * @param name
	 */
	public CDATASectionTest4(String name) {
		super(name);
	}

	public CDATASectionTest4() {
		super();
	}


	public void testModel() {
		IDOMModel model = createHTMLModel();
		assertNotNull(model);
		IDOMDocument document = model.getDocument();
		assertNotNull(document);
		
		Element html = document.createElement("html");
		html.setAttribute("xmlns:jsp", "http://java.sun.com/JSP/Page");
		
		Element head = document.createElement("head");
		Element script = document.createElement("script");
		script.setAttribute("type", "text/javascript");
		CDATASection cdataSection = document.createCDATASection("test");
		
		try {
			document.appendChild(html);
			html.appendChild(head);
			head.appendChild(script);
			script.appendChild(cdataSection);
		}
		catch (Exception e) {
			fail("Did not permit CDATA section in script element.");
		}

	}

}
