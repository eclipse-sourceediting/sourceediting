/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementImplTests extends TestCase {

	private static final String contents = "<elementPrefix:localName attrPrefix:local='lorem' xmlns:elementPrefix='urn:prefix' xmlns:attributePrefix='urn:attribute:prefix' />"; //$NON-NLS-1$
	private static final String decl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$
	public ElementImplTests() {
	}

	public ElementImplTests(String name) {
		super(name);
	}

	public void testElementImplPrefix() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		assertEquals("attribute prefix was not as expected", "elementPrefix", documentElement.getPrefix());
	}

	public void testElementImplLocalName() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		assertEquals("attribute local name was not as expected", "localName", documentElement.getLocalName());
	}

	public void testAttrBasedElementNamespace() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		assertNotNull("Namespace was not found.", documentElement.getNamespaceURI());
		String namespace = documentElement.getNamespaceURI();
		assertEquals("attribute local name was not as expected", "urn:prefix", namespace);
	}

	public void testNamespaceURIOnCreation() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element element = model.getDocument().createElement("simple");
		assertNull("namespace was found", element.getNamespaceURI());

		Element element2 = model.getDocument().createElementNS("http://lorem.ipsum", "complex");
		assertEquals("attribute namespace URI was not as expected", "http://lorem.ipsum", element2.getNamespaceURI());
		Element element3 = model.getDocument().createElementNS(null, "complex");
		assertEquals("attribute namespace URI was not as expected", null, element3.getNamespaceURI());
	}

	public void testGetElementsByTagNameNoChildren() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("root");
		document.appendChild(root);

		NodeList children = root.getElementsByTagName("*");
		assertEquals(0, children.getLength());
	}

	public void testGetElementsByTagNameChildren() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("root");
		document.appendChild(root);
		root.appendChild(document.createElement("child"));
		root.appendChild(document.createElement("child"));

		NodeList children = root.getElementsByTagName("*");
		assertEquals(2, children.getLength());
	}

	public void testGetElementsByTagNameChildrenBySameName() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("child");
		document.appendChild(root);
		root.appendChild(document.createElement("child"));
		root.appendChild(document.createElement("child"));

		NodeList children = root.getElementsByTagName("child");
		assertEquals(2, children.getLength());
	}

	public void testGetElementsByTagNameNS() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("test:root");
		root.setAttribute("xmlns:test", "http://test");
		document.appendChild(root);
		root.appendChild(document.createElement("test:child"));
		root.appendChild(document.createElement("test:child"));

		NodeList children = root.getElementsByTagNameNS("http://test", "*");
		assertEquals(2, children.getLength());
	}

	public void testGetElementsByTagNameNSTestNS() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("test:root");
		root.setAttribute("xmlns:test", "http://test");
		root.setAttribute("xmlns:test2", "http://test2");
		document.appendChild(root);
		root.appendChild(document.createElement("test:child"));
		root.appendChild(document.createElement("test:child"));
		root.appendChild(document.createElement("test2:child"));

		NodeList children = root.getElementsByTagNameNS("http://test", "*");
		assertEquals(2, children.getLength());
	}

	public void testGetElementsByTagNameNSAnyNS() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("test:root");
		root.setAttribute("xmlns:test", "http://test");
		root.setAttribute("xmlns:test2", "http://test2");
		document.appendChild(root);
		root.appendChild(document.createElement("test:child"));
		root.appendChild(document.createElement("test:child"));
		root.appendChild(document.createElement("test2:child"));

		NodeList children = root.getElementsByTagNameNS("*", "*");
		assertEquals(3, children.getLength());
	}

	public void testRemoveNonexistantAttrByName() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(decl);

		Document document = model.getDocument();
		Element root = document.createElement("test:root");
		root.setAttribute("xmlns:test", "http://test");
		root.setAttribute("xmlns:test2", "http://test2");
		document.appendChild(root);

		boolean success = false;
		try {
			root.removeAttribute(getName());
			success = true;
		}
		catch (DOMException ex) {
			assertTrue("threw_NOT_FOUND_ERR", ex.code != DOMException.NOT_FOUND_ERR);
		}
		assertTrue("threw exception", success);
	}
}
