package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootEditPart;

public abstract class CommonMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, CommandStackListener, ITabbedPropertySheetPageContributor, IPropertyListener
{
  public static int SOURCE_PAGE_INDEX = 1, DESIGN_PAGE_INDEX = 0;
  
  protected IContentOutlinePage fOutlinePage;
  protected DefaultEditDomain editDomain;
  protected SelectionSynchronizer synchronizer;
  protected ActionRegistry actionRegistry;
  protected StructuredTextEditor structuredTextEditor;
  protected CommonSelectionManager selectionProvider;
  protected ScrollingGraphicalViewer graphicalViewer;
  
  public CommonMultiPageEditor()
  {
    super();
    editDomain = new DefaultEditDomain(this);

  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
   */
  public abstract String getContributorId();
  
  
  /**
   * 
   */
  protected abstract void createActions();
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
   */
  protected void createPages()
  {

  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  public void doSave(IProgressMonitor monitor)
  {
//    getEditor(1).doSave(monitor); 
    structuredTextEditor.doSave(monitor);
    getCommandStack().markSaveLocation();
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  public void doSaveAs()
  {
    IEditorPart editor = getEditor(1);
//    editor.doSaveAs();
    structuredTextEditor.doSaveAs();
    setInput(structuredTextEditor.getEditorInput());
    setPartName(editor.getTitle());
    getCommandStack().markSaveLocation();
    
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
   * 
   * Closes all project files on project close.
   */
  public void resourceChanged(final IResourceChangeEvent event)
  {
    if (event.getType() == IResourceChangeEvent.PRE_CLOSE)
    {
      Display.getDefault().asyncExec(new Runnable()
      {
        public void run()
        {
          IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
          for (int i = 0; i < pages.length; i++)
          {
            if (((FileEditorInput) structuredTextEditor.getEditorInput()).getFile().getProject().equals(event.getResource()))
            {
              IEditorPart editorPart = pages[i].findEditor(structuredTextEditor.getEditorInput());
              pages[i].closeEditor(editorPart, true);
            }
          }
        }
      });
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
   */
  public void commandStackChanged(EventObject event)
  {
    firePropertyChange(PROP_DIRTY);
  }
  
  /**
   * Indicates that a property has changed.
   * 
   * @param source
   *          the object whose property has changed
   * @param propId
   *          the id of the property which has changed; property ids are
   *          generally defined as constants on the source class
   */
  public void propertyChanged(Object source, int propId)
  {
    switch (propId)
    {
      // had to implement input changed "listener" so that
      // strucutedText could tell it containing editor that
      // the input has change, when a 'resource moved' event is
      // found.
      case IEditorPart.PROP_INPUT :
      case IEditorPart.PROP_DIRTY : {
        if (source == structuredTextEditor)
        {
          if (structuredTextEditor.getEditorInput() != getEditorInput())
          {
            setInput(structuredTextEditor.getEditorInput());
            // title should always change when input changes.
            // create runnable for following post call
            Runnable runnable = new Runnable()
            {
              public void run()
              {
                _firePropertyChange(IWorkbenchPart.PROP_TITLE);
              }
            };
            // Update is just to post things on the display queue
            // (thread). We have to do this to get the dirty
            // property to get updated after other things on the
            // queue are executed.
            postOnDisplayQue(runnable);
          }
        }
        break;
      }
      case IWorkbenchPart.PROP_TITLE : {
        // update the input if the title is changed
        if (source == structuredTextEditor)
        {
          if (structuredTextEditor.getEditorInput() != getEditorInput())
          {
            setInput(structuredTextEditor.getEditorInput());
          }
        }
        break;
      }
      default : {
        // propagate changes. Is this needed? Answer: Yes.
        if (source == structuredTextEditor)
        {
          firePropertyChange(propId);
        }
        break;
      }
    }
  }

  /**
   * @return
   */
  protected SelectionSynchronizer getSelectionSynchronizer()
  {
    if (synchronizer == null)
      synchronizer = new SelectionSynchronizer();
    return synchronizer;
  }

  public CommonSelectionManager getSelectionManager()
  {
    if (selectionProvider == null)
    {
      selectionProvider = new CommonSelectionManager(this);
    }
    return selectionProvider;
  }
  
  /*
   * This method is just to make firePropertyChanged accessbible from some
   * (anonomous) inner classes.
   */
  protected void _firePropertyChange(int property)
  {
    super.firePropertyChange(property);
  }
  
  /**
   * Posts the update code "behind" the running operation.
   */
  protected void postOnDisplayQue(Runnable runnable)
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    if (windows != null && windows.length > 0)
    {
      Display display = windows[0].getShell().getDisplay();
      display.asyncExec(runnable);
    }
    else
      runnable.run();
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
    actionRegistry.dispose();
    
    if (structuredTextEditor != null) {
      structuredTextEditor.removePropertyListener(this);
    }
    structuredTextEditor = null;

    super.dispose();
  }

  protected CommandStack getCommandStack()
  {
    return editDomain.getCommandStack();
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
//    if (!(editorInput instanceof IFileEditorInput))
//      throw new PartInitException("Invalid Input: Must be IFileEditorInput"); //$NON-NLS-1$
    super.init(site, editorInput);
    
    getCommandStack().addCommandStackListener(this);
    
    initializeActionRegistry();
    
    String title = null;
    if (getEditorInput() != null) {
      title = getEditorInput().getName();
    }
    setPartName(title);
  }

  protected void initializeActionRegistry()
  {
    createActions();
  }
  
  protected ActionRegistry getActionRegistry()
  {
    if (actionRegistry == null)
      actionRegistry = new ActionRegistry();
    return actionRegistry;
  }

  public Object getAdapter(Class type)
  {
    if (type == CommandStack.class)
      return getCommandStack();
    if (type == ActionRegistry.class)
      return getActionRegistry();
    
    return super.getAdapter(type);
  }
  
  protected DefaultEditDomain getEditDomain()
  {
    return editDomain;
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
    if (getCommandStack().isDirty())
      return true;
    else
      return super.isDirty();
  }

  public StructuredTextEditor getTextEditor()
  {
    return structuredTextEditor;
  }
  
  protected void createGraphPage()
  {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    parent.setLayout(new FillLayout());

    graphicalViewer = getGraphicalViewer();
    graphicalViewer.createControl(parent);
    getEditDomain().addViewer(graphicalViewer);
    
    configureGraphicalViewer();
    hookGraphicalViewer();    
    int index = addPage(parent);
    setPageText(index, Messages._UI_LABEL_DESIGN);
  }

  protected void createSourcePage()
  {
    structuredTextEditor = new StructuredTextEditor();
    try
    {
      int index = addPage(structuredTextEditor, getEditorInput());
      setPageText(index, Messages._UI_LABEL_SOURCE);
      structuredTextEditor.update();
      structuredTextEditor.setEditorPart(this);
      structuredTextEditor.addPropertyListener(this);
      firePropertyChange(PROP_TITLE);
    }
    catch (PartInitException e)
    {
      ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus()); //$NON-NLS-1$
    }
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

    //connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(connectionLayer));
    // connectionLayer.setVisible(false);

    // Zoom
    ZoomManager manager = (ZoomManager) graphicalViewer.getProperty(ZoomManager.class.toString());
    if (manager != null)
      manager.setZoom(1.0);
    // Scroll-wheel Zoom
    graphicalViewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL), MouseWheelZoomHandler.SINGLETON);
    graphicalViewer.setRootEditPart(root);
    graphicalViewer.setEditPartFactory(getEditPartFactory());
  }
  
  protected void hookGraphicalViewer()
  {
    getSelectionSynchronizer().addViewer(graphicalViewer);
  }
  
  protected abstract ScrollingGraphicalViewer getGraphicalViewer();
  protected abstract EditPartFactory getEditPartFactory();
  protected abstract void initializeGraphicalViewer();
}
