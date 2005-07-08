/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.misc;

import org.eclipse.wst.html.core.tests.parser.ModelTest;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HTMLTagInfoTest extends ModelTest {

	public HTMLTagInfoTest(String name) {
		super(name);
	}

	public HTMLTagInfoTest() {
		super();
	}

	public static void main(java.lang.String[] args) {
		new HTMLTagInfoTest().testModel();
	}

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			Document document = model.getDocument();

			Element html = document.createElement("HTML"); //$NON-NLS-1$
			document.appendChild(html);
			checkElementTagInfo(html);

			Element body = document.createElement("BODY"); //$NON-NLS-1$
			html.appendChild(body);
			checkElementTagInfo(body);

			Attr onclick = document.createAttribute("onclick"); //$NON-NLS-1$
			body.setAttributeNode(onclick);
			checkAttributeTagInfo(body, onclick);
		}
		finally {
			model.releaseFromEdit();
		}
	}

	private void checkElementTagInfo(Element node) {
		// check taginfo
		CMElementDeclaration elementDecl = getCMElementDeclaration(node);
		assertNotNull("Cannot check taginfo because no cm element declaration for " + node.getNodeName(), elementDecl); //$NON-NLS-1$
		if (elementDecl != null) {
			String tagInfo = (String) elementDecl.getProperty("tagInfo"); //$NON-NLS-1$
			assertNotNull("No taginfo found for " + elementDecl.getNodeName(), tagInfo); //$NON-NLS-1$
		}
	}

	private void checkAttributeTagInfo(Element element, Attr attribute) {
		// check taginfo
		CMElementDeclaration elementDecl = getCMElementDeclaration(element);
		assertNotNull("Cannot check taginfo because no element declaration for " + element.getNodeName(), elementDecl); //$NON-NLS-1$
		if (elementDecl != null) {
			CMAttributeDeclaration attDecl = getCMAttributeDeclaration(elementDecl, attribute.getName());
			assertNotNull("Cannot check taginfo because no attribute declaration for " + attribute.getName(), attDecl); //$NON-NLS-1$

			String tagInfo = (String) attDecl.getProperty("tagInfo"); //$NON-NLS-1$
			assertNull("Unexpected taginfo found for " + attDecl.getNodeName(), tagInfo); //$NON-NLS-1$
		}
	}

	/**
	 * Retreives CMAttributeDeclaration indicated by attribute name within
	 * elementDecl
	 */
	private CMAttributeDeclaration getCMAttributeDeclaration(CMElementDeclaration elementDecl, String attName) {
		CMAttributeDeclaration attrDecl = null;

		if (elementDecl != null) {
			CMNamedNodeMap attributes = elementDecl.getAttributes();
			String noprefixName = DOMNamespaceHelper.getUnprefixedName(attName);
			if (attributes != null) {
				attrDecl = (CMAttributeDeclaration) attributes.getNamedItem(noprefixName);
				if (attrDecl == null) {
					attrDecl = (CMAttributeDeclaration) attributes.getNamedItem(attName);
				}
			}
		}
		return attrDecl;
	}

	/**
	 * Retreives CMElementDeclaration for given node
	 * 
	 * @return CMElementDeclaration - CMElementDeclaration of node or
	 *         <code>null</code> if not possible
	 */
	private CMElementDeclaration getCMElementDeclaration(Element element) {
		CMElementDeclaration result = null;

		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
		if (modelQuery != null)
			result = modelQuery.getCMElementDeclaration(element);
		return result;
	}
}
