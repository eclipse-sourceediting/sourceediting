/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.xml.core.internal.catalog.Catalog;
import org.eclipse.wst.xml.core.internal.catalog.CatalogSet;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;


/**
 * The main plugin class to be used in the desktop.
 */
public class XMLCorePlugin extends Plugin {
	// The shared instance.
	private static XMLCorePlugin plugin;
	public static final String USER_CATALOG_ID = "user_catalog"; //$NON-NLS-1$
	public static final String DEFAULT_CATALOG_ID = "default_catalog"; //$NON-NLS-1$
	public static final String SYSTEM_CATALOG_ID = "system_catalog"; //$NON-NLS-1$
	private CatalogSet catalogSet = null;
	private String defaultCatalogFileStateLocation;


	/**
	 * Returns the shared instance.
	 */
	public static XMLCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * @deprecated use ResourcesPlugin.getWorkspace() directly
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public XMLCorePlugin() {
		super();
		plugin = this;
	}

	private String getPluginStateLocation(String fileName) {
		String location = getStateLocation().append(fileName).toString();
		String file_protocol = "file:"; //$NON-NLS-1$
		if (location != null && !location.startsWith(file_protocol)) {
			location = file_protocol + location;
		}
		return location;
	}

	public ICatalog getDefaultXMLCatalog() {
		if (catalogSet == null) {
			catalogSet = new CatalogSet();

			defaultCatalogFileStateLocation = getPluginStateLocation(Catalog.DEFAULT_CATALOG_FILE);

			catalogSet.putCatalogPersistenceLocation(DEFAULT_CATALOG_ID, defaultCatalogFileStateLocation);
			catalogSet.putCatalogPersistenceLocation(SYSTEM_CATALOG_ID, getPluginStateLocation(Catalog.SYSTEM_CATALOG_FILE));
			catalogSet.putCatalogPersistenceLocation(USER_CATALOG_ID, getPluginStateLocation(Catalog.USER_CATALOG_FILE));
		}
		return catalogSet.lookupOrCreateCatalog(DEFAULT_CATALOG_ID, defaultCatalogFileStateLocation);
	}

	public void clearCatalogCache() {
		if (catalogSet != null) {
			catalogSet.clearResourceCache();
		}
	}

}
