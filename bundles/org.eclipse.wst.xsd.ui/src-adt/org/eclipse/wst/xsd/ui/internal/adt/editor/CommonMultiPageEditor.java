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
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewerGraphicConstants;
import org.eclipse.wst.xsd.ui.internal.adt.design.FlatCCombo;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootEditPart;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public abstract class CommonMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, CommandStackListener, ITabbedPropertySheetPageContributor, IPropertyListener, IEditorModeListener
{
  public static int SOURCE_PAGE_INDEX = 1, DESIGN_PAGE_INDEX = 0;  
  
  protected IContentOutlinePage fOutlinePage;
  protected DefaultEditDomain editDomain;
  protected SelectionSynchronizer synchronizer;
  protected ActionRegistry actionRegistry;
  protected StructuredTextEditor structuredTextEditor;
  protected CommonSelectionManager selectionProvider;
  protected ScrollingGraphicalViewer graphicalViewer;
  protected EditorModeManager editorModeManager;
  protected FlatCCombo modeCombo;
  private EditorModeAndCustomizedName[] editorModeAndCustomizedNames;
  protected Composite toolbar;
  protected ModeComboListener modeComboListener;
  protected int maxLength = 0;
  protected CommonActivationListener fActivationListener;
  
  public CommonMultiPageEditor()
  {
    super();
    editDomain = new DefaultEditDomain(this)
    {
      public void mouseDown(MouseEvent mouseEvent, EditPartViewer viewer)
      {
        boolean eatTheEvent = false;      
        LayerManager manager = (LayerManager)viewer.getEditPartRegistry().get(LayerManager.ID);     
        IFigure layer = manager.getLayer(DesignViewerGraphicConstants.SCALED_HANDLE_LAYER);
        if (layer != null)
        {  
          Point p = new Point(mouseEvent.x, mouseEvent.y);
          layer.translateToRelative(p);
          IFigure figure = layer.findFigureAt(p);     
          if (figure != null && figure != layer)
          {
            // we eat this selection event!
            eatTheEvent = true;
          } 
        }
        if (!eatTheEvent)
        {
          super.mouseDown(mouseEvent, viewer);
        }  
      }      
    };
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
  
  // Should override to set the input to the design viewer for a new document change
  // ie. when doing a saveAs
  protected void setInputToGraphicalViewer(IDocument newInput)
  {
  }

  protected void setInput(IEditorInput input)
  {
    super.setInput(input);
    if (graphicalViewer != null)
    {
      setInputToGraphicalViewer(getDocument());
    }
  }

  protected IDocument getDocument()
  {
    IDocument document = null;
    if (structuredTextEditor != null)
    {
      document = structuredTextEditor.getDocumentProvider().getDocument(structuredTextEditor.getEditorInput());
    }
    return document;
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
    editDomain = null;
    fOutlinePage = null;
    synchronizer = null;
    actionRegistry = null;
    selectionProvider = null;
    graphicalViewer = null;
    if (modeCombo != null && !modeCombo.isDisposed())
    {
      modeCombo.removeSelectionListener(modeComboListener);
      modeComboListener = null;
    }
    
    if (fActivationListener != null) {
      fActivationListener.dispose();
      fActivationListener = null;
    }
   
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
    setActivePage(SOURCE_PAGE_INDEX);
    IDE.gotoMarker(structuredTextEditor, marker);
  }

  /**
   * The <code>MultiPageEditorExample</code> implementation of this method
   * checks that the input is an instance of <code>IFileEditorInput</code>.
   */
  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
  {
    super.init(site, editorInput);
    
    getCommandStack().addCommandStackListener(this);
    
    initializeActionRegistry();
    
    String title = null;
    if (getEditorInput() != null) {
      title = getEditorInput().getName();
    }
    setPartName(title);
    
    fActivationListener = new CommonActivationListener(site.getWorkbenchWindow().getPartService());
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
    if (type == EditorModeManager.class)
      return getEditorModeManager();
    if (type == IGotoMarker.class) {
      return new IGotoMarker() {
        public void gotoMarker(IMarker marker) {
          CommonMultiPageEditor.this.gotoMarker(marker);
        }
      };
    }
    if (type == ITextEditor.class)
      return getTextEditor();
    
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

  public StructuredTextEditor getTextEditor()
  {
    return structuredTextEditor;
  }

  protected Composite createGraphPageComposite()
  {
    Composite parent = new Composite(getContainer(), SWT.NONE);    
    parent.setLayout(new FillLayout());
    return parent;
  }
  
  protected void createGraphPage()
  {
    Composite parent = createGraphPageComposite();
    
    graphicalViewer = getGraphicalViewer();
    graphicalViewer.createControl(parent);
        
    getEditDomain().addViewer(graphicalViewer);
    
    configureGraphicalViewer();
    hookGraphicalViewer();    
    int index = addPage(parent);
    setPageText(index, Messages._UI_LABEL_DESIGN);
  }

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 */
	protected IEditorSite createSite(IEditorPart editor) {
		IEditorSite site = null;
		if (editor == structuredTextEditor) {
			site = new MultiPageEditorSite(this, editor) {
				public String getId() {
					// sets this id so nested editor is considered xml source
					// page
					return ContentTypeIdForXML.ContentTypeID_XML + ".source"; //$NON-NLS-1$;
				}
			};
		}
		else {
			site = super.createSite(editor);
		}
		return site;
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
    ZoomManager zoomManager = root.getZoomManager();
    
    List zoomLevelContributions = new ArrayList(3);
    zoomLevelContributions.add(ZoomManager.FIT_ALL);
    zoomLevelContributions.add(ZoomManager.FIT_WIDTH);
    zoomLevelContributions.add(ZoomManager.FIT_HEIGHT);
    zoomManager.setZoomLevelContributions(zoomLevelContributions);

    double[] zoomLevels = {.10, .25, .5, .75, 1.0, 1.25, 1.5, 2.0, 2.5, 3, 4, 5};
    zoomManager.setZoomLevels(zoomLevels);

    IAction zoomIn = new ZoomInAction(zoomManager);
    IAction zoomOut = new ZoomOutAction(zoomManager);
    getActionRegistry().registerAction(zoomIn);
    getActionRegistry().registerAction(zoomOut);

    getSite().getKeyBindingService().registerAction(zoomIn);
    getSite().getKeyBindingService().registerAction(zoomOut);

    //ConnectionLayer connectionLayer = (ConnectionLayer) root.getLayer(LayerConstants.CONNECTION_LAYER);
    //connectionLayer.setConnectionRouter(new BendpointConnectionRouter());

    //connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(connectionLayer));
    // connectionLayer.setVisible(false);

    // Zoom
    zoomManager.setZoom(1.0);
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

  protected EditorModeManager getEditorModeManager()
  {
    if (editorModeManager == null)
    {
      editorModeManager = createEditorModeManager();
      editorModeManager.addListener(this);
      editorModeManager.init();
    }  
    return editorModeManager;
  }
  
  protected abstract EditorModeManager createEditorModeManager();
  
  
  private String getEditModeName(EditorMode editorMode, ProductCustomizationProvider productCustomizationProvider)
  {
    String result = editorMode.getDisplayName();
    if (productCustomizationProvider != null)
    {
      String customizedName = productCustomizationProvider.getEditorModeDisplayName(editorMode.getId());
      if (customizedName != null)
      {
        result = customizedName;
      }  
    } 
    return result;
  }
  
  class EditorModeAndCustomizedName
  {
    EditorMode mode;
    String name;
  }
  
  protected void createViewModeToolbar(Composite parent)
  {
    EditorModeManager manager = (EditorModeManager)getAdapter(EditorModeManager.class);
    final ProductCustomizationProvider productCustomizationProvider = (ProductCustomizationProvider)getAdapter(ProductCustomizationProvider.class);
    EditorMode [] modeList = manager.getModes();
    
    int modeListLength = modeList.length;
    boolean showToolBar = modeListLength > 1;
   
    if (showToolBar)
    {
      toolbar = new Composite(parent, SWT.FLAT | SWT.DRAW_TRANSPARENT);
      toolbar.setBackground(ColorConstants.listBackground);
      toolbar.addPaintListener(new PaintListener() {

        public void paintControl(PaintEvent e)
        {
          Rectangle clientArea = toolbar.getClientArea(); 
          e.gc.setForeground(ColorConstants.lightGray);
          e.gc.drawRectangle(clientArea.x, clientArea.y, clientArea.width - 1, clientArea.height - 1);
        }
      });
      
      GridLayout gridLayout = new GridLayout(3, false);
      toolbar.setLayout(gridLayout);

      Label label = new Label(toolbar, SWT.FLAT | SWT.HORIZONTAL);
      label.setBackground(ColorConstants.listBackground);
      label.setText(Messages._UI_LABEL_VIEW);
      label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

      modeCombo = new FlatCCombo(toolbar, SWT.FLAT);
      modeCombo.setEditable(false);
      modeCombo.setText(getEditModeName(manager.getCurrentMode(), productCustomizationProvider)); 
      GC gc = new GC(modeCombo);
      int textWidth = 0;
      maxLength = 0;
      
      // populate combo with modes
      editorModeAndCustomizedNames = new EditorModeAndCustomizedName[modeListLength];      
      for (int i = 0; i < modeListLength; i++)
      {  
        EditorModeAndCustomizedName entry = new EditorModeAndCustomizedName();
        editorModeAndCustomizedNames[i] = entry;
        entry.name = getEditModeName(modeList[i], productCustomizationProvider);
        entry.mode = modeList[i];
      }        
      Arrays.sort(editorModeAndCustomizedNames, new Comparator()
      {
        public int compare(Object arg0, Object arg1)
        {
          EditorModeAndCustomizedName a = (EditorModeAndCustomizedName)arg0;
          EditorModeAndCustomizedName b = (EditorModeAndCustomizedName)arg1;        
          return Collator.getInstance().compare(a.name, b.name);
        }
      });
      for (int i = 0; i < editorModeAndCustomizedNames.length; i++ )
      {
        EditorModeAndCustomizedName entry = editorModeAndCustomizedNames[i];
        modeCombo.add(entry.name);
        maxLength = Math.max (gc.stringExtent(entry.name).x, maxLength);
        int approxWidthOfStrings = Math.max (gc.stringExtent(entry.name).x, textWidth);
        if (approxWidthOfStrings > maxLength)
          maxLength = approxWidthOfStrings;
      }
      
      maxLength += gc.stringExtent(Messages._UI_LABEL_VIEW).x; 
      gc.dispose();
      
      modeComboListener = new ModeComboListener();
      modeCombo.addSelectionListener(modeComboListener);
      modeCombo.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
      modeCombo.setBackground(toolbar.getBackground());

      ImageHyperlink hyperlink = new ImageHyperlink(toolbar, SWT.FLAT);
      hyperlink.setBackground(ColorConstants.white);
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=154457
      Image image = XSDEditorPlugin.getDefault().getIconImage("etool16/help_contents");
      hyperlink.setImage(image);
      hyperlink.setToolTipText(Messages._UI_HOVER_VIEW_MODE_DESCRIPTION);
      hyperlink.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
      hyperlink.addMouseListener(new MouseAdapter()
      {
        public void mouseDown(MouseEvent e)
        {
          if (productCustomizationProvider != null)
          {
            productCustomizationProvider.handleAction("showEditorModeHelp");
          }
        }
      });      
    }
  }
  
  
  protected class ModeComboListener implements SelectionListener
  {
    public ModeComboListener()
    {
    }
    
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
      if (e.widget == modeCombo)
      {        
        EditorModeManager manager = (EditorModeManager)getAdapter(EditorModeManager.class);
        EditorMode [] modeList = manager.getModes();
        if (modeList.length >= 1)
        {
          EditorModeAndCustomizedName entry = editorModeAndCustomizedNames[modeCombo.getSelectionIndex()];
          if (manager.getCurrentMode() != entry.mode)
          {  
            manager.setCurrentMode(entry.mode);
            storeCurrentModePreference(entry.mode.getId());
            ProductCustomizationProvider productCustomizationProvider = (ProductCustomizationProvider) getAdapter(ProductCustomizationProvider.class);
            if (productCustomizationProvider != null)
            {
              productCustomizationProvider.handleAction("editorModeChanged");
            }
          }
        }  
      }
    }
  }
  
  protected void storeCurrentModePreference(String id)
  {
    // Don't do anything as default.  Allow extenders to implement.
  }
  
  
  protected class CommonActivationListener implements IPartListener, IWindowListener
  {

    /** Cache of the active workbench part. */
    private IWorkbenchPart fActivePart;
    /** Indicates whether activation handling is currently be done. */
    private boolean fIsHandlingActivation = false;
    /**
     * The part service.
     * 
     * @since 3.1
     */
    private IPartService fPartService;

    /**
     * Creates this activation listener.
     * 
     * @param partService
     *          the part service on which to add the part listener
     * @since 3.1
     */
    public CommonActivationListener(IPartService partService)
    {
      fPartService = partService;
      fPartService.addPartListener(this);
      PlatformUI.getWorkbench().addWindowListener(this);
    }

    /**
     * Disposes this activation listener.
     * 
     * @since 3.1
     */
    public void dispose()
    {
      fPartService.removePartListener(this);
      PlatformUI.getWorkbench().removeWindowListener(this);
      fPartService = null;
    }

    /*
     * @see IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated(IWorkbenchPart part)
    {
      fActivePart = part;
      handleActivation();
    }

    /*
     * @see IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part)
    {
      // do nothing
    }

    /*
     * @see IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part)
    {
      // do nothing
    }

    /*
     * @see IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part)
    {
      fActivePart = null;
    }

    /*
     * @see IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part)
    {
    	if (CommonMultiPageEditor.this.equals(part))
    	{
    		doPostEditorOpenTasks();
    	}
    }

    /**
     * Handles the activation triggering a element state check in the editor.
     */
    void handleActivation()
    {
      if (fIsHandlingActivation || (getTextEditor() == null))
      {
        return;
      }

      if (fActivePart == CommonMultiPageEditor.this)
      {
        fIsHandlingActivation = true;
        try
        {
          getTextEditor().safelySanityCheckState(getEditorInput());
        }
        finally
        {
          fIsHandlingActivation = false;
        }
      }
    }

    /*
     * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
     * @since 3.1
     */
    public void windowActivated(IWorkbenchWindow window)
    {
      if (window == getEditorSite().getWorkbenchWindow())
      {
        /*
         * Workaround for problem described in
         * http://dev.eclipse.org/bugs/show_bug.cgi?id=11731 Will be removed
         * when SWT has solved the problem.
         */
        window.getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            handleActivation();
          }
        });
      }
    }

    /*
     * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
     * @since 3.1
     */
    public void windowDeactivated(IWorkbenchWindow window)
    {
      // do nothing
    }

    /*
     * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
     * @since 3.1
     */
    public void windowClosed(IWorkbenchWindow window)
    {
      // do nothing
    }

    /*
     * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
     * @since 3.1
     */
    public void windowOpened(IWorkbenchWindow window)
    {
      // do nothing
    }
  }
  
  public boolean isSourcePageActive()
  {
    return getActivePage() == SOURCE_PAGE_INDEX;
  }
  
  /**
   * Invoked during IPartListener#partOpened. Derived classes can override
   * to provide specialized behaviour.  
   */
  protected void doPostEditorOpenTasks() {
	  // Nothing to do in the base class.
  }
}
