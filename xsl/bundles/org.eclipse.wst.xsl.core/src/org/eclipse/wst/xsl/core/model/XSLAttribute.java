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

/**
 * An attribute of an element in the XSL namespace.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLAttribute extends XSLNode
{
	final String name;
	final String value;

	/**
	 * Create a new instance of this.
	 * 
	 * @param element the element this belongs to
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public XSLAttribute(XSLElement element, String name, String value)
	{
		super(element.getStylesheet(), XSLNode.ATTRIBUTE_NODE);
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Get the name of this.
	 * 
	 * @return the attribute's name
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get the value of this.
	 * 
	 * @return the attribute's value
	 */
	public String getValue()
	{
		return value;
	}
	
	@Override
	public Type getModelType()
	{
		return Type.ATTRIBUTE;
	}
}
