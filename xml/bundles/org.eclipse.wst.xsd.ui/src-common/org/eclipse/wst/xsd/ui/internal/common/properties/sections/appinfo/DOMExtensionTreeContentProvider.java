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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMExtensionTreeContentProvider implements ITreeContentProvider, INodeAdapter
{
  protected String facet;
  protected Viewer viewer;
  
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    this.viewer = viewer;
  }
    
  public Object[] getChildren(Object parentElement)
  {
    if (parentElement instanceof Element)
    {
      Element element = (Element)parentElement;        
      ArrayList list = new ArrayList();
      for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling())
      {
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          list.add(node);
        }  
      }  
      return list.toArray();      
    }
    return Collections.EMPTY_LIST.toArray();
  }
  
  public boolean hasChildren(Object element)
  {
    Object[] children = getChildren(element);
    return children.length > 0;
  }
  
  public Object getParent(Object element)
  {
    return null;
  }

  public java.lang.Object[] getElements(java.lang.Object inputElement)
  {
    return getChildren(inputElement);
  }

  public void dispose()
  {
  }
  
  public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
  {
    if (viewer != null)
    {  
      viewer.refresh();
    }  
  }
  
  public boolean isAdapterForType(Object type)
  {
    // this method is not used
    return false;
  }
}