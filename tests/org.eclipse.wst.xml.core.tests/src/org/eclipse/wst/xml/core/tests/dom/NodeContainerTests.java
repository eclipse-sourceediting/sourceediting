/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 */
public class NodeContainerTests extends TestCase {

	private static final String CONTENT_1 = "<root><a id='a'></a></root>";
	private static final String CONTENT_2 = "<root><a id='a'><b id='b'></b></a></root>";

	/**
	 * Default Constructor
	 */
	public NodeContainerTests() {
		super("Test NodeContainer");
	}

	/**
	 * Constructor
	 * 
	 * @param name the name of this test run
	 */
	public NodeContainerTests(String name) {
		super(name);
	}

	public void testAppendValidChild() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(CONTENT_1);

		IDOMDocument doc = model.getDocument();

		Element a = doc.getElementById("a");
		assertNotNull("Could not find element with id 'a' in " + CONTENT_1, a);

		Element b = doc.createElement("b");
		b.setAttribute("id", "b");

		try {
			a.appendChild(b);
		} catch (DOMException e) {
			fail("Should have been able to append " + b + " as a child of " + a);
		}
	}

	public void testAppendParentAsChildOfChild() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(CONTENT_2);

		IDOMDocument doc = model.getDocument();

		Element a = doc.getElementById("a");
		assertNotNull("Could not find element with id 'a' in " + CONTENT_2, a);

		Element b = doc.getElementById("b");
		assertNotNull("Could not find element with id 'b' in " + CONTENT_2, b);

		boolean threwException = false;
		try {
			b.appendChild(a);
		} catch (DOMException e) {
			assertEquals("Wrong type of exception was thrown: " + e, DOMException.HIERARCHY_REQUEST_ERR, e.code);
			threwException = true;
		}

		assertTrue("A DOMException with code HIERARCHY_REQUEST_ERR should have been thrown when appending a parent to its own child", threwException);
	}

	public void testReplaceChild() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(CONTENT_1);

		IDOMDocument doc = model.getDocument();

		Element a = doc.getElementById("a");
		assertNotNull("Could not find element with id 'a' in " + CONTENT_1, a);

		Element root = (Element) a.getParentNode();

		assertNotNull("Could not find parent node 'root'", root);
		Node replaced = root.replaceChild(a, a);
		assertTrue("Not replaced is not the same as 'a'", a == replaced);
		assertTrue("Child of root is not 'a'", root.getFirstChild() == a);
	}
}
