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
package org.eclipse.wst.xml.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.xml.core.internal.catalog.Catalog;
import org.eclipse.wst.xml.core.internal.catalog.CatalogSet;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class XMLCorePlugin extends Plugin {
	//The shared instance.
	private static XMLCorePlugin plugin;	
    public static final String USER_CATALOG_ID = "user_catalog";
	public static final String DEFAULT_CATALOG_ID = "default_catalog";
	public static final String SYSTEM_CATALOG_ID = "system_catalog";
    private CatalogSet catalogSet = new CatalogSet();
	   

	/**
	 * Returns the shared instance.
	 */
	public static XMLCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * @deprecated use ResourcesPlugin.getWorkspace();
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
	
    private String getPluginStateLocation(String fileName)
    {
      String location = getStateLocation().append(fileName).toString();
      String file_protocol = "file:";
      if (location != null && !location.startsWith(file_protocol))
      {
    	  location = file_protocol + location;
      }                                                          
      return location;
    }
    
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
	    catalogSet.putCatalogPersistenceLocation(DEFAULT_CATALOG_ID, getPluginStateLocation(Catalog.DEFAULT_CATALOG_FILE));
	    catalogSet.putCatalogPersistenceLocation(SYSTEM_CATALOG_ID, getPluginStateLocation(Catalog.SYSTEM_CATALOG_FILE));
	    catalogSet.putCatalogPersistenceLocation(USER_CATALOG_ID, getPluginStateLocation(Catalog.USER_CATALOG_FILE));
	}
	
	public ICatalog getDefaultXMLCatalog()
	{
	    return catalogSet.lookupOrCreateCatalog(DEFAULT_CATALOG_ID, getPluginStateLocation(Catalog.DEFAULT_CATALOG_FILE));
	}

}
