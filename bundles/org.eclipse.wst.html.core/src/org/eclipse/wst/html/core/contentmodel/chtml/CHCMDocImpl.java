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
package org.eclipse.wst.html.core.contentmodel.chtml;



import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.CMNamespace;
import org.eclipse.wst.common.contentmodel.CMNode;
import org.eclipse.wst.html.core.contentmodel.HTMLCMDocument;
import org.eclipse.wst.html.core.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.contentmodel.HTMLEntityDeclaration;

/**
 * CMDocument implementation for the HTML.
 */
public class CHCMDocImpl extends CMNodeImpl implements HTMLCMDocument {

	/** Namespace for all names of elements, entities and attributes. */
	private CMNamespace namespace = null;
	private ElementCollection elements = null;
	private EntityCollection entities = null;
	private AttributeCollection attributes = null;

	/**
	 */
	public CHCMDocImpl(String docTypeName, CMNamespace targetNamespace) {
		super(docTypeName);
		namespace = targetNamespace;
		attributes = new AttributeCollection();
		elements = new ElementCollection(attributes);
		entities = new EntityCollection();
	}

	/**
	 * @return com.ibm.sed.contentmodel.html.AttributeCollection
	 */
	AttributeCollection getAttributes() {
		return attributes;
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
		return entities;
	}

	/**
	 * @see com.ibm.sed.contentmodel.html.HTMLCMDocument
	 */
	public HTMLEntityDeclaration getEntityDeclaration(String entityName) {
		if (entities == null)
			return null;
		return (HTMLEntityDeclaration) entities.getNamedItem(entityName);
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