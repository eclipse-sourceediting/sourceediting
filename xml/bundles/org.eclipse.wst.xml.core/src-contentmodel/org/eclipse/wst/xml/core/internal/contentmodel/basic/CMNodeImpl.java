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
package org.eclipse.wst.xml.core.internal.contentmodel.basic;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;


public abstract class CMNodeImpl implements CMNode
{            
  protected boolean isInferred = false;
   
  public String getNodeName()
  {
    return ""; //$NON-NLS-1$
  }
 
  public boolean supports(String propertyName)
  {
    return false;
  } 

  public Object getProperty(String propertyName)
  {              
    Object result = null;
    if ("isInferred".equals(propertyName)) //$NON-NLS-1$
    {
      result = isInferred ? Boolean.TRUE : Boolean.FALSE;
    }
    return result;
  }    

  public void setInferred(boolean isInferred)
  {
    this.isInferred = isInferred;
  }
}
