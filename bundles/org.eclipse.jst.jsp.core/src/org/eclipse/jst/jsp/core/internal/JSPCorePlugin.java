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
package org.eclipse.jst.jsp.core.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPCorePlugin extends Plugin {
	//The shared instance.
	private static JSPCorePlugin plugin;	

	/**
	 * The constructor.
	 */
	public JSPCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JSPCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// JSPIndexManager depends on TaglibController, so TaglibController
		// should be started first
		TaglibController.startup();
		
		// add JSPIndexManager to keep JSP Index up to date
		// listening for IResourceChangeEvent.PRE_DELETE and IResourceChangeEvent.POST_CHANGE
		ResourcesPlugin.getWorkspace().addResourceChangeListener(JSPIndexManager.getInstance(), IResourceChangeEvent.POST_CHANGE);
		
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5091
		// makes sure IndexManager is aware of our indexes
		JSPIndexManager.getInstance().saveIndexes();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// stop listening
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(JSPIndexManager.getInstance());
		// stop any searching
		JSPSearchSupport.getInstance().setCanceled(true);
		// stop any indexing
		JSPIndexManager.getInstance().shutdown();
		// stop taglib controller
		TaglibController.shutdown();

		super.stop(context);
	}
}
