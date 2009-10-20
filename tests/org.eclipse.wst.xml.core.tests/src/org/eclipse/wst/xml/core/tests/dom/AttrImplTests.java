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
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class AttrImplTests extends TestCase {

	private static final String contents = "<elementName attrPrefix:local='lorem' />"; //$NON-NLS-1$
	private static final String xmlNamespaceContents = "<elementName xml:space=\"preserve\"/>";

	public AttrImplTests() {
	}

	public AttrImplTests(String name) {
		super(name);
	}

	public void testAttrImplPrefix() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);
		
		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		NamedNodeMap attributes = documentElement.getAttributes();
		assertTrue("no attributes found", attributes.getLength() > 0);
		Attr attribute = (Attr) attributes.item(0);
		assertEquals("attribute prefix was not as expected", "attrPrefix", attribute.getPrefix());
	}

	public void testAttrImplLocalName() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Element documentElement = model.getDocument().getDocumentElement();
		assertNotNull("no document element found", documentElement);
		NamedNodeMap attributes = documentElement.getAttributes();
		assertTrue("no attributes found", attributes.getLength() > 0);
		Attr attribute = (Attr) attributes.item(0);
		assertEquals("attribute local name was not as expected", "local", attribute.getLocalName());
	}

	public void testNamespaceURIOnCreation() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(contents);

		Attr attr = model.getDocument().createAttribute("simple");
		assertNull("namespace was found", attr.getNamespaceURI());
		
		Attr attr2 = model.getDocument().createAttributeNS("http://lorem.ipsum", "complex");
		assertEquals("attribute namespace URI was not as expected", "http://lorem.ipsum", attr2.getNamespaceURI());
		Attr attr3 = model.getDocument().createAttributeNS(null, "complex");
		assertEquals("attribute namespace URI was not as expected", null, attr3.getNamespaceURI());
	}

	public void testNullAttributeValue() {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		Attr attribute = model.getDocument().createAttribute("attr");
		try {
			attribute.setNodeValue(null);
		} catch (NullPointerException npe) {
			fail("Setting a null node value caused a NullPointerException.");
		}
	}

	public void testGetAttributeNSForXMLNamespace() {
		final String xmlns = "http://www.w3.org/XML/1998/namespace";
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set(xmlNamespaceContents);

		Element element = model.getDocument().getDocumentElement();
		String value = element.getAttributeNS(xmlns, "space");
		assertEquals("Did not find space attribute for XML Namespace", "preserve", value);
	}

}
