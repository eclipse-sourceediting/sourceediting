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
package org.eclipse.jst.jsp.core.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class SSEModelJSPTestsPlugin extends Plugin {
	//The shared instance.
	private static SSEModelJSPTestsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public SSEModelJSPTestsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.jst.jsp.core.tests.TestsPluginResources");
		}
		catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static SSEModelJSPTestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = SSEModelJSPTestsPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		}
		catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static URL getInstallLocation() {
		URL installLocation = getDefault().getDescriptor().getInstallURL();
		URL resolvedLocation = null;
		try {
			resolvedLocation = Platform.resolve(installLocation);
		}
		catch (IOException e) {
			// impossible
			throw new Error(e);
		}
		return resolvedLocation;
	}
	
	public static File getTestFile(String filepath) {
		URL installURL = getInstallLocation();
		//String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + filepath;
		File result = new File(location);
		return result;
	}

}