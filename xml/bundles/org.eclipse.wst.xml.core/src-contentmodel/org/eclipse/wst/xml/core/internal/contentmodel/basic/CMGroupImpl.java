/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
