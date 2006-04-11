/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class WSTWebUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static WSTWebUIPlugin plugin;
	public static final String[] ICON_DIRS = new String[]{"icons/full/obj16", //$NON-NLS-1$
		"icons/full/ctool16", //$NON-NLS-1$
		"icons/full/wizban", //$NON-NLS-1$
		"icons", //$NON-NLS-1$
		""}; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public WSTWebUIPlugin() {
		super();
		plugin = this;
	}
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	
	/**
	 * This gets a .gif from the icons folder.
	 */
	public ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = null;
		URL gifImageURL = getImageURL(key, getBundle());
		if (gifImageURL != null)
			imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
		return imageDescriptor;
	}
	/**
	 * This gets a .gif from the icons folder.
	 */
	public static URL getImageURL(String key, Bundle bundle) {
		String gif = "/" + key + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$
		IPath path = null;
		for (int i = 0; i < ICON_DIRS.length; i++) {
			path = new Path(ICON_DIRS[i]).append(gif);
			if (bundle.getEntry(path.toString()) == null)
				continue;
			try {
				return new URL(bundle.getEntry("/"), path.toString()); //$NON-NLS-1$
			} catch (MalformedURLException exception) {
				Logger.log(Logger.WARNING, "Load_Image_Error_", exception); //$NON-NLS-1$
				continue;
			}
		}
		return null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static WSTWebUIPlugin getDefault() {
		return plugin;
	}

}
