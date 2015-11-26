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
package org.eclipse.jst.jsp.ui.tests.modelquery;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.tests.JSPUITestsPlugin;
import org.eclipse.jst.jsp.ui.tests.Logger;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author pavery
 */
public class ModelQueryTester extends TestCase {

	private final boolean testShippedDTDLookup = false;

	// Our Structured Model, which should always be an XMLModel (or subclass)
	protected IDOMModel fModel = null;

	protected ModelQuery fModelQuery = null;

	public ModelQueryTester(String name) {
		super(name);
	}

	protected static Test suite() {
		return new TestSuite(ModelQueryTester.class);
	}

	public static void main(java.lang.String[] args) {
		if (args == null || args.length == 0) {
			runAll();
		}
		else if (args.length == 1) {
			String methodToRun = args[0].trim();
			runOne(methodToRun);
		}

	}

	protected static void runAll() {
		TestRunner.run(suite());

	}

	protected static void runOne(String methodName) {
		TestSuite testSuite = new TestSuite();
		TestCase test = new ModelQueryTester(methodName);
		testSuite.addTest(test);
		junit.textui.TestRunner.run(testSuite);
	}

	/**
	 * Create an empty HTML model
	 */
	protected void setUpHTML() {
		fModel = (IDOMModel) createModel(ContentTypeIdForHTML.ContentTypeID_HTML);
		initVars();
	}

	/**
	 * Create an empty XML model
	 */
	protected void setUpXML() {
		fModel = (IDOMModel) createModel(ContentTypeIdForXML.ContentTypeID_XML);
		initVars();
	}


	/**
	 * Initialize local fields for this test
	 */
	protected void initVars() {
		fModelQuery = ModelQueryUtil.getModelQuery(fModel.getDocument());


	}

	/**
	 * Test the HTML BODY Element for the "bgcolor" attribute declaration
	 */
	public void testBodyElement() {
		setUpHTML();
		fModel.getStructuredDocument().set("<html><body bgcolor=\"#ffffff\"><form method=\"post\"></form></body></html>"); // set
		// text

		// TEST getting a CMElementDeclaration
		Element bodyElement = (Element) fModel.getIndexedRegion(7); // node at
		// offset7--should
		// be
		// <body>
		CMElementDeclaration bodyDecl = fModelQuery.getCMElementDeclaration(bodyElement);

		int contentType = bodyDecl.getContentType();
		assertTrue("CMElementDeclaration CONTENT TYPE INCORRECT FOR BODY", (contentType == CMElementDeclaration.MIXED));

		// get permissible attrs
		CMNamedNodeMap map = bodyDecl.getAttributes();
		assertTrue("ATTRIBUTES FROM ELEMENT DECLARATION == NULL", (map != null));
		Vector allowed = new Vector();
		for (int i = 0; i < map.getLength(); i++) {
			CMAttributeDeclaration node = (CMAttributeDeclaration) map.item(i);
			String name = node.getNodeName();
			allowed.add(name);
			if (name.equalsIgnoreCase("bgcolor")) {
				assertTrue("GOT INCORRECT ATTRIBUTE NODE TYPE", (node.getNodeType() == CMNode.ATTRIBUTE_DECLARATION));

				CMDataType attrType = node.getAttrType();
				// System.out.println("attribute type > " + attrType);
				assertTrue("COULD NOT GET ATTRIBUTE TYPE", (attrType != null));
				assertTrue("COULDN'T GET IMPLIED VALUE KIND", (attrType.getImpliedValueKind() == CMDataType.IMPLIED_VALUE_NONE));
			}
		}
	}

	/**
	 * Test the HTML HTML Element for its declared children
	 */
	public void testHtmlChildren() {
		setUpHTML();
		fModel.getStructuredDocument().set("<html></html>"); // set text
		Element htmlElement = fModel.getDocument().getDocumentElement();
		CMElementDeclaration htmlDecl = fModelQuery.getCMElementDeclaration(htmlElement);

		// HTML's children are within a group
		CMContent contents = htmlDecl.getContent();

		assertTrue("content type is not a group", contents.getNodeType() == CMNode.GROUP);

		CMGroup group = (CMGroup) contents;
		int operator = group.getOperator();
		CMNodeList childList = group.getChildNodes();
		int max = contents.getMaxOccur();
		int min = contents.getMinOccur();

		// the group should be allowed once, with a sequence whose first entry
		// is the declaration for HEAD
		assertTrue("occurrance of group", min == 1 && max == 1);
		assertTrue("relationship in group", operator == CMGroup.SEQUENCE);
		assertTrue("content descriptor type, position 0", contents.getNodeType() == CMNode.GROUP);
		assertTrue("child order (HEAD first)", childList.item(0).getNodeName().equalsIgnoreCase(HTML40Namespace.ElementName.HEAD));

		assertTrue("content descriptor type, position 1", childList.item(1).getNodeType() == CMNode.GROUP);
		// The second child should be a group as well, containing BODY and
		// FRAMESET with an
		// operator of CMGroup.CHOICE
		assertTrue("content descriptor type, position 1 - relationship of group", ((CMGroup) childList.item(1)).getOperator() == CMGroup.CHOICE);
	}

