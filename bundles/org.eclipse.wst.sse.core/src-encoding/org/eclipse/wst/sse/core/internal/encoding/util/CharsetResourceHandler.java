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
package org.eclipse.wst.sse.core.internal.encoding.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.encoding.ICodedResourcePlugin;


public class CharsetResourceHandler {
	private static ResourceBundle fgResourceBundle;

	/**
	 * Returns the resource bundle used by all classes in this Project
	 */
	public static ResourceBundle getResourceBundle() {
		try {
			// TODO: rework this in terms of Platform.getPlugin(descriptor)
			// and Location
			URL configURI = Platform.getBundle(ICodedResourcePlugin.ID).getEntry("/"); //$NON-NLS-1$
			String configPathString = configURI + "config/charset"; //$NON-NLS-1$
			return ResourceBundleHelper.getResourceBundle(configPathString);
		}
		catch (MissingResourceException e) {
			Logger.logException("invalid install or configuration", e); //$NON-NLS-1$
		}
		catch (MalformedURLException e) {
			Logger.logException("invalid install or configuration", e); //$NON-NLS-1$
		}
		catch (IOException e) {
			Logger.logException("invalid install or configuration", e); //$NON-NLS-1$
		}
		return null;
	}

	public static String getString(String key) {
		String result = null;
		if (fgResourceBundle == null) {
			fgResourceBundle = getResourceBundle();
		}
		if (fgResourceBundle != null) {
			try {
				result = fgResourceBundle.getString(key);
			}
			catch (MissingResourceException e) {
				result = "!" + key + "!";//$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			result = "!" + key + "!";//$NON-NLS-2$//$NON-NLS-1$
		}
		return result;
	}

	public static String getString(String key, Object[] args) {
		try {
			return MessageFormat.format(getString(key), args);
		}
		catch (IllegalArgumentException e) {
			return getString(key);
		}
	}
}
