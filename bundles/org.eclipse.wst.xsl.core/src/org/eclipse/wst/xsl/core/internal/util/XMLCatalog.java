/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - bug 258937 - initial API and implementation
 *                         - bug 287499 - add XML Catalog retrieval.
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.CatalogSet;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;

/**
 * This class provides convience methods for reading from the WTP
 * XML Catalog implementation.
 * 
 * @since 1.1
 */
public class XMLCatalog {
    
    protected ICatalog systemCatalog;
    
	protected ICatalog userCatalog;

    protected ICatalog defaultCatalog;
    
    public XMLCatalog() {
    	initXMLCatalog();
    }
    
	private void initXMLCatalog() {
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
	
	/**
	 * Checks if the provided uri string exists in the catalog.
	 * @param uri
	 * @return true if it exists, false if not.
	 */
	public boolean exists(String uri) {
		try {
			if (defaultCatalog.resolveURI(uri) != null ||
			    systemCatalog.resolveURI(uri) != null ||
			    userCatalog.resolveURI(uri) != null) {
				return true;
			}
		} catch (IOException ex) {
			
		}
		return false;
	}
    

    public ICatalog getSystemCatalog() {
		return systemCatalog;
	}

	public ICatalog getUserCatalog() {
		return userCatalog;
	}

	public ICatalog getDefaultCatalog() {
		return defaultCatalog;
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
	
	public String resolve(String uri) {
		String resolvedURI = null;
		try {
			resolvedURI = defaultCatalog.resolveURI(uri);
			if (resolvedURI != null) {
				return resolvedURI;
			}
			
			resolvedURI = systemCatalog.resolveURI(uri);
			
			if (resolvedURI != null) {
				return resolvedURI;
			}
			resolvedURI =  userCatalog.resolveURI(uri);
		} catch (IOException ex) {
			
		}
		return resolvedURI;
	}
	
}
