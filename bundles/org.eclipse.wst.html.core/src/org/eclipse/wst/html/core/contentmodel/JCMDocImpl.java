/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.contentmodel;



import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.CMNode;

/**
 * Implementation of CMDocument for the JSP 1.1.
 */
class JCMDocImpl extends CMNodeImpl implements JSPCMDocument {

	/** Namespace for all names of elements, entities and attributes. */
	private CMNamespaceImpl namespace = null;
	private JSPElementCollection elements = null;

	/**
	 * HCMDocImpl constructor comment.
	 */
	public JCMDocImpl(String docTypeName, CMNamespaceImpl targetNamespace) {
		super(docTypeName);
		namespace = targetNamespace;
		elements = new JSPElementCollection();
	}

	/**
	 * @see com.ibm.sed.contentmodel.html.HTMLCMDocument
	 */
	public HTMLElementDeclaration getElementDeclaration(String elementName) {
		if (elements == null)
			return null;
		return (HTMLElementDeclaration) elements.getNamedItem(elementName);
	}

	/**
	 * @see org.eclipse.wst.common.contentmodel.CMDocument
	 */
	public CMNamedNodeMap getElements() {
		return elements;
	}

	/**
	 * @see org.eclipse.wst.common.contentmodel.CMDocument
	 */
	public CMNamedNodeMap getEntities() {
		return null;
	}

	/**
	 * @see com.ibm.sed.contentmodel.html.HTMLCMDocument
	 */
	public HTMLEntityDeclaration getEntityDeclaration(String entityName) {
		return null;
	}

	/**
	 * @see org.eclipse.wst.common.contentmodel.CMDocument
	 */
	public org.eclipse.wst.common.contentmodel.CMNamespace getNamespace() {
		return namespace;
	}

	/**
	 * @see org.eclipse.wst.common.contentmodel.CMNode
	 */
	public int getNodeType() {
		return CMNode.DOCUMENT;
	}
}