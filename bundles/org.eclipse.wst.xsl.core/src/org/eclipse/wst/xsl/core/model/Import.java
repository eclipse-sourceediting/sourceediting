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
 * The <code>xsl:import</code> model element.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Import extends Include
{
	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Import(Stylesheet stylesheet)
	{
		super(stylesheet,IMPORT);
	}
}
