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
import org.eclipse.wst.xsl.core.internal.model.SourceFile;
import org.eclipse.wst.xsl.core.internal.model.SourceFileBuilder;

/**
 * TODO: Add JavaDoc
 */
public class XSLCore
{
	private static XSLCore instance;
	private SourceFileBuilder builder;
	private Map<IFile, SourceFile> sourceFiles = new HashMap<IFile, SourceFile>();

	private XSLCore()
	{
		try
		{
			builder = new SourceFileBuilder();
		}
		catch (XPathExpressionException e)
		{
			XSLCorePlugin.log(e);
		}
	}

	/**
	 * Get the cached sourceFile, or build it if it has not yet been built.
	 * 
	 * @param file
	 * @return source file, or null if could not be built
	 */
	public synchronized SourceFile getSourceFile(IFile file)
	{
		SourceFile sourceFile = sourceFiles.get(file);
		if (sourceFile == null)
			sourceFile = buildSourceFile(file);
		return sourceFile;
	}

	/**
	 * Completely rebuild the source file from its DOM
	 * 
	 * @param file
	 * @return
	 */
	public synchronized SourceFile buildSourceFile(IFile file)
	{
		SourceFile sourceFile = builder.buildSourceFile(file);
		sourceFiles.put(file, sourceFile);
		return sourceFile;
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
