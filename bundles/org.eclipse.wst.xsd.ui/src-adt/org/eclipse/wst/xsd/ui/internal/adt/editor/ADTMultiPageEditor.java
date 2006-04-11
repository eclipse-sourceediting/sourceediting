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

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ADTEditPartFactory;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlinePage;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlineProvider;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTLabelProvider;

/**
 * </ul>
 * <li>page 0 graph
 * <li>page 1 source
 * </ul>
 */
public abstract class ADTMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, CommandStackListener, ITabbedPropertySheetPageContributor
{
  protected IContentOutlinePage fOutlinePage;
  protected long lastModificationStamp;
  protected DesignViewGraphicalViewer graphicalViewer;
  
  protected IModel model;
  private DefaultEditDomain editDomain;
  private SelectionSynchronizer synchronizer;
  private ActionRegistry actionRegistry;
  private List selectionActions = new ArrayList();
  private List stackActions = new ArrayList();
  private List propertyActions = new ArrayList();
  protected ADTSelectionManager selectionManager;

  /**
   * Creates a multi-page editor example.
   */
  public ADTMultiPageEditor()
  {
    super();
    DefaultEditDomain defaultGEFEditDomain = new DefaultEditDomain(this);
    setEditDomain(defaultGEFEditDomain);
  }

  public String getContributorId()
  {
    return "org.eclipse.wst.xsd.ui.internal.editor";
  }
  
  
  public ADTSelectionManager getSelectionManager()
  {
    if (selectionManager == null)
    {
      selectionManager = new ADTSelectionManager(this);
    }
    return selectionManager;
  }

  protected void createGraphPage()
  {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    parent.setLayout(new FillLayout());

    graphicalViewer = new DesignViewGraphicalViewer(this, getSelectionProvider());//getSelectionManager(), xsdModelAdapterFactory);
    graphicalViewer.createControl(parent);
    getEditDomain().addViewer(graphicalViewer);
    
    configureGraphicalViewer();
    hookGraphicalViewer();
    initializeGraphicalViewer();
    int index = addPage(parent);
    setPageText(index, "Design");
  }

  protected ADTSelectionManager selectionProvider;
  public ADTSelectionManager getSelectionProvider()
  {
    return selectionProvider;
  }
  
  /**
   * Creates the pages of the multi-page editor.
   */
  protected void createPages()
  {
    model = buildModel((IFileEditorInput)getEditorInput());
    
    selectionProvider = getSelectionManager();
    getEditorSite().setSelectionProvider(selectionProvider);
    
    createGraphPage();
  }

  /**
   * The <code>MultiPageEditorPart</code> implementation of this
   * <code>IWorkbenchPart</code> method disposes all nested editors.
   * Subclasses may extend.
   */
  public void dispose()
  {
    getCommandStack().removeCommandStackListener(this);
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    getActionRegistry().dispose();
    super.dispose();
  }

  /**
   * Saves the multi-page editor's document.
   */
  public void doSave(IProgressMonitor monitor)
  {
    getEditor(1).doSave(monitor); 
    getCommandStack().markSaveLocation();
  }

  /**
   * Saves the multi-page editor's document as another file. Also updates the
   * text for page 0's tab, and updates this multi-page editor's input to
   * correspond to the nested editor's.
   */
  public void doSaveAs()
  {
    IEditorPart editor = getEditor(1);
    editor.doSaveAs();
    setPageText(1, editor.getTitle());
    setInput(editor.getEditorInput());
    getCommandStack().markSaveLocation();
  }

  /*
   * (non-Javadoc) Method declared on IEditorPart
   */
  public void gotoMarker(IMarker marker)
  {
    setActivePage(0);
    IDE.gotoMarker(getEditor(0), marker);
  }

  /**
   * The <code>MultiPageEditorExample</code> implementation of this method
   * checks that the input is an instance of <code>IFileEditorInput</code>.
   */
  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
  {
    if (!(editorInput instanceof IFileEditorInput))
      throw new PartInitException("Invalid Input: Must be IFileEditorInput");
    super.init(site, editorInput);
    
    getCommandStack().addCommandStackListener(this);
    initializeActionRegistry();
    
    String title = null;
    if (getEditorInput() != null) {
      title = getEditorInput().getName();
    }
    setPartName(title);
  }

  /*
   * (non-Javadoc) Method declared on IEditorPart.
   */
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  /**
   * Calculates the contents of page 2 when the it is activated.
   */
  protected void pageChange(int newPageIndex)
  {
    super.pageChange(newPageIndex);
    if (newPageIndex == 1)
    {
    }
  }

  abstract public IModel buildModel(IFileEditorInput editorInput);
  
  protected void initializeActionRegistry()
  {
    createActions();
  }

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

  //protected XSDModelAdapterFactoryImpl xsdModelAdapterFactory;
  //protected XSDAdapterFactoryLabelProvider adapterFactoryLabelProvider;

