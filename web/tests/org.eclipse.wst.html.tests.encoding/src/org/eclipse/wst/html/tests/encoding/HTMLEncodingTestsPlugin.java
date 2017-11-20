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
package org.eclipse.wst.html.tests.encoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class HTMLEncodingTestsPlugin extends Plugin {
	// The shared instance.
	private static HTMLEncodingTestsPlugin plugin;

	public static List getAllTestFiles(String topDirName) {
		List result = null;
		URL installURL = getInstallLocation();
		// String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + topDirName;
		File topDir = new File(location);
		if (!topDir.isDirectory()) {
			throw new IllegalArgumentException(topDirName + " is not a directory");
		}
		result = getFilesInDir(topDir);
		return result;
	}

	/**
	 * Returns the shared instance.
	 */
	public static HTMLEncodingTestsPlugin getDefault() {
		return plugin;
	}

	private static List getFilesInDir(File topDir) {
		List files = new ArrayList();
		File[] topFiles = topDir.listFiles();
		for (int i = 0; i < topFiles.length; i++) {
			File file = topFiles[i];
			if (file.isFile()) {
				files.add(file);
			}
			else if (file.isDirectory() && !file.getName().endsWith("CVS")) {
				List innerFiles = getFilesInDir(file);
				files.addAll(innerFiles);
			}
		}
		return files;
	}

	public static URL getInstallLocation() {
		URL installLocation = Platform.getBundle("org.eclipse.wst.html.tests.encoding").getEntry("/");
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
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = HTMLEncodingTestsPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		}
		catch (MissingResourceException e) {
			return key;
		}
	}

	public static File getTestFile(String filepath) {
		URL installURL = getInstallLocation();
		// String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + filepath;
		File result = new File(location);
		return result;
	}

	public static Reader getTestReader(String filepath) throws FileNotFoundException {
		URL installURL = getInstallLocation();
		// String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + filepath;
		Reader result = new FileReader(location);
		return result;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public HTMLEncodingTestsPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return null;
	}

}
