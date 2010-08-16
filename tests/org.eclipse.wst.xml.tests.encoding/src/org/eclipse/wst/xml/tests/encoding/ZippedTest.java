/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.tests.encoding.util.ProjectUnzipUtility;

public class ZippedTest {

	private static final String TEST_PROJECT_NAME = "org.eclipse.encoding.resource.newtests";

	private boolean isSetUp = false;
	private IProject fProject = null;

	public void setUp() throws CoreException {
		if (!isSetUp) {
			createProject();
			// unzip files to the root of workspace directory
			String destinationProjectString = fProject.getLocation().toOSString();
			String destinationFolder = destinationProjectString + "/";

			File zipFile = TestsPlugin.getTestFile("testfiles.zip");
			ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
			projUtil.unzipAndImport(zipFile, destinationFolder);
			projUtil.initJavaProject(TEST_PROJECT_NAME);
			fProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			isSetUp = true;
		}
	}

	public void shutDown() throws CoreException {
		if (isSetUp) {
			if (fProject != null && fProject.isAccessible()) {
				fProject.delete(true, new NullProgressMonitor());
			}
			isSetUp = false;
		}
	}

	public IFile getFile(String fileName) {
		IFile file = null;
		if (fProject != null) {
			file = (IFile) fProject.findMember(fileName);
		}
		return file;
	}

	protected IProject createProject() throws CoreException {
		IWorkspace workspace = TestsPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fProject = root.getProject(TEST_PROJECT_NAME);
		// this form creates project as "linked" back to 'fileRoot'
		//createProject(testProject, new Path(fileRoot), null);
		createProject(fProject, null, null);
		fProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		return fProject;
	}

	private void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
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
		}
		finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
}
