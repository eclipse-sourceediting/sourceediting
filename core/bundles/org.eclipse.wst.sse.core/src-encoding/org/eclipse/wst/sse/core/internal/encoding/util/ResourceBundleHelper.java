/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;

//TODO: rework this with new platform/runtime APIs (if still needed).

public class ResourceBundleHelper {

	public static ResourceBundle getResourceBundle(String resourceURI) throws MalformedURLException, IOException {
		return getResourceBundle(resourceURI, Locale.getDefault());
	}

	public static ResourceBundle getResourceBundle(String resourceURI, Locale targetLocale) throws MalformedURLException, IOException {
		// try to load bundle from the location specified in the resourceURI
		// we make the assumption that the resourceURI points to the local
		// file system

		int index = resourceURI.lastIndexOf("/"); //$NON-NLS-1$
		if (index == -1) {
			throw new IllegalArgumentException("Invalid resourceURI"); //$NON-NLS-1$
		}

		// Below we set 'resourceDirectory' so that it ends with a '/'.
		// Here's an excerpt from the ClassLoader Javadoc ...
		// Any URL that ends with a '/' is assumed to refer to a directory.
		// Otherwise, the URL is assumed
		// to refer to a JAR file which will be opened as needed.
		//
		String resourceDirectory = resourceURI.substring(0, index + 1);
		String resourceBundleName = resourceURI.substring(index + 1);

		// create a class loader with a class path that points to the resource
		// bundle's location
		//         
		URL[] classpath = new URL[1];
		classpath[0] = Platform.resolve(new URL(resourceDirectory));
		ClassLoader resourceLoader = new URLClassLoader(classpath, null);

		return ResourceBundle.getBundle(resourceBundleName, targetLocale, resourceLoader);
	}
}

