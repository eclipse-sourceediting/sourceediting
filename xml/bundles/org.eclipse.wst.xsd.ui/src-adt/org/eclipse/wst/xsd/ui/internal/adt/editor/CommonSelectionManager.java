/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;

public class CommonSelectionManager extends MultiPageSelectionProvider implements ISelectionProvider, ISelectionChangedListener
{

  public CommonSelectionManager(MultiPageEditorPart multiPageEditor)
  {
    super(multiPageEditor);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    listenerList.add(listener);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
   */
  public ISelection getSelection()
  {
    return currentSelection;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    listenerList.remove(listener);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
   */
  public void setSelection(ISelection selection)
  {
    setSelection(selection, this);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
   */
  public void selectionChanged(SelectionChangedEvent event)
  {
    if (enableNotify)
    {
      setSelection(event.getSelection(), event.getSelectionProvider());
    }
  }

  
  protected List listenerList = new ArrayList();
  protected ISelection currentSelection;
  protected boolean enableNotify = true;

  public boolean getEnableNotify()
  {
    return enableNotify;
  }
  
  public void setSelection(ISelection selection, ISelectionProvider source)
  {  
    if (enableNotify)
    {
      currentSelection = selection;
      enableNotify = false;
      try
      {
        SelectionChangedEvent event = new SelectionChangedEvent(source, selection);
        List copyOfListenerList = new ArrayList(listenerList);
        for (Iterator i = copyOfListenerList.iterator(); i.hasNext(); )
        {
          ISelectionChangedListener listener = (ISelectionChangedListener)i.next();
          listener.selectionChanged(event);
        }
        // Bug 338126 Properties view not synced with XSD and WSDL design views
        // PropertySheet was changed to be a post selection listener
        // We need to fire off a post selection change event in order for the properties view to update.
        IEditorSite site = getMultiPageEditor().getEditorSite();
        if (site != null)
        {
          ISelectionProvider selectionProvider = site.getSelectionProvider();
          if (selectionProvider instanceof MultiPageSelectionProvider)
          {
            ((MultiPageSelectionProvider)selectionProvider).firePostSelectionChanged(event);
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        enableNotify = true;
      }
    }
  }
}
