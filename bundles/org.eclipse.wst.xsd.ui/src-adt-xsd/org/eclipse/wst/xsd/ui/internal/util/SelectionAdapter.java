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
package org.eclipse.wst.xsd.ui.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public abstract class SelectionAdapter implements ISelectionProvider
{
  protected List listenerList = new ArrayList();
  protected ISelection selection = new StructuredSelection();
  protected ISelectionProvider eventSource;

  public void setEventSource(ISelectionProvider eventSource)
  {
    this.eventSource = eventSource;
  }

  public void addSelectionChangedListener(ISelectionChangedListener listener) 
  {
    listenerList.add(listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener listener) 
  {
    listenerList.remove(listener);
  }                    

  public ISelection getSelection() 
  {
    return selection;
  }    
  
  /**
   * This method should be specialized to return the correct object that corresponds to the 'other' model
   */
  abstract protected Object getObjectForOtherModel(Object object);

    
  public void setSelection(ISelection modelSelection)  
  { 
    List otherModelObjectList = new ArrayList();
    if (modelSelection instanceof IStructuredSelection)
    {
      for (Iterator i = ((IStructuredSelection)modelSelection).iterator(); i.hasNext(); )
      {
        Object modelObject = i.next(); 
        Object otherModelObject = getObjectForOtherModel(modelObject);       
        if (otherModelObject != null)
        { 
          otherModelObjectList.add(otherModelObject);
        }
      }
    }                
      
    StructuredSelection nodeSelection = new StructuredSelection(otherModelObjectList);
    selection = nodeSelection;
    SelectionChangedEvent event = new SelectionChangedEvent(eventSource != null ? eventSource : this, nodeSelection);

    for (Iterator i = listenerList.iterator(); i.hasNext(); )
    {
      ISelectionChangedListener listener = (ISelectionChangedListener)i.next();
      listener.selectionChanged(event);
    }
  }

}
