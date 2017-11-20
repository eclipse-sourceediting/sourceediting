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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;


public class CMNodeListImpl implements CMNodeList 
{
  public static CMNodeListImpl EMPTY_NODE_LIST = new CMNodeListImpl(Collections.EMPTY_LIST);
  protected List list;

  public CMNodeListImpl()
  {
    this(new Vector());
  }

  public CMNodeListImpl(List list)
  {
    this.list = list;
  }

  /**
   * getLength method
   * @return int
   */
  public int getLength()
  {
    return list.size();
  }
  /**
   * item method
   * @return CMNode
   * @param index int
   */
  public CMNode item(int index)
  {
    return (CMNode)list.get(index);
  }
  
  public List getList()
  {
    return list;
  }
  
  public Iterator iterator()
  {
    return list.iterator();
  }
  
  public boolean contains(CMNode cmNode)
  {
    return list.contains(cmNode);
  } 
  
  public void add(CMNode cmNode)
  {
    list.add(cmNode);
  }  
}

