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
 *     Raj Mandayam, IBM
 *           136400 NextCatalog.getReferencedCatalog() takes a lot of time computing constant information
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;



public class NextCatalog extends CatalogElement implements INextCatalog
{
  private String location;
  private ICatalog referencedCatalog;

  public NextCatalog()
  {
    super(ICatalogElement.TYPE_NEXT_CATALOG);
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
	NextCatalog nextCatalog = (NextCatalog)super.clone();
	nextCatalog.setCatalogLocation(location);
    return nextCatalog;
  }
}
