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
package org.eclipse.wst.css.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.common.encoding.CommonCharsetNames;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.css.core.preferences.CSSPreferenceHelper;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;


/**
 * The main plugin class to be used in the desktop.
 */
public class CSSCorePlugin extends Plugin {
	// The shared instance.
	private static CSSCorePlugin plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public CSSCorePlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.css.core.CssPluginResources"); //$NON-NLS-1$
		}
		catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static CSSCorePlugin getDefault() {
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
		ResourceBundle bundle = CSSCorePlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPluginPreferences() {
		super.initializeDefaultPluginPreferences();

		Preferences prefs = getDefault().getPluginPreferences();

		CSSPreferenceHelper.createDefaultPreferences(prefs);

		// set model preference defaults
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.INSERT_MISSING_TAGS, true);
		prefs.setDefault(CommonModelPreferenceNames.QUOTE_ATTR_VALUES, true);
		prefs.setDefault(CommonModelPreferenceNames.FORMAT_SOURCE, true);
		prefs.setDefault(CommonModelPreferenceNames.CONVERT_EOL_CODES, false);

		prefs.setDefault(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$

		String defaultEnc = CommonModelPreferenceNames.UTF_8;
		String systemEnc = System.getProperty("file.encoding"); //$NON-NLS-1$
		if (systemEnc != null) {
			defaultEnc = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, CommonModelPreferenceNames.UTF_8);
		}
		prefs.setDefault(CommonEncodingPreferenceNames.OUTPUT_CODESET, defaultEnc);

		prefs.setDefault(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$

		prefs.setDefault(CommonModelPreferenceNames.TAB_WIDTH, 4);

		prefs.setDefault(CommonModelPreferenceNames.FORMATTING_SUPPORTED, true);
		prefs.setDefault(CommonModelPreferenceNames.LINE_WIDTH, 72);
		prefs.setDefault(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, false);
		prefs.setDefault(CommonModelPreferenceNames.INDENT_USING_TABS, true);
		prefs.setDefault(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, false);

		prefs.setDefault(CommonModelPreferenceNames.PREFERRED_MARKUP_CASE_SUPPORTED, false);
		prefs.setDefault(CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.LOWER);
		prefs.setDefault(CommonModelPreferenceNames.ATTR_NAME_CASE, CommonModelPreferenceNames.LOWER);
	}

}