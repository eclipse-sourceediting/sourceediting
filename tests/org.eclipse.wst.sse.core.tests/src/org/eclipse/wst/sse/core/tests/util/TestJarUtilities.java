/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.sse.core.tests.SSEModelTestsPlugin;


/**
 * Tests for org.eclipse.wst.sse.core.internal.util.JarUtilities
 */
public class TestJarUtilities extends TestCase {

	private static final String[] fExpectedNames = new String[]{".project", "about.html", "build.properties", "plugin.properties", "test.xml"};
	private static final String TEST_JAR_UTILITIES_PROJECT = "TestJarUtilities";
	private static final String TEST_JAR_UTILITIES_TESTFILE_JAR = "testfile.jar";
	private static final String TESTFILE_BUNDLE_ENTRY = "resources/testfile.jar";

	static IFile _copyBundleEntryIntoWorkspace(String entryname, String fullPath) throws Exception {
		IFile file = null;
		URL entry = SSEModelTestsPlugin.getDefault().getBundle().getEntry(entryname);
		if (entry != null) {
			IPath path = new Path(fullPath);
			byte[] b = new byte[2048];
			InputStream input = entry.openStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int i = -1;
			while ((i = input.read(b)) > -1) {
				output.write(b, 0, i);
			}
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file != null) {
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
				}
				else {
					file.setContents(new ByteArrayInputStream(output.toByteArray()), true, false, new NullProgressMonitor());
				}
			}
		}
		else {
			System.err.println("can't find " + entryname);
		}
		return file;
	}

	static IFile copyBundleEntryIntoWorkspace(final String entryname, final String fullPath) throws Exception {
		final IFile file[] = new IFile[1];
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					file[0] = _copyBundleEntryIntoWorkspace(entryname, fullPath);
				}
				catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, SSEModelTestsPlugin.getDefault().getBundle().getSymbolicName(), 0, null, e));
				}
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		return file[0];
	}

	/**
	 * Creates a simple project.
	 * 
	 * @param name
	 *            - the name of the project
	 * @return
	 */
	static IProject createSimpleProject(String name) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}

	public TestJarUtilities() {
		super("JarUtilities Tests");
	}

	private String readContents(InputStream is) {
		String charset = "utf8";

		StringBuffer s = new StringBuffer();
		Reader reader = null;
		try {
			reader = new InputStreamReader(is, charset);
			char[] readBuffer = new char[2048];
			int n = reader.read(readBuffer);
			while (n > 0) {
				s.append(readBuffer, 0, n);
				n = reader.read(readBuffer);
			}

		}
		catch (Exception e) {
		}
		finally {
			try {
				if (is != null) {
					is.close();
				}
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				// nothing to do
			}
		}
		return s.toString();
	}

	protected void setUp() throws Exception {
		super.setUp();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
		if (!project.isAccessible()) {
			project = createSimpleProject(getName());
			copyBundleEntryIntoWorkspace(TESTFILE_BUNDLE_ENTRY, getJarFile().getFullPath().toString());
		}
	}

	private IFile getJarFile() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
		return project.getFile(TEST_JAR_UTILITIES_TESTFILE_JAR);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
		project.delete(true, null);
		super.tearDown();
	}
	
	public void testEntryCountFromFilesystem() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile().getLocation().toString());
		assertEquals("incorrect number of entries", 5, entryNames.length);
	}

	public void testEntryCountFromMissingFile() {
		String[] names = JarUtilities.getEntryNames(TEST_JAR_UTILITIES_PROJECT + "/nonexistent.jar");
		assertNotNull("null names", names);
		assertEquals("entries returned for missing file", 0, names.length);
	}


	public void testEntryCountFromWorkspace() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile());
		assertEquals("incorrect number of entries", 5, entryNames.length);
	}

	public void testEntryCountWithDirectoriesFromFilesystem() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile().getLocation().toString(), false);
		assertEquals("incorrect number of entries", 6, entryNames.length);
	}

	public void testEntryNamesFromFilesystem() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile().getLocation().toString());
		Collection names = new ArrayList(Arrays.asList(entryNames));

		for (int i = 0; i < fExpectedNames.length; i++) {
			assertTrue("missing name of entry: " + fExpectedNames[i], names.contains(fExpectedNames[i]));
		}
	}

	public void testEntryNamesFromWorkspace() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile());
		Collection names = new ArrayList(Arrays.asList(entryNames));

		for (int i = 0; i < fExpectedNames.length; i++) {
			assertTrue("missing name of entry: " + fExpectedNames[i], names.contains(fExpectedNames[i]));
		}
	}

	public void testEntryNamesWithDirectoriesFromFilesystem() {
		String[] entryNames = JarUtilities.getEntryNames(getJarFile().getLocation().toString(), false);
		Collection names = new ArrayList(Arrays.asList(entryNames));

		String[] expectedNames = new String[]{".project", "about.html", "build.properties", "plugin.properties", "test.xml"};
		for (int i = 0; i < expectedNames.length; i++) {
			assertTrue("missing name of entry: " + entryNames[i], names.contains(entryNames[i]));
		}
		assertTrue("missing name of folder entry: empty/", names.contains("empty/"));
	}

	public void testReadFromFilesystem() {
		String location = getJarFile().getLocation().toString();
		String contents = readContents(JarUtilities.getInputStream(location, "plugin.properties"));
		assertNotNull("no contents loaded", contents);
		assertTrue("not enough contents read", contents.length() > 40);
		assertTrue("contents not as expected" + contents.substring(0, 40), contents.startsWith("##########################################################"));
	}



	public void testReadFromMissingFile() {
		InputStream contents = JarUtilities.getInputStream(TEST_JAR_UTILITIES_PROJECT + "/nonexistent.jar", "plugin.properties");
		assertNull("contents loaded", contents);
	}

	public void testReadFromSupportedURL() throws MalformedURLException {
		String location = getJarFile().getLocation().toString();
		String contents = readContents(JarUtilities.getInputStream(new URL("jar:file:" + location + "!/plugin.properties")));
		assertNotNull("no contents loaded", contents);
		assertTrue("not enough contents read", contents.length() > 0);
		assertTrue("contents not as expected", contents.startsWith("##########################################################"));
	}

	public void testReadFromUnsupportedURL() {
		URL entry = SSEModelTestsPlugin.getDefault().getBundle().getEntry("about.html");
		assertNotNull("null URL to about.html", entry);
		InputStream inputStream = JarUtilities.getInputStream(entry);
		assertNotNull("null input stream to "+entry, inputStream);
		String contents = readContents(inputStream);
		assertNotNull("no contents loaded", contents);
		assertTrue("not enough contents read. Contents Length was " + contents.length() + " but expected more than 750.", contents.length() > 750);
		assertTrue("contents not as expected" + contents.substring(0, 75), contents.indexOf("DOCTYPE") > 0);
	}

	public void testReadFromWorkspace() {
		IResource jar = getJarFile();
		String contents = readContents(JarUtilities.getInputStream(jar, "plugin.properties"));
		assertNotNull("no contents loaded", contents);
		assertTrue("not enough contents read", contents.length() > 40);
		assertTrue("contents not as expected" + contents.substring(0, 40), contents.startsWith("##########################################################"));
	}

}
