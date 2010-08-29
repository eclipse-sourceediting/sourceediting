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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

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
import org.osgi.framework.Bundle;

/* 
 * Made abstract, so won't be automatically picked up as test (since intended to be subclassed).
 */
public abstract class AbstractXSLUITest extends TestCase {
	protected static IProject fTestProject;
	protected static boolean fTestProjectInitialized;
	protected static final String PROJECT_FILES = "projectfiles";
	protected static final String TEST_PROJECT_NAME = "xsltestfiles";

	@Override
	protected void setUp() throws Exception {
		getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
		setupTestProjectFiles(XSLUITestsPlugin.PLUGIN_ID);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	protected void setupTestProjectFiles(String bundleId) throws CoreException,
			IOException, URISyntaxException {
		getAndCreateProject();

		Bundle coreBundle = Platform.getBundle(bundleId);
		Enumeration<String> e = coreBundle.getEntryPaths("/projectfiles");
		while (e.hasMoreElements()) {
			String path = e.nextElement();
			URL url = coreBundle.getEntry(path);
			if (!url.getFile().endsWith("/")) {
				String relativePath = path;
				url = FileLocator.resolve(url);
				path = path.substring("projectfiles".length());
				IFile destFile = fTestProject.getFile(path);
				if (url.toExternalForm().startsWith("jar:file")) {
					InputStream source = FileLocator.openStream(coreBundle,
							new Path(relativePath), false);
					if (destFile.exists()) {
						destFile.delete(true, new NullProgressMonitor());
					}
					destFile.create(source, true, new NullProgressMonitor());
				} else {
					// if resource is not compressed, link
					destFile.createLink(url.toURI(), IResource.REPLACE,
							new NullProgressMonitor());
				}
			}
		}
	}

	protected static void getAndCreateProject() throws CoreException {
		IWorkspace workspace = getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
	}

	private static void createProject(IProject project, IPath locationPath,
			IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc = project.getWorkspace()
						.newProjectDescription(project.getName());
				if (Platform.getLocation().equals(locationPath)) {
					locationPath = null;
				}
				desc.setLocation(locationPath);
				project.create(desc, monitor);
				monitor = null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor = null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	protected void tearDown() throws Exception {
		String projName = TEST_PROJECT_NAME;

		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projName);
		if (project.isAccessible()) {
			project.delete(true, true, new NullProgressMonitor());
		}
		getWorkspace().getRoot().refreshLocal(2, new NullProgressMonitor());
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

}
