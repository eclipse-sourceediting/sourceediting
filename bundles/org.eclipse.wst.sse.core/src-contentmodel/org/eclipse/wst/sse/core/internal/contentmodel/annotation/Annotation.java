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
package org.eclipse.wst.sse.core.internal.contentmodel.annotation;

import java.util.Hashtable;
import java.util.List;
         
/**
 * 
 */
public class Annotation
{       
  protected Hashtable hashtable = new Hashtable();                     

  public Annotation()
  {
  }

  public void setSpec(String spec)
  {
    hashtable.put("spec", spec);
  }     

  public String getSpec()
  {
    return (String)hashtable.get("spec");
  }
      
  public void setProperty(String name, String value)
  {                             
    hashtable.put(name, value);
  }

  public String getProperty(String propertyName)
  {     
    return (String)hashtable.get(propertyName);
  }    

  public List getAttributeList()
  {
    return null; // todo
  }
}
