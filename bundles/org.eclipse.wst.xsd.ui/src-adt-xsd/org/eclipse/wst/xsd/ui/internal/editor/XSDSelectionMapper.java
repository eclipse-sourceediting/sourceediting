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
package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class XSDSelectionMapper implements ISelectionMapper
{
  public ISelection mapSelection(ISelection selection)
  {
    List list = new ArrayList();
    if (selection instanceof IStructuredSelection)
    {  
      IStructuredSelection structuredSelection = (IStructuredSelection)selection;
      for (Iterator i = structuredSelection.iterator(); i.hasNext(); )
      {
        Object o = i.next();
        if (o instanceof Adapter)
        {
          list.add(((Adapter)o).getTarget());
        }  
        else
        {
          list.add(o);
        }  
      }  
    }
    return new StructuredSelection(list);
  }
}
