/******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.xml.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

public class DOMImplementationTests extends TestCase {

	public void testCreateDocumentEmpty() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final Document document = model.createDocument(null, null, null);
		assertNull("The document should be empty", document.getFirstChild());
	}

	public void testCreateDocumentNoDocumentType() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final Document document = model.createDocument("http://eclipse.org", "foo:bar", null);
		final Node node = document.getFirstChild();
		assertNotNull("Document should not be empty", node);
		assertEquals("Element qualified name is not equal", "foo:bar", node.getNodeName());
		assertEquals("Element namespace URI is not equal", "http://eclipse.org", node.getNamespaceURI());
	}

	public void testCreateDocumentUsedDoctype() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final DocumentType doctype = model.createDocumentType("bar", "publicTest", "systemTest");
		IDOMDocument document = model.getDocument();
		document.appendChild(doctype);
		
		try {
			model.createDocument("http://eclipse.org", "foo:bar", doctype);
		}
		catch (DOMException e) {
			assertEquals("Wrong DOMExcetion thrown", DOMException.WRONG_DOCUMENT_ERR, e.code);
			return;
		}
		fail("Reusing the doctype from another document should have caused an exception");
	}

	public void testCreateDocument() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final DocumentType doctype = model.createDocumentType("bar", "publicTest", "systemTest");
		final Document document = model.createDocument("http://eclipse.org", "foo:bar", doctype);
		assertEquals("Document's doctype was not properly set", doctype, document.getDoctype());
		assertEquals("Document owner node is not set properly", document, doctype.getOwnerDocument());

		final Node node = document.getDocumentElement();
		assertNotNull("Document should not be empty", node);
		assertEquals("Element qualified name is not equal", "foo:bar", node.getNodeName());
		assertEquals("Element namespace URI is not equal", "http://eclipse.org", node.getNamespaceURI());
	}

	public void testCreateDocumentIllegalChar() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final char[] illegalTest = new char[] { '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '=', '+', ';', '\'', '\"', ',', '\\', '/', '<', '>', '[', ']', '{', '}', ' '};
		for (int i = 0; i < illegalTest.length; i++) {
			try {
				model.createDocument("http://eclipse.org", "foo:" + illegalTest[i], null);
			}
			catch (DOMException e) {
				assertEquals("Did not receive the correct DOMException", DOMException.INVALID_CHARACTER_ERR, e.code);
				continue;
			}
			fail("Illegal character ["+ illegalTest[i]+"] allowed in document element qualified name");
		}

		final String[] illegalNames = { " ", "f oo:bar", "foo: bar", "foo:bar ", ""};
		for (int i = 0; i < illegalNames.length; i++) {
			try {
				model.createDocument("http://eclipse.org", illegalNames[i], null);
			}
			catch (DOMException e) {
				assertEquals("Did not receive the correct DOMException for ["+ illegalNames[i]+ "]", DOMException.INVALID_CHARACTER_ERR, e.code);
				continue;
			}
			fail("Illegal character ["+ illegalNames[i]+"] allowed in document element qualified name");
		}
	}

	public void testCreateDocumentIllegalNamespace() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		try {
			model.createDocument(null, "foo:bar", null);
		}
		catch (DOMException e) {
			assertEquals("Did not receive the correct DOMException", DOMException.NAMESPACE_ERR, e.code);
			return;
		}
		fail("No exceptions for illegal input");
	}

	public void testCreateDocumentNamespaceWithNoQualifiedName() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		try {
			model.createDocument("http://eclipse.org", null, null);
		}
		catch (DOMException e) {
			assertEquals("Did not receive the correct DOMException", DOMException.NAMESPACE_ERR, e.code);
			return;
		}
		fail("No exceptions for illegal input");
	}

	public void testCreateDocumentLegalNamespaceXML() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		try {
			model.createDocument("http://www.w3.org/XML/1998/namespace", "xml:bar", null);
		}
		catch (DOMException e) {
			fail("xml prefix is allowed for the namespace http://www.w3.org/XML/1998/namespace");
		}
	}

	public void testCreateDocumentIllegalNamespaceXML() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		try {
			model.createDocument("http://eclipse.org", "xml:bar", null);
		}
		catch (DOMException e) {
			assertEquals("Did not receive the correct DOMException", DOMException.NAMESPACE_ERR, e.code);
			return;
		}
		fail("No exceptions for illegal input");
	}

	public void testCreateDocumentMalformedQualifiedNames() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final String[] names = {"foo:", "foo:.", "foo:-", "foo:bar:baz"};
		for (int i = 0; i < names.length; i++) {
			try {
				model.createDocument("http://eclipse.org", names[i], null);
			}
			catch (DOMException e) {
				assertEquals("Did not receive the correct DOMException for [" + names[i] +"]", DOMException.NAMESPACE_ERR, e.code);
				continue;
			}
			fail("Malformd qualified name ["+ names[i]+"]");
		}
	}

	public void testCreateDocumentWellformedQualifiedNames() {
		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		final String[] names = {"bar", "foo:bar"};
		for (int i = 0; i < names.length; i++) {
			try {
				model.createDocument("http://eclipse.org", names[i], null);
			}
			catch (DOMException e) {
				fail("[" + names[i] +"] flagged as an invalid qualified name");
			}
		}
	}
}