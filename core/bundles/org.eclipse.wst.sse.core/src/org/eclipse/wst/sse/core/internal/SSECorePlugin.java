/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.tasks.TaskScanningScheduler;
import org.osgi.framework.BundleContext;


/**
 * SSE Core Plugin.
 */
public class SSECorePlugin extends Plugin {
	static SSECorePlugin instance = null;

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

		prefs.setDefault(CommonEncodingPreferenceNames.USE_3BYTE_BOM_WITH_UTF8, false);
		
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_ENABLE, false);
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_TAGS, "TODO,FIXME,XXX"); //$NON-NLS-1$
		prefs.setDefault(CommonModelPreferenceNames.TASK_TAG_PRIORITIES, "1,2,1"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		savePluginPreferences();
		
		TaskScanningScheduler.shutdown();

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

		// initialize FileBuffer handling
		FileBufferModelManager.startup();

		/**
		 * If the user starts the workbench with
		 * -Dorg.eclipse.wst.sse.core.taskscanner=off, the scanner should be
		 * disabled
		 */
		String scan = System.getProperty("org.eclipse.wst.sse.core.taskscanner"); //$NON-NLS-1$
		if (scan == null || !scan.equalsIgnoreCase("off")) { //$NON-NLS-1$
			TaskScanningScheduler.startup();
		}
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
