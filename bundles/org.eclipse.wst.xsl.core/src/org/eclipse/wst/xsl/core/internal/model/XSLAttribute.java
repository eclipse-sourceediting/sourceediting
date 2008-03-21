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

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class XSLAttribute extends XSLNode
{
	final String name;
	final String value;

	/**
	 * TODO: Add Javadoc
	 * @param element
	 * @param name
	 * @param value
	 */
	public XSLAttribute(XSLElement element, String name, String value)
	{
		super(element.getStylesheet(), XSLNode.ATTRIBUTE_NODE);
		this.name = name;
		this.value = value;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getValue()
	{
		return value;
	}
}
