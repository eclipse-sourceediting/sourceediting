/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.validation.plugin.ValidationPlugin;
import org.osgi.framework.Bundle;

/**
 * The main plugin class to be used in the desktop.
 */
public class WSTWebPlugin extends AbstractUIPlugin
{
	//The shared instance.
	private static WSTWebPlugin plugin;

	private WSTWebPreferences preferences;
	
	public static final String PLUGIN_ID = "com.ibm.etools.webtools.staticwebproject"; //$NON-NLS-1$
	public static final String VALIDATION_BUILDER_ID = ValidationPlugin.VALIDATION_BUILDER_ID; // plugin
	
	public static final String[] ICON_DIRS = new String[]{"icons/full/obj16", //$NON-NLS-1$
				"icons/full/ctool16", //$NON-NLS-1$
				"icons/full/wizban", //$NON-NLS-1$
				"icons", //$NON-NLS-1$
				""}; //$NON-NLS-1$
	/**
	 * The constructor.
	 */
	public WSTWebPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static WSTWebPlugin getDefault()
	{
		return plugin;
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
				return new URL(bundle.getEntry("/"), path.toString());
			} catch (MalformedURLException exception) {
				org.eclipse.jem.util.logger.proxy.Logger.getLogger().logWarning("Load_Image_Error_"); //$NON-NLS-1$
				exception.printStackTrace();
				continue;
			}
		}
		return null;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	protected void initializeDefaultPluginPreferences() {
		getWSTWebPreferences().initializeDefaultPreferences();
	}
	/**
	 * @return Returns the preferences.
	 */
	public WSTWebPreferences getWSTWebPreferences() {
		if (this.preferences == null)
			this.preferences = new WSTWebPreferences(this);
		return this.preferences;
	}
}