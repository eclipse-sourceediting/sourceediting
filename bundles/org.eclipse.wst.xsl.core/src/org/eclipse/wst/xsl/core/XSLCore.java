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
package org.eclipse.wst.xsl.core;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.StylesheetBuilder;

/**
 * TODO: Add JavaDoc
 */
public class XSLCore
{
	public static final String XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Transform";
	
	private static XSLCore instance;
	private StylesheetBuilder builder;
	private Map<IFile, Stylesheet> stylesheets = new HashMap<IFile, Stylesheet>();

	private XSLCore()
	{
		try
		{
			builder = new StylesheetBuilder();
		}
		catch (XPathExpressionException e)
		{
			XSLCorePlugin.log(e);
		}
	}

	/**
	 * Get the cached stylesheet, or build it if it has not yet been built.
	 * 
	 * @param file
	 * @return source file, or null if could not be built
	 */
	public synchronized Stylesheet getStylesheet(IFile file)
	{
		Stylesheet stylesheet = stylesheets.get(file);
		if (stylesheet == null)
			stylesheet = buildStylesheet(file);
		return stylesheet;
	}

	/**
	 * Completely rebuild the source file from its DOM
	 * 
	 * @param file
	 * @return
	 */
	public synchronized Stylesheet buildStylesheet(IFile file)
	{
		Stylesheet stylesheet = builder.buildSourceFile(file);
		stylesheets.put(file, stylesheet);
		return stylesheet;
	}

	/**
	 * TODO: Add JavaDoc
	 * 
	 * @return An Instances of XSLCore
	 */
	public static synchronized XSLCore getInstance()
	{
		if (instance == null)
			instance = new XSLCore();
		return instance;
	}
}
