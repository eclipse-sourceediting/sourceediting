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


package org.eclipse.wst.xml.ui.util;


import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.markers.internal.ImageFactory;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;



/**
 * This class exists temporarily until the properties files can be
 * re-organized and the various resource references can be updated
 */
public class XMLCommonResources {
	protected static XMLCommonResources instance;

	public synchronized static XMLCommonResources getInstance() {
		if (instance == null)
			instance = new XMLCommonResources(XMLEditorPlugin.getDefault());
		return instance;
	}

	private XMLEditorPlugin editorPlugin;

	protected ImageFactory imageFactory;
	private ResourceBundle resourceBundle;

	public XMLCommonResources(XMLEditorPlugin editorPlugin) {
		instance = this;
		this.editorPlugin = editorPlugin;
		//imageFactory = new ImageFactory();
		try {
			resourceBundle = ResourceBundle.getBundle("EditingXML"); //$NON-NLS-1$
			imageFactory = new ImageFactory();
		} catch (java.util.MissingResourceException exception) {
			//TODO... log an error message
			//B2BUtilPlugin.getPlugin().getMsgLogger().write(B2BUtilPlugin.getGUIString("_WARN_PLUGIN_PROPERTIES_MISSING")
			// + descriptor.getLabel());
			resourceBundle = null;
		}
	}

	 ImageDescriptor _getImageDescriptor(String iconName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(XMLEditorPlugin.ID, iconName);
	}


	public ResourceBundle getResourceBundle() {
		return resourceBundle;
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

	public IWorkbench getWorkbench() {
		return editorPlugin.getWorkbench();
	}
	/*
	 * public ImageFactory getImageFactory() { return imageFactory; }
	 */
}
