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

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

/**
 * this thing parsers xml artifacts and picks out the components names
 * 
 */
public class XMLComponentFinder {	
    protected IFile currentIFile;
//	protected List validExtensions;			// List of extensions as String objects
//	protected List excludeFiles;			// List of files (full path) as String objects

	
	/**
	 * 
	 */
	public XMLComponentFinder() {
//		validExtensions = new ArrayList();
//		excludeFiles = new ArrayList();
	}
    
    /*
     * Takes in the IFile we are currently editing.
     * The currentIFile must be set before the getEnclosingProjectFiles()
     * method will return correctly.
     */
    public void setFile(IFile file) {
        currentIFile = file;
    }
    
/*    
     private String getNormalizedLocation(String location) {
        try {
            URL url = new URL(location);
            URL resolvedURL = Platform.resolve(url);
            location = resolvedURL.getPath();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
  */   

     
// /////////////////////////////////////////////////////////////////////////////////
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
                boolean b = true;
                if (extensions.contains(ext) && !excludeFiles.contains(fileName)) {
                    list.add(file.getLocation());
                }
            }
        }
    }


///////////////////////////////////////////////////////////////////////////////////
}
