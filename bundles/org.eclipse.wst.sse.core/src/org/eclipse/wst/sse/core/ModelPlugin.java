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
package org.eclipse.wst.sse.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.builder.StructuredDocumentBuilder;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.osgi.framework.Bundle;


/**
 * This model manager plugin helper is used to return the model manager
 * desired to be used for creating a new model manager, for managing (add or
 * remove) model loaders and model dumpers, and for managing (get, release,
 * save, and save as) models. Here is an example on on how to use it. Under
 * the subdirectory named after your package which contains your
 * implementation of the model manager (e.g. "org.eclipse.wst.sse.core") in
 * the "project_resources\plugins" directory, create a "plugin.xml" file. In
 * this file ("projectproject_resources\plugins\com.ibm.sed.model\plugin.xml")
 * enter: <?xml version="1.0"?> <plugin name="IBM Web Tooling Model Manager
 * Plugin" id="org.eclipse.wst.sse.core" version="1.0" vendor-name="RTP"
 * class="org.eclipse.wst.sse.core.ModelManagerPlugin"> <!-- The XML package:
 * com.ibm.etools.b2bxmlrt, is required before the others to give precedence
 * to DOM2 APIs, instead of the DOM1 (used by others, such as the desktop
 * ("com.ibm.eclipse.*")) --> <requires><import
 * plugin="com.ibm.etools.b2bxmlrt" export="true"/> <import
 * plugin="org.eclipse.wst.common.contentmodel" export="true"/> </requires>
 * <runtime><library name="runtime/sedmodel.jar"> <export name="*"/>
 * </library> </runtime> </plugin> to tell the workbench the model manager
 * plugin ID and where to find this model manager plugin class. Then in the
 * getModelManager method of this model manager plugin class, create and
 * return the model manager desired to be used. In the client code that
 * requests the model manager, the following code should be used to access the
 * model manager via the plugin: // get the model manager from the plugin
 * ModelManagerPlugin plugin = (ModelManagerPlugin)
 * Platform.getPlugin(ModelManagerPlugin.ID); fModelManager =
 * plugin.getModelManager(); Note that the
 * wb.getPluginRegistry().getPluginDescriptor() method expects the plugin ID
 * as a parameter ("org.eclipse.wst.sse.core" in the above example).
 */
public class ModelPlugin extends Plugin implements IModelManagerPlugin {

	static ModelPlugin instance = null;

	private static final String OFF = "off"; //$NON-NLS-1$
	public static final String STRUCTURED_BUILDER = "org.eclipse.wst.sse.core.structuredbuilder"; //$NON-NLS-1$

	public static ModelPlugin getDefault() {
		return instance;
	}

	public static String getID() {
		return getDefault().getBundle().getSymbolicName();
	}

	public ModelPlugin() {
		super();
		instance = this;
	}

	public ModelPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;

	}

	public ModelHandlerRegistry getModelHandlerRegistry() {
		return ModelHandlerRegistry.getInstance();
	}

	public IModelManager getModelManager() {
		boolean isReady = false;
		IModelManager modelManager = null;
		while (!isReady) {
			Bundle localBundle = getBundle();
			int state = localBundle.getState();
			if (state == Bundle.ACTIVE) {
				isReady = true;
				// getInstance is a synchronized static method.
				modelManager = ModelManagerImpl.getInstance();
			}
			else if (state == Bundle.STARTING) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore, just loop again
				}
			}
			else if (state == Bundle.STOPPING) {
				isReady = true;
				modelManager = new NullModelManager();
			}
			else if (state == Bundle.UNINSTALLED) {
				isReady = true;
				modelManager = new NullModelManager();
			}
			else {
				// not sure about other states, 'resolved', 'installed'
				isReady = true;
			}
		}
		return modelManager;
	}

	/**
	 * Set default non-UI
	 */
	protected void initializeDefaultPluginPreferences() {
		super.initializeDefaultPluginPreferences();
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
	}

	public void shutdown() throws CoreException {
		super.shutdown();
		savePluginPreferences();
		StructuredDocumentBuilder.shutdown();
		FileBufferModelManager.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();

		// initialize FileBuffer support
		FileBufferModelManager.startup();

		// allow for explicitly disabling the builder (testing)
		String build = System.getProperty(STRUCTURED_BUILDER);
		if (build == null || !build.equalsIgnoreCase(OFF)) {
			StructuredDocumentBuilder.startup();
		}
	}

}
