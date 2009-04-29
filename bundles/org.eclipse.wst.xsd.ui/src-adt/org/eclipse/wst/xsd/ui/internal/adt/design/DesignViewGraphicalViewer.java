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
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.RedefineCategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDRedefineAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.IHolderEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.StructureEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
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
  protected InputChangeManager inputChangeManager = new InputChangeManager();
  private IEditorPart editorPart;

  public DesignViewGraphicalViewer(IEditorPart editor, CommonSelectionManager manager)
  {
    super();
    this.editorPart = editor;
    setContextMenu(new DesignViewContextMenuProvider(editor, this, this));
    editor.getEditorSite().registerContextMenu("org.eclipse.wst.xsd.ui.popup.graph", getContextMenu(), internalSelectionProvider, false); //$NON-NLS-1$
    
    // make the internalSelectionProvider listen to graph view selection changes
    addSelectionChangedListener(internalSelectionProvider);    
    internalSelectionProvider.addSelectionChangedListener(manager);
    manager.addSelectionChangedListener(this);  
    
    setKeyHandler(new BaseGraphicalViewerKeyHandler(this));    
  }
  
  
  // this method is called when something changes in the selection manager
  // (e.g. a selection occured from another view)
  public void selectionChanged(SelectionChangedEvent event)
  {
    Object selectedObject = null;
    ISelection eventSelection = event.getSelection();
    if (eventSelection instanceof StructuredSelection)
    {
    	selectedObject = ((StructuredSelection) eventSelection).getFirstElement(); 
    }    
    // TODO (cs) It seems like there's way more selection going on than there
    // should
    // be!! There's at least 2 selections getting fired when something is
    // selected in the
    // outline view. Are we listening to too many things?
    //
    // if (event.getSource() instanceof ADTContentOutlinePage)
    if (selectedObject != null)
    {
    if (event.getSource() != internalSelectionProvider)
    {
      if (selectedObject instanceof IStructure)
      {
        if (((getInput() instanceof IModel) && (event.getSource() instanceof ADTContentOutlinePage)) ||
            (!(getInput() instanceof IModel)))
        {
          if ((selectedObject instanceof IGraphElement) && ((IGraphElement)selectedObject).isFocusAllowed()) 
          {
            if (!(event.getSource() instanceof org.eclipse.jface.viewers.IPostSelectionProvider))            
            {
              setInputAndMarkLocation((IStructure)selectedObject);
            }
          }
        }
      }
      else if (selectedObject instanceof IGraphElement)
      {
        if (((IGraphElement)selectedObject).isFocusAllowed() && ((event.getSource() instanceof ADTContentOutlinePage)))
        {
          setInputAndMarkLocation((IADTObject)selectedObject);              
        }
        else if (!((IGraphElement)selectedObject).isFocusAllowed())
        {
          // We encountered an object that is not a valid input to the graph viewer
          // Now find the top container that can be a valid input
          IADTObject obj = ((IGraphElement)selectedObject).getTopContainer();
          if (event.getSource() instanceof ADTContentOutlinePage)
          {
            // In this case, if the selection is originated from the outline, we should 
            // change the inputs
            if (obj != null && getInput() != obj)  // Don't change inputs if the obj is already the input
              setInputAndMarkLocation(obj);
          }
          else if (event.getSource() instanceof CommonSelectionManager)
          {
            // In this case, if the selection is originated from some action, ie. adding
            // a new element, we should change the input only if the current input is the model
            // otherwise, inputs will change unexpectedly!!
            if (getInput() instanceof IModel) 
            {
              if (obj != null)
                setInput (obj);
            }
          }
          else if (event.getSource() instanceof org.eclipse.jface.viewers.IPostSelectionProvider )
          {
            // In this case, if the selection is originated from the source viewer
            // we should change the input regardless.  Test is for multiple levels
            // of anonymous types
            if (obj != null)
              setInput (obj);
          }
          else
          {
            if (obj != null && getInput() instanceof IModel)
              setInputAndMarkLocation(obj);
          }
        }
        if (selectedObject instanceof IField) 
        {
          IField field = (IField)selectedObject;
          if ( (!field.isGlobal() && getInput() instanceof IModel) ||
               (!field.isGlobal() && !(event.getSource() instanceof CommonSelectionManager)))
          {
            IADTObject obj = ((IGraphElement)selectedObject).getTopContainer();
            if (obj != null)
              setInputAndMarkLocation(obj);
          }
          else if (field.isGlobal() && !(getInput() instanceof IModel))
          {
            if (event.getSource() instanceof org.eclipse.jface.viewers.IPostSelectionProvider )
              setInput(field);
            else
              setInputAndMarkLocation(field);
          }
        }
      }
      else if (selectedObject instanceof IField)
      {
        IField field = (IField)selectedObject;
        if ( (field.isGlobal() && (getInput() instanceof IModel) && (event.getSource() instanceof ADTContentOutlinePage)) ||
            ( (field.isGlobal() && !(getInput() instanceof IModel))))
        {  
          setInputAndMarkLocation(field);
        }
      }
      else if (selectedObject instanceof IModelProxy)
      {
        IModelProxy adapter = (IModelProxy)selectedObject;        
        if (selectedObject instanceof RedefineCategoryAdapter)
        {
        	RedefineCategoryAdapter selectionAdapter = (RedefineCategoryAdapter)selectedObject;
        	XSDRedefineAdapter selectionParentAdapter = selectionAdapter.getXsdRedefineAdapter(); 
        	setInputAndMarkLocation(selectionParentAdapter);
        }
        else if (getInput() != adapter.getModel())
           setInput(adapter.getModel());
      }
      else if (selectedObject instanceof IModel)
      {
        if (getInput() != selectedObject)
          setInput((IModel)selectedObject);
      }
      
      EditPart editPart = getEditPart(getRootEditPart(), selectedObject);
      if (editPart != null)
      {
        setSelection(new StructuredSelection(editPart));
        setFocus(editPart);
      }
    }
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
      if (editPart.getModel() == object && !(editPart instanceof IHolderEditPart) && !(editPart instanceof RootContentEditPart))
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
          // First check to see if there is a selection
          ISelection currentSelection = getSelection();
          
          // If there is a selection then we will try to select 
          // the target edit part that is one of its children
          // This is handy when you add an element to a structured edit part
          // then you want to select the element immediately and put it in
          // direct edit mode
          if (currentSelection != null)
          {
            if (currentSelection instanceof StructuredSelection)
            {
              EditPart targetStructureEditPart = (EditPart)((StructuredSelection)currentSelection).getFirstElement();
              if (targetStructureEditPart != null)
              {
                while (targetStructureEditPart != null)
                {
                  if (targetStructureEditPart instanceof StructureEditPart)
                  {
                    break;
                  }
                  targetStructureEditPart = targetStructureEditPart.getParent();
                }
              }
              EditPart potentialEditPartToSelect = editPart;
              
              while (potentialEditPartToSelect != null)
              {
                if (potentialEditPartToSelect instanceof StructureEditPart)
                {
                  break;
                }
                potentialEditPartToSelect = potentialEditPartToSelect.getParent();
              }
              
              // If we found a potential edit part to select then return it
              // OR, if there is no target found, then we should just return
              // the edit part we found
              if (potentialEditPartToSelect == targetStructureEditPart || potentialEditPartToSelect == null || targetStructureEditPart == null)
              {
                result = editPart;
                break;
              }
            }
          }
          else // Otherwise just find the first one and return
          {
            result = editPart;
            break;
          }
        }
      }            
    }
  
    return result;
  }
  
  public void setInputAndMarkLocation(IADTObject object)
  {
    IADTObject oldInput = getInput();
    INavigationHistory navigationHistory = null;
    IWorkbench workbench = PlatformUI.getWorkbench();
    if(workbench != null)
    {
    	IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
    	if(activeWorkbenchWindow != null)
    	{
    		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
    		if(activePage != null)
    		{
    			navigationHistory = activePage.getNavigationHistory();
    		}
    	}
    }

    setInput(object);

    if (editorPart != null && oldInput != object)
    {
      navigationHistory.markLocation(editorPart);
    }
  }
  
  public void setInput(IADTObject object)
  {
    RootContentEditPart rootContentEditPart = (RootContentEditPart)getRootEditPart().getContents();
    rootContentEditPart.setModel(object);
    rootContentEditPart.refresh();

    if (object != null)
    {  
      inputChangeManager.setSelection(new StructuredSelection(object));
    }
    // Select the editpart when it is set as input
    EditPart editPart = getEditPart(rootContentEditPart, object);
    if (editPart != null)
      select(editPart);
  }
  
  public IADTObject getInput()
  {
    RootContentEditPart rootContentEditPart = (RootContentEditPart)getRootEditPart().getContents();    
    return (IADTObject)rootContentEditPart.getModel();
  }
  
  public EditPart getInputEditPart()
  {
    return getRootEditPart().getContents();    
  }
  
  public void addInputChangdListener(ISelectionChangedListener listener)
  {
    inputChangeManager.addSelectionChangedListener(listener);
  }
  
  public void removeInputChangdListener(ISelectionChangedListener listener)
  {
    inputChangeManager.removeSelectionChangedListener(listener);    
  }  
  
  
  private class InputChangeManager implements ISelectionProvider
  {
    List listeners = new ArrayList();
       
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
      if (!listeners.contains(listener))
      {  
        listeners.add(listener);
      }        
    }

    public ISelection getSelection()
    {   
      // no one should be calling this method     
      return null;
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
      listeners.remove(listener);      
    }

    public void setSelection(ISelection selection)
    { 
      notifyListeners(selection);
    }

    void notifyListeners(ISelection selection)
    {
      List list = new ArrayList(listeners);
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        ISelectionChangedListener listener = (ISelectionChangedListener)i.next();
        listener.selectionChanged(new SelectionChangedEvent(this, selection));
      }  
    }       
  }
}
