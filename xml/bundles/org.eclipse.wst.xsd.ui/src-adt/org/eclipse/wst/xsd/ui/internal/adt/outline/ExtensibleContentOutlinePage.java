/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTMultiPageEditor;

public class ExtensibleContentOutlinePage extends Page implements IContentOutlinePage, ISelectionChangedListener
{
  protected ListenerList selectionChangedListeners = new ListenerList();
  protected TreeViewer treeViewer;
  protected Object model;
  protected ITreeContentProvider contentProvider;
  protected ILabelProvider labelProvider;
  protected ADTMultiPageEditor editor;

  protected ExtensibleContentOutlinePage()
  {
    super();
  }

  public void createControl(Composite parent)
  {
    treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
    treeViewer.addSelectionChangedListener(this);
  }

  public void setEditor(ADTMultiPageEditor editor)
  {
    this.editor = editor;
  }
  
  public void setModel(Object newModel)
  {
    model = newModel;
  }

  public void setContentProvider(ITreeContentProvider contentProvider)
  {
    this.contentProvider = contentProvider;
  }

  public void setLabelProvider(ILabelProvider labelProvider)
  {
    this.labelProvider = labelProvider;
  }

  public void setFocus()
  {
    treeViewer.getControl().setFocus();
  }

  public void setSelection(ISelection selection)
  {
    if (treeViewer != null)
    {
      treeViewer.setSelection(selection);
    }
  }

  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.add(listener);
  }

  protected void fireSelectionChanged(ISelection selection)
  {
    // create an event
    final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);

    // fire the event
    Object[] listeners = selectionChangedListeners.getListeners();
    for (int i = 0; i < listeners.length; ++i)
    {
      final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
      Platform.run(new SafeRunnable()
      {
        public void run()
        {
          l.selectionChanged(event);
        }
      });
    }
  }

  public Control getControl()
  {
    if (treeViewer == null)
    {
      return null;
    }
    return treeViewer.getControl();
  }

  public ISelection getSelection()
  {
    if (treeViewer == null)
    {
      return StructuredSelection.EMPTY;
    }
    return treeViewer.getSelection();
  }

  public TreeViewer getTreeViewer()
  {
    return treeViewer;
  }

  public void init(IPageSite pageSite)
  {
    super.init(pageSite);
    pageSite.setSelectionProvider(this);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.remove(listener);
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    fireSelectionChanged(event.getSelection());
  }
}
