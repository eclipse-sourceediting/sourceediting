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
package org.eclipse.wst.sse.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.builder.StructuredDocumentBuilder;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.osgi.framework.BundleContext;


/**
 * SSE Core Plugin.
 */
public class SSECorePlugin extends Plugin {
	static SSECorePlugin instance = null;	

	private static final String OFF = "off"; //$NON-NLS-1$
	public static final String STRUCTURED_BUILDER = "org.eclipse.wst.sse.core.structuredbuilder"; //$NON-NLS-1$
	public static final String ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	public static SSECorePlugin getDefault() {
		return instance;
	}

	public SSECorePlugin() {
		super();
		instance = this;
	}

	/**
	 * Set default non-UI
	 */
	protected void initializeDefaultPluginPreferences() {
		Preferences prefs = getDefault().getPluginPreferences();
		// set model preference defaults
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CommonModelPreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS, true);
		prefs.setDefault(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS, true);
		prefs.setDefault(CommonModelPreferenceNames.INSERT_MISSING_TAGS, true);
		prefs.setDefault(CommonModelPreferenceNames.QUOTE_ATTR_VALUES, true);
		prefs.setDefault(CommonModelPreferenceNames.FORMAT_SOURCE, true);
		prefs.setDefault(CommonModelPreferenceNames.CONVERT_EOL_CODES, false);
		prefs.setDefault(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		prefs.setDefault(CommonEncodingPreferenceNames.OUTPUT_CODESET, CommonModelPreferenceNames.UTF_8);
		prefs.setDefault(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$
		prefs.setDefault(CommonModelPreferenceNames.TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);
		prefs.setDefault(CommonModelPreferenceNames.FORMATTING_SUPPORTED, false);
		prefs.setDefault(CommonModelPreferenceNames.LINE_WIDTH, 72);
		prefs.setDefault(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, false);
		prefs.setDefault(CommonModelPreferenceNames.INDENT_USING_TABS, true);
		prefs.setDefault(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, false);
		prefs.setDefault(CommonModelPreferenceNames.PREFERRED_MARKUP_CASE_SUPPORTED, false);
		prefs.setDefault(CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.UPPER);
		prefs.setDefault(CommonModelPreferenceNames.ATTR_NAME_CASE, CommonModelPreferenceNames.LOWER);
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_ENABLE, false);
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_TAGS, "TODO,FIXME,XXX"); //$NON-NLS-1$
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_PRIORITIES, "1,2,1"); //$NON-NLS-1$

		initializeDefaultPluginPreferencesForEncoding(prefs);

	}

	private void initializeDefaultPluginPreferencesForEncoding(Preferences prefs) {
		prefs.setDefault(CommonEncodingPreferenceNames.USE_3BYTE_BOM_WITH_UTF8, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		savePluginPreferences();
		StructuredDocumentBuilder.shutdown();
		FileBufferModelManager.shutdown();

		super.stop(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		String build = System.getProperty(STRUCTURED_BUILDER);
		if (build == null || !build.equalsIgnoreCase(OFF)) {
			StructuredDocumentBuilder.startup();
		}
		// initialize FileBuffer handling
		FileBufferModelManager.startup();
	}

	/**
	 * @deprecated
	 */
	public ModelHandlerRegistry getModelHandlerRegistry() {
		return ModelHandlerRegistry.getInstance();
	}

	/**
	 * @deprecated - use StructuredModelManager.getModelManager();
	 */
	public IModelManager getModelManager() {
		return StructuredModelManager.getModelManager();
	}
}
