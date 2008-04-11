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

package org.eclipse.wst.xsl.internal.core.tests;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.StylesheetModel;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationReport;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;
import org.eclipse.wst.xsl.core.tests.XSLCoreTestsPlugin;

/*
 * Made abstract, so won't be automatically picked up as test (since intended to be subclassed).
 */
public abstract class AbstractValidationTest extends TestCase
{
	protected static IProject fTestProject;
	private static boolean fTestProjectInitialized;
	private static final String TEST_PROJECT_NAME = "testproject";

	protected void setUp() throws Exception
	{
		super.setUp();
		if (!fTestProjectInitialized)
		{
			getAndCreateProject();

			// URL installLocation = Platform.getBundle(XSLCoreTestsPlugin.PLUGIN_ID).getEntry("/");
			Enumeration<String> e = Platform.getBundle(XSLCoreTestsPlugin.PLUGIN_ID).getEntryPaths("/projectfiles");// (path, filePattern, recurse)("/projectfiles", null, true);
			while (e.hasMoreElements())
			{
				String path = e.nextElement();
				URL url = Platform.getBundle(XSLCoreTestsPlugin.PLUGIN_ID).getEntry(path);
				url = FileLocator.resolve(url);
				path = path.substring("projectfiles".length());
				IFile destFile = fTestProject.getFile(path);
				System.out.println(destFile.getLocation()+" --> "+url.toExternalForm());
				destFile.createLink(url.toURI(), IResource.REPLACE, new NullProgressMonitor());
			}
			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}
	
	
	protected IFile getFile(String path)
	{
		return fTestProject.getFile(new Path(path));
	}
	
	protected XSLValidationReport validate(IFile file, Set<Integer> expectedErrors, Set<Integer> expectedWarnings) throws CoreException
	{
		XSLValidationReport report = new XSLValidationReport(file.getLocationURI().toString());
		XSLValidator.getInstance().validate(file,report);
		StylesheetModel model = XSLCore.getInstance().getStylesheet(file);		
		assertFalse("Stylesheet model is null",model == null);
		// Simply ensure that expected errors + warnings exist at a given line number
		validateErrors(model,report.getErrors(),new HashSet<Integer>(expectedErrors));
		validateErrors(model,report.getWarnings(),new HashSet<Integer>(expectedWarnings));
		return report;
	}
	
	private void validateErrors(StylesheetModel model, List<XSLValidationMessage> errors, Set<Integer> expectedErrors)
	{
		for (XSLValidationMessage error : errors)
		{
			assertTrue("Error report must be for the current stylesheet only", error.getNode().getStylesheet() == model.getStylesheet());
			assertTrue("Unxpected error at line "+error.getLineNumber()+": "+error,expectedErrors.remove(error.getLineNumber()));
		}
		for (Integer integer : expectedErrors)
		{
			assertTrue("Expected error at line "+integer, false);
		}
	}

	private static final boolean DEBUG = false;

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
}
