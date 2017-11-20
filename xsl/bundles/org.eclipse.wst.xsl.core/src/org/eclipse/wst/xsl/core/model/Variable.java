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
 * The <code>xsl:variable</code> model element.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Variable extends XSLElement
{
	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Variable(Stylesheet stylesheet)
	{
		super(stylesheet);
	}

	/**
	 * Get the value of the <code>name</code> attribute if one exists.
	 * 
	 * @return the variable name, or null
	 */
	@Override
	public String getName()
	{
		return getAttributeValue("name"); //$NON-NLS-1$
	}
	
	/**
	 * Get the value of the <code>select</code> attribute if one exists.
	 * 
	 * @return the select value, or null
	 */
	public String getSelect()
	{
		return getAttributeValue("select"); //$NON-NLS-1$
	}
	
	@Override
	public Type getModelType()
	{
		return Type.VARIABLE;
	}
	
	/**
	 * If an XSLT 2.0, return the value of the <code>as</code> attribute if one exists.
	 * 
	 * @return the as value, or null
	 * @since 1.1
	 */
	public String getAs() {
		return getAttributeValue("as"); //$NON-NLS-1$
	}
}
