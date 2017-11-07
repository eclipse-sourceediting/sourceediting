/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslatorPersister;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.indexing.AbstractIndexManager;
import org.osgi.service.prefs.BackingStoreException;

/**
 * <p>Index manager used to update the JDT index with the Java translations
 * of JSPs.</p>
 * 
 * <p>Also keeps JSP persistence up to date</p>
 * 
 * <p>Any action that needs the JDT index to have all of the latest JSP changes processed
 * should wait for this manager to report that it is consistent,
 * {@link #waitForConsistent(org.eclipse.core.runtime.IProgressMonitor)}.  Such actions
 * include but are not limited to searching and refactoring JSPs.</p>
 */
public class JSPIndexManager extends AbstractIndexManager {
	/** the singleton instance of the {@link JSPIndexManager} */
	private static JSPIndexManager INSTANCE;

	private static final String INDEX_VERSION = "JSP Index Manager v3.9.2_20171107_01"; //$NON-NLS-1$

	/** the location to store state */
	private IPath fWorkingLocation;

	/**
	 * <p>
	 * Private singleton constructor
	 * </p>
	 */
	protected JSPIndexManager() {
		super(JSPCoreMessages.JSPIndexManager);
	}

	/**
	 * @return the singleton instance of the {@link JSPIndexManager}
	 */
	public static JSPIndexManager getDefault() {
		return INSTANCE != null ? INSTANCE : (INSTANCE = new JSPIndexManager());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.indexing.AbstractIndexManager#isForcedFullReIndexNeeded()
	 * Add a versioning check so we can force a re-index if needed
	 */
	protected boolean isForcedFullReIndexNeeded() {
		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());
		String stored = node.get(JSPIndexManager.class.getName(), null);
		return !INDEX_VERSION.equals(stored) || super.isForcedFullReIndexNeeded();
	}

	/*
	 * @see org.eclipse.wst.sse.core.indexing.AbstractIndexManager#isResourceToIndex(int, org.eclipse.core.runtime.IPath)
	 */
	protected boolean isResourceToIndex(int type, IPath path) {
		String name = path.lastSegment();
		
		return type == IResource.PROJECT || (type == IResource.FOLDER && !name.equals("bin") && !name.startsWith(".")) || ContentTypeIdForJSP.indexOfJSPExtension(path.lastSegment()) >= 0;//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see indexer.internal.indexing.AbstractIndexManager#getWorkingLocation()
	 */
	protected IPath getWorkingLocation() {
		if(this.fWorkingLocation == null) {
			//create path to working area
    		IPath workingLocation =
    			JSPCorePlugin.getDefault().getStateLocation().append("jspsearch"); //$NON-NLS-1$

            // ensure that it exists on disk
            File folder = new File(workingLocation.toOSString());
    		if (!folder.isDirectory()) {
    			try {
    				folder.mkdir();
    			}
    			catch (SecurityException e) {
    				Logger.logException(this.getName() +
    						": Error while creating state location: " + folder + //$NON-NLS-1$
    						" This renders the index manager irrevocably broken for this workspace session", //$NON-NLS-1$
    						e);
    			}
    		}
    		
    		this.fWorkingLocation = workingLocation;
    	}
    	
        return this.fWorkingLocation;
	}

	/**
	 * @see indexer.internal.indexing.AbstractIndexManager#performAction(byte, byte, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IPath)
	 */
	protected void performAction(byte source, byte action, IResource resource,
			IPath movePath) {
		
		//inform the persister of the action unless it come from a full workspace scan
		if(JSPTranslatorPersister.ACTIVATED && source != AbstractIndexManager.SOURCE_WORKSPACE_SCAN) {
			switch(action) {
				case AbstractIndexManager.ACTION_ADD: {
					JSPTranslatorPersister.persistTranslation(resource);
					break;
				}
				case AbstractIndexManager.ACTION_REMOVE: {
					JSPTranslatorPersister.removePersistedTranslation(resource);
					break;
				}
				case AbstractIndexManager.ACTION_ADD_MOVE_FROM: {
					JSPTranslatorPersister.movePersistedTranslation(resource, movePath);
					break;
				}
				case AbstractIndexManager.ACTION_REMOVE_MOVE_TO: {
					//do nothing, taken care of by AbstractIndexManager.ACTION_ADD_MOVE_FROM
					break;
				}
			}
		}
		
		//add any new JSP files to the JDT index using the JSPSearchSupport
		if(action == AbstractIndexManager.ACTION_ADD ||
				action == AbstractIndexManager.ACTION_ADD_MOVE_FROM) {
		
			IFile file = (IFile)resource; //this assumption can be made because of #isResourceToIndex
			JSPSearchSupport ss = JSPSearchSupport.getInstance();
			try {
				IProject project = file.getProject();
				if (project != null) {
					IJavaProject jproject = JavaCore.create(project);
					if (jproject.exists()) {
						ss.addJspFile(file);
					}
				}
			}
			catch (Exception e) {
				String filename = file != null ? file.getFullPath().toString() : ""; //$NON-NLS-1$
				Logger.logException("JPSIndexManager: problem indexing:" + filename, e); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Save index version
	 */
	public void doStop() {
		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());
		node.put(JSPIndexManager.class.getName(), INDEX_VERSION);
		try {
			node.flush();
		}
		catch (BackingStoreException e) {
			Logger.logException(e);
		}
		super.doStop();
	}
}
