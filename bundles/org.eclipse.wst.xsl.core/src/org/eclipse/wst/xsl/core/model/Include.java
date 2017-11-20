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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.XSLCore;

/**
 * The <code>xsl:include</code> model element.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Include extends XSLElement
{
	/**
	 * Constant indicating that this is an <code>Include</code>.
	 */
	public static final int INCLUDE = 1;
	
	/**
	 * Constant indicating that this is an <code>Import</code>.
	 */
	public static final int IMPORT = 2;
	
	private final int type;
	
	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Include(Stylesheet stylesheet)
	{
		this(stylesheet,INCLUDE);
	}

	/**
	 * Create a new instance of this, specifying whether an <code>Include</code> or an <code>Import</code>.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 * @param type one of the constants <code>INCLUDE</code> or <code>IMPORT</code>
	 */
	protected Include(Stylesheet stylesheet, int type)
	{
		super(stylesheet);
		this.type = type;
	}
	
	/**
	 * Get the type of include, whether an <code>Include</code> or an <code>Import</code>.
	 * 
	 * @return one of the constants <code>INCLUDE</code> or <code>IMPORT</code>
	 */
	public int getIncludeType()
	{
		return type;
	}
	
	/**
	 * Get the value of the <code>href</code> attribute if one exists.
	 * 
	 * @return the <code>href</code> value, or null
	 */
	public String getHref() {
		return getAttributeValue("href"); //$NON-NLS-1$
	}
	

	/**
	 * Gets the included file as a source file, if possible (returned file may be null and need not exist).
	 * 
	 * @return the included file, or null
	 */
	public IFile getHrefAsFile()
	{
		return XSLCore.resolveFile(getStylesheet().getFile(), getHref());
	}

	@Override
	public Type getModelType()
	{
		return Type.INCLUDE;
	} 
}
