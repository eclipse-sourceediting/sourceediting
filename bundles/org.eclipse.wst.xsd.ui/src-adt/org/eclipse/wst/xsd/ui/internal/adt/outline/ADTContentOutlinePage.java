/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;

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

    // cs... why are we doing this from the outline view?
    //
    // xsdTextEditor.getXSDEditor().getSelectionManager().setSelection(new
    // StructuredSelection(xsdTextEditor.getXSDSchema()));
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
//    TreeViewer treeViewer = getTreeViewer();
    // disconnect from old one
    if (selectionManager != null)
    {
      selectionManager.removeSelectionChangedListener(selectionManagerSelectionChangeListener);
//      treeViewer.removeSelectionChangedListener(treeSelectionChangeListener);
    }
    selectionManager = newSelectionManager;
    // connect to new one
    if (selectionManager != null)
    {
      selectionManager.addSelectionChangedListener(selectionManagerSelectionChangeListener);
//      treeViewer.addSelectionChangedListener(treeSelectionChangeListener);
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
        
        // TODO: Hack to prevent losing a selection when the schema is selected in the
        // source.  Fix is to prevent the source from firing off selection changes when
        // the selection source is not the source view.
        if (selection.getFirstElement() instanceof IModel)
        {
          if (!(currentSelection.getFirstElement() instanceof IModelProxy))
          {
            getTreeViewer().setSelection(event.getSelection(), true);
          }
        }
        else
        {
          getTreeViewer().setSelection(event.getSelection(), true);
        }
      }
    }
  }

//  class TreeSelectionChangeListener implements ISelectionChangedListener
//  {
//    public void selectionChanged(SelectionChangedEvent event)
//    {
//      if (selectionManager != null)
//      {
//        ISelection selection = event.getSelection();
//        if (selection instanceof IStructuredSelection)
//        {
//          IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//          Object o = structuredSelection.getFirstElement();
//          if (o != null)
//          {
//            selectionManager.setSelection(structuredSelection);
//          }
//        }
//      }
//    }
//  }
  
  
  protected void updateStatusLine(IStatusLineManager mgr, ISelection selection)
  {
    String text = null;
    Image image = null;
    ILabelProvider statusLineLabelProvider = new StatusLineLabelProvider(getTreeViewer());
    if (statusLineLabelProvider != null && selection instanceof IStructuredSelection && !selection.isEmpty())
    {
      Object firstElement = ((IStructuredSelection) selection).getFirstElement();
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
