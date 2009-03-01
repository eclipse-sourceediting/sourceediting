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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchShortcutExtension;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;

import junit.framework.TestCase;

public abstract class AbstractLaunchingTest extends TestCase {
	protected String XSL_LAUNCH_SHORTCUT_ID = "org.eclipse.wst.xsl.debug.ui.launchshortcut"; 
	protected String XSL_LAUNCH_CONFIG_TYPEID = "org.eclipse.wst.xsl.launching.launchConfigurationType";
	protected String LAUNCHCONFIGS = "launchConfigs";
	protected TestEnvironment env;
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
		env = new TestEnvironment();
		testProject = env.createProject("XSLTestProject");
		IPath path = testProject.getFullPath();
		folder = testProject.getFolder("launchConfigs");
        if (folder.exists()) {
            folder.delete(true, null);
        }
        folder.create(true, true, null);
		
        // delete any existing launch configs
        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations();
        for (int i = 0; i < configs.length; i++) {
            configs[i].delete();
        }

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		testProject.delete(true, new NullProgressMonitor());
	}

	protected void addLaunchConfiguration(IPath folder, String filename)
			throws Exception {
		copyConfigurationToWorkspace(folder, filename);
	}


	private void copyConfigurationToWorkspace(IPath folder, String filename)
			throws Exception {
		URL url = Activator.getDefault().getBundle().getEntry("testFiles" + File.separator + filename);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath rootPath = root.getLocation();
		String workspacePath = rootPath.toPortableString();
		
		File target = new File(workspacePath + folder.toPortableString() + File.separator + filename);
		copyFile(url, target);
	}
	
	private static void copyFile(URL src, File target) throws Exception
	{
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try
		{
			bos = new BufferedOutputStream(new FileOutputStream(target));
			bis = new BufferedInputStream(src.openStream());
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
	
	/**
	 * Returns the launch shortcut with the given id
	 * @param id
	 * @return the <code>LaunchShortcutExtension</code> with the given id, 
	 * or <code>null</code> if none
	 * 
	 * @since 1.0
	 */
	protected LaunchShortcutExtension getLaunchShortcutExtension(String id) {
		List exts = getLaunchConfigurationManager().getLaunchShortcuts();
		LaunchShortcutExtension ext = null;
		for (int i = 0; i < exts.size(); i++) {
			ext = (LaunchShortcutExtension) exts.get(i);
			if(ext.getId().equals(id)) {
				return ext;
			}
		}
		return null;
	}
	
	/**
	 * Returns the singleton instance of the <code>LaunchConfigurationManager</code>
	 * 
	 * @return the singleton instance of the <code>LaunchConfigurationManager</code>
	 * @since 1.0
	 */
	protected LaunchConfigurationManager getLaunchConfigurationManager() {
		return DebugUIPlugin.getDefault().getLaunchConfigurationManager();
	}
	
	private ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}
    
}
