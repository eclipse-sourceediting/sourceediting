/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moeller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IDelegateCatalog;



public class DelegateCatalog extends TypedCatalogElement implements IDelegateCatalog
{
  private String location;
  private ICatalog referencedCatalog;
  private String startString; 

  public String getStartString() 
  {
    return startString;
  }

  public void setStartString(String startString) 
  {
    this.startString = startString;
  }

  public DelegateCatalog(int type)
  {
    super(ICatalogElement.TYPE_DELEGATE, type);
  }

  public String getCatalogLocation()
  {
    return location;
  }

  public ICatalog getReferencedCatalog()
  {
    if (referencedCatalog == null)
    {
      referencedCatalog = ((Catalog)ownerCatalog).getCatalogSet().lookupOrCreateCatalog(getId(), getAbsolutePath(location));
    }
    return referencedCatalog;
  }

  public void setCatalogLocation(String uri)
  {
    location = uri;
    referencedCatalog = null;
  }

  public Object clone()
  {
    DelegateCatalog nextCatalog = (DelegateCatalog)super.clone();
    nextCatalog.setCatalogLocation(location);
    nextCatalog.setStartString(startString);
    return nextCatalog;
  }
}
