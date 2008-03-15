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

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;


public class XSLModel
{
	private final long lastBuildTime;
	private final IProject project;
	private final Map<IFile, SourceFile> sourceFiles;
	
	public XSLModel(IProject project, Map<IFile, SourceFile> sourceFiles)
	{
		this.lastBuildTime = System.currentTimeMillis();
		this.project = project;
		this.sourceFiles = sourceFiles;
	}
	
	public IProject getProject()
	{
		return project;
	}
	
	// TODO sort out threading issues
	public SourceFile getSourceFile(IFile file)
	{
		return sourceFiles.get(file);
	}
	
	public long getLastBuildTime()
	{
		return lastBuildTime;
	}

	// TODO sort out threading issues
	public Map<IFile, SourceFile> getSourceFiles()
	{
		return sourceFiles;
	}
}

