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
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentFinder;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLQuickScan;

public class XSDComponentFinder extends XMLComponentFinder {
    public static final int ENCLOSING_PROJECT_SCOPE = 0;
    public static final int ENTIRE_WORKSPACE_SCOPE = 1;    
    
    private List extensions;
    private List excludeFiles;
    
    public XSDComponentFinder() {
        extensions = new ArrayList();
        excludeFiles = new ArrayList();
        
        extensions.add("xsd");
    }
    
    public void addValidExtensions(List newExtensions) {
        extensions.addAll(newExtensions);
    }

    public void addExcludeFiles(List newExclude) {
        excludeFiles.addAll(newExclude);
    }
    
    public List getWorkbenchResourceComponents(int scope) {
        List components = new ArrayList();
        List filePaths = new ArrayList();
        
        // We don't want to search through the current file we're working on.
        if (currentIFile != null) {
            excludeFiles.add(currentIFile.getLocation().toOSString());
        }
        
        // Find files matching the search criteria specified in List extensions and
        // List excludeFiles.
        switch (scope) {
        case ENCLOSING_PROJECT_SCOPE:
            filePaths = getEnclosingProjectFiles();
            break;
            
        case ENTIRE_WORKSPACE_SCOPE:
            filePaths = getWorkspaceFiles();
            break;
            
        default:
            
            break;
        }
        
        // Search for the components in each of the files specified in the path.
        List paths = new ArrayList();
        paths.add("/schema/complexType");
        paths.add("/schema/simpleType");
        
        List attributes = new ArrayList();
        String[] nameAttr = new String[1];
        String[] nameAttr2 = new String[1];
        nameAttr[0] = "name";
        nameAttr2[0] = "name";
        attributes.add(nameAttr);
        attributes.add(nameAttr2);

        Iterator pathsIterator = filePaths.iterator();
        while (pathsIterator.hasNext()) {
//           String stringPath = ((Path) pathsIterator.next()).toOSString();
           String stringPath = ((Path) pathsIterator.next()).toString();
           components.addAll(XMLQuickScan.getTagInfo(stringPath, paths, attributes));
        }
        
        return components;
    }
    
    /*
     * Returns a List of absolute file locations. For example
     * "D:\files\....\file.xsd"
     */
    private List getEnclosingProjectFiles() {
        List files = new ArrayList();
        
        if (currentIFile != null) {
            IWorkspaceRoot iwr = ResourcesPlugin.getWorkspace().getRoot();
            IProject project = currentIFile.getProject();
            try {
                traverseIContainer(project, extensions, excludeFiles, files);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return files;
    }

    private List getWorkspaceFiles() {
        List files = new ArrayList();
        IWorkspaceRoot iwr = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = iwr.getProjects();

        try {
            for (int index = 0; index < projects.length; index++) {
                traverseIContainer(projects[index], extensions, excludeFiles, files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }
}
