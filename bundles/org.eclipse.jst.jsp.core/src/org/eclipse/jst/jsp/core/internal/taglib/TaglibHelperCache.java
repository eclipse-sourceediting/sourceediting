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
package org.eclipse.jst.jsp.core.internal.taglib;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.internal.Logger;

/**
 * A simple cache for TaglibHelpers to avoid excessive creation of TaglibClassLoaders
 * @author pavery
 */
class TaglibHelperCache {

    private static final boolean DEBUG;
    static {
        String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibvars"); //$NON-NLS-1$
        DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }

    /**
     * An entry for the cache (projectPath string & TaglibHelper)
     */
    class Entry {
        private TaglibHelper fHelper;
        private String fProjectPath;
        
        public Entry(String projectPath, TaglibHelper helper) {
            setProjectPath(projectPath);
            setHelper(helper);
        }
        public TaglibHelper getHelper() {
            return fHelper;
        }
        public void setHelper(TaglibHelper helper) {
            fHelper = helper;
        }
        public String getProjectPath() {
            return fProjectPath;
        }
        public void setProjectPath(String projectPath) {
            fProjectPath = projectPath;
        }
        public String toString() {
            return "Taglib Helper Entry [" + getProjectPath() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    private List fHelpers;
    // max size for the cache
    private int MAX_SIZE;

    /**
     * Not intended to be large since underlying implmementation uses
     * a List.
     */
    public TaglibHelperCache(int size) {
        MAX_SIZE = size;
        fHelpers = Collections.synchronizedList(new ArrayList(MAX_SIZE));
    }
    /**
     * 
     * @param projectPath
     * @param f
     * @param mq
     * @return
     */
    public final synchronized TaglibHelper getHelper(IProject project) {
        TaglibHelper helper = null;
        Entry entry = null;
        String projectPath = project.getFullPath().toString();
        int size = fHelpers.size();
        // fist check for existing
        for (int i=0; i<size; i++) {
            entry = (Entry)fHelpers.get(i);
            if(entry.getProjectPath().equals(projectPath)) {
                // exists
                helper = entry.getHelper();
                // only move to front if it's not the first entry
                if(i>0) {
                    fHelpers.remove(entry);
	                fHelpers.add(1, entry);
	                if(DEBUG) {
	                    Logger.log(Logger.INFO, "(->) TaglibHelperCache moved: " + entry + " to the front of the list"); //$NON-NLS-1$ //$NON-NLS-2$
	                    printCacheContents();
	                }
                }
                break;
            }
        }
        // didn't exist
        if(helper == null) {
            helper = createNewHelper(projectPath, project);
        }
        return helper;
    }
    
    /**
     * @param projectPath
     * @param f
     * @param mq
     * @return
     */
    private TaglibHelper createNewHelper(String projectPath, IProject project) {

        TaglibHelper helper;
        // create
        helper = new TaglibHelper(project);
        Entry newEntry = new Entry(projectPath, helper);
        fHelpers.add(0, newEntry);
        if(DEBUG) {
        	Logger.log(Logger.INFO, "(+) TaglibHelperCache added: " + newEntry); //$NON-NLS-1$
            printCacheContents();
        }
        if(fHelpers.size() > MAX_SIZE) {
            // one too many, remove last
            Object removed = fHelpers.remove(fHelpers.size()-1);
            if(DEBUG) {
            	Logger.log(Logger.INFO, "(-) TaglibHelperCache removed: " + removed); //$NON-NLS-1$
                printCacheContents();
            }
        }
        return helper;
    }
 
    public final synchronized void removeHelper(String projectPath) {
        Entry entry = null;
        Iterator it = fHelpers.iterator();
        while(it.hasNext()) {
            entry = (Entry)it.next();
            if(entry.getProjectPath().equals(projectPath)) {
                fHelpers.remove(entry);
                if(DEBUG) { 
                    Logger.log(Logger.INFO, "(-) TaglibHelperCache removed: " + entry); //$NON-NLS-1$
                    printCacheContents();
                }
                break;
            }
        }
    }
    
    private void printCacheContents() {
        StringBuffer debugString = new StringBuffer();
        debugString.append("\n-----------------------------------------------------------"); //$NON-NLS-1$
        debugString.append("\ncache contents:"); //$NON-NLS-1$
        for (int i=0; i<fHelpers.size(); i++)	
        	debugString.append("\n -" + i + "- " + fHelpers.get(i)); //$NON-NLS-1$ //$NON-NLS-2$
        debugString.append("\n-----------------------------------------------------------"); //$NON-NLS-1$
        Logger.log(Logger.INFO, debugString.toString());
    }
}
