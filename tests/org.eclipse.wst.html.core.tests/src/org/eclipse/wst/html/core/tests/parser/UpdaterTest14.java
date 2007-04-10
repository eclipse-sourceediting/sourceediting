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

public class UpdaterTest14 extends ModelTest {
	/**
	 * Constructor for UpdaterTest14.
	 * 
	 * @param name
	 */
	public UpdaterTest14(String name) {
		super(name);
	}

	public UpdaterTest14() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new UpdaterTest14().testModel();
	}

	public void testModel() {
		IDOMModel model = createXMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			//Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<%= aaaa %>");

			printSource(model);
			printTree(model);

			structuredDocument.replaceText(null, 2, 0, " ");

			printSource(model);
			printTree(model);

			saveAndCompareTestResults();

		}
		finally {
			model.releaseFromEdit();
		}

	}
}
