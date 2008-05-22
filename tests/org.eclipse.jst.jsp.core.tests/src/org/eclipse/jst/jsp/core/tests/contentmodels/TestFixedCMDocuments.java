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
package org.eclipse.jst.jsp.core.tests.contentmodels;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.wst.html.core.internal.contentmodel.JSP11Namespace;
import org.eclipse.wst.html.core.internal.contentmodel.JSP20Namespace;
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

	/**
	 * @param cm_doc_type
	 * @param elementName
	 * @param attrNameImport
	 */
	private void checkAttrNames(String documentKey, String elementName, String[] attrNames) {
		CMDocument document = JSPCMDocumentFactory.getCMDocument(documentKey);
		CMNode elementDeclaration = document.getElements().getNamedItem(elementName);
		assertEquals("not an element declaration:" + elementDeclaration, CMNode.ELEMENT_DECLARATION, elementDeclaration.getNodeType());
		assertNotNull("missing element declaration:" + elementName, elementDeclaration);
		
		CMNamedNodeMap attributes = ((CMElementDeclaration) elementDeclaration).getAttributes();
		
		for (int i = 0; i < attrNames.length; i++) {
			assertNotNull("missing attribute declaration:" + attrNames[i], attributes.getNamedItem(attrNames[i]));
		}
		assertEquals("Attributes defined in content model that are not expected by the test", attributes.getLength(), attrNames.length);
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

	public void testAttributesOnJSP11Forward() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.FORWARD, new String[]{JSP11Namespace.ATTR_NAME_PAGE});
	}

	public void testAttributesOnJSP11GetProperty() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.GETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY});
	}

	public void testAttributesOnJSP11Include() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_PAGE, JSP11Namespace.ATTR_NAME_FLUSH});
	}

	public void testAttributesOnJSP11IncludeDirective() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_FILE});
	}

	public void testAttributesOnJSP11PageDirective() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_PAGE, new String[]{JSP11Namespace.ATTR_NAME_LANGUAGE, JSP11Namespace.ATTR_NAME_EXTENDS, JSP11Namespace.ATTR_NAME_IMPORT, JSP11Namespace.ATTR_NAME_SESSION, JSP11Namespace.ATTR_NAME_BUFFER, JSP11Namespace.ATTR_NAME_AUTOFLUSH, JSP11Namespace.ATTR_NAME_IS_THREAD_SAFE, JSP11Namespace.ATTR_NAME_INFO, JSP11Namespace.ATTR_NAME_ERROR_PAGE, JSP11Namespace.ATTR_NAME_IS_ERROR_PAGE, JSP11Namespace.ATTR_NAME_CONTENT_TYPE, JSP11Namespace.ATTR_NAME_PAGE_ENCODING});
	}

	public void testAttributesOnJSP11Param() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.PARAM, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_VALUE});
	}

	public void testAttributesOnJSP11Plugin() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.PLUGIN, new String[]{JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_CODE, JSP11Namespace.ATTR_NAME_CODEBASE, JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_ARCHIVE, JSP11Namespace.ATTR_NAME_ALIGN, JSP11Namespace.ATTR_NAME_HEIGHT, JSP11Namespace.ATTR_NAME_WIDTH, JSP11Namespace.ATTR_NAME_HSPACE, JSP11Namespace.ATTR_NAME_VSPACE, JSP11Namespace.ATTR_NAME_JREVERSION, JSP11Namespace.ATTR_NAME_NSPLUGINURL, JSP11Namespace.ATTR_NAME_IEPLUGINURL});
	}

	public void testAttributesOnJSP11Root() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.ROOT, new String[]{JSP11Namespace.ATTR_NAME_XMLNS_JSP, JSP11Namespace.ATTR_NAME_VERSION});
	}
	
