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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.StylesheetBuilder;
import org.eclipse.wst.xsl.core.internal.model.StylesheetModel;
import org.eclipse.wst.xsl.core.internal.model.XSLAttribute;

/**
 * TODO: Add JavaDoc
 */
public class XSLCore
{
	/**
	 * The XSL namespace URI (= http://www.w3.org/1999/XSL/Transform)
	 */
	public static final String XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$
	
	private static XSLCore instance;
	private Map<IFile, StylesheetModel> stylesheetsComposed = new HashMap<IFile, StylesheetModel>();

	private XSLCore()
	{}

	/**
	 * Get the cached stylesheet, or build it if it has not yet been built.
	 * 
	 * @param file
	 * @return source file, or null if could not be built
	 */
	public synchronized StylesheetModel getStylesheet(IFile file)
	{
		StylesheetModel stylesheet = stylesheetsComposed.get(file);
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
	public synchronized StylesheetModel buildStylesheet(IFile file)
	{
		Stylesheet stylesheet = StylesheetBuilder.getInstance().getStylesheet(file, true);
		if (stylesheet == null)
			return null;
		StylesheetModel stylesheetComposed = new StylesheetModel(stylesheet);			
		stylesheetsComposed.put(file, stylesheetComposed);
		stylesheetComposed.fix();
		return stylesheetComposed;
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

	/**
	 * Locates a file for the given current file and URI.
	 * 
	 * @param currentFile
	 * @param uri
	 * @return
	 */
	public static IFile resolveFile(IFile currentFile, String uri)
	{
		if (uri == null)
			return null;
		// TODO depends on how we resolve URIs
		return currentFile.getParent().getFile(new Path(uri));
	}
	
	public static boolean isXSLFile(IFile file)
	{
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType[] types = contentTypeManager.findContentTypesFor(file.getName());
		for (IContentType contentType : types)
		{
			if (contentType.isKindOf(contentTypeManager.getContentType("org.eclipse.wst.xml.core.xslsource"))) //$NON-NLS-1$
			{
				return true;
			}
		}
		return false;
	}
}
