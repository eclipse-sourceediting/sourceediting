/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.dom;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.tests.Logger;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This test will cause a "region management error" to be thrown in DOM parser
 * (and its subsequent 'handleRefresh' to be called). This is "normal" in this
 * error case, of appending an jsp element to an html document. This
 * error/exception is not normally printed out, but is if 'debug' is turned on.
 */

public class TestOrphan extends TestCase {

	private static final String fCategory = "unittests";

	public TestOrphan(String name) {

		super(name);
	}

	private Document getJSPDoc() {
		IDOMModel structuredModel = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		Document doc = structuredModel.getDocument();
		return doc;
	}

	private Document getHTMLDoc() {

		IDOMModel structuredModel = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForHTML.ContentTypeID_HTML);
		Document doc = structuredModel.getDocument();
		return doc;
	}

	private Element makeElement(Document document) {

		Element element = document.createElement("IMG");
		element.setAttribute("src", "<bean:message />");
		return element;
	}

	public void testNonOrphanInHTMLDoc() {

		Logger.trace(fCategory, "testNonOrphanInHTMLDoc");
		Document doc = getHTMLDoc();
		Element element = makeElement(doc);
		AttrImpl attr = (AttrImpl) element.getAttributeNode("src");
		String attrValue = attr.getValue();
		Logger.trace(fCategory, "attrValue: " + attrValue);
		doc.appendChild(element);
		boolean isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertFalse(isJspValue);
	}

	public void testNonOrphanInJSPDoc() {

		Logger.trace(fCategory, "testNonOrphanInJSPDoc");
		Document doc = getJSPDoc();
		Element element = makeElement(doc);
		AttrImpl attr = (AttrImpl) element.getAttributeNode("src");
		String attrValue = attr.getValue();
		Logger.trace(fCategory, "attrValue: " + attrValue);
		doc.appendChild(element);
		boolean isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertTrue(isJspValue);
	}

	public void testNonOrphanInBoth() {

		Logger.trace(fCategory, "testNonOrphanInBoth");
		Document jspDoc = getJSPDoc();
		Element commonElement = makeElement(jspDoc);
		AttrImpl attr = (AttrImpl) commonElement.getAttributeNode("src");
		String attrValue = attr.getValue();
		Logger.trace(fCategory, "attrValue: " + attrValue);
		jspDoc.appendChild(commonElement);
		boolean isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertTrue(isJspValue);
		Document htmlDoc = getHTMLDoc();
		// this test will cause a "region management error" to be
		// thrown in parser (and its subsequent 'handleRefresh').
		// this is "normal" in this error case, of appending an jsp
		// element to an html document. This error/exception is not
		// normally printed out, but is if 'debug' is turned on.
		htmlDoc.appendChild(commonElement);
		isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertFalse(isJspValue);
	}

	public void testNonOrphanInBothReversedOrder() {

		Logger.trace(fCategory, "testNonOrphanInBothReversedOrder");
		Document htmlDoc = getHTMLDoc();
		Element commonElement = makeElement(htmlDoc);
		AttrImpl attr = (AttrImpl) commonElement.getAttributeNode("src");
		String attrValue = attr.getValue();
		Logger.trace(fCategory, "attrValue: " + attrValue);
		htmlDoc.appendChild(commonElement);
		boolean isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertFalse(isJspValue);
		Document jspDoc = getJSPDoc();
		// this little test shows its important to
		// actually create the element with the right kind of
		// document, not just append.
		// (and, append is needed too, as can be seen by
		// commenting out one or the other of the following
		// two lines.
		commonElement = makeElement(jspDoc);
		jspDoc.appendChild(commonElement);
		//
		attr = (AttrImpl) commonElement.getAttributeNode("src");
		attrValue = attr.getValue();
		Logger.trace(fCategory, "attrValue: " + attrValue);
		isJspValue = attr.hasNestedValue();
		Logger.trace(fCategory, "isJspValue: " + isJspValue);
		assertTrue(isJspValue);
	}

	public void doBothTests() {

		testNonOrphanInHTMLDoc();
		testNonOrphanInJSPDoc();
		testNonOrphanInBoth();
		testNonOrphanInBothReversedOrder();
	}
}
