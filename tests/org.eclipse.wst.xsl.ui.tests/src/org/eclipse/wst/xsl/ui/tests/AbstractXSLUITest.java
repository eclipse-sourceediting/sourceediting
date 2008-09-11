/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;

/* 
 * Made abstract, so won't be automatically picked up as test (since intended to be subclassed).
 */
public abstract class AbstractXSLUITest extends TestCase
{
	protected static IProject fTestProject;
	private static boolean fTestProjectInitialized;
	private static final String PROJECT_FILES = "projectfiles";
	private static final String TEST_PROJECT_NAME = "xsltestfiles";

	protected void setUp() throws Exception
	{
		super.setUp();
		if (!fTestProjectInitialized)
		{
			getAndCreateProject();

			File srcDir = XSLModelXMLTestsPlugin.getTestFile("/" + PROJECT_FILES);
			String destinationProjectString = fTestProject.getLocation().toOSString();
			String destinationFolder = destinationProjectString + "/";
			File targetDir = new File(destinationProjectString);
			copyDir(srcDir, targetDir);

			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}

	private static void getAndCreateProject() throws CoreException
	{
		IWorkspace workspace = getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
	}

	private static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try
		{
			if (!project.exists())
			{
				IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
				if (Platform.getLocation().equals(locationPath))
				{
					locationPath = null;
				}
				desc.setLocation(locationPath);
				project.create(desc, monitor);
				monitor = null;
			}
			if (!project.isOpen())
			{
				project.open(monitor);
				monitor = null;
			}
		}
		finally
		{
			if (monitor != null)
			{
				monitor.done();
			}
		}
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	private static void copyDir(File src, File target) throws Exception
	{
		if (!target.exists())
			target.mkdir();
		File[] files = src.listFiles();
		for (File file : files)
		{
			File toFile = new File(target, file.getName());
			if (file.isDirectory())
				copyDir(file, toFile);
			else
				copyFile(file, toFile);
		}
	}

	private static void copyFile(File src, File target) throws Exception
	{
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try
		{
			bos = new BufferedOutputStream(new FileOutputStream(target));
			bis = new BufferedInputStream(new FileInputStream(src));
			while (bis.available() > 0)
			{
				int size = bis.available();
				if (size > 1024)
					size = 1024;
				byte[] b = new byte[size];
				bis.read(b, 0, b.length);
				bos.write(b);
			}
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (bis != null)
			{
				try
				{
					bis.close();
				}
				catch (IOException e)
				{
					throw e;
				}
			}
			if (bos != null)
			{
				try
				{
					bos.close();
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}
	}
}
