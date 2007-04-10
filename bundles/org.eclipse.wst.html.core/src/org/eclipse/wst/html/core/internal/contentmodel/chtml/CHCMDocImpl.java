/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocument;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLEntityDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

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

	AttributeCollection getAttributes() {
		return attributes;
	}

	public HTMLElementDeclaration getElementDeclaration(String elementName) {
		if (elements == null)
			return null;
		return (HTMLElementDeclaration) elements.getNamedItem(elementName);
	}

	public CMNamedNodeMap getElements() {
		return elements;
	}

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
	public org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace getNamespace() {
		return namespace;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	public int getNodeType() {
		return CMNode.DOCUMENT;
	}
}
