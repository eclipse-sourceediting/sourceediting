/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.css.ui.style.IStyleConstantsCSS;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;


/**
 * The main plugin class to be used in the desktop.
 */
public class CSSEditorPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.css.ui"; //$NON-NLS-1$
	//The shared instance.
	private static CSSEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public CSSEditorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.css.ui.CSSEditorPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

		// Force a call to initialize default preferences since
		// initializeDefaultPreferences is only called if *this* plugin's
		// preference store is accessed
		initializeDefaultCSSPreferences(((AbstractUIPlugin)Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore());
	}

	/**
	 * Returns the shared instance.
	 */
	public static CSSEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CSSEditorPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {

		// ignore this preference store
		// use EditorPlugin preference store
		IPreferenceStore editorStore = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore();
		initializeDefaultCSSPreferences(editorStore);
	}

	private void initializeDefaultCSSPreferences(IPreferenceStore store) {
		String ctId = IContentTypeIdentifier.ContentTypeID_CSS;

		// CSS Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.NORMAL, ctId), styleValue);

		styleValue = ColorHelper.getColorString(63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.ATMARK_RULE, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.SELECTOR, ctId), styleValue);

		styleValue = ColorHelper.getColorString(42, 0, 225) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.MEDIA, ctId), styleValue);

		styleValue = ColorHelper.getColorString(63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.COMMENT, ctId), styleValue);

		styleValue = ColorHelper.getColorString(127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.PROPERTY_NAME, ctId), styleValue);

		styleValue = ColorHelper.getColorString(42, 0, 225) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.PROPERTY_VALUE, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.URI, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.STRING, ctId), styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.COLON, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.SEMI_COLON, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.CURLY_BRACE, ctId), styleValue);

		styleValue = ColorHelper.getColorString(191, 63, 63) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsCSS.ERROR, ctId), styleValue);
	}
}