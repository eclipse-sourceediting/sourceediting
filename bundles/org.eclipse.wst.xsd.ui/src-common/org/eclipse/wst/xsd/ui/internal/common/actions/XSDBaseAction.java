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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TopLevelComponentEditPart;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDBaseAction extends BaseSelectionAction
{

  public XSDBaseAction(IWorkbenchPart part)
  {
    super(part);
  }

  protected boolean calculateEnabled()
  {
    if (getWorkbenchPart() instanceof IEditorPart)
    {
      IEditorPart owningEditor = (IEditorPart)getWorkbenchPart();
      
      Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
      if (selection instanceof XSDBaseAdapter)
      {
        selection = ((XSDBaseAdapter) selection).getTarget();
      }
      XSDSchema xsdSchema = null;
      if (selection instanceof XSDConcreteComponent)
      {
        xsdSchema = ((XSDConcreteComponent)selection).getSchema();
      }
      
      if (xsdSchema != null && xsdSchema == owningEditor.getAdapter(XSDSchema.class))
      {
        return true;
      }
    }
    return false;
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
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        Object object = owningEditor.getAdapter(GraphicalViewer.class);
        if (object instanceof AbstractEditPartViewer)
        {
          AbstractEditPartViewer viewer = (AbstractEditPartViewer)object;
          Object obj = viewer.getSelectedEditParts().get(0);
        
          if (obj instanceof TopLevelComponentEditPart)
          {
            TopLevelComponentEditPart editPart = (TopLevelComponentEditPart)obj;
            editPart.setScroll(true);
            editPart.addFeedback();
            editPart.doEditName(!(part instanceof ContentOutline));
          }
          else if (obj instanceof BaseFieldEditPart)
          {
            BaseFieldEditPart editPart = (BaseFieldEditPart)obj;
            editPart.doEditName(!(part instanceof ContentOutline));
          }
        }
      }
      catch (Exception e)
      {
        
      }
    }        

  }
  
}
