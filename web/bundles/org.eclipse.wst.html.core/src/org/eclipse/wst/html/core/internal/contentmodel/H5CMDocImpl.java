/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * CMDocument implementation for the HTML.
 */
class H5CMDocImpl extends CMNodeImpl implements HTMLCMDocument {

	/** Namespace for all names of elements, entities and attributes. */
	private CMNamespaceImpl namespace = null;
	private HTML5ElementCollection elements = null;
	private EntityCollection entities = null;
	private AttributeCollection attributes = null;

	/**
	 */
	public H5CMDocImpl(String docTypeName, CMNamespaceImpl targetNamespace) {
		super(docTypeName);
		namespace = targetNamespace;
		
		attributes = new HTML5AttributeCollection();
		elements = new HTML5ElementCollection(attributes);
		entities = new EntityCollection();
	}

	AttributeCollection getAttributes() {
		return attributes;
	}

	public HTMLElementDeclaration getElementDeclaration(String elementName) {
		if (elements == null)
			return null;
		return (HTMLElementDeclaration) elements.getNamedItem(elementName);
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 */
	public CMNamedNodeMap getElements() {
		return elements;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 */
	public CMNamedNodeMap getEntities() {
		return entities;
	}

	public HTMLEntityDeclaration getEntityDeclaration(String entityName) {
		if (entities == null)
			return null;
		return (HTMLEntityDeclaration) entities.getNamedItem(entityName);
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 */
	public CMNamespace getNamespace() {
		return namespace;
	}

	/**
	 * @see CMNode
	 */
	public int getNodeType() {
		return CMNode.DOCUMENT;
	}
}
