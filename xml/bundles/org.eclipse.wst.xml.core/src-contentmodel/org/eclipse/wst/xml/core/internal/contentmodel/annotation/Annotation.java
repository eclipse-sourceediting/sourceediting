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
package org.eclipse.wst.xml.core.internal.contentmodel.annotation;

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
    hashtable.put("spec", spec); //$NON-NLS-1$
  }     

  public String getSpec()
  {
    return (String)hashtable.get("spec"); //$NON-NLS-1$
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
