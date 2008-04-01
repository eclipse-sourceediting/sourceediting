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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.IIncludeVisitor;

/**
 * @author Doug Satchwell
 *
 */
public class Include extends XSLElement
{
	/**
	 * TODO: Add JavaDoc
	 */
	public static final int INCLUDE = 1;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static final int IMPORT = 2;
	private final int type;
	
	/**
	 * TODO: Add JavaDoc
	 * @param stylesheet
	 * @param href
	 * @param type
	 */
	public Include(Stylesheet stylesheet)
	{
		this(stylesheet,INCLUDE);
	}

	protected Include(Stylesheet stylesheet, int type)
	{
		super(stylesheet);
		this.type = type;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public int getIncludeType()
	{
		return type;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getHref() {
		return getAttributeValue("href"); //$NON-NLS-1$
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param visitor
	 */
	public void accept(IIncludeVisitor visitor)
	{
		IFile file = getHrefAsFile();
		if (file != null && file.exists())
		{
			Stylesheet stylesheet = StylesheetBuilder.getInstance().getStylesheet(file, false);
			for (Include include : stylesheet.getIncludes())
			{
				include.accept(visitor);
			}
			for (Import include : stylesheet.getImports())
			{
				include.accept(visitor);
			}
		}
		visitor.visit(this);
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
}
