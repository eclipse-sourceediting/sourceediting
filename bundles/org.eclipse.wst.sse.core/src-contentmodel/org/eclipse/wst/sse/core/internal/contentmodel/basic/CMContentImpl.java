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
package org.eclipse.wst.sse.core.internal.contentmodel.basic;

import org.eclipse.wst.sse.core.internal.contentmodel.CMContent;


public abstract class CMContentImpl extends CMNodeImpl implements CMContent
{
  protected int minOccur = 0;
  protected int maxOccur = -1;       
                
  // implements CMContent
  //
  public int getMaxOccur()
  {
    return maxOccur;
  }          
 
  public int getMinOccur()
  {
    return minOccur;
  }  
     
  // implementation specific
  //
  public void setMaxOccur(int n)
  {
    maxOccur = n;
  }          
 
  public void setMinOccur(int n)
  {
    minOccur = n;
  }                      
}
