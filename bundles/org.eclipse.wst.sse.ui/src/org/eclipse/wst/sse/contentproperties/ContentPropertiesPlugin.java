/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.contentproperties;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;

/**
 * The main plugin class to be used in the desktop.
 */
public class ContentPropertiesPlugin extends Plugin implements IStartup {
	//The shared instance.
	private static ContentPropertiesPlugin plugin;
	
	/**
	 * Returns the shared instance.
	 */
	public static ContentPropertiesPlugin getDefault() {
		if (plugin == null) {
			plugin = new ContentPropertiesPlugin();
		}
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ContentPropertiesPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		}
		catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	private ContentPropertiesPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.sse.contentproperties.ContentPropertiesPluginResources"); //$NON-NLS-1$
		}
		catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static void disableSynchronizer() {
		ContentSettingsSynchronizer listener = ContentSettingsSynchronizer.getInstance();
		if (listener != null && listener.isListening())
			listener.unInstall();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		enableSynchronizer();
	}

	public static void enableSynchronizer() {
		ContentSettingsSynchronizer listener = ContentSettingsSynchronizer.getInstance();
		if (listener != null && !listener.isListening())
			listener.install();
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#shutdown()
	 */
	public void shutdown() throws CoreException {
		super.shutdown();
		disableSynchronizer();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();
		enableSynchronizer();
	}

}
