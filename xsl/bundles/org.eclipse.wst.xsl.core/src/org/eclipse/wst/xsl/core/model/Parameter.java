/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 246500 - Add Paramters to global variables type.
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

/**
 * The <code>xsl:parameter</code> or <code>xsl:with-param</code> model element.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Parameter extends Variable
{
	private boolean valueSet;

	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Parameter(Stylesheet stylesheet)
	{
		super(stylesheet);
	}
	
	/**
	 * Set whether this has a value.
	 * 
	 * @param valueSet <code>true</code> if this has a value
	 */
	public void setValue(boolean valueSet)
	{
		this.valueSet = valueSet;
	}

	/**
	 * Get whether this has a value.
	 * 
	 * @return <code>true</code> if this has a value
	 */
	public boolean isValue()
	{
		if (valueSet)
			return true;
		if (getAttributeValue("select") != null) //$NON-NLS-1$
			return true;
		return false; 
	}
	
	@Override
	public Type getModelType() {
		return Type.PARAM;
	}
	
	/**
	 * @since 1.1
	 */
	@Override
	public String getAs() {
		return getAttributeValue("as"); //$NON-NLS-1$
	}
}
