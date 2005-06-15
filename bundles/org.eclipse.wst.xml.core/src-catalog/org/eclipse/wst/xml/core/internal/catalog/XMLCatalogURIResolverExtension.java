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
package org.eclipse.wst.xml.core.internal.catalog;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;


/**
 * This class is used to inject the XMLCatalog resolution behaviour into the
 * Common Extensible URI Resolver. This class is referenced in the XML Catalog
 * plugin's plugin.xml file.
 */
public class XMLCatalogURIResolverExtension implements URIResolverExtension
{
  public String resolve(IFile file, String baseLocation, String publicId, String systemId)
  {
    // if we have catalog in a project we may add it
    // to the catalog manager first
    ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    if (catalog == null)
    {
      Logger.log(Logger.ERROR_DEBUG, XMLCoreMessages.Catalog_resolution_null_catalog);
      return null;
    }
    String resolved = null;
    if (systemId != null)
    {
      try
      {
        resolved = catalog.resolveSystem(systemId);
        if (resolved == null)
        {
          resolved = catalog.resolveURI(systemId);
        }
      }
      catch (MalformedURLException me)
      {
        Logger.log(Logger.ERROR_DEBUG, XMLCoreMessages.Catalog_resolution_malformed_url);
        resolved = null;
      }
      catch (IOException ie)
      {
        Logger.log(Logger.ERROR_DEBUG, XMLCoreMessages.Catalog_resolution_io_exception);
        resolved = null;
      }
    }
    if (resolved == null)
    {
      if (publicId != null)
      {
        try
        {
          resolved = catalog.resolvePublic(publicId, systemId);
          if (resolved == null)
          {
            resolved = catalog.resolveURI(publicId);
          }
        }
        catch (MalformedURLException me)
        {
          Logger.log(Logger.ERROR_DEBUG, XMLCoreMessages.Catalog_resolution_malformed_url);
          resolved = null;
        }
        catch (IOException ie)
        {
          Logger.log(Logger.ERROR_DEBUG, XMLCoreMessages.Catalog_resolution_io_exception);
          resolved = null;
        }
      }
    }
    return resolved;
  }
}
