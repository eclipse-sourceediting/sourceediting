/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class DTDUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static DTDUIPlugin plugin;

	/**
	 * Returns the shared instance.
	 */
	public static DTDUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DTDUIPlugin.getDefault().getResourceBundle();
		String bundleKey = null;
		if (key.startsWith("%")) {
			bundleKey = key.substring(1);
		}
		else {
			bundleKey = key;
		}
		try {
			if (bundle != null)
				return bundle.getString(bundleKey);
		}
		catch (MissingResourceException e) {
		}
		return key;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public DTDUIPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			try {
				resourceBundle = Platform.getResourceBundle(getBundle());
			}
			catch (MissingResourceException x) {
				resourceBundle = null;
			}
		}
		return resourceBundle;
	}
}
