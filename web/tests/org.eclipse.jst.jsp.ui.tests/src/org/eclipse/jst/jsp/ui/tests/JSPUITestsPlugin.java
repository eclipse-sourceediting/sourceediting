/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPUITestsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static JSPUITestsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	public static final String ID = "org.eclipse.jst.jsp.ui.tests";

    /**
	 * The constructor.
	 */
	public JSPUITestsPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.jst.jsp.ui.tests.SSEForJSPTestsPluginResources");
		}
		catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static JSPUITestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = JSPUITestsPlugin.getDefault().getResourceBundle();
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
		URL installLocation = Platform.getBundle("org.eclipse.jst.jsp.ui.tests").getEntry("/");
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
	
    /**
     * Find a file inside any bundle
     * @param bundleId
     * @param path
     * @return
     */
    public static File getFileLocation(String bundleId, String path) throws CoreException {

            Bundle bundle = Platform.getBundle(bundleId);
            URL url1 = bundle.getEntry(path);
            URL url = null;
            try {
                    url = FileLocator.toFileURL(url1);
            } catch (IOException e) {
                    String msg = "Cannot find file " + path + " in " + bundleId;
                    IStatus status = new Status(IStatus.ERROR, bundleId, msg, e);
                    throw new CoreException(status);
            }
            String location = url.getFile();
            return new File(location);
    }

	
	
	public static File getTestFile(String filepath) {
		try {
			return getFileLocation("org.eclipse.jst.jsp.ui.tests", filepath);
		} catch(CoreException ce) {
			throw new RuntimeException(ce);
		}
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		JSPUITestImages.initializeImageRegistry(reg);
	}
}
