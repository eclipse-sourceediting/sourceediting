/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;

public class ADTContentOutlineProvider implements ITreeContentProvider, IADTObjectListener
{
  protected Viewer viewer = null;
  protected Object oldInput, newInput;
  protected ADTContentOutlineRefreshJob refreshJob;

  public ADTContentOutlineProvider()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  public Object[] getChildren(Object parentElement)
  {
    if (parentElement instanceof ITreeElement)
    {
      Object[] children = ((ITreeElement) parentElement).getChildren();
      if (children != null)
      {
        int length = children.length;
        for (int i = 0; i < length; i++)
        {
          Object child = children[i];
          if (child instanceof IADTObject)
          {
            ((IADTObject) child).registerListener(this);
          }
        }
      }
      return children;
    }
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  public Object getParent(Object element)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  public boolean hasChildren(Object element)
  {
    if (element instanceof ITreeElement)
    {
      return ((ITreeElement) element).hasChildren();
    }
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  public Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  public void dispose()
  {
    Object input = viewer.getInput();
    if (input instanceof IADTObject)
    {
      removeListener((IADTObject) input);
    }
    if (refreshJob != null)
    {
      refreshJob.cancel();
      refreshJob = null;
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    this.viewer = viewer;
    this.oldInput = oldInput;
    this.newInput = newInput;
    if (refreshJob != null)
    {
      refreshJob.cancel();
    }
    refreshJob = new ADTContentOutlineRefreshJob(viewer);
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener#propertyChanged(java.lang.Object, java.lang.String)
   */
  public void propertyChanged(Object object, String property)
  {
    refreshJob.refresh((IADTObject)object);
  }

  /**
   * @param model
   */
  private void removeListener(IADTObject model)
  {
    model.unregisterListener(this);
    Object[] children = null;
    
    if (model instanceof ITreeElement)
    {
      children = ((ITreeElement) model).getChildren();
    }
    
    if (children != null)
    {
      int length = children.length;
      for (int i = 0; i < length; i++)
      {
        Object child = children[i];
        if (child instanceof IADTObject && model != child)
        {
          removeListener((IADTObject) child);
        }
      }
    }
  }

}
