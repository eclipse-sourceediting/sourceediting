/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.catalog.XMLCatalogURIResolverExtension
 *                                           modified in order to process JSON Objects.                 
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.JSONCoreMessages;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.schema.catalog.ICatalog;

/**
 * This class is used to inject the JSONCatalog resolution behaviour into the
 * Common Extensible URI Resolver. This class is referenced in the JSON Catalog
 * plugin's plugin.xml file.
 */
public class JSONCatalogURIResolverExtension implements URIResolverExtension
{
  public String resolve(IFile file, String baseLocation, String publicId, String systemId)
  {
    // if we have catalog in a project we may add it
    // to the catalog manager first
    ICatalog catalog = JSONCorePlugin.getDefault().getDefaultJSONCatalog();
    if (catalog == null)
    {
      Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_null_catalog);
      return null;
    }
    if (publicId != null || systemId != null) {
    	return null;
    }
    try
    {
      return catalog.resolveSchema(file.getName());      
    }
    catch (MalformedURLException me)
    {
      Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_malformed_url);
      return null;
    }
    catch (IOException ie)
    {
      Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_io_exception);
      return null;
    }
    
//    String resolved = null;
//    if (systemId != null)
//    {
//      
//    }
//    if (resolved == null)
//    {
//      if (publicId != null)
//      {
//        // CS : this is a temporary workaround for bug 96772
//        //
//        // For schemas we always use locations where available and only use
//        // namespace when no location is specified.  For JSON entities (such as DOCTYPE) 
//        // default always utilize the public catalog entry.
//        //
//        // This lame test below roughly discriminate between schema and JSON entities.
//        // TODO (bug 103243) remove this lame test once we move to the new URIResolver API
//        // since the new API is explicit about namespace and publicId
//        // 
//        if (!(systemId != null && systemId.endsWith(".xsd"))) //$NON-NLS-1$
//        {
//          try
//          {
//            resolved = catalog.resolveSchema(publicId, systemId);
//            if (resolved == null)
//            {
//              resolved = catalog.resolveURI(publicId);
//            }
//          }
//          catch (MalformedURLException me)
//          {
//            Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_malformed_url);
//            resolved = null;
//          }
//          catch (IOException ie)
//          {
//            Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_io_exception);
//            resolved = null;
//          }
//        }
//      }
//    }
//    return resolved;
  }
}
