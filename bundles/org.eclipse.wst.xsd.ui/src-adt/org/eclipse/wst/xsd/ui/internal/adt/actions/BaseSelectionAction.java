/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;

public abstract class BaseSelectionAction extends SelectionAction
{
  public static final String SEPARATOR_ID = "org.eclipse.jface.action.Separator"; //$NON-NLS-1$
  public static final String SUBMENU_START_ID = "SUBMENU_START_ID: "; //$NON-NLS-1$
  public static final String SUBMENU_END_ID = "SUBMENU_END_ID: "; //$NON-NLS-1$

  protected ISelectionProvider provider;
  protected boolean doDirectEdit = true;
  
  public BaseSelectionAction(IWorkbenchPart part)
  {
    super(part);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.SelectionAction#getSelection()
   */
  protected ISelection getSelection()
  {
    // always get selection from selection provider first
    if (provider!=null)
      return provider.getSelection();
    
    return super.getSelection();
  }
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.SelectionAction#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
   */
  public void setSelectionProvider(ISelectionProvider provider)
  {
    super.setSelectionProvider(provider);
    this.provider = provider;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction#calculateEnabled()
   */
  protected boolean calculateEnabled()
  {
    if (getSelectedObjects().size() > 0)
    {
      Object o = getSelectedObjects().get(0);
      if (o instanceof IComplexType)
      {
        return !((IComplexType)o).isReadOnly();
      }
      else if (o instanceof IField)
      {
        return !((IField)o).isReadOnly();
      }
      else if (o instanceof IGraphElement)
      {
    	return !((IGraphElement)o).isReadOnly();
      }
    }
    return true;
  }
  
  protected void selectAddedComponent(final Adapter adapter)
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        if (adapter != null)
        {
          provider.setSelection(new StructuredSelection(adapter));
          if (doDirectEdit)
            activateDirectEdit();
        }
      }
    };
    Display.getCurrent().asyncExec(runnable);
  }

  protected void activateDirectEdit()
  {
    if (getWorkbenchPart() instanceof IEditorPart)
    {
      try
      {
        IEditorPart owningEditor = (IEditorPart)getWorkbenchPart();
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchPart part = workbench.getActiveWorkbenchWindow().getActivePage().getActivePart();
        Object object = owningEditor.getAdapter(GraphicalViewer.class);
        if (object instanceof AbstractEditPartViewer)
        {
          AbstractEditPartViewer viewer = (AbstractEditPartViewer)object;
          Object obj = viewer.getSelectedEditParts().get(0);
          doEdit(obj, part);
        }
      }
      catch (Exception e)
      {
        
      }
    }        
  }
  
  protected void doEdit(Object obj, IWorkbenchPart part)
  {
    
  }
}
