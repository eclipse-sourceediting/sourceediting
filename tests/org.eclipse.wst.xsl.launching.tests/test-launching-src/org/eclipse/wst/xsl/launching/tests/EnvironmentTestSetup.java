/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gary Karasiuk (IBM Corporation) - initial implementation
 *     Jesper Steen Møller - adapted for XSL
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

public class EnvironmentTestSetup {
	
	public static final boolean DEBUG = true;
	
	private IWorkspace	_workspace;
	private HashMap<String, IProject> _projects = new HashMap<String, IProject>(20);
	
	public EnvironmentTestSetup() throws CoreException {
		_workspace = ResourcesPlugin.getWorkspace();
		if (DEBUG){
			_workspace.getRoot().delete(true, true, null);
		}
	}
	
	public IPath addFolder(IPath root, String folderName) throws CoreException {
		IPath path = root.append(folderName);
		createFolder(path);
		return path;
	}
	
	public void incrementalBuild() throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	public void fullBuild() throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
	}
	
	public void waitForBuild(){
		boolean wasInterrupted = false;
		do {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, null);
				wasInterrupted = false;
			}
			catch (InterruptedException e){
				wasInterrupted = true;
			}
		} while(wasInterrupted);
	}

	private IFolder createFolder(IPath path) throws CoreException {
		if (path.segmentCount() <= 1)return null;
		
		IFolder folder = _workspace.getRoot().getFolder(path);
		if (!folder.exists()){
			folder.create(true, true, null);
		}
		return folder;
	}

	public IProject createProject(String name) throws CoreException {
		final IProject project = _workspace.getRoot().getProject(name);
		IWorkspaceRunnable create = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(monitor);
				project.open(monitor);	
			}		
		};
		
		_workspace.run(create, null);
		_projects.put(name, project);
		
		return project;
	}

	public void dispose() throws CoreException {
		if (DEBUG)return;
		for (Iterator<IProject> it=_projects.values().iterator(); it.hasNext();){
			IProject project = it.next();
			project.delete(true, null);
		}
	}

	public IPath addFile(IPath folder, String fileName, String contents) throws CoreException, UnsupportedEncodingException {
		IPath filePath = folder.append(fileName);
		createFile(filePath, contents.getBytes("UTF8"));
		return filePath;
	}
	
	public IPath addFileFromResource(IPath folder, String fileName, String path) throws CoreException {
		IPath filePath = folder.append(fileName);
		createFileFromResource(filePath, path);
		return filePath;
	}
	
	private IFile createFile(IPath filePath, byte[] contents) throws CoreException {
		IFile file = _workspace.getRoot().getFile(filePath);
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		if (file.exists())file.setContents(in, true, false, null);
		else file.create(in, true, null);
		return file;
	}

	private IFile createFileFromResource(IPath filePath, String path) throws CoreException {
		IFile file = _workspace.getRoot().getFile(filePath);
		InputStream in = EnvironmentTestSetup.class.getResourceAsStream(path);
		if (file.exists())file.setContents(in, true, false, null);
		else file.create(in, true, null);
		return file;
	}
	public IWorkspace getWorkspace(){
		return _workspace;
	}

}
