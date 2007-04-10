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
package org.eclipse.wst.xml.tests.encoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

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
import org.eclipse.wst.sse.core.internal.encoding.CodedReaderCreator;
import org.eclipse.wst.sse.core.internal.encoding.NonContentBasedEncodingRules;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.xml.tests.encoding.util.ProjectUnzipUtility;

/**
 * This class is intended to be executed only at development time, and it
 * creates the class to JUnit test all the files found in the testfiles
 * directory.
 */
public class CreateCodedReaderTester extends TestCase {
	//	private final String fileDir = "html/";
	//	private final String fileRoot =
	// "/builds/Workspaces/HeadWorkspace/org.eclipse.wst.xml.tests.encoding/";
	//	private final String fileLocation = fileRoot + fileDir;
	private static final String TESTFILES_ZIPFILE_NAME = "testfiles.zip"; //$NON-NLS-1$
	private static final boolean DEBUG = false;
	private static final String TEST_FILE_DIR = "testfiles"; //$NON-NLS-1$
	// needs to be static, since JUnit creates difference instances for each
	// test
	private static boolean fTestProjectInitialized;
	private static final String TEST_PROJECT_NAME = "org.eclipse.wst.xml.tests.encoding"; //$NON-NLS-1$
	static IProject fTestProject;
	private static int nSetups = 0;
	private static final String currentPlatformCharset = getPlatformDefault();
	private boolean RECREATE_FILES = false;

	/**
	 *  
	 */
	public CreateCodedReaderTester() {
		super();
		//System.out.println(currentPlatformCharset);
	}

	/**
	 * @return
	 */
	private static String getPlatformDefault() {
		String platformDefault = NonContentBasedEncodingRules.useDefaultNameRules(null);
		return platformDefault;
	}

	public static void main(String[] args) {
		//				try {
		//					new CreateCodedReaderTester().doCreateAllFiles();
		//				} catch (CoreException e) {
		//					e.printStackTrace();
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
	}

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
		//TestsPlugin testsPlugin = (TestsPlugin)
		// Platform.getPlugin("org.eclipse.wst.xml.tests.encoding");
		IWorkspace workspace = TestsPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		// this form creates project as "linked" back to 'fileRoot'
		//createProject(testProject, new Path(fileRoot), null);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
		//		IContainer testFiles = testProject.getFolder("testfiles");
		//		assertTrue(testFiles.exists());
		//		IResource[] allFolders = testFiles.members();
		//		assertNotNull(allFolders);
	}

	private void createTestMethodSource(int count, String filePathAndName, String detectedCharsetName, String javaCharsetName, String expectedException) {
		String javaCharsetNameOrKey = javaCharsetName;
		if (null != javaCharsetNameOrKey && javaCharsetNameOrKey.equals(currentPlatformCharset))
			javaCharsetNameOrKey = "expectPlatformCharset";
		System.out.println("public void testFile" + count + " () throws CoreException, IOException {" + " doTest( \"" + javaCharsetNameOrKey + "\", \"" + detectedCharsetName + "\",  \"" + filePathAndName + "\", " + expectedException + "); }");
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
			File zipFile = TestsPlugin.getTestFile("testfiles.zip");
			ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
			projUtil.unzipAndImport(zipFile, destinationFolder);
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
				//				Display display = PlatformUI.getWorkbench().getDisplay();
				//				display.asyncExec(new Runnable() {
				//					public void run() {
				//						ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
				//						IProject proj = fTestProject;
				//						fTestProject = null;
				//						try {
				//							projUtil.deleteProject(proj);
				//						} catch (Exception e) {
				//							e.printStackTrace();
				//						}
				//					}
				//				});
			}
		}
	}

	public void testCreateAllFiles() throws CoreException, IOException {
		if (RECREATE_FILES) {
			List allFiles = TestsPlugin.getAllTestFiles(TEST_FILE_DIR);
			URL outputDirURL = TestsPlugin.getInstallLocation();
			File zipoutFile = new File(outputDirURL.getPath(), TESTFILES_ZIPFILE_NAME);
			java.io.FileOutputStream zipOut = new FileOutputStream(zipoutFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(zipOut);
			int count = 1;
			for (Iterator iter = allFiles.iterator(); iter.hasNext();) {
				File file = (File) iter.next();
				createZipEntry(zipOutputStream, file);
				CodedReaderCreator codedReaderCreator = new CodedReaderCreator();
				codedReaderCreator.set(file.getName(), new FileInputStream(file));
				String detectedCharsetName = null;
				String javaCharsetName = null;
				String expectedException = null;
				try {
					// just used for debug info, but can throw exception
					javaCharsetName = codedReaderCreator.getEncodingMemento().getJavaCharsetName();
					detectedCharsetName = codedReaderCreator.getEncodingMemento().getDetectedCharsetName();
				}
				catch (UnsupportedCharsetExceptionWithDetail e) {
					// ignore for simply creating tests
					expectedException = e.getClass().getName() + ".class";
				}
				catch (MalformedInputException e) {
					// ignore for simply creating tests
					expectedException = e.getClass().getName() + ".class";
				}
				catch (IllegalCharsetNameException e) {
					// ignore for simply creating tests
					expectedException = e.getClass().getName() + ".class";
				}
				String subpath = getSubPathName(file);
				createTestMethodSource(count, subpath, detectedCharsetName, javaCharsetName, expectedException);
				count++;
			}
			zipOutputStream.close();
			zipOut.close();
			assertTrue(true);
		}
	}

	private String getSubPathName(File file) {
		String path = file.getPath();
		int lastIndex = path.lastIndexOf(TEST_FILE_DIR);
		String subpath = path.substring(lastIndex);
		subpath = subpath.replace('\\', '/');
		return subpath;
	}

	/**
	 * @param zipOutputStream
	 * @param element
	 */
	private void createZipEntry(ZipOutputStream zipOutputStream, File file) throws IOException {
		String subPathName = getSubPathName(file);
		ZipEntry zipEntry = new ZipEntry(subPathName);
		zipOutputStream.putNextEntry(zipEntry);
		InputStream inputStream = new FileInputStream(file);
		int nRead = 0;
		byte[] buffer = new byte[1024 * 8];
		while (nRead != -1) {
			nRead = inputStream.read(buffer);
			if (nRead > 0) {
				zipOutputStream.write(buffer, 0, nRead);
			}
		}
		zipOutputStream.flush();
	}
}
