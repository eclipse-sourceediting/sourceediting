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
package org.eclipse.wst.dtd.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.dtd.ui.style.IStyleConstantsDTD;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;


/**
 * The main plugin class to be used in the desktop.
 */
public class DTDEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static DTDEditorPlugin plugin;

	/**
	 * Returns the shared instance.
	 */
	public static DTDEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DTDEditorPlugin.getDefault().getResourceBundle();
		try {
			if (bundle != null)
				return bundle.getString(key);
			else
				return key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * true if editor preference store has been initialized with default
	 * values false otherwise
	 */
	private boolean preferencesInitd = false;

	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public DTDEditorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			//resourceBundle =
			// ResourceBundle.getBundle("org.eclipse.wst.dtd.ui.DTDPluginResources");
			resourceBundle = descriptor.getResourceBundle();
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

		// reference the preference store so
		// initializeDefaultPreferences(IPreferenceStore preferenceStore) is
		// called
		IPreferenceStore store = getPreferenceStore();
		// for some reason initializeDefaultPreferences is not always called,
		// so
		// just add an extra check to see if not initialized, then call init
		if (!preferencesInitd) {
			initializeDefaultPreferences(store);
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	private void initializeDefaultDTDPreferences(IPreferenceStore store) {
		String ctId = IContentTypeIdentifier.ContentTypeID_DTD;

		// DTD Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String styleValue = ColorHelper.getColorString(0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_DEFAULT, ctId), styleValue); //black

		styleValue = ColorHelper.getColorString(63, 63, 191) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_TAG, ctId), styleValue); // blue
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_TAGNAME, ctId), styleValue); // blue

		styleValue = ColorHelper.getColorString(127, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_COMMENT, ctId), styleValue); // grey

		styleValue = ColorHelper.getColorString(128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_KEYWORD, ctId), styleValue); // dark
		// red

		styleValue = ColorHelper.getColorString(63, 159, 95) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_STRING, ctId), styleValue); //green

		styleValue = ColorHelper.getColorString(191, 95, 95) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_DATA, ctId), styleValue); // light
		// red

		styleValue = ColorHelper.getColorString(128, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsDTD.DTD_SYMBOL, ctId), styleValue); // dark
		// red
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {

		// ignore this preference store
		// use EditorPlugin preference store
		IPreferenceStore editorStore = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore();
		EditorPlugin.initializeDefaultEditorPreferences(editorStore);
		initializeDefaultDTDPreferences(editorStore);
		preferencesInitd = true;
	}
}
