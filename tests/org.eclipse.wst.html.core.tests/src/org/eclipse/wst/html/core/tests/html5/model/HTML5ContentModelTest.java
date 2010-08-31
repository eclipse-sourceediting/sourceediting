/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.html5.model;

import junit.framework.TestCase;

import org.eclipse.wst.html.core.internal.contentmodel.HTML5AttributeCollection;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

public class HTML5ContentModelTest extends TestCase {

	public HTML5ContentModelTest(String name) {
		super(name);
	}

	/**
	 * @param cm_doc_type
	 * @param elementName
	 * @param attrNameImport
	 */
	private void checkAttrNames(String documentKey, String elementName, String[] attrNames) {
		CMDocument document = HTMLCMDocumentFactory.getCMDocument(documentKey);
		CMNode elementDeclaration = document.getElements().getNamedItem(elementName);
		assertEquals("not an element declaration:" + elementDeclaration, CMNode.ELEMENT_DECLARATION, elementDeclaration.getNodeType());
		assertNotNull("missing element declaration:" + elementName, elementDeclaration);
		
		CMNamedNodeMap attributes = ((CMElementDeclaration) elementDeclaration).getAttributes();
		
		for (int i = 0; i < attrNames.length; i++) {
			assertNotNull("missing attribute declaration:" + attrNames[i] + " for element: " + elementName, attributes.getNamedItem(attrNames[i]));
		}
		assertEquals("Attributes defined in content model that are not expected by the test for element: " + elementName, attributes.getLength(), attrNames.length);
	}

	private void checkDocument(Object documentKey) {
		CMDocument document = HTMLCMDocumentFactory.getCMDocument(documentKey.toString());
		assertNotNull("missing doc:" + documentKey.toString(), document);
		CMNamedNodeMap elements = document.getElements();
		for (int i = 0; i < elements.getLength(); i++) {
			CMNode item = elements.item(i);
			verifyElementDeclarationHasName(item);
		}
	}

	private String[] getMergedlist(String[] list1, String[] list2){
		
		String[] mergerList = new String[list1.length + list2.length];
		System.arraycopy(list1, 0, mergerList, 0, list1.length);
		System.arraycopy(list2, 0, mergerList, list1.length, list2.length);

		return mergerList;
		
	}
	
	private String[] getGlobalList(){
		return getMergedlist(HTML5AttributeCollection.getGlobalAttributeList(), HTML5AttributeCollection.getGlobalEventList());
	}
	
