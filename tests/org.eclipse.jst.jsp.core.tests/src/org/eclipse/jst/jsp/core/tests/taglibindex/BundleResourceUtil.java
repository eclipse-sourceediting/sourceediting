/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.taglibindex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.eclipse.jst.jsp.core.tests.JSPCoreTestsPlugin;

public class BundleResourceUtil {

	public static void _copyBundleEntriesIntoWorkspace(final String rootEntry, final String fullTargetPath) throws CoreException {
		Enumeration entries = JSPCoreTestsPlugin.getDefault().getBundle().getEntryPaths(rootEntry);
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

	public static IFile _copyBundleEntryIntoWorkspace(String entryname, String fullPath) throws CoreException {
		IFile file = null;
		URL entry = JSPCoreTestsPlugin.getDefault().getBundle().getEntry(entryname);
		if (entry != null) {
			IPath path = new Path(fullPath);
			// for (int j = 1; j <= path.segmentCount() - 2; j++) {
			// IPath folderPath = path.removeLastSegments(path.segmentCount()
			// - j);
			// IFolder folder =
			// ResourcesPlugin.getWorkspace().getRoot().getFolder(folderPath);
			// if (!folder.exists()) {
			// folder.create(true, true, null);
			// }
			// }
			try {
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
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.err.println("can't find " + entryname);
		}
		return file;
	}

	public static void copyBundleEntriesIntoWorkspace(final String rootEntry, final String fullTargetPath) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				_copyBundleEntriesIntoWorkspace(rootEntry, fullTargetPath);
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
	}

	public static IFile copyBundleEntryIntoWorkspace(final String entryname, final String fullPath) throws CoreException {
		final IFile file[] = new IFile[1];
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				file[0] = _copyBundleEntryIntoWorkspace(entryname, fullPath);
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		return file[0];
	}

	public static void copyBundleZippedEntriesIntoWorkspace(final String zipFileEntry, final IPath fullTargetPath) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IFile file = null;
				URL entry = JSPCoreTestsPlugin.getDefault().getBundle().getEntry(zipFileEntry);
				if (entry != null) {
					try {
						byte[] b = new byte[2048];
						ZipInputStream input = new ZipInputStream(entry.openStream());

						ZipEntry nextEntry = input.getNextEntry();
						while (nextEntry != null) {
							IPath path = fullTargetPath.append(nextEntry.getName());

							if (nextEntry.isDirectory()) {
								IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
								if (!folder.exists()) {
									folder.create(true, true, null);
								}
							}
							else {
								IPath folderPath = path.removeLastSegments(1);
								for (int i = folderPath.segmentCount(); i > 0; i--) {
									IPath parentFolderPath = path.removeLastSegments(i);
									if (parentFolderPath.segmentCount() > 1) {
										IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(parentFolderPath);
										if (!folder.exists()) {
											folder.create(true, true, null);
										}
									}
								}
								file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
								ByteArrayOutputStream output = new ByteArrayOutputStream();
								int i = -1;
								while ((i = input.read(b)) > -1) {
									output.write(b, 0, i);
								}
								if (!file.exists()) {
									file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
								}
								else {
									file.setContents(new ByteArrayInputStream(output.toByteArray()), true, false, new NullProgressMonitor());
								}
							}
							ResourcesPlugin.getWorkspace().checkpoint(true);
							nextEntry = input.getNextEntry();
						}
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					System.err.println("can't find " + zipFileEntry);
				}
				ResourcesPlugin.getWorkspace().checkpoint(true);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
	}

	/**
	 * Creates a simple project.
	 * 
	 * @param name -
	 *            the name of the project
	 * @param location -
	 *            the location of the project, or null if the default of
	 *            "/name" within the workspace is to be used
	 * @param natureIds -
	 *            an array of natures IDs to set on the project, null if none
	 *            should be set
	 * @return
	 */
	public static IProject createSimpleProject(String name, IPath location, String[] natureIds) {
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

	public static final String JAVA_NATURE_ID = "org.eclipse.jdt.core.javanature";

	/**
	 * Add a library entry (like a jar) to the classpath of a project. The jar
	 * must be in your poject. You can copy the jar into your workspace using
	 * copyBundleEntryIntoWorkspace(String entryname, String fullPath)
	 * 
	 * @param proj
	 *            assumed it has java nature
	 * @param pathToJar
	 *            project relative, no leading slash
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

			IClasspathEntry[] newEntries = (IClasspathEntry[]) l.toArray(new IClasspathEntry[l.size()]);
			jProj.setRawClasspath(newEntries, new NullProgressMonitor());
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
