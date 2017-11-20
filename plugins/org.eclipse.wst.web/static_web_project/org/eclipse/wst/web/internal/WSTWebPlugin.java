/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.web.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import java.lang.Throwable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.MultiStatus;

/**
 * The main plugin class to be used in the desktop.
 */
public class WSTWebPlugin extends Plugin
{
	//The shared instance.
	private static WSTWebPlugin plugin;

	private WSTWebPreferences preferences;
	
	public static final String VALIDATION_BUILDER_ID = ValidationPlugin.VALIDATION_BUILDER_ID; // plugin
	
	public static final String[] ICON_DIRS = new String[]{"icons/full/obj16", //$NON-NLS-1$
				"icons/full/ctool16", //$NON-NLS-1$
				"icons/full/wizban", //$NON-NLS-1$
				"icons", //$NON-NLS-1$
				""}; //$NON-NLS-1$

	//the ID for this plugin (added automatically by logging quickfix)
	public static final String PLUGIN_ID = "org.eclipse.wst.web"; //$NON-NLS-1$
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

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	@Override
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

	public static IStatus createStatus(int severity, String message, Throwable exception) {
		return new Status(severity, PLUGIN_ID, message, exception);
	}

	public static IStatus createStatus(int severity, String message) {
		return createStatus(severity, message, null);
	}

	public static void logError(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message));
	}

	public static void logError(String message, Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message, exception));
	}

	public static void logError(String message, CoreException exception) {
		MultiStatus status = new MultiStatus(PLUGIN_ID,IStatus.ERROR,new IStatus[]{exception.getStatus()},message,exception);
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( status );
	}

	public static void logError(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, exception.getMessage(), exception));
	}

	public static void logError(CoreException exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( exception.getStatus() );
	}
}
