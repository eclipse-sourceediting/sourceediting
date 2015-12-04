/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core;

import java.io.IOException;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.internal.schema.SchemaProcessorRegistryReader;
import org.eclipse.wst.json.core.internal.schema.catalog.Catalog;
import org.eclipse.wst.json.core.internal.schema.catalog.CatalogSet;
import org.eclipse.wst.json.core.schema.catalog.ICatalog;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSONCorePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.json.core"; //$NON-NLS-1$

	public static final String USER_CATALOG_ID = "user_catalog"; //$NON-NLS-1$
	public static final String DEFAULT_CATALOG_ID = "default_catalog"; //$NON-NLS-1$
	public static final String SYSTEM_CATALOG_ID = "system_catalog"; //$NON-NLS-1$

	private CatalogSet catalogSet = null;
	private String defaultCatalogFileStateLocation;

	// The shared instance
	private static JSONCorePlugin plugin;

	/**
	 * The constructor
	 */
	public JSONCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JSONCorePlugin getDefault() {
		return plugin;
	}

	private String getPluginStateLocation(String fileName) {
		String location = getStateLocation().append(fileName).toString();
		String file_protocol = "file:"; //$NON-NLS-1$
		if (location != null && !location.startsWith(file_protocol)) {
			location = file_protocol + location;
		}
		return location;
	}

	public IJSONSchemaDocument getSchemaDocument(IJSONNode node)
			throws IOException {
		return SchemaProcessorRegistryReader.getInstance().getSchemaDocument(
				node.getModel());
	}

	public ICatalog getDefaultJSONCatalog() {
		if (catalogSet == null) {
			catalogSet = new CatalogSet();

			defaultCatalogFileStateLocation = getPluginStateLocation(Catalog.DEFAULT_CATALOG_FILE);

			catalogSet.putCatalogPersistenceLocation(DEFAULT_CATALOG_ID,
					defaultCatalogFileStateLocation);
			catalogSet.putCatalogPersistenceLocation(SYSTEM_CATALOG_ID,
					getPluginStateLocation(Catalog.SYSTEM_CATALOG_FILE));
			catalogSet.putCatalogPersistenceLocation(USER_CATALOG_ID,
					getPluginStateLocation(Catalog.USER_CATALOG_FILE));
		}
		return catalogSet.lookupOrCreateCatalog(DEFAULT_CATALOG_ID,
				defaultCatalogFileStateLocation);
	}

	public void clearCatalogCache() {
		if (catalogSet != null) {
			catalogSet.clearResourceCache();
		}
	}
}
