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
package org.eclipse.wst.html.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.html.core.internal.contentproperties.HTMLContentPropertiesManager;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class HTMLCorePlugin extends Plugin {
	// The shared instance.
	private static HTMLCorePlugin plugin;

	/**
	 * The constructor.
	 */
	public HTMLCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static HTMLCorePlugin getDefault() {
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
		HTMLContentPropertiesManager.startup();
	}

	public void stop(BundleContext context) throws Exception {
		// stop listenning for resource changes to update content properties
		// keys
		HTMLContentPropertiesManager.shutdown();

		super.stop(context);
	}
}
