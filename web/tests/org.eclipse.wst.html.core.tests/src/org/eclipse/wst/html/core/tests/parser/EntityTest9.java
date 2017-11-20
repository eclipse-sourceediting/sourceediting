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

public class EntityTest9 extends ModelTest {
	/**
	 * Constructor for EntityTest9.
	 * 
	 * @param name
	 */
	public EntityTest9(String name) {
		super(name);
	}

	public EntityTest9() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new EntityTest9().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			//Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "&lt;");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(this, 4, 0, "&gt");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(this, 7, 0, ";");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
