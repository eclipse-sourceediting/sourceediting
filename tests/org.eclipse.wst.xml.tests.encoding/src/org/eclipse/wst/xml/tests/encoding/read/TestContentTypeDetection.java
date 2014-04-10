/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.read;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

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
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.xml.tests.encoding.TestsPlugin;
import org.eclipse.wst.xml.tests.encoding.util.ProjectUnzipUtility;


public class TestContentTypeDetection extends TestCase {
	private static final boolean DEBUG = false;
	static IProject fTestProject;
	// needs to be static, since JUnit creates difference instances for each
	// test
	private static boolean fTestProjectInitialized;
	private static int nSetups = 0;
	private static final String TEST_PROJECT_NAME = "org.eclipse.wst.xml.temp.tests.encoding.resource.newtests";

	private static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
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

	private static void getAndCreateProject() throws CoreException {
		IWorkspace workspace = TestsPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		// this form creates project as "linked" back to 'fileRoot'
		// createProject(testProject, new Path(fileRoot), null);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
	}

	/**
	 * 
	 */
	public TestContentTypeDetection() {
		super();
		// System.out.println(currentPlatformCharset);
	}

	protected void doTest(String expectedContentType, String filePath, Class expectedException) throws CoreException, IOException {
		IFile file = (IFile) fTestProject.findMember(filePath);
		assertNotNull("Error in test case: file not found: " + filePath, file);


		IContentDescription streamContentDescription = doGetContentTypeBasedOnStream(file);
		IContentDescription fileContentDescription = doGetContentTypeBasedOnFile(file);

		IContentType fileContentType = fileContentDescription.getContentType();
		assertNotNull("file content type was null", fileContentType);

		IContentType streamContentType = streamContentDescription.getContentType();
		assertNotNull("stream content type was null", streamContentType);

		assertEquals("comparing content type based on file and stream: ", fileContentType, streamContentType);

		// if equal, above, as expected, then only need to check one.
		assertEquals("compareing with expected content type id", expectedContentType, fileContentType.getId());

	}

	protected IContentDescription doGetContentTypeBasedOnStream(IFile file) throws CoreException, IOException {
		IContentDescription streamContentDescription = null;
		InputStream inputStream = null;
		try {
			inputStream = file.getContents();
			streamContentDescription = Platform.getContentTypeManager().getDescriptionFor(inputStream, file.getName(), IContentDescription.ALL);
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		assertNotNull("content description was null", streamContentDescription);
		return streamContentDescription;
	}

	protected IContentDescription doGetContentTypeBasedOnFile(IFile file) throws CoreException {
		IContentDescription fileContentDescription = file.getContentDescription();
		assertNotNull("file content description was null", fileContentDescription);
		return fileContentDescription;
	}

	protected void doTestForParent(String expectedContentType, String filePath, Class expectedException) throws CoreException, IOException {
		IFile file = (IFile) fTestProject.findMember(filePath);
		assertNotNull("Error in test case: file not found: " + filePath, file);

		IContentDescription contentDescription = file.getContentDescription();
		if (contentDescription == null) {
			InputStream inputStream = null;
			try {
				inputStream = file.getContents();
				contentDescription = Platform.getContentTypeManager().getDescriptionFor(inputStream, file.getName(), IContentDescription.ALL);
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		assertNotNull("content description was null", contentDescription);
		IContentType contentType = contentDescription.getContentType();
		assertNotNull("content type was null", contentType);

		IContentType parentContentType = contentType;
		boolean found = false;
		while (parentContentType != null && !found) {
			found = expectedContentType.equals(parentContentType.getId());
			parentContentType = parentContentType.getBaseType();
		}
		assertTrue("did not find '" + expectedContentType + "' in parent chain of base types", found);

	}

	protected void setUp() throws Exception {
		super.setUp();
		nSetups++;
		if (!fTestProjectInitialized) {
			getAndCreateProject();
			// unzip files to the root of workspace directory
			String destinationProjectString = fTestProject.getLocation().toOSString();
			String destinationFolder = destinationProjectString + "/";
			// this zip file is sitting in the "root" of test plugin
			ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
			projUtil.unzipAndImport(TestsPlugin.getTestResource("testfiles.zip"), destinationFolder);
			projUtil.initJavaProject(TEST_PROJECT_NAME);
			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		nSetups--;
		if (nSetups == 0) {
			if (!DEBUG) {
//				 Display display = PlatformUI.getWorkbench().getDisplay();
//				display.asyncExec(new Runnable() {
//					public void run() {
//						ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
//						IProject proj = fTestProject;
//						fTestProject = null;
//						try {
//							projUtil.deleteProject(proj);
//						}
//						catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
			}
		}
	}

	protected static IProject getTestProject() {
		return fTestProject;
	}
}
