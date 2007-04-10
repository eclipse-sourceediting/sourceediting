/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;

public class TestImportedNodes extends TestCase {


	public void testImportedComments() {
		IDOMModel orgModel = (IDOMModel) StructuredModelManager.getModelManager().

		createUnManagedStructuredModelFor("org.eclipse.jst.jsp.core.jspsource");
		IDOMModel foreignModel = (IDOMModel) StructuredModelManager.getModelManager().

		createUnManagedStructuredModelFor("org.eclipse.jst.jsp.core.jspsource");
		foreignModel.getStructuredDocument().set("<%-- abc --%>");
		Node child = foreignModel.getDocument().getLastChild();
		// import comment node
		child = orgModel.getDocument().importNode(child, true);
		orgModel.getDocument().appendChild(child);
		// create text node and insert it after comment node
		child = orgModel.getDocument().createTextNode("abc");
		orgModel.getDocument().appendChild(child);
		String text = orgModel.getStructuredDocument().get();
		assertEquals("document text was not expected", "<%-- abc --%>abc", text);
	}

}
