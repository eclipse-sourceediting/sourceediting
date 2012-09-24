/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding.util;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.encoding.ICodedResourcePlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class CodedResourcePlugin extends Plugin implements ICodedResourcePlugin {
	//The shared instance.
	private static CodedResourcePlugin plugin;

	/**
	 * Returns the shared instance.
	 */
	public static CodedResourcePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public CodedResourcePlugin() {
		super();
		plugin = this;
	}

	protected void initializeDefaultPluginPreferences() {
		Preferences prefs = getPluginPreferences();
		prefs.setDefault(CommonEncodingPreferenceNames.USE_3BYTE_BOM_WITH_UTF8, false);
	}
}
