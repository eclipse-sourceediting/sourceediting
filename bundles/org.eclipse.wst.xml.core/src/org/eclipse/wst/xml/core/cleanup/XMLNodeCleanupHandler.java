/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.cleanup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.w3c.dom.Node;

/**
 * @deprecated renamed to NodeCleanupHandler
 *
 * TODO will delete in C5
 */
public class XMLNodeCleanupHandler implements XMLCleanupHandler {

	protected XMLCleanupPreferences fCleanupPreferences = null;
	protected IProgressMonitor fProgressMonitor = null;

	/**
	 */
	public Node cleanup(Node node) {

		return node;
	}

	public void setCleanupPreferences(XMLCleanupPreferences cleanupPreferences) {

		fCleanupPreferences = cleanupPreferences;
	}

	private IModelManagerPlugin getModelManagerPlugin() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin;
	}

	/**
	 */
	public XMLCleanupPreferences getCleanupPreferences() {

		if (fCleanupPreferences == null) {
			fCleanupPreferences = new XMLCleanupPreferencesImpl();
			Preferences preferenceStore = getModelManagerPlugin().getPluginPreferences();
			if (preferenceStore != null) {
				fCleanupPreferences.setPreferences(preferenceStore);
			}
		}
		return fCleanupPreferences;
	}
}
