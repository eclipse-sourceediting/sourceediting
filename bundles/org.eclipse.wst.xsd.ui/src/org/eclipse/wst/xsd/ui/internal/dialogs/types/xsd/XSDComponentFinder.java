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

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentFinder;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLQuickScan;

public class XSDComponentFinder extends XMLComponentFinder {    
    public XSDComponentFinder() {
        validExtensions.add("xsd");
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
        nameAttr[0] = "name";
        attributes.add(nameAttr);
        attributes.add(nameAttr);

        Iterator pathsIterator = filePaths.iterator();
        while (pathsIterator.hasNext()) {
//           String stringPath = ((Path) pathsIterator.next()).toOSString();
           String stringPath = ((Path) pathsIterator.next()).toString();
           components.addAll(XMLQuickScan.getTagInfo(stringPath, paths, attributes));
        }
        
        return components;
    }
}
