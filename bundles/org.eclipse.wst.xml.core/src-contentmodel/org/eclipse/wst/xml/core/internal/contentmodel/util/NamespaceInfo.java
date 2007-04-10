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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class NamespaceInfo
{                        
  public String uri;
  public String prefix;
  public String locationHint;
  public boolean isPrefixRequired; 
  protected Hashtable hashtable;

  public NamespaceInfo()
  {
  }

  public NamespaceInfo(String uri, String prefix, String locationHint)
  {                                                                  
    this.uri = uri;                             
    this.prefix = prefix;
    this.locationHint = locationHint;
  }   

  public NamespaceInfo(NamespaceInfo that)
  {                                               
    this.uri = that.uri;                             
    this.prefix = that.prefix;
    this.locationHint = that.locationHint;
    // todo... see if we need to clone the hastable 
  }

  public void normalize()
  {
    uri = getNormalizedValue(uri);
    prefix  = getNormalizedValue(prefix);
    locationHint= getNormalizedValue(locationHint);
  }

  protected String getNormalizedValue(String string)
  {
    return (string != null && string.trim().length() == 0) ? null : string;
  }
    
  public void setProperty(String property, Object value)
  {
    if (hashtable == null)
    {
      hashtable = new Hashtable();
    }
    hashtable.put(property, value);
  }

  public Object getProperty(String property)
  {
    return (hashtable != null) ? hashtable.get(property) : null;
  }

  public static List cloneNamespaceInfoList(List oldList)
  {  
    List newList = new Vector(oldList.size());
    for (Iterator i = oldList.iterator(); i.hasNext(); )
    {
      NamespaceInfo oldInfo = (NamespaceInfo)i.next();
      newList.add(new NamespaceInfo(oldInfo));
    }               
    return newList;
  }
}      
