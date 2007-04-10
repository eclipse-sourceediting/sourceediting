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

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CDATASectionTest3 extends ModelTest {
	/**
	 * Constructor for CDATASectionTest3.
	 * 
	 * @param name
	 */
	public CDATASectionTest3(String name) {
		super(name);
	}

	public CDATASectionTest3() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new CDATASectionTest3().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			Document document = model.getDocument();

			Element a = document.createElement("a");
			document.appendChild(a);
			CDATASection cdata = document.createCDATASection("contentOfCDATASection");
			a.appendChild(cdata);

			printSource(model);
			printTree(model);

			fOutputWriter.writeln(cdata.getData());

			cdata.setData("new < content");


			printSource(model);
			printTree(model);

			fOutputWriter.writeln(cdata.getData());

			cdata.setData("new > content");

			printSource(model);
			printTree(model);

			fOutputWriter.writeln(cdata.getData());

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
