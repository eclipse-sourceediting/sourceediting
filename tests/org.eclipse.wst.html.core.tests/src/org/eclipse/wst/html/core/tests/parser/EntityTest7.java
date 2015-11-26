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

public class EntityTest7 extends ModelTest {
	/**
	 * Constructor for EntityTest7.
	 * 
	 * @param name
	 */
	public EntityTest7(String name) {
		super(name);
	}

	public EntityTest7() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new EntityTest7().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			//Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a>a&#65;&#x41;&unk;&unk&unk</a>");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();

		}
		finally {
			model.releaseFromEdit();
		}

	}
}