//	public void testAttributesOnJSP21PageDirective() {
//		checkAttrNames(CMDocType.JSP21_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_PAGE, new String[]{JSP11Namespace.ATTR_NAME_LANGUAGE, JSP11Namespace.ATTR_NAME_EXTENDS, JSP11Namespace.ATTR_NAME_IMPORT, JSP11Namespace.ATTR_NAME_SESSION, JSP11Namespace.ATTR_NAME_BUFFER, JSP11Namespace.ATTR_NAME_AUTOFLUSH, JSP11Namespace.ATTR_NAME_IS_THREAD_SAFE, JSP11Namespace.ATTR_NAME_INFO, JSP11Namespace.ATTR_NAME_ERROR_PAGE, JSP11Namespace.ATTR_NAME_IS_ERROR_PAGE, JSP11Namespace.ATTR_NAME_CONTENT_TYPE, JSP11Namespace.ATTR_NAME_PAGE_ENCODING, JSP20Namespace.ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL, JSP20Namespace.ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES, JSP20Namespace.ATTR_NAME_ISELIGNORED});
//	}

	public void testAttributesOnJSP11SetProperty() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.SETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY, JSP11Namespace.ATTR_NAME_PARAM, JSP11Namespace.ATTR_NAME_VALUE});
	}

	public void testAttributesOnJSP11TaglibDirective() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_TAGLIB, new String[]{JSP11Namespace.ATTR_NAME_URI, JSP11Namespace.ATTR_NAME_PREFIX});
	}

	public void testAttributesOnJSP11UseBean() {
		checkAttrNames(CMDocType.JSP11_DOC_TYPE, JSP11Namespace.ElementName.USEBEAN, new String[]{JSP11Namespace.ATTR_NAME_ID, JSP11Namespace.ATTR_NAME_SCOPE, JSP11Namespace.ATTR_NAME_CLASS, JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_BEAN_NAME});
	}

	public void testAttributesOnJSP20Attribute() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP20Namespace.ElementName.ATTRIBUTE, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP20Namespace.ATTR_NAME_TRIM});
	}

	public void testAttributesOnJSP20Element() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP20Namespace.ElementName.ELEMENT, new String[]{JSP11Namespace.ATTR_NAME_NAME});
	}


	public void testAttributesOnJSP20Forward() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.FORWARD, new String[]{JSP11Namespace.ATTR_NAME_PAGE});
	}
	
	public void testAttributesOnJSP20GetProperty() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.GETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY});
	}

	// JSP 2.1 attributes on tag directives
