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
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * this thing parsers xml artifacts and picks out the specified components attributes
 * 
 */
public class XMLComponentFinder {
    public static final int ENCLOSING_PROJECT_SCOPE = 0;
    public static final int ENTIRE_WORKSPACE_SCOPE = 1;

    protected IFile currentIFile;
	protected List validExtensions;			// List of extensions as String objects
	protected List excludeFiles;			// List of files (full path) as String objects

	public XMLComponentFinder() {
		validExtensions = new ArrayList();
		excludeFiles = new ArrayList();
	}
    
    /*
     * Takes in the IFile we are currently editing.
     * The currentIFile must be set before the getEnclosingProjectFiles()
     * method will return correctly.
     */
    public void setFile(IFile file) {
        currentIFile = file;
    }
    
    public void setValidExtensions(List newExtensions) {
        validExtensions.clear();
        validExtensions.addAll(newExtensions);
    }

    public void addExcludeFiles(List newExclude) {
        excludeFiles.addAll(newExclude);
    }

    /*
     * Returns a List of absolute file locations. For example
     * "D:\files\....\file.xsd"
     */
    protected List getEnclosingProjectFiles() {
        List files = new ArrayList();
        
        if (currentIFile != null) {
            IProject project = currentIFile.getProject();
            try {
                traverseIContainer(project, validExtensions, excludeFiles, files);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return files;
    }

    protected List getWorkspaceFiles() {
        List files = new ArrayList();
        IWorkspaceRoot iwr = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = iwr.getProjects();

        try {
            for (int index = 0; index < projects.length; index++) {
                traverseIContainer(projects[index], validExtensions, excludeFiles, files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * Returns a List of absolute file locations. For example
     * "D:\files\....\file.xsd"
     */
    protected void traverseIContainer(IContainer container, List extensions, List excludeFiles, List list) throws Exception {
        IResource[] children = container.members();

        for (int index = 0; index < children.length; index++) {
            if (children[index] instanceof IFolder) {
                traverseIContainer((IFolder) children[index], extensions, excludeFiles, list);
            } else if (children[index] instanceof IFile) {
                IFile file = (IFile) children[index];
                String fileName = file.getLocation().toOSString();
                String ext = file.getFileExtension();
                if (extensions.contains(ext) && !excludeFiles.contains(fileName)) {
                    list.add(file.getLocation());
                }
            }
        }
    }
}