  public Object getAdapter(Class type)
  {
    Object result = null;
    if (type == ZoomManager.class)
      return graphicalViewer.getProperty(ZoomManager.class.toString());
    
    if (type == ISelectionProvider.class)
    {
      result = getSelectionManager();
    }    
    /*
    if (type == org.eclipse.ui.views.properties.IPropertySheetPage.class)
    {
      // PropertySheetPage page = new PropertySheetPage();
      // page.setRootEntry(new UndoablePropertySheetEntry(getCommandStack()));
      XSDTabbedPropertiesPage page = new XSDTabbedPropertiesPage(this);
      return page;
    }*/
    if (type == GraphicalViewer.class)
      return graphicalViewer;
    if (type == CommandStack.class)
      return getCommandStack();
    if (type == ActionRegistry.class)
      return getActionRegistry();
    if (type == EditPart.class && graphicalViewer != null)
      return graphicalViewer.getRootEditPart();
    if (type == IFigure.class && graphicalViewer != null)
      return ((GraphicalEditPart) graphicalViewer.getRootEditPart()).getFigure();

    if (type == IContentOutlinePage.class)
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

    return super.getAdapter(type);
  }

  protected DefaultEditDomain getEditDomain()
  {
    return editDomain;
  }


  protected void configureGraphicalViewer()
  {
    graphicalViewer.getControl().setBackground(ColorConstants.listBackground);

    // Set the root edit part
    // ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
    RootEditPart root = new RootEditPart();

    List zoomLevels = new ArrayList(3);
    zoomLevels.add(ZoomManager.FIT_ALL);
    zoomLevels.add(ZoomManager.FIT_WIDTH);
    zoomLevels.add(ZoomManager.FIT_HEIGHT);
    root.getZoomManager().setZoomLevelContributions(zoomLevels);

    IAction zoomIn = new ZoomInAction(root.getZoomManager());
    IAction zoomOut = new ZoomOutAction(root.getZoomManager());
    getActionRegistry().registerAction(zoomIn);
    getActionRegistry().registerAction(zoomOut);

    getSite().getKeyBindingService().registerAction(zoomIn);
    getSite().getKeyBindingService().registerAction(zoomOut);

    //ConnectionLayer connectionLayer = (ConnectionLayer) root.getLayer(LayerConstants.CONNECTION_LAYER);
    //connectionLayer.setConnectionRouter(new BendpointConnectionRouter());
    IFigure feedbackLayer = root.getLayer(LayerConstants.FEEDBACK_LAYER);

    //connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(connectionLayer));
    // connectionLayer.setVisible(false);

    // Zoom
    ZoomManager manager = (ZoomManager) graphicalViewer.getProperty(ZoomManager.class.toString());
    if (manager != null)
      manager.setZoom(1.0);
    // Scroll-wheel Zoom
    graphicalViewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL), MouseWheelZoomHandler.SINGLETON);

    
    
    graphicalViewer.setRootEditPart(root);
    graphicalViewer.setEditPartFactory(new ADTEditPartFactory());
  }

  protected void hookGraphicalViewer()
  {
    getSelectionSynchronizer().addViewer(graphicalViewer);
    getSelectionManager().addSelectionChangedListener(graphicalViewer);
  }

  protected SelectionSynchronizer getSelectionSynchronizer()
  {
    if (synchronizer == null)
      synchronizer = new SelectionSynchronizer();
    return synchronizer;
  }

  protected void initializeGraphicalViewer()
  {
    graphicalViewer.setContents(model);
  }

  protected void setEditDomain(DefaultEditDomain ed)
  {
    this.editDomain = ed;
  }

  protected CommandStack getCommandStack()
  {
    return getEditDomain().getCommandStack();
  }

  protected ActionRegistry getActionRegistry()
  {
    if (actionRegistry == null)
      actionRegistry = new ActionRegistry();
    return actionRegistry;
  }

  public void commandStackChanged(EventObject event)
  {
    updateActions(stackActions);
    firePropertyChange(PROP_DIRTY);
  }

  /**
   * From GEF GraphicalEditor A convenience method for updating a set of actions
   * defined by the given List of action IDs. The actions are found by looking
   * up the ID in the {@link #getActionRegistry() action registry}. If the
   * corresponding action is an {@link UpdateAction}, it will have its
   * <code>update()</code> method called.
   * 
   * @param actionIds
   *          the list of IDs to update
   */
  protected void updateActions(List actionIds)
  {
    ActionRegistry registry = getActionRegistry();
    Iterator iter = actionIds.iterator();
    while (iter.hasNext())
    {
      IAction action = registry.getAction(iter.next());
      if (action instanceof UpdateAction)
        ((UpdateAction) action).update();
    }
  }

  /**
   * Returns <code>true</code> if the command stack is dirty
   * 
   * @see org.eclipse.ui.ISaveablePart#isDirty()
   */
  public boolean isDirty()
  {
    super.isDirty();
    return getCommandStack().isDirty();
  }
}
