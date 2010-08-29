/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 262046 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.launching.tests;

import java.io.*;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchShortcutExtension;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public abstract class AbstractLaunchingTest extends TestCase {
	private static final String XSL_TEST_PROJECT = "XSLTestProject";
	protected static final String XSL_LAUNCH_SHORTCUT_ID = "org.eclipse.wst.xsl.debug.ui.launchshortcut";
	protected static final String LAUNCHCONFIGS = "launchConfigs";
	protected EnvironmentTestSetup env;
	protected IProject testProject;
	protected IFolder folder;

	public AbstractLaunchingTest() {
		super();
	}

	public AbstractLaunchingTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		createEmptyLaunchConfigsFolder();
		deleteExistingLaunchConfigs();
	}

	private void createProject() throws CoreException {
		env = new EnvironmentTestSetup();
		testProject = env.createProject(XSL_TEST_PROJECT);
	}
	
	private void createEmptyLaunchConfigsFolder() throws CoreException {
		IPath path = testProject.getFullPath();
		folder = testProject.getFolder(LAUNCHCONFIGS);
		if (folder.exists()) {
			folder.delete(true, null);
		}
		folder.create(true, true, null);
	}
	
	private void deleteExistingLaunchConfigs() throws CoreException {
		ILaunchConfiguration[] configs = getLaunchManager()
				.getLaunchConfigurations();
		for (int i = 0; i < configs.length; i++) {
			configs[i].delete();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		testProject.delete(true, new NullProgressMonitor());
	}

	protected void copyConfigurationToWorkspace(IPath folder, String filename)
			throws Exception {
		URL url = Activator.getDefault().getBundle().getEntry(
				"testFiles" + File.separator + filename);

		String workspacePath = getWorkspacePath();

		File target = new File(workspacePath + folder.toPortableString()
				+ File.separator + filename);
		copyFile(url, target);
	}

	private String getWorkspacePath() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath rootPath = root.getLocation();
		String workspacePath = rootPath.toPortableString();
		return workspacePath;
	}

	private void copyFile(URL src, File target) throws Exception {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(target));
		BufferedInputStream bis = new BufferedInputStream(src.openStream());
		try {
			while (bis.available() > 0) {
				int size = bis.available();
				if (size > 1024)
					size = 1024;
				byte[] b = new byte[size];
				bis.read(b, 0, b.length);
				bos.write(b);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	/**
	 * Returns the launch shortcut with the given id
	 * 
	 * @param id
	 * @return the <code>LaunchShortcutExtension</code> with the given id, or
	 *         <code>null</code> if none
	 * 
	 * @since 1.0
	 */
	protected LaunchShortcutExtension getLaunchShortcutExtension(String id) {
		List exts = getLaunchConfigurationManager().getLaunchShortcuts();
		LaunchShortcutExtension ext = null;
		for (int i = 0; i < exts.size(); i++) {
			ext = (LaunchShortcutExtension) exts.get(i);
			if (ext.getId().equals(id)) {
				return ext;
			}
		}
		return null;
	}

	/**
	 * Returns the singleton instance of the
	 * <code>LaunchConfigurationManager</code>
	 * 
	 * @return the singleton instance of the
	 *         <code>LaunchConfigurationManager</code>
	 * @since 1.0
	 */
	protected LaunchConfigurationManager getLaunchConfigurationManager() {
		return DebugUIPlugin.getDefault().getLaunchConfigurationManager();
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunch launch(String name) throws Exception {
		ILaunchConfiguration configuration = getLaunchConfiguration(name);
		ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE,
				new NullProgressMonitor());
		return launch;
	}

	/**
	 * Returns the launch configuration for the given main type
	 * 
	 * @param mainTypeName
	 *            program to launch
	 * @see ProjectCreationDecorator
	 */
	protected ILaunchConfiguration getLaunchConfiguration(String mainTypeName) throws Exception {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		IFile file = testProject.getProject().getFolder("launchConfigs")
				.getFile(mainTypeName + ".launch");
		ILaunchConfiguration mine = mgr.getLaunchConfiguration(file);
		assertEquals("Wrong type found: ",
				XSLLaunchConfigurationConstants.ID_LAUNCH_CONFIG_TYPE, mine
						.getType().getIdentifier());
	
		return mine;
	}

	protected void refreshProject() throws CoreException,
			InterruptedException {
				testProject.refreshLocal(IResource.DEPTH_INFINITE,
						new NullProgressMonitor());
				while (testProject.isSynchronized(IResource.DEPTH_INFINITE) == false) {
					Thread.sleep(1000);
				}
			}

	protected void launchConfiguration(String launchConfigName) throws Exception {
		ILaunch launch = launch(launchConfigName);
		// Wait until the launch configuration terminates.
		while (launch.isTerminated() == false) {
			Thread.sleep(1000);
		}
		refreshProject();
	}

	protected String readFile(InputStream input) {
		String str;
		String finalString = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			while ((str = in.readLine()) != null) {
				finalString = finalString + str + "\n";
			}
			in.close();
		} catch (IOException e) {
		}
		return finalString;
	}

	protected Document parseXml(InputStream contents) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
		return builder.parse(contents);
	}

}
