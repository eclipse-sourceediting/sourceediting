/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;

import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.RedefineCategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDRedefineAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSimpleTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewContextMenuProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IModelProxy;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class ADTContentOutlinePage extends ExtensibleContentOutlinePage
{
  protected int level = 0;
  protected MultiPageSelectionProvider selectionManager;
  protected SelectionManagerSelectionChangeListener selectionManagerSelectionChangeListener = new SelectionManagerSelectionChangeListener();

  public ADTContentOutlinePage()
  {
    super();
  }

  public void createControl(Composite parent)
  {
    super.createControl(parent);
    getTreeViewer().setContentProvider(contentProvider);
    getTreeViewer().setLabelProvider(labelProvider);
    getTreeViewer().setInput(model);
    getTreeViewer().addSelectionChangedListener(this);
    MenuManager menuManager = new MenuManager("#popup");//$NON-NLS-1$
    menuManager.setRemoveAllWhenShown(true);
    Menu menu = menuManager.createContextMenu(getTreeViewer().getControl());
    getTreeViewer().getControl().setMenu(menu);
    setSelectionManager(editor.getSelectionManager());
    
    // Create menu...for now reuse graph's.  Note edit part viewer = null
    DesignViewContextMenuProvider menuProvider = new DesignViewContextMenuProvider(editor, null, editor.getSelectionManager());
    menuManager.addMenuListener(menuProvider);
    getSite().registerContextMenu("org.eclipse.wst.xsd.ui.popup.outline", menuManager, editor.getSelectionManager()); //$NON-NLS-1$

    // enable popupMenus extension
    // getSite().registerContextMenu("org.eclipse.wst.xsdeditor.ui.popup.outline",
    // menuManager, xsdEditor.getSelectionManager());

    // drill down from outline view
    getTreeViewer().getControl().addMouseListener(new MouseAdapter()
    {
      public void mouseDoubleClick(MouseEvent e)
      {
        ISelection iSelection = getTreeViewer().getSelection();
        if (iSelection instanceof StructuredSelection)
        {
          StructuredSelection selection = (StructuredSelection) iSelection;
          Object obj = selection.getFirstElement();
          if (obj instanceof XSDConcreteComponent)
          {
            XSDConcreteComponent comp = (XSDConcreteComponent) obj;
            if (comp.getContainer() instanceof XSDSchema)
            {
              // getXSDEditor().getGraphViewer().setInput(obj);
            }
          }
        }

      }
    });
  }

  class XSDKeyListener extends KeyAdapter
  {
  }

  public void dispose()
  {
    contentProvider.dispose();
    super.dispose();
  }

  public void setExpandToLevel(int i)
  {
    level = i;
  }

  public void setInput(Object value)
  {
    getTreeViewer().setInput(value);
    getTreeViewer().expandToLevel(level);
  }

  // public ISelection getSelection()
  // {
  // if (getTreeViewer() == null)
  // return StructuredSelection.EMPTY;
  // return getTreeViewer().getSelection();
  // }
  public void setSelectionManager(MultiPageSelectionProvider newSelectionManager)
  {
    // disconnect from old one
    if (selectionManager != null)
    {
      selectionManager.removeSelectionChangedListener(selectionManagerSelectionChangeListener);
    }
    selectionManager = newSelectionManager;
    // connect to new one
    if (selectionManager != null)
    {
      selectionManager.addSelectionChangedListener(selectionManagerSelectionChangeListener);
    }
  }

  protected class SelectionManagerSelectionChangeListener implements ISelectionChangedListener
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      updateStatusLine(getSite().getActionBars().getStatusLineManager(), event.getSelection());
      if (event.getSelectionProvider() != ADTContentOutlinePage.this)  //getTreeViewer())
      {
        StructuredSelection selection = (StructuredSelection)event.getSelection();
        StructuredSelection currentSelection = (StructuredSelection) getTreeViewer().getSelection();
        
        Object selectionFirstElement = selection.getFirstElement();
        Object currentFirstElement = currentSelection.getFirstElement();
       
        // TODO: Hack to prevent losing a selection when the schema is selected in the
        // source.  Fix is to prevent the source from firing off selection changes when
        // the selection source is not the source view.
        
        if (selectionFirstElement instanceof IModel)
        {          
          if (!(currentFirstElement instanceof IModelProxy) || currentFirstElement instanceof RedefineCategoryAdapter)
          {
            getTreeViewer().setSelection(event.getSelection(), true);
          }
        }
        else if (selectionFirstElement instanceof XSDSchemaDirectiveAdapter)
        {
        	if (!(currentFirstElement instanceof RedefineCategoryAdapter))
        	{
        		getTreeViewer().setSelection(event.getSelection(), true);
        	}
        }
        else
        {
          IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
          List list = structuredSelection.toList();
          int selectionLength = list.size();
          if (selectionLength > 0)
          {
            for (int i = 0; i < selectionLength; i++)
            {
              Object item = list.get(i);

              if (item instanceof AdapterImpl)
              {
                Notifier target = ((AdapterImpl) item).getTarget();

                if (!(target instanceof XSDConcreteComponent))
                  continue;

                XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent) target;

                // need to expand parent before child, thus a stack is required
                Stack componentStack = new Stack();
                while (xsdConcreteComponent != null)
                {
                  componentStack.push(xsdConcreteComponent);
                  xsdConcreteComponent = xsdConcreteComponent.getContainer();
                }

                // At this point the stack must have the XSDSchemaAdapter and
                // at least one component
                if (componentStack.size() < 2)
                  continue;

                // Pop off the top node, since it is the XSDSchemaAdapter, which
                // isn't
                // used as part of finding the outline view hierarchy
                componentStack.pop();

                if (((XSDConcreteComponent) componentStack.peek()).eAdapters().size() <= 0)
                  continue;

                Object object = ((XSDConcreteComponent) componentStack.peek()).eAdapters().get(0);

                // Find out which category the selected item is contained in
                int categoryIndex = -1;
                if (object instanceof XSDSchemaDirectiveAdapter)
                {
                  categoryIndex = CategoryAdapter.DIRECTIVES;
                }
                else if (object instanceof XSDElementDeclarationAdapter)
                {
                  categoryIndex = CategoryAdapter.ELEMENTS;
                }
                else if (object instanceof XSDAttributeDeclarationAdapter)
                {
                  categoryIndex = CategoryAdapter.ATTRIBUTES;
                }
                else if (object instanceof XSDAttributeGroupDefinitionAdapter)
                {
                  categoryIndex = CategoryAdapter.ATTRIBUTES;
                }
                else if (object instanceof XSDComplexTypeDefinitionAdapter || object instanceof XSDSimpleTypeDefinitionAdapter)
                {
                  categoryIndex = CategoryAdapter.TYPES;
                }
                else if (object instanceof XSDModelGroupDefinitionAdapter)
                {
                  categoryIndex = CategoryAdapter.GROUPS;
                }

                // Expand the category
                if (categoryIndex == -1)
                  continue;
                CategoryAdapter category = ((XSDSchemaAdapter) model).getCategory(categoryIndex);
                treeViewer.setExpandedState(category, true);

                // Do not expand current node of interest, just its parents
                while (componentStack.size() > 1)
                {
                  object = componentStack.pop();
                  if (object instanceof XSDConcreteComponent)
                  {
                    XSDConcreteComponent component = (XSDConcreteComponent) object;
                    if (component.eAdapters().size() > 0)
                    {
                      // expand
                      getTreeViewer().setExpandedState(component.eAdapters().get(0), true);
                    }
                  }
                }
              }
            }
          }
          // Bug 251474 - We will restrict selection to only one item
          if (selectionLength == 1)
          {
            getTreeViewer().setSelection(event.getSelection(), true);
          }
          else
          {
        	  if(structuredSelection.getFirstElement() !=null)
        		  getTreeViewer().setSelection(new StructuredSelection(structuredSelection.getFirstElement()), true);
          }
        }
      }
    }
  }
  
  protected void updateStatusLine(IStatusLineManager mgr, ISelection selection)
  {
    String text = null;
    Image image = null;
    ILabelProvider statusLineLabelProvider = new StatusLineLabelProvider(getTreeViewer());
    if (statusLineLabelProvider != null && selection instanceof IStructuredSelection && !selection.isEmpty())
    {
      Object firstElement = ((IStructuredSelection) selection).getFirstElement();
      if (firstElement instanceof XSDRedefineAdapter)
    	  return;
      text = statusLineLabelProvider.getText(firstElement);
      image = statusLineLabelProvider.getImage(firstElement);
    }
    if (image == null)
    {
      mgr.setMessage(text);
    }
    else
    {
      mgr.setMessage(image, text);
    }
  }
  
  protected class StatusLineLabelProvider extends JFaceNodeLabelProvider
  {
    TreeViewer treeViewer = null;

    public StatusLineLabelProvider(TreeViewer viewer)
    {
      treeViewer = viewer;
    }

    public String getText(Object element)
    {
      if (element == null)
        return null;

      StringBuffer s = new StringBuffer();
      s.append(labelProvider.getText(element));
      return s.toString();
    }

    public Image getImage(Object element)
    {
      return labelProvider.getImage(element);
    }
  }


}
