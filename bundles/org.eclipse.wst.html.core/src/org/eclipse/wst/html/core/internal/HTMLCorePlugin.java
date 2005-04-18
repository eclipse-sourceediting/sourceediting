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
package org.eclipse.wst.html.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.provisional.HTMLFilesPreferenceNames;
import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;

/**
 * The main plugin class to be used in the desktop.
 */
public class HTMLCorePlugin extends Plugin {
	//The shared instance.
	private static HTMLCorePlugin plugin;	

	/**
	 * The constructor.
	 */
	public HTMLCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static HTMLCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPluginPreferences() {
		Preferences prefs = getDefault().getPluginPreferences();
		// set model preference defaults
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS, true);
		prefs.setDefault(CommonModelPreferenceNames.INSERT_MISSING_TAGS, true);
		prefs.setDefault(CommonModelPreferenceNames.QUOTE_ATTR_VALUES, true);
		prefs.setDefault(CommonModelPreferenceNames.FORMAT_SOURCE, true);
		prefs.setDefault(CommonModelPreferenceNames.CONVERT_EOL_CODES, false);

		prefs.setDefault(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		
		String defaultEnc = CommonModelPreferenceNames.UTF_8;
		String systemEnc = System.getProperty("file.encoding");	 //$NON-NLS-1$
		if (systemEnc != null) {
			defaultEnc = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, CommonModelPreferenceNames.UTF_8);
		}
		prefs.setDefault(CommonEncodingPreferenceNames.OUTPUT_CODESET, defaultEnc);
		
		prefs.setDefault(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$
		prefs.setDefault(CommonModelPreferenceNames.TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);

		prefs.setDefault(CommonModelPreferenceNames.FORMATTING_SUPPORTED, true);
		prefs.setDefault(CommonModelPreferenceNames.LINE_WIDTH, 72);
		prefs.setDefault(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, false);
		prefs.setDefault(CommonModelPreferenceNames.INDENT_USING_TABS, true);
		prefs.setDefault(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, false);

		prefs.setDefault(CommonModelPreferenceNames.PREFERRED_MARKUP_CASE_SUPPORTED, true);
		prefs.setDefault(CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.LOWER);
		prefs.setDefault(CommonModelPreferenceNames.ATTR_NAME_CASE, CommonModelPreferenceNames.LOWER);
		
		prefs.setDefault(HTMLFilesPreferenceNames.DEFAULT_SUFFIX, HTMLFilesPreferenceNames.HTML_SUFFIX);
		prefs.setDefault(HTMLFilesPreferenceNames.GENERATE_DOCUMENT_TYPE, true);
		prefs.setDefault(HTMLFilesPreferenceNames.GENERATE_GENERATOR, true);
	}
}
