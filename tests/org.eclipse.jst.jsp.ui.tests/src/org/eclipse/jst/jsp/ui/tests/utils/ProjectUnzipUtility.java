/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * Imports zipped files into the test workspace.
 * Deletes all projects in workspace.
 * 
 * @author pavery
 */
public class ProjectUnzipUtility {

	class MyOverwriteQuery implements IOverwriteQuery {
		public String queryOverwrite(String pathString) {
			return ALL;
		}
	}

	public final static String PROJECT_ZIPS_FOLDER = "ProjectTestFiles";
	private List fCreatedProjects = null;

	public ProjectUnzipUtility() {
		// for deletion later
		fCreatedProjects = new ArrayList();
	}

	/**
	 * 
	 * @param fileToImport the file you wish to import
	 * @param folderPath the container path within the workspace
	 */
	public void importFile(File fileToImport, String folderPath) {
		WorkspaceProgressMonitor importProgress = new WorkspaceProgressMonitor();
		try {
			if (fileToImport.exists()) {
				IPath containerPath = new Path(folderPath);
				fCreatedProjects.add(folderPath);

				IImportStructureProvider provider = FileSystemStructureProvider.INSTANCE;
				IOverwriteQuery overwriteImplementor = new MyOverwriteQuery();
				File[] filesToImport = {fileToImport};

				ImportOperation importOp = new ImportOperation(containerPath, null, provider, overwriteImplementor, Arrays.asList(filesToImport));
				importOp.setCreateContainerStructure(false);
				importOp.setOverwriteResources(true);
				importOp.run(importProgress);
			}
			else {
				System.out.println("handle source doesn't exist");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			importProgress.done();
		}

	}

	/**
	 * the following is logic from
	 * http://www.devshed.com/c/a/Java/Zip-Meets-Java/2/
	 */
	// specify buffer size for extraction
	static final int BUFFER = 2048;

	public void unzipAndImport(File inFile, Location destinationDirectoryLocation) {

		try {
			// Specify file to decompress
			String inFileName = inFile.getAbsolutePath(); //"c:/example.zip";

			// Specify destination where file will be unzipped
			//String destinationDirectory = "d:/eclipsedev/M5_SSE_TESTS_WORKSPACE/"; //"c:/temp/";

			File sourceZipFile = new File(inFileName);

			URL destinationDirectoryURL = destinationDirectoryLocation.getURL();
			String destinationDirectory = destinationDirectoryURL.getPath();
			File unzipDestinationDirectory = new File(destinationDirectory);

			// Open Zip file for reading
			ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

			// Create an enumeration of the entries in the zip file
			Enumeration zipFileEntries = zipFile.entries();
			String projectFolderName = null;
			IProject currentProject = null;

			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

				String currentEntry = entry.getName();
				//System.out.println("Extracting: " + entry);

				File destFile = new File(unzipDestinationDirectory, currentEntry);

				// grab file's parent directory structure
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();


				// extract file if not a directory
				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];

					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();

					if (projectFolderName != null)
						importFile(destFile, projectFolderName);

				}
				else {
					// need handle to the main project folder to create containerPath 
					if (projectFolderName == null) {
						projectFolderName = destFile.getName();
						fCreatedProjects.add(projectFolderName);

						currentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectFolderName);
					}
				}
			}
			zipFile.close();

			// fixes workspace metadata for the project
			// for clean startup next run
			if (currentProject != null) {
				try {
					Path projectLocation = new Path(Platform.getLocation().toOSString());
					createProject(currentProject, projectLocation, new WorkspaceProgressMonitor());
				}
				catch (CoreException cex) {
					cex.printStackTrace();
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void refreshWorkspace() throws CoreException {
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wsRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		wsRoot.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	/**
	 * Delete projects created (unzipped and imported) by this utility
	 * @throws Exception
	 */
	public void deleteProjects() throws Exception {
		final IProject[] projects = getCreatedProjects();
		WorkspaceModifyOperation deleteOp = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < projects.length; i++) {
					projects[i].clearHistory(monitor);
					projects[i].close(monitor);
					try {
						projects[i].delete(true, true, monitor);
					}
					catch (CoreException e) {
						// exception deleting?  don't care since it's not the focus of any test case
					}
				}
				refreshWorkspace();
			}
		};

		WorkspaceProgressMonitor progress = new WorkspaceProgressMonitor();
		deleteOp.run(progress);

		// saves the new workspace metadata
		ResourcesPlugin.getWorkspace().save(true, null);
	}

	public void deleteProject(String projectName) throws Exception {
		final String name = projectName;
		IWorkspaceRunnable deleteOp = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
				IProject proj = wsRoot.getProject(name);
				if (proj != null && proj.exists()) {
					proj.clearHistory(monitor);
					proj.close(monitor);
					try {
						proj.delete(true, true, monitor);
					}
					catch (CoreException e) {
						// exception deleting?  don't care since it's not the focus of any test case
					}
					proj = null;
				}
				refreshWorkspace();
			}
		};

		WorkspaceProgressMonitor progress = new WorkspaceProgressMonitor();
		ResourcesPlugin.getWorkspace().run(deleteOp, null, IWorkspace.AVOID_UPDATE, progress);

		// saves the new workspace metadata
		ResourcesPlugin.getWorkspace().save(true, null);
	}

	/**
	 * 
	 * @return IProjects that were unzipped and imported into the workspace by this utility
	 */
	public IProject[] getCreatedProjects() {
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

		String[] projectNames = (String[]) fCreatedProjects.toArray(new String[fCreatedProjects.size()]);
		IProject[] projects = new IProject[projectNames.length];
		for (int i = 0; i < projectNames.length; i++) {
			projects[i] = wsRoot.getProject(projectNames[i]);
		}
		return projects;
	}

	public void initJavaProject(String projName) throws CoreException {
		// resynch
		refreshWorkspace();

		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);

		// need to add java nature, or else project won't "exist()" in the java element sense
		String[] natureIds = {JavaCore.NATURE_ID};
		IProjectDescription desc = proj.getDescription();
		desc.setNatureIds(natureIds);
		proj.setDescription(desc, null);
	}

	private void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new WorkspaceProgressMonitor();
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