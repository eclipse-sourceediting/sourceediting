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

import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

public class CMGroupImpl extends CMContentImpl implements CMGroup
{
  protected CMNodeList nodeList;                         
  protected int operator;  

  public CMGroupImpl(CMNodeList nodeList, int operator)
  {
    this.nodeList = nodeList;
    this.operator = operator;
  }

  public int getNodeType()
  {
    return GROUP;
  }
 
  public CMNodeList getChildNodes()
  {
    return nodeList;
  }

  public int getOperator()
  {
    return operator;
  } 
}   
