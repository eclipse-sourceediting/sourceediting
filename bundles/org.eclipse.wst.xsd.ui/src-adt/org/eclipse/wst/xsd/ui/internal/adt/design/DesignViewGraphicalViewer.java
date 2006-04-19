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
package org.eclipse.wst.xsd.ui.internal.adt.design;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IModelProxy;
import org.eclipse.wst.xsd.ui.internal.adt.editor.CommonSelectionManager;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlinePage;

public class DesignViewGraphicalViewer extends ScrollingGraphicalViewer implements ISelectionChangedListener
{
  protected ADTSelectionChangedListener internalSelectionProvider = new ADTSelectionChangedListener();

  public DesignViewGraphicalViewer(IEditorPart editor, CommonSelectionManager manager)
  {
    super();
    setContextMenu(new DesignViewContextMenuProvider(editor, this, this));
    editor.getEditorSite().registerContextMenu("org.eclipse.wst.xsd.ui.popup.graph", getContextMenu(), internalSelectionProvider, false); //$NON-NLS-1$
    
    // make the internalSelectionProvider listen to graph view selection changes
    addSelectionChangedListener(internalSelectionProvider);    
    internalSelectionProvider.addSelectionChangedListener(manager);
    manager.addSelectionChangedListener(this);
  }
  
  // this method is called when something changes in the selection manager
  // (e.g. a selection occured from another view)
  public void selectionChanged(SelectionChangedEvent event)
  {
    Object selectedObject = ((StructuredSelection) event.getSelection()).getFirstElement();
    
    // TODO (cs) It seems like there's way more selection going on than there should
    // be!!  There's at least 2 selections getting fired when something is selected in the
    // outline view.  Are we listening to too many things?
    //
    //if (event.getSource() instanceof ADTContentOutlinePage)
    if (event.getSource() != internalSelectionProvider)
    {
      if (selectedObject instanceof IStructure)
      {
        if (((getInput() instanceof IModel) && (event.getSource() instanceof ADTContentOutlinePage)) ||
            (!(getInput() instanceof IModel)))
        {
          ((RootContentEditPart) getContents()).setInput(selectedObject);
        }
      }
      else if (selectedObject instanceof IField)
      {
        IField field = (IField)selectedObject;
        if ( (field.isGlobal() && (getInput() instanceof IModel) && (event.getSource() instanceof ADTContentOutlinePage)) ||
            ( (field.isGlobal() && !(getInput() instanceof IModel))))
        {  
          ((RootContentEditPart) getContents()).setInput(selectedObject);
        }
      }
      else if (selectedObject instanceof IModelProxy)
      {
        IModelProxy adapter = (IModelProxy)selectedObject;
        if (((RootContentEditPart) getContents()).getInput() != adapter.getModel())
        ((RootContentEditPart) getContents()).setInput(adapter.getModel());
      }
      else if (selectedObject instanceof IModel)
      {
        if (((RootContentEditPart) getContents()).getInput() != selectedObject)
          ((RootContentEditPart) getContents()).setInput(selectedObject);
      }
      
      EditPart editPart = getEditPart(getRootEditPart(), selectedObject);
      if (editPart != null)
        setSelection(new StructuredSelection(editPart));
    }
  }
  
  /*
   * We need to convert from edit part selections to model object selections
   */
  class ADTSelectionChangedListener implements ISelectionProvider, ISelectionChangedListener
  {
    protected List listenerList = new ArrayList();
    protected ISelection selection = new StructuredSelection();

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

    protected void notifyListeners(SelectionChangedEvent event)
    {
      for (Iterator i = listenerList.iterator(); i.hasNext();)
      {
        ISelectionChangedListener listener = (ISelectionChangedListener) i.next();
        listener.selectionChanged(event);
      }
    }

    public StructuredSelection convertSelectionFromEditPartToModel(ISelection editPartSelection)
    {
      List selectedModelObjectList = new ArrayList();
      if (editPartSelection instanceof IStructuredSelection)
      {
        for (Iterator i = ((IStructuredSelection) editPartSelection).iterator(); i.hasNext();)
        {
          Object obj = i.next();
          Object model = null;
          if (obj instanceof EditPart)
          {
            EditPart editPart = (EditPart) obj;
            model = editPart.getModel();
          }
          if (model != null)
          {
            selectedModelObjectList.add(model);
          }
        }
      }
      return new StructuredSelection(selectedModelObjectList);
    }

    public void setSelection(ISelection selection)
    {
      this.selection = selection;
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
      ISelection newSelection = convertSelectionFromEditPartToModel(event.getSelection());
      this.selection = newSelection;
      SelectionChangedEvent newEvent = new SelectionChangedEvent(this, newSelection);
      notifyListeners(newEvent);
    }
  }
  
  protected EditPart getEditPart(EditPart parent, Object object)
  {
    EditPart result = null;
    for (Iterator i = parent.getChildren().iterator(); i.hasNext(); )
    {
      EditPart editPart = (EditPart)i.next();
      if (editPart.getModel() == object)
      {  
        result = editPart;
        break;
      }
    }             
  
    if (result == null)
    { 
      for (Iterator i = parent.getChildren().iterator(); i.hasNext(); )
      {
        EditPart editPart = getEditPart((EditPart)i.next(), object);
        if (editPart != null)
        {
          result = editPart;
          break;
        }
      }            
    }
  
    return result;
  }
  
  public void setInput(IADTObject object)
  {
    ((RootContentEditPart)getRootEditPart().getContents()).setInput(object);
  }
  
  public IADTObject getInput()
  {
    return (IADTObject)((RootContentEditPart)getRootEditPart().getContents()).getInput();
  }
}
