/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (STAR) - bug 259447 - content assistance tests
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class XMLUITestsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static XMLUITestsPlugin plugin;
	protected static Bundle pluginBundle;

	/**
	 * The constructor.
	 */
	public XMLUITestsPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		pluginBundle = context.getBundle();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		pluginBundle = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static XMLUITestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		return key;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return null;
	}
	
    /**
     * Get the install URL of this plugin.
     * 
     * @return the install url of this plugin
     */
    public static String getInstallURL()
    {
	    try
	    {
	    	return FileLocator.resolve(pluginBundle.getEntry("/")).getFile(); //$NON-NLS-1$
	    }
	    catch (IOException e)
	    {
	    	return null;
	    }
	  }
    
	public static File getTestFile(String filepath) {
		URL installURL = getInstallLocation();
		//String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + filepath;
		File result = new File(location);
		return result;
	}    

	public static URL getInstallLocation() {
		URL installLocation = pluginBundle.getEntry("/");
		URL resolvedLocation = null;
		try {
			resolvedLocation = FileLocator.resolve(installLocation);
		}
		catch (IOException e) {
			// impossible
			throw new Error(e);
		}
		return resolvedLocation;
	}	
}