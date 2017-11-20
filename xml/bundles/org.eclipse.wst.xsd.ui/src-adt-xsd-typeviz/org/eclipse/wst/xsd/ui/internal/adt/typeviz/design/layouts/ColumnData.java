/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.layouts;

import java.util.HashMap;
import java.util.Iterator;

public class ColumnData
{
  HashMap map = new HashMap();
  
  class Entry
  {
    int width = 0;
    int weight = 1;
  }
  
  public void clearColumnWidths()
  {
    for (Iterator i = map.values().iterator(); i.hasNext();)
    {
      Entry entry = (Entry)i.next();
      entry.width = 0;
    }  
  }  
  
  private Entry lookupOrCreateColumnEntry(String identifier)
  {
    Entry entry = (Entry)map.get(identifier);
    if (entry == null)
    {
      entry = new Entry();
      map.put(identifier, entry);
    }  
   return entry; 
  }  
  
  void stretchColumnWidthIfNeeded(String identifier, int width)
  {
    Entry entry = lookupOrCreateColumnEntry(identifier);
    entry.width = Math.max(entry.width, width);
  }
  
  int getColumnWidth(String identifier)
  {
    Entry entry = (Entry)map.get(identifier);
    if (entry != null)
    {
      return entry.width;
    }  
    else
    {
      return 0;//hmm should we return -1 ?
    }  
  }
  
  int getColumnWeight(String identifier)
  {
    Entry entry = (Entry)map.get(identifier);
    if (entry != null)
    {
      return entry.weight;
    }  
    else
    {
      return 0;
    }  
  }
  
  public void setColumnWeight(String identifier, int weight)
  {
    Entry entry = lookupOrCreateColumnEntry(identifier);
    entry.weight = weight;
  }
}