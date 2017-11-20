/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

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
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.ui.tests.JSPUITestsPlugin;

/**
 * Some utilities for creating projects, and copying files into the workspace.
 */
public class ProjectUtil {

	public static final String JAVA_NATURE_ID = "org.eclipse.jdt.core.javanature";
	
	/**
	 * Add a library entry (like a jar) to the classpath of a project.
	 * The jar must be in your poject. You can copy the jar into your workspace using
	 * copyBundleEntryIntoWorkspace(String entryname, String fullPath)
	 * 
	 * @param proj assumed it has java nature
	 * @param pathToJar project relative, no leading slash
	 */
	public static void addLibraryEntry(IProject proj, String pathToJar) {
		
		IPath projLocation = proj.getLocation();
		IPath absJarPath = projLocation.append(pathToJar);
		
		IJavaProject jProj = JavaCore.create(proj);
		
		IClasspathEntry strutsJarEntry = JavaCore.newLibraryEntry(absJarPath, null, null);
		try {
			IClasspathEntry[] currentEntries = jProj.getRawClasspath();
			
			List l = new ArrayList();
			l.addAll(Arrays.asList(currentEntries));
			l.add(strutsJarEntry);
			
			IClasspathEntry[] newEntries = (IClasspathEntry[])l.toArray(new IClasspathEntry[l.size()]);
			jProj.setRawClasspath(newEntries, new NullProgressMonitor());
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}	
	}

	static void _copyBundleEntriesIntoWorkspace(final String rootEntry, final String fullTargetPath) throws CoreException {
		Enumeration entries = JSPUITestsPlugin.getDefault().getBundle().getEntryPaths(rootEntry);
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
		URL entry = JSPUITestsPlugin.getDefault().getBundle().getEntry(entryname);
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
	 * @param entryname path relative to TEST plugin starting w/ a "/"
	 * 						(eg. "/testfiles/bugnumber/struts-logic.tld")
	 * @param fullPath path relative to junit test workpace
	 * 						(eg. "/myruntimeproj/struts-logic.tld")
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
	
	public static  IProject createProject(String name, IPath location, String[] natureIds) {
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
