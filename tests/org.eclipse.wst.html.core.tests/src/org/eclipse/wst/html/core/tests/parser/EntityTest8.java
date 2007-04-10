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

public class EntityTest8 extends ModelTest {
	/**
	 * Constructor for EntityTest8.
	 * 
	 * @param name
	 */
	public EntityTest8(String name) {
		super(name);
	}

	public EntityTest8() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new EntityTest8().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			//Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "&ibm;&rtp;");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();
		}
		finally {
			model.releaseFromEdit();
		}


	}
}
