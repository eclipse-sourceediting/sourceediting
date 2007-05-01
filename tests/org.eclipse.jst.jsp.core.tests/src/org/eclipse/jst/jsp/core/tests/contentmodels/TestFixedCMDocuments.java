/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.contentmodels;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

public class TestFixedCMDocuments extends TestCase {
	public TestFixedCMDocuments(String name) {
		super(name);
	}

	public TestFixedCMDocuments() {
		super();
	}

	public static Test suite() {
		return new TestFixedCMDocuments();
	}

	public void testCHTMLdocument() {
		checkDocument(CMDocType.CHTML_DOC_TYPE);
	}

	private void checkDocument(Object documentKey) {
		CMDocument document = JSPCMDocumentFactory.getCMDocument(documentKey.toString());
		assertNotNull("missing doc:" + documentKey.toString(), document);
		CMNamedNodeMap elements = document.getElements();
		for (int i = 0; i < elements.getLength(); i++) {
			CMNode item = elements.item(i);
			verifyElementDeclarationHasName(item);
		}
	}

	private void verifyElementDeclarationHasName(CMNode item) {
		assertTrue(item.getNodeType() == CMNode.ELEMENT_DECLARATION);
		assertNotNull("no name on an element declaration", item.getNodeName());
		CMNamedNodeMap attrs = ((CMElementDeclaration) item).getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			CMNode attr = attrs.item(i);
			verifyAttributeDeclaration(((CMElementDeclaration) item), attr);
		}
	}

	private void verifyAttributeDeclaration(CMElementDeclaration elemDecl, CMNode attr) {
		assertTrue(attr.getNodeType() == CMNode.ATTRIBUTE_DECLARATION);
		assertNotNull("no name on an attribute declaration", attr.getNodeName());
		CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) attr;
		assertNotNull("no attribute 'type' on an attribute declaration " + elemDecl.getNodeName() + "/" + attr.getNodeName(), attrDecl.getAttrType());
	}

	public void testHTML4document() {
		checkDocument(CMDocType.HTML_DOC_TYPE);
	}

	public void testJSP11document() {
		checkDocument(CMDocType.JSP11_DOC_TYPE);

	}

	public void testJSP12document() {
		checkDocument(CMDocType.JSP12_DOC_TYPE);

	}

	public void testJSP20document() {
		checkDocument(CMDocType.JSP20_DOC_TYPE);

	}

	public void testTag20document() {
		checkDocument(CMDocType.TAG20_DOC_TYPE);

	}
}
