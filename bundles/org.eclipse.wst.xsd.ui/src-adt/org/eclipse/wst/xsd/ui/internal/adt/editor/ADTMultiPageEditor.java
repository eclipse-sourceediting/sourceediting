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
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ADTEditPartFactory;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlinePage;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlineProvider;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTLabelProvider;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public abstract class ADTMultiPageEditor extends CommonMultiPageEditor
{
  protected IModel model;
  private int currentPage = -1;
  private Label tableOfContentsButton;
  
  /**
   * Creates a multi-page editor example.
   */
  public ADTMultiPageEditor()
  {
    super();
  }

  
  private class InternalLayout extends StackLayout
  {
    public InternalLayout()
    {
      super();  
    }
    
    protected void layout(Composite composite, boolean flushCache)
    {
      Control children[] = composite.getChildren();
      Rectangle rect = composite.getClientArea();
      rect.x += marginWidth;
      rect.y += marginHeight;
      rect.width -= 2 * marginWidth;
      rect.height -= 2 * marginHeight;
      
      for (int i = 0; i < children.length; i++) 
      { 
        if (i == 1)
        {  
          children[i].setBounds(rect);
        }
        else if (i == 0)
        {  
          children[i].setBounds(rect.x + 10, rect.y + 10, 40, 40);
        }  
      }       
    }               
  }
  
  protected Composite createGraphPageComposite()
  {    
    Composite parent = new Composite(getContainer(), SWT.NONE);    
    parent.setLayout(new InternalLayout());    
    tableOfContentsButton = new Label(parent, 0);
    
    // TODO (cs) need to review how the initial 'input' gets specified
    // on the designViewer somehow a schema gets set without going thru
    // the designViewer's setInput.  Need to setVisible(false) to workaround this.
    //
    tableOfContentsButton.setVisible(false);
    tableOfContentsButton.setBackground(ColorConstants.white);
    tableOfContentsButton.setImage(XSDEditorPlugin.getPlugin().getIcon("obj16/index.gif"));
    tableOfContentsButton.addMouseListener(new MouseAdapter()
    {
      public void mouseUp(MouseEvent e)
      {
        // TODO (cs) do we need to register this action?
        //
        System.out.println("MouseUp!!");
        SetInputToGraphView action = new SetInputToGraphView(ADTMultiPageEditor.this, getModel());
        action.run();
      }          
    });
    return parent;
  }
  
  protected void createGraphPage()
  {
    super.createGraphPage(); 
    tableOfContentsButton.moveAbove(graphicalViewer.getControl());
    graphicalViewer.getControl().moveBelow(tableOfContentsButton);        
  }
  
  public String getContributorId()
  {
    return "org.eclipse.wst.xsd.ui.internal.editor"; //$NON-NLS-1$
  }
  
  public IContentOutlinePage getContentOutlinePage()
  {
    if (fOutlinePage == null || fOutlinePage.getControl() == null || fOutlinePage.getControl().isDisposed())
    {
      ADTContentOutlinePage outlinePage = new ADTContentOutlinePage(this);
      ADTContentOutlineProvider adtContentProvider = new ADTContentOutlineProvider();
      outlinePage.setContentProvider(adtContentProvider);
      ADTLabelProvider adtLabelProvider = new ADTLabelProvider();
      outlinePage.setLabelProvider(adtLabelProvider);
      outlinePage.setModel(getModel());
      
      fOutlinePage = outlinePage;
    }
    return fOutlinePage;
  }

  /**
   * Creates the pages of the multi-page editor.
   */
  protected void createPages()
  {
    selectionProvider = getSelectionManager();
    
    createGraphPage();
    createSourcePage();
    
    getEditorSite().setSelectionProvider(selectionProvider);

    model = buildModel();  // (IFileEditorInput)getEditorInput());
    
    initializeGraphicalViewer();
    
    int pageIndexToShow = getDefaultPageTypeIndex();
    setActivePage(pageIndexToShow);
  }

  protected int getDefaultPageTypeIndex() {
    int pageIndex = SOURCE_PAGE_INDEX;
    if (XSDEditorPlugin.getPlugin().getDefaultPage().equals(XSDEditorPlugin.DESIGN_PAGE)) {
        pageIndex = DESIGN_PAGE_INDEX;
    }

    return pageIndex;
  }

  protected void pageChange(int newPageIndex)
  {
    currentPage = newPageIndex;
    super.pageChange(newPageIndex);
  }
  
  private boolean isTableOfContentsApplicable(Object graphViewInput)
  {
    return !(graphViewInput instanceof IModel);
  }

  protected ScrollingGraphicalViewer getGraphicalViewer()
  {
    DesignViewGraphicalViewer viewer = new DesignViewGraphicalViewer(this, getSelectionManager());
    viewer.addInputChangdListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {        
        IStructuredSelection input = (IStructuredSelection)event.getSelection();
        System.out.println("inputChanged:" + input);
        tableOfContentsButton.setVisible(isTableOfContentsApplicable(input.getFirstElement()));         
      }      
    });
    return viewer;
  }
  
  abstract public IModel buildModel();  // (IFileEditorInput editorInput);
  
  protected void createActions()
  {
    ActionRegistry registry = getActionRegistry();
    
    BaseSelectionAction action = new SetInputToGraphView(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
  }


  public IModel getModel()
  {
    return model;
  }

  public Object getAdapter(Class type)
  {
    if (type == ZoomManager.class)
      return graphicalViewer.getProperty(ZoomManager.class.toString());
    
    if (type == GraphicalViewer.class)
      return graphicalViewer;
    if (type == EditPart.class && graphicalViewer != null)
      return graphicalViewer.getRootEditPart();
    if (type == IFigure.class && graphicalViewer != null)
      return ((GraphicalEditPart) graphicalViewer.getRootEditPart()).getFigure();

    if (type == IContentOutlinePage.class)
    {
      return getContentOutlinePage();
    }

    return super.getAdapter(type);
  }

  protected EditPartFactory getEditPartFactory() {
    return new ADTEditPartFactory();
  }

  protected void initializeGraphicalViewer()
  {
    graphicalViewer.setContents(model);
  }
  
  public void dispose()
  {
    if (currentPage == SOURCE_PAGE_INDEX)
    {
      XSDEditorPlugin.getPlugin().setSourcePageAsDefault();
    }
    else
    {
      XSDEditorPlugin.getPlugin().setDesignPageAsDefault();
    }

    super.dispose();
  }
}
