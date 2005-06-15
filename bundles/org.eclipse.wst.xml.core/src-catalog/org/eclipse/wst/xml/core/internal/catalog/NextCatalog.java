package org.eclipse.wst.xml.core.internal.catalog;

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;



public class NextCatalog extends CatalogElement implements INextCatalog
{
  String location;

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
    return ((Catalog)ownerCatalog).getCatalogSet().lookupOrCreateCatalog(getId(), getAbsolutePath(location));  
  }

  public void setCatalogLocation(String uri)
  {
    location = uri;
  }
  
  public Object clone()
  {
	NextCatalog nextCatalog = (NextCatalog)super.clone();
	nextCatalog.setCatalogLocation(location);
    return nextCatalog;
  }
  
 
}
