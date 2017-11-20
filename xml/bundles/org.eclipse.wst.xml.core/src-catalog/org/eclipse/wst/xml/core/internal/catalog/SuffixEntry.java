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

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ISuffixEntry;

public class SuffixEntry extends TypedCatalogElement implements ISuffixEntry, Cloneable
{
  String suffix;
  String uri;

  protected SuffixEntry(int anEntryType)
  {
    super(ICatalogElement.TYPE_SUFFIX, anEntryType);
  }

  public void setSuffix(String value)
  {
    suffix = value;
  }

  public String getSuffix()
  {
    return suffix;
  }

  public String getURI()
  {
    return uri;
  }
  
  public void setURI(String value)
  {
    uri = value;
  }

  public Object clone()
  {
    SuffixEntry entry = (SuffixEntry)super.clone();
    entry.setSuffix(suffix);
    entry.setURI(uri);
    return entry;
  }
}
