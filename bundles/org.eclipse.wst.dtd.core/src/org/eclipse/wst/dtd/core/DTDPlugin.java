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
package org.eclipse.wst.dtd.core;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DTDPlugin extends AbstractUIPlugin {
	private static DTDPlugin instance;
	private ResourceBundle resourceBundle;

	public DTDPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
		try {
			resourceBundle = descriptor.getResourceBundle();
		}
		catch (java.util.MissingResourceException exception) {
			//B2BUtilPlugin.getPlugin().getMsgLogger().write(B2BUtilPlugin.getGUIString("_WARN_PLUGIN_PROPERTIES_MISSING") + descriptor.getLabel());
			resourceBundle = null;
		}
	}

	public void startup() {
		instance = this;
	}

	public synchronized static DTDPlugin getInstance() {
		return instance;
	}

	public static DTDPlugin getPlugin() {
		return instance;
	}

	public static Image getDTDImage(String iconName) {
		return getInstance().getImage(iconName);
	}

	public static ImageDescriptor getDTDImageDescriptor(String iconName) {
		String thisID = getInstance().getBundle().getSymbolicName();
		return AbstractUIPlugin.imageDescriptorFromPlugin(thisID, iconName);
	}

	public static String getDTDString(String key) {
		// In case it is invoked from a command line
		if (getInstance() == null) {
			return ""; //$NON-NLS-1$
		}

		return getInstance().getString(key);
	}

	public Image getImage(String iconName) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(iconName);
		
		if (image == null) {
			String thisID = getInstance().getBundle().getSymbolicName();
			imageRegistry.put(iconName, imageDescriptorFromPlugin(thisID, iconName));
			image = imageRegistry.get(iconName);
		}
		
		return image;
	}

	/**
	 * This gets the string resource.
	 */
	public String getString(String key) {
		return getResourceBundle().getString(key);
	}

	/**
	 * This gets the string resource and does one substitution.
	 */
	public String getString(String key, Object s1) {
		return MessageFormat.format(getString(key), new Object[]{s1});
	}

	/**
	 * This gets the string resource and does two substitutions.
	 */
	public String getString(String key, Object s1, Object s2) {
		return MessageFormat.format(getString(key), new Object[]{s1, s2});
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
