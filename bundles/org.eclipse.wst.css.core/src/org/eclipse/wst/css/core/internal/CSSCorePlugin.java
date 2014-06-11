/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.css.core.internal.contentproperties.CSSContentPropertiesManager;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CSSCorePlugin extends Plugin {
	// The shared instance.
	private static CSSCorePlugin plugin;

	/**
	 * The constructor.
	 */
	public CSSCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CSSCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// listen for resource changes to update content properties keys
		CSSContentPropertiesManager.startup();
	}

	public void stop(BundleContext context) throws Exception {
		// stop listenning for resource changes to update content properties
		// keys
		CSSContentPropertiesManager.shutdown();

		super.stop(context);
	}
}
