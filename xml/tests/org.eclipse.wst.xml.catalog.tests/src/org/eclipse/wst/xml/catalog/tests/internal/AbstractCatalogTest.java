/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.catalog.tests.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.CatalogContributorRegistryReader;
import org.eclipse.wst.xml.core.internal.catalog.CatalogSet;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.osgi.framework.Bundle;

public abstract class AbstractCatalogTest extends TestCase
{
    private CatalogSet catalogSet = new CatalogSet();
    
    protected ICatalog systemCatalog;

    protected ICatalog userCatalog;

    protected ICatalog defaultCatalog;
    
    
    public AbstractCatalogTest(String name)
    {
        super(name);
        
    }

    protected static List getCatalogEntries(ICatalog catalog, int entryType)
    {
        List result = new ArrayList();
        ICatalogEntry[] entries = catalog.getCatalogEntries();
        for (int i = 0; i < entries.length; i++)
        {
            ICatalogEntry entry = entries[i];
            if (entry.getEntryType() == entryType)
            {
                result.add(entry);
            }
        }
        return result;
    }
    
    protected ICatalog getCatalog(String id, String uriString) throws Exception
    {
		return catalogSet.lookupOrCreateCatalog(id, uriString);
    }
    

    public void initCatalogs()
    {
        defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
        INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
        for (int i = 0; i < nextCatalogs.length; i++)
        {
            INextCatalog catalog = nextCatalogs[i];
            ICatalog referencedCatalog = catalog.getReferencedCatalog();
            if (referencedCatalog != null)
            {
                if (XMLCorePlugin.SYSTEM_CATALOG_ID
                        .equals(referencedCatalog.getId()))
                {
                    systemCatalog = referencedCatalog;
                } else if (XMLCorePlugin.USER_CATALOG_ID
                        .equals(referencedCatalog.getId()))
                {
                    userCatalog = referencedCatalog;
                }
            }
        }
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        initCatalogs();
    }
	
	protected static String makeAbsolute(String baseLocation, String location)
	  {
		  URL local = null;
		  location = location.replace('\\', '/');
		  try
		  {
			  URL baseURL = new URL(baseLocation);
			  local = new URL(baseURL, location);
		  } catch (MalformedURLException e)
		  {
		  }
		  
		  if (local != null)
		  {
			  return local.toString();
		  } else
		  {
			  return location;
		  }
	  }
	
	protected static URL resolvePluginLocation(String pluginId){
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle != null)
		{
			URL bundleEntry = bundle.getEntry("/");
			try
			{
				return FileLocator.resolve(bundleEntry);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	// see CatalogContributorRegistryReader.resolvePath(String path) 
	  protected String resolvePath(String pluginId, String path) 
	  {

		  return  CatalogContributorRegistryReader.resolvePath( 
				  CatalogContributorRegistryReader.getPlatformURL(pluginId), path);
	  }
	  
	  
	  protected String getFileLocation(String path) {
		  String result = null;
		  try {
			result = FileLocator.toFileURL(FileLocator.find(TestPlugin.getDefault().getBundle(), new Path(path), null)).toString();
		} catch (IOException e) {}
		return URIHelper.ensureFileURIProtocolFormat(result);
	  }

}
