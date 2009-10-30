/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentPropertiesManager;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelperManager;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPCorePlugin extends Plugin {
	// The shared instance.
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
	 * 
	 * @deprecated - will be removed. Currently used to get "model
	 *             preferences", but there are other, better ways.
	 */
	public static JSPCorePlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		TaglibIndex.startup();
		// JSPIndexManager depends on TaglibController, so TaglibController
		// should be started first
		TaglibController.startup();

		// listen for classpath changes
		JavaCore.addElementChangedListener(TaglibHelperManager.getInstance());


		JSPIndexManager.getInstance().initialize();

		// listen for resource changes to update content properties keys
		JSPFContentPropertiesManager.startup();

		DeploymentDescriptorPropertyCache.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		DeploymentDescriptorPropertyCache.stop();
		// stop listenning for resource changes to update content properties
		// keys
		JSPFContentPropertiesManager.shutdown();

		// stop any indexing
		JSPIndexManager.getInstance().shutdown();

		// stop listening for classpath changes
		JavaCore.removeElementChangedListener(TaglibHelperManager.getInstance());

		// stop taglib controller
		TaglibController.shutdown();
		TaglibIndex.shutdown();

		super.stop(context);
	}
}
