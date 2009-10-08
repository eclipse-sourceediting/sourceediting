/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An element in the XSL namespace.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLElement extends XSLNode {
	final Map<String, XSLAttribute> attributes = new HashMap<String, XSLAttribute>();
	final List<XSLElement> childElements = new ArrayList<XSLElement>();

	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet
	 *            the stylesheet that this belongs to
	 */
	public XSLElement(Stylesheet stylesheet) {
		super(stylesheet, XSLNode.ELEMENT_NODE);
	}

	/**
	 * Add an attribute of this
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void setAttribute(XSLAttribute attribute) {
		attributes.put(attribute.name, attribute);
	}

	/**
	 * Get the attribute with the given name.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the attribute
	 */
	public XSLAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Get the attributes keyed by their names.
	 * 
	 * @return the map of attribute names and instances
	 */
	public Map<String, XSLAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * Get the value of the attribute with the given name.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the attribute value
	 */
	public String getAttributeValue(String name) {
		XSLAttribute attribute = attributes.get(name);
		return attribute == null ? null : attribute.getValue();
	}

	/**
	 * Add a child element of this.
	 * 
	 * @param element
	 *            the chold element
	 */
	public void addChild(XSLElement element) {
		childElements.add(element);
	}

	/**
	 * Get the list of child elements
	 * 
	 * @return the list of children
	 */
	public List<XSLElement> getChildElements() {
		return childElements;
	}

	@Override
	public Type getModelType() {
		return Type.OTHER_ELEMENT;
	}
}
