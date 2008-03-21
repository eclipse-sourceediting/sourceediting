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

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public abstract class XSLElement extends XSLNode
{
	Map<String, XSLAttribute> attributes = new HashMap<String, XSLAttribute>();
	
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
}
