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
package org.eclipse.wst.xsl.internal.launching;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class XSLTSourcePathComputerDelegate implements ISourcePathComputerDelegate
{
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException
	{
		List<ISourceContainer> containers = new ArrayList<ISourceContainer>();

		String sourceFileExpr = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
		IPath sourceFile = getSubstitutedPath(sourceFileExpr);
		LaunchPipeline pipeline = hydratePipeline(configuration);

		// TODO have some way of knowing whether it is an IResource or not
		containers.add(new DirectorySourceContainer(sourceFile, false));

		for (Iterator<?> iter = pipeline.getTransformDefs().iterator(); iter.hasNext();)
		{
			LaunchTransform transform = (LaunchTransform) iter.next();
			IPath path = transform.getPath();
			ISourceContainer sourceContainer = null;
			if (transform.getPathType().equals(LaunchTransform.RESOURCE_TYPE))
			{
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
				IContainer container = resource.getParent();
				if (container.getType() == IResource.PROJECT)
				{
					sourceContainer = new ProjectSourceContainer((IProject) container, false);
				}
				else if (container.getType() == IResource.FOLDER)
				{
					sourceContainer = new FolderSourceContainer(container, false);
				}
			}
			else
			{
				sourceContainer = new DirectorySourceContainer(path, false);
			}
			containers.add(sourceContainer);
		}

		// if (sourceContainer == null)
		// {
		// sourceContainer = new WorkspaceSourceContainer();
		// }

		return containers.toArray(new ISourceContainer[0]);
	}

	private static LaunchPipeline hydratePipeline(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchPipeline pipeline = null;
		String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PIPELINE, (String) null);
		if (s != null && s.length() > 0)
		{
			ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
			pipeline = LaunchPipeline.fromXML(inputStream);
		}
		return pipeline;
	}

	private static IPath getSubstitutedPath(String path) throws CoreException
	{
		if (path != null)
		{
			path = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(path);
			return new Path(path);
		}
		return null;
	}

}
