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
import org.eclipse.core.runtime.Path;
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
	public Include(Stylesheet stylesheet, int type)
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
	 * @return
	 */
	public String getHref() {
		return getAttributeValue("href");
	}
	
	public void accept(IIncludeVisitor visitor)
	{
		boolean carryOn = visitor.visit(this);
		if (carryOn)
		{
			Stylesheet sf = findIncludedStylesheet();
			if (sf != null)
			{
				for (Include include : sf.getIncludes())
				{
					include.accept(visitor);
				}
			}
		}
	}

	/**
	 * Gets the included file as a source file, if possible
	 * 
	 * @return the included stylesheet, or null if none exists
	 */
	public Stylesheet findIncludedStylesheet()
	{
		// TODO this depends on the project settings and URIResolver
		String href = getHref();
		if (href == null)
			return null;
		IFile includedFile = stylesheet.getFile().getProject().getFile(new Path(href));
		return XSLCore.getInstance().getStylesheet(includedFile);
	} 
}
