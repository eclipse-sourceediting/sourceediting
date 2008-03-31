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
package org.eclipse.wst.xsl.core.internal.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class XSLElement extends XSLNode
{
	final Map<String, XSLAttribute> attributes = new HashMap<String, XSLAttribute>();
	final List<XSLElement> childElements = new ArrayList<XSLElement>();
	
	/**
	 * TODO: Add Javadoc
	 * @param stylesheet
	 */
	public XSLElement(Stylesheet stylesheet)
	{
		super(stylesheet, XSLNode.ELEMENT_NODE);
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param attribute
	 */
	public void setAttribute(XSLAttribute attribute)
	{
		attributes.put(attribute.name, attribute);
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param name
	 * @return
	 */
	public XSLAttribute getAttribute(String name)
	{
		return attributes.get(name);
	}

	public Map<String, XSLAttribute> getAttributes()
	{
		return attributes;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param name
	 * @return
	 */
	public String getAttributeValue(String name)
	{
		XSLAttribute attribute = attributes.get(name);
		return attribute == null ? null : attribute.getValue();
	}

	public void addChild(XSLElement element)
	{
		childElements.add(element);
	}
	
	public List<XSLElement> getChildElements()
	{
		return childElements;
	}
}
