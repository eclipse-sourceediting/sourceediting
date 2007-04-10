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
 *     
 *******************************************************************************/
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
        // CS : this is a temporary workaround for bug 96772
        //
        // For schemas we always use locations where available and only use
        // namespace when no location is specified.  For XML entities (such as DOCTYPE) 
        // default always utilize the public catalog entry.
        //
        // This lame test below roughly discriminate between schema and XML entities.
        // TODO (bug 103243) remove this lame test once we move to the new URIResolver API
        // since the new API is explicit about namespace and publicId
        // 
        if (!(systemId != null && systemId.endsWith(".xsd"))) //$NON-NLS-1$
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
    }
    return resolved;
  }
}
