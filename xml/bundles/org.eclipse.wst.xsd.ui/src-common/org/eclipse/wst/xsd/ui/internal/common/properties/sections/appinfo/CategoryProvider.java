/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;

public class CategoryProvider
{
  private ICatalog systemCatalog;

  public CategoryProvider()
  {
    
  }
  
  /**
   * Extenders should implement and return a list of 
   * SpecificationForExtensionsSchema
   * @return
   */
  public List getCategories()
  {
    return new ArrayList();
  }
  
  /**
   * Helper method to find the physical location of the schema
   * in the XML Catalog
   * @param namespaceURI
   * @return physical location of the schema
   */
  public String locateFileUsingCatalog(String namespaceURI)
  {
    retrieveCatalog();

    ICatalogEntry[] entries = systemCatalog.getCatalogEntries();
    for (int i = 0; i < entries.length; i++)
    {
      if (entries[i].getKey().equals(namespaceURI))
        return entries[i].getURI();
    }

    return null;
  }

  private void retrieveCatalog()
  {
    if (systemCatalog != null)
      return;

    ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
    for (int i = 0; i < nextCatalogs.length; i++)
    {
      INextCatalog catalog = nextCatalogs[i];
      ICatalog referencedCatalog = catalog.getReferencedCatalog();
      if (referencedCatalog != null)
      {
        if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId()))
        {
          systemCatalog = referencedCatalog;
        }
      }
    }
  }

}
