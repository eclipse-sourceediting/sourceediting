/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslatorPersister;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.indexing.AbstractIndexManager;

/**
 * <p>Index manger used to update the JDT index with the Java translations
 * of JSPs.</p>
 * 
 * <p>Also keeps JSP persistence up to date</p>
 * 
 * <p>Any action that needs the JDT index to have all of the latest JSP changes processed
 * should wait for this manger to report that it is consistent,
 * {@link #waitForConsistent(org.eclipse.core.runtime.IProgressMonitor)}.  Such actions
 * include but are not limited to searching and refactoring JSPs.</p>
 */
public class JSPIndexManager extends AbstractIndexManager {
	/** the singleton instance of the {@link JSPIndexManager} */
	private static JSPIndexManager INSTANCE;
	
	/** the JSP {@link IContentType} */
	private static final IContentType JSP_CONTENT_TYPE =
		Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
	
	/** the location to store state */
	private IPath fWorkingLocation;
	
	/**
	 * <p>Private singleton constructor</p>
	 */
	private JSPIndexManager() {
		super(JSPCoreMessages.JSPIndexManager);
	}
	
	/**
	 * @return the singleton instance of the {@link JSPIndexManager}
	 */
	public static JSPIndexManager getDefault() {
		return INSTANCE != null ? INSTANCE : (INSTANCE = new JSPIndexManager());
	}

	/**
	 * @see indexer.internal.indexing.AbstractIndexManager#isResourceToIndex(int, java.lang.String)
	 */
	protected boolean isResourceToIndex(int type, IPath path) {
		String name = path.lastSegment();
		return 
			type == IResource.PROJECT || 
			(type == IResource.FOLDER && !name.equals("bin") && !name.startsWith(".")) || //$NON-NLS-1$ //$NON-NLS-2$
			JSP_CONTENT_TYPE.isAssociatedWith(path.lastSegment());
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
				Logger.logException("JPSIndexManger: problem indexing:" + filename, e); //$NON-NLS-1$
			}
		}
	}
}
