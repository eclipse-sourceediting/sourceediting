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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.util;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;



public class FTable
{
  public final static String START = "START";

  protected Hashtable hashtable = new Hashtable();

  public void add(CMElementDeclaration ed1, CMElementDeclaration ed2)
  {
    String name = ed1 != null ? ed1.getElementName() : START;
    Vector v = (Vector)hashtable.get(name);
    if (v == null)
    {
      v = new Vector();
      hashtable.put(name, v);
    }
    v.add(ed2);
  }


  public boolean follows(String name1, String name2)
  {
    boolean result = false;
    Vector v = (Vector)hashtable.get(name1);
    if (v != null)
    {
      int size = v.size();
      for (int i = 0; i < size; i++)
      {
        CMElementDeclaration ed = (CMElementDeclaration)v.get(i);
        if (ed.getElementName().equals(name2))
        {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  public List getFollows(String name)
  {
    Vector v = (Vector)hashtable.get(name);
    return v != null ? v : Collections.EMPTY_LIST;
  }
}
