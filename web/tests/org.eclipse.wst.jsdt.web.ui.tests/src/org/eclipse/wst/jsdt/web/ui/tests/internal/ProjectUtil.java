/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.web.ui.tests.Activator;

/**
 * Some utilities for creating projects, and copying files into the workspace.
 * Copied from JSP UI tests.
 */
public class ProjectUtil {

	static void _copyBundleEntriesIntoWorkspace(final String rootEntry, final String fullTargetPath) throws CoreException {
		Enumeration entries = Activator.getDefault().getBundle().getEntryPaths(rootEntry);
		while (entries != null && entries.hasMoreElements()) {
			String entryPath = entries.nextElement().toString();
			String targetPath = new Path(fullTargetPath + "/" + entryPath.substring(rootEntry.length())).toString();
			if (entryPath.endsWith("/")) {
				IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(targetPath));
				if (!folder.exists()) {
					folder.create(true, true, new NullProgressMonitor());
				}
				_copyBundleEntriesIntoWorkspace(entryPath, targetPath);
			}
			else {
				_copyBundleEntryIntoWorkspace(entryPath, targetPath);
			}
			// System.out.println(entryPath + " -> " + targetPath);
		}
	}

	static IFile _copyBundleEntryIntoWorkspace(String entryname, String fullPath) throws CoreException {
		IFile file = null;
		URL entry = Activator.getDefault().getBundle().getEntry(entryname);
		if (entry != null) {
			try {
				byte[] b = new byte[2048];
				InputStream input = entry.openStream();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				int i = -1;
				while ((i = input.read(b)) > -1) {
					output.write(b, 0, i);
				}
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fullPath));
				if (file != null) {
					if (!file.exists()) {
						file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
					}
					else {
						file.setContents(new ByteArrayInputStream(output.toByteArray()), true, false, new NullProgressMonitor());
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * @param rootEntry - avoid trailing separators
	 * @param fullTargetPath
	 */
	public static void copyBundleEntriesIntoWorkspace(final String rootEntry, final String fullTargetPath) {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				_copyBundleEntriesIntoWorkspace(rootEntry, fullTargetPath);
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param entryname
	 *            path relative to TEST plugin starting w/ a "/" (eg.
	 *            "/testfiles/bugnumber/struts-logic.tld")
	 * @param fullPath
	 *            path relative to junit test workpace (eg.
	 *            "/myruntimeproj/struts-logic.tld")
	 * @return
	 */
	public static IFile copyBundleEntryIntoWorkspace(final String entryname, final String fullPath) {
		final IFile file[] = new IFile[1];
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				file[0] = _copyBundleEntryIntoWorkspace(entryname, fullPath);
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return file[0];
	}

	public static IProject createProject(String name, IPath location, String[] natureIds) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		if (location != null) {
			description.setLocation(location);
		}
		if (natureIds != null) {
			description.setNatureIds(natureIds);
		}
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
}