	public void testAttributesOnHTML5Article() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.ARTICLE, getGlobalList());
	}
	
	public void testAttributesOnHTML5Aside() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.ASIDE, getGlobalList());
	}
	
	public void testAttributesOnHTML5Audio() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.AUDIO, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_SRC, HTML50Namespace.ATTR_NAME_PRELOAD, HTML50Namespace.ATTR_NAME_AUTOPLAY,
			HTML50Namespace.ATTR_NAME_LOOP, HTML50Namespace.ATTR_NAME_CONTROLS}));
	}

	public void testAttributesOnHTML5Canvas() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.CANVAS, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH}));
		
	}
	
	public void testAttributesOnHTML5Command() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.COMMAND, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_TYPE, HTML40Namespace.ATTR_NAME_LABEL, HTML50Namespace.ATTR_NAME_ICON,
			HTML40Namespace.ATTR_NAME_DISABLED, HTML40Namespace.ATTR_NAME_CHECKED, HTML50Namespace.ATTR_NAME_RADIOGROUP}));
		
	}
	
	public void testAttributesOnHTML5Datalist() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.DATALIST, getGlobalList());
	}
	
	public void testAttributesOnHTML5Details() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.DETAILS, getMergedlist(getGlobalList(), 
				new String[]{HTML50Namespace.ATTR_NAME_OPEN}));
	
	}
	
	public void testAttributesOnHTML5Figure() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.FIGURE, getGlobalList());
	}
	
	public void testAttributesOnHTML5FigCaption() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.FIGCAPTION, getGlobalList());
	}
	
	public void testAttributesOnHTML5Header() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.HEADER, getGlobalList());
	}
	
	public void testAttributesOnHTML5HGroup() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.HGROUP, getGlobalList());
	}
	
	public void testAttributesOnHTML5Keygen() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.KEYGEN, getMergedlist(getGlobalList(), 
				new String[]{HTML50Namespace.ATTR_NAME_AUTOFOCUS, HTML50Namespace.ATTR_NAME_CHALLENGE, HTML40Namespace.ATTR_NAME_DISABLED,
				HTML50Namespace.ATTR_NAME_FORM, HTML50Namespace.ATTR_NAME_KEYTYPE, HTML40Namespace.ATTR_NAME_NAME}));
	
	}
	
	public void testAttributesOnHTML5Mark() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.MARK, getGlobalList());
	}
	
	public void testAttributesOnHTML5Math() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.MATH, getGlobalList());
	}
	
	public void testAttributesOnHTML5Meter() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.METER, getMergedlist(getGlobalList(), 
				new String[]{HTML50Namespace.ATTR_NAME_MAX, HTML50Namespace.ATTR_NAME_MIN, HTML50Namespace.ATTR_NAME_LOW,
				HTML50Namespace.ATTR_NAME_HIGH, HTML50Namespace.ATTR_NAME_OPTIMUM, HTML50Namespace.ATTR_NAME_FORM, HTML40Namespace.ATTR_NAME_VALUE}));
	}
	
	public void testAttributesOnHTML5Nav() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.NAV, getGlobalList());
	}
	
	public void testAttributesOnHTML5Output() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.OUTPUT, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_FOR, HTML50Namespace.ATTR_NAME_FORM, HTML40Namespace.ATTR_NAME_NAME}));
	}
	
	public void testAttributesOnHTML5Progress() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.PROGRESS, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_VALUE, HTML50Namespace.ATTR_NAME_MAX, HTML50Namespace.ATTR_NAME_FORM}));
	}
	
	public void testAttributesOnHTML5RT() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.RT, getGlobalList());
	}
	
	public void testAttributesOnHTML5RP() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.RP, getGlobalList());
	}
	
	public void testAttributesOnHTML5Ruby() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.RUBY, getGlobalList());
	}
	
	public void testAttributesOnHTML5Section() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.SECTION, getGlobalList());
	}
	
	public void testAttributesOnHTML5Source() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.SOURCE, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_SRC, HTML40Namespace.ATTR_NAME_TYPE, HTML40Namespace.ATTR_NAME_MEDIA}));
	}
	
	public void testAttributesOnHTML5Summary() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.SUMMARY, getGlobalList());
	}
	
	public void testAttributesOnHTML5SVG() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.SVG, getGlobalList());
	}
	
	public void testAttributesOnHTML5Time() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.TIME, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_DATETIME, HTML50Namespace.ATTR_NAME_PUBDATE}));
	}
	
	public void testAttributesOnHTML5Video() {
		checkAttrNames(CMDocType.HTML5_DOC_TYPE, HTML50Namespace.ElementName.VIDEO, getMergedlist(getGlobalList(), 
				new String[]{HTML40Namespace.ATTR_NAME_SRC, HTML50Namespace.ATTR_NAME_PRELOAD, HTML50Namespace.ATTR_NAME_AUTOPLAY,
				HTML50Namespace.ATTR_NAME_LOOP, HTML50Namespace.ATTR_NAME_CONTROLS, HTML50Namespace.ATTR_NAME_POSTER, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH}));
	}
	public void testHTML5document() {
		checkDocument(CMDocType.HTML5_DOC_TYPE);
	}
	
	
	
	private void verifyAttributeDeclaration(CMElementDeclaration elemDecl, CMNode attr) {
		assertTrue(attr.getNodeType() == CMNode.ATTRIBUTE_DECLARATION);
		assertNotNull("no name on an attribute declaration", attr.getNodeName());
		CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) attr;
		assertNotNull("no attribute 'type' on an attribute declaration " + elemDecl.getNodeName() + "/" + attr.getNodeName(), attrDecl.getAttrType());
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
}