//	public void testAttributesOnTAG21TagDirective() {
//		checkAttrNames(CMDocType.TAG21_DOC_TYPE, JSP20Namespace.ElementName.DIRECTIVE_TAG, new String[]{JSP20Namespace.ATTR_NAME_DISPLAY_NAME, JSP20Namespace.ATTR_NAME_BODY_CONTENT, JSP20Namespace.ATTR_NAME_DYNAMIC_ATTRIBUTES, JSP20Namespace.ATTR_NAME_SMALL_ICON, JSP20Namespace.ATTR_NAME_LARGE_ICON, JSP20Namespace.ATTR_NAME_DESCRIPTION, JSP20Namespace.ATTR_NAME_EXAMPLE, JSP20Namespace.ATTR_NAME_LANGUAGE, JSP11Namespace.ATTR_NAME_IMPORT, JSP11Namespace.ATTR_NAME_PAGE_ENCODING, JSP20Namespace.ATTR_NAME_ISELIGNORED, JSP20Namespace.ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL, JSP20Namespace.ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES});
//	}

	public void testAttributesOnJSP20Include() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_PAGE, JSP11Namespace.ATTR_NAME_FLUSH});
	}
	
	public void testAttributesOnJSP20IncludeDirective() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_FILE});
	}
	
	public void testAttributesOnJSP20Output() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP20Namespace.ElementName.OUTPUT, new String[]{JSP20Namespace.ATTR_NAME_OMIT_XML_DECL, JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC, JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT, JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM});
	}
	
	public void testAttributesOnJSP20PageDirective() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_PAGE, new String[]{JSP11Namespace.ATTR_NAME_LANGUAGE, JSP11Namespace.ATTR_NAME_EXTENDS, JSP11Namespace.ATTR_NAME_IMPORT, JSP11Namespace.ATTR_NAME_SESSION, JSP11Namespace.ATTR_NAME_BUFFER, JSP11Namespace.ATTR_NAME_AUTOFLUSH, JSP11Namespace.ATTR_NAME_IS_THREAD_SAFE, JSP11Namespace.ATTR_NAME_INFO, JSP11Namespace.ATTR_NAME_ERROR_PAGE, JSP11Namespace.ATTR_NAME_IS_ERROR_PAGE, JSP11Namespace.ATTR_NAME_CONTENT_TYPE, JSP11Namespace.ATTR_NAME_PAGE_ENCODING, JSP20Namespace.ATTR_NAME_ISELIGNORED});
	}
	
	public void testAttributesOnJSP20Param() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.PARAM, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_VALUE});
	}
	
	public void testAttributesOnJSP20Plugin() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.PLUGIN, new String[]{JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_CODE, JSP11Namespace.ATTR_NAME_CODEBASE, JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_ARCHIVE, JSP11Namespace.ATTR_NAME_ALIGN, JSP11Namespace.ATTR_NAME_HEIGHT, JSP11Namespace.ATTR_NAME_WIDTH, JSP11Namespace.ATTR_NAME_HSPACE, JSP11Namespace.ATTR_NAME_VSPACE, JSP11Namespace.ATTR_NAME_JREVERSION, JSP11Namespace.ATTR_NAME_NSPLUGINURL, JSP11Namespace.ATTR_NAME_IEPLUGINURL, JSP20Namespace.ATTR_NAME_MAYSCRIPT});
	}
	
	public void testAttributesOnJSP20Root() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.ROOT, new String[]{JSP11Namespace.ATTR_NAME_XMLNS_JSP, JSP11Namespace.ATTR_NAME_VERSION});
	}
	
	public void testAttributesOnJSP20SetProperty() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.SETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY, JSP11Namespace.ATTR_NAME_PARAM, JSP11Namespace.ATTR_NAME_VALUE});
	}
	
	public void testAttributesOnJSP20TaglibDirective() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_TAGLIB, new String[]{JSP11Namespace.ATTR_NAME_URI, JSP11Namespace.ATTR_NAME_PREFIX, JSP20Namespace.ATTR_NAME_TAGDIR});
	}
	
	public void testAttributesOnJSP20UseBean() {
		checkAttrNames(CMDocType.JSP20_DOC_TYPE, JSP11Namespace.ElementName.USEBEAN, new String[]{JSP11Namespace.ATTR_NAME_ID, JSP11Namespace.ATTR_NAME_SCOPE, JSP11Namespace.ATTR_NAME_CLASS, JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_BEAN_NAME});
	}
	
	public void testAttributesOnTAG20Attribute() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.ATTRIBUTE, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP20Namespace.ATTR_NAME_TRIM});
	}
	
	public void testAttributesOnTAG20AttributeDirective() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.DIRECTIVE_ATTRIBUTE, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP20Namespace.ATTR_NAME_REQUIRED, JSP20Namespace.ATTR_NAME_FRAGMENT, JSP20Namespace.ATTR_NAME_RTEXPRVALUE, JSP20Namespace.ATTR_NAME_TYPE, JSP20Namespace.ATTR_NAME_DESCRIPTION});
	}
	
	public void testAttributesOnTAG20DoBody() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.DOBODY, new String[]{JSP20Namespace.ATTR_NAME_VAR, JSP20Namespace.ATTR_NAME_VARREADER, JSP11Namespace.ATTR_NAME_SCOPE});
	}
	
	public void testAttributesOnTAG20Element() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.ELEMENT, new String[]{JSP11Namespace.ATTR_NAME_NAME});
	}
	
	public void testAttributesOnTAG20Forward() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.FORWARD, new String[]{JSP11Namespace.ATTR_NAME_PAGE});
	}
	
	public void testAttributesOnTAG20GetProperty() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.GETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY});
	}
	
	public void testAttributesOnTAG20Include() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_PAGE, JSP11Namespace.ATTR_NAME_FLUSH});
	}
	
	public void testAttributesOnTAG20IncludeDirective() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_INCLUDE, new String[]{JSP11Namespace.ATTR_NAME_FILE});
	}
	
	public void testAttributesOnTAG20Invoke() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.INVOKE, new String[]{JSP20Namespace.ATTR_NAME_FRAGMENT, JSP20Namespace.ATTR_NAME_VAR, JSP20Namespace.ATTR_NAME_VARREADER, JSP11Namespace.ATTR_NAME_SCOPE});
	}
	
	public void testAttributesOnTAG20Output() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.OUTPUT, new String[]{JSP20Namespace.ATTR_NAME_OMIT_XML_DECL, JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC, JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT, JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM});
	}
	
	public void testAttributesOnTAG20Param() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.PARAM, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_VALUE});
	}
	
	public void testAttributesOnTAG20Plugin() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.PLUGIN, new String[]{JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_CODE, JSP11Namespace.ATTR_NAME_CODEBASE, JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_ARCHIVE, JSP11Namespace.ATTR_NAME_ALIGN, JSP11Namespace.ATTR_NAME_HEIGHT, JSP11Namespace.ATTR_NAME_WIDTH, JSP11Namespace.ATTR_NAME_HSPACE, JSP11Namespace.ATTR_NAME_VSPACE, JSP11Namespace.ATTR_NAME_JREVERSION, JSP11Namespace.ATTR_NAME_NSPLUGINURL, JSP11Namespace.ATTR_NAME_IEPLUGINURL, JSP20Namespace.ATTR_NAME_MAYSCRIPT});
	}
	
	public void testAttributesOnTAG20Root() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.ROOT, new String[]{JSP11Namespace.ATTR_NAME_XMLNS_JSP, JSP11Namespace.ATTR_NAME_VERSION});
	}
	
	public void testAttributesOnTAG20SetProperty() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.SETPROPERTY, new String[]{JSP11Namespace.ATTR_NAME_NAME, JSP11Namespace.ATTR_NAME_PROPERTY, JSP11Namespace.ATTR_NAME_PARAM, JSP11Namespace.ATTR_NAME_VALUE});
	}
	
	public void testAttributesOnTAG20TagDirective() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.DIRECTIVE_TAG, new String[]{JSP20Namespace.ATTR_NAME_DISPLAY_NAME, JSP20Namespace.ATTR_NAME_BODY_CONTENT, JSP20Namespace.ATTR_NAME_DYNAMIC_ATTRIBUTES, JSP20Namespace.ATTR_NAME_SMALL_ICON, JSP20Namespace.ATTR_NAME_LARGE_ICON, JSP20Namespace.ATTR_NAME_DESCRIPTION, JSP20Namespace.ATTR_NAME_EXAMPLE, JSP20Namespace.ATTR_NAME_LANGUAGE, JSP11Namespace.ATTR_NAME_IMPORT, JSP11Namespace.ATTR_NAME_PAGE_ENCODING, JSP20Namespace.ATTR_NAME_ISELIGNORED});
	}
	
	public void testAttributesOnTAG20TaglibDirective() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.DIRECTIVE_TAGLIB, new String[]{JSP11Namespace.ATTR_NAME_URI, JSP11Namespace.ATTR_NAME_PREFIX, JSP20Namespace.ATTR_NAME_TAGDIR});
	}
	
	public void testAttributesOnTAG20UseBean() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP11Namespace.ElementName.USEBEAN, new String[]{JSP11Namespace.ATTR_NAME_ID, JSP11Namespace.ATTR_NAME_SCOPE, JSP11Namespace.ATTR_NAME_CLASS, JSP11Namespace.ATTR_NAME_TYPE, JSP11Namespace.ATTR_NAME_BEAN_NAME});
	}
	
	public void testAttributesOnTAG20VariableDirective() {
		checkAttrNames(CMDocType.TAG20_DOC_TYPE, JSP20Namespace.ElementName.DIRECTIVE_VARIABLE, new String[]{JSP20Namespace.ATTR_NAME_NAME_GIVEN, JSP20Namespace.ATTR_NAME_NAME_FROM_ATTRIBUTE, JSP20Namespace.ATTR_NAME_ALIAS, JSP20Namespace.ATTR_NAME_VARIABLE_CLASS, JSP20Namespace.ATTR_NAME_DECLARE, JSP11Namespace.ATTR_NAME_SCOPE, JSP20Namespace.ATTR_NAME_DESCRIPTION});
	}
	
	public void testCHTMLdocument() {
		checkDocument(CMDocType.CHTML_DOC_TYPE);
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
