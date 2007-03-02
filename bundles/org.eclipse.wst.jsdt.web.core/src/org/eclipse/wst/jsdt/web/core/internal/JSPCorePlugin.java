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
package org.eclipse.wst.jsdt.web.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.jsdt.core.JavaCore;

import org.eclipse.wst.jsdt.web.core.internal.contentproperties.JSPFContentPropertiesManager;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JSPIndexManager;

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
	 * @deprecated - will be removed. Currently used to get "model preferences",
	 *             but there are other, better ways.
	 */
	@Deprecated
	public static JSPCorePlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		
		// JSPIndexManager depends on TaglibController, so TaglibController
		// should be started first
		

		// listen for classpath changes
	

		JSPIndexManager.getInstance().initialize();

		// listen for resource changes to update content properties keys
		JSPFContentPropertiesManager.startup();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// stop listenning for resource changes to update content properties
		// keys
		JSPFContentPropertiesManager.shutdown();

		// stop any indexing
		JSPIndexManager.getInstance().shutdown();

		
		
		super.stop(context);
	}
}