	public void testFormMethodAttr() {
		setUpHTML();
		fModel.getStructuredDocument().set("<html><body bgcolor=\"#ffffff\"><form method=\"post\"></form></body></html>"); // set
		// text

		// TEST GETTING A CMAttributeDeclaratoin
		Element formElement = (Element) fModel.getIndexedRegion(31); // <form>
		Attr attrNode = formElement.getAttributeNode(HTML40Namespace.ATTR_NAME_METHOD);
		CMAttributeDeclaration methodAttrDecl = fModelQuery.getCMAttributeDeclaration(attrNode);
		assertTrue("Content Model missing the attribute declaration for \"method\"", methodAttrDecl != null);

		List values = getValidStrings(formElement, methodAttrDecl);
		assertTrue("wrong number of values for attribute \"method\"", (values.size() == 2));
		assertTrue("default value for attribute \"method\" is wrong", (methodAttrDecl.getAttrType().getImpliedValue().equalsIgnoreCase("get")));
	}

	/**
	 * A short test to ensure that a DTD can be loaded from a system
	 * reference.
	 * 
	 * Note: May require a functioning network connection for the references
	 * to be resolved properly.
	 * 
	 * @throws IOException
	 */
	public void testDTDLoadFromSystemID_1() throws IOException {
		if (testShippedDTDLookup) {
			setUpXML();
			URL installationPath = Platform.getBundle(JSPUITestsPlugin.ID).getEntry("/");
			String diskLocation = null;
			diskLocation = FileLocator.resolve(installationPath).toExternalForm();

			assertTrue("failed to resolve plugin install path", diskLocation != null);
			String content = "<?xml version=\"1.0\"?><!DOCTYPE html SYSTEM " + diskLocation + "TestFiles/DTDs/wapDTDs/WAP-2-0/wml20.dtd\"" + "><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:wml=\"http://www.wapforum.org/2001/wml\"></html>";
			fModel.getStructuredDocument().set(content);
			CMDocumentManager documentManagaer = fModelQuery.getCMDocumentManager();
			documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, false);
			documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, true);

			Node domNode = (Node) fModel.getIndexedRegion(content.length() - 2);
			CMNode node = fModelQuery.getCMNode(domNode);
			assertTrue("wml20.dtd failed to load", node != null && node.getNodeName().equalsIgnoreCase("html"));
		}
	}


	/**
	 * A short test to ensure that a DTD, the XHTML 1.0 Transitional one, can
	 * be loaded from a system reference.
	 * 
	 * Note: May require a functioning network connection for the references
	 * to be resolved properly.
	 * @throws IOException 
	 */
	public void testDTDLoadFromSystemID_2() throws IOException {
		if (testShippedDTDLookup) {
			URL installationPath = Platform.getBundle(JSPUITestsPlugin.ID).getEntry("/");
			String diskLocation = null;

			diskLocation = FileLocator.resolve(installationPath).toExternalForm();

			assertTrue("failed to resolve plugin install path", diskLocation != null);
			setUpXML();
			String content = "<?xml version=\"1.0\"?><!DOCTYPE html SYSTEM " + diskLocation + "testfiles/XHTML/xhtml1-transitional.dtd\"" + "><html></html>";
			fModel.getStructuredDocument().set(content);

			CMDocumentManager documentManagaer = fModelQuery.getCMDocumentManager();
			documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, false);
			documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, true);

			// see defect 282429
			CMElementDeclaration htmlDecl = (CMElementDeclaration) fModelQuery.getCMNode((Node) fModel.getIndexedRegion(content.length() - 2));
			assertTrue("xhtml1-transitional.dtd not loaded", htmlDecl != null);

			// HTML's children are within a group
			CMContent contents = htmlDecl.getContent();

			assertTrue("content type is not a group", contents.getNodeType() == CMNode.GROUP);

			CMGroup group = (CMGroup) contents;
			int operator = group.getOperator();
			CMNodeList childList = group.getChildNodes();
			int max = contents.getMaxOccur();
			int min = contents.getMinOccur();

			// the group should be allowed once, with a sequence whose first
			// entry
			// is the declaration for HEAD
			assertTrue("occurrance of group", min == 1 && max == 1);
			assertTrue("relationship in group", operator == CMGroup.SEQUENCE);
			assertTrue("content descriptor type, position 0", contents.getNodeType() == CMNode.GROUP);
			assertTrue("child order (HEAD first)", childList.item(0).getNodeName().equals(HTML40Namespace.ElementName.HEAD.toLowerCase()));
			assertTrue("child order (BODY second)", childList.item(1).getNodeName().equals(HTML40Namespace.ElementName.BODY.toLowerCase()));
		}
	}

	/**
	 * A short test to ensure that the DTD for JSP 1.2 tag libraries can be
	 * loaded from a public reference registered in the XML catalog
	 */
	public void testDTDLoadFromPublicID() {
		// No longer provided in WTP
		// TODO: provide DTD?
		// if (testShippedDTDLookup) {
		// setUpXML();
		// String contents = "<!DOCTYPE taglib PUBLIC \"-//Sun Microsystems,
		// Inc.//DTD JSP Tag Library 1.2//EN\">
		// <taglib><tag>foo</tag></taglib>";
		// fModel.getStructuredDocument().set(contents);
		//
		// CMDocumentManager documentManagaer =
		// fModelQuery.getCMDocumentManager();
		// documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD,
		// false);
		// documentManagaer.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD,
		// true);
		//
		//
		// // taglib
		// CMNode node = fModelQuery.getCMNode((Node)
		// fModel.getIndexedRegion(contents.length() - 2));
		// assertTrue("web-jsptaglibrary_1_2.dtd failed to load through
		// catalog", node != null && node.getNodeType() ==
		// CMNode.ELEMENT_DECLARATION &&
		// node.getNodeName().equalsIgnoreCase("taglib"));
		//
		// // tag
		// node = fModelQuery.getCMNode((Node)
		// fModel.getIndexedRegion(contents.length() - 12));
		// assertTrue("CMElementDeclaration for \"tag\" from
		// web-jsptaglibrary_1_2.dtd is missing", node != null &&
		// node.getNodeType() == CMNode.ELEMENT_DECLARATION &&
		// node.getNodeName().equalsIgnoreCase("tag"));
		// CMContent content = ((CMElementDeclaration) node).getContent();
		// assertTrue("only one occurrence of child group allowed",
		// content.getNodeType() == CMNode.GROUP && content.getMaxOccur() ==
		// 1);
		// }
	}



	public static IStructuredModel createModel(String contentTypeID) {
		// create an empty model with its default factories
		IStructuredModel model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(contentTypeID);

		// add editor adapter factories
		AdapterFactoryRegistry adapterRegistry = JSPUIPlugin.getDefault().getAdapterFactoryRegistry();
		Iterator adapterList = adapterRegistry.getAdapterFactories();
		// And all those appropriate for this particular type of content
		while (adapterList.hasNext()) {
			try {
				AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterList.next();
				if (provider.isFor(model.getModelHandler())) {
					provider.addAdapterFactories(model);
				}
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
		return model;
	}

	/**
	 * Return the valid values for an attribute with the given declaration on
	 * the given element. Derived from XMLPropertySource
	 */
	private List getValidStrings(Element element, CMAttributeDeclaration attrDecl) {
		CMDataType valuesHelper = attrDecl.getAttrType();
		Vector values = new Vector();

		if (valuesHelper.getImpliedValueKind() == CMDataType.IMPLIED_VALUE_FIXED && valuesHelper.getImpliedValue() != null) {
			// FIXED value
			values.add(valuesHelper.getImpliedValue());
		}
		else {
			// ENUMERATED values
			String[] valueStrings = null;
			// new way
			valueStrings = fModelQuery.getPossibleDataTypeValues(element, attrDecl);
			if (valueStrings == null)
				// older way
				valueStrings = attrDecl.getAttrType().getEnumeratedValues();
			if (valueStrings != null) {
				for (int i = 0; i < valueStrings.length; i++) {
					values.add(valueStrings[i]);
				}
			}
		}
		if (valuesHelper.getImpliedValueKind() != CMDataType.IMPLIED_VALUE_NONE && valuesHelper.getImpliedValue() != null) {
			if (!values.contains(valuesHelper.getImpliedValue()))
				values.add(valuesHelper.getImpliedValue());
		}

		return values;
	}

}
