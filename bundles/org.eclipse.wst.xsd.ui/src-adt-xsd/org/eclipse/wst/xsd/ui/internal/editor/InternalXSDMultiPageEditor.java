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
package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.AddFieldAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDComplexTypeDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDSimpleTypeDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.DeleteXSDConcreteComponentAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetTypeAction;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDEditPartFactory;
import org.eclipse.wst.xsd.ui.internal.navigation.DesignViewNavigationLocation;
import org.eclipse.wst.xsd.ui.internal.navigation.MultiPageEditorTextSelectionNavigationLocation;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelReconcileAdapter;
import org.eclipse.wst.xsd.ui.internal.utils.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InternalXSDMultiPageEditor extends ADTMultiPageEditor implements ITabbedPropertySheetPageContributor, IPropertyListener, INavigationLocationProvider
{
  ResourceSet resourceSet;
  Resource xsdResource;
  // IModel model;
  IStructuredModel structuredModel;
  XSDSchema xsdSchema;
  private OutlineTreeSelectionChangeListener fOutlineListener;
  private SourceEditorSelectionListener fSourceEditorSelectionListener;
  private XSDSelectionManagerSelectionListener fXSDSelectionListener;
  private StructuredTextEditor structuredTextEditor;

  public IModel buildModel(IFileEditorInput editorInput)
  {
    try
    {
      EPackage.Registry reg = EPackage.Registry.INSTANCE;
      XSDPackage xsdPackage = (XSDPackage) reg.getEPackage(XSDPackage.eNS_URI);
      xsdSchema = xsdPackage.getXSDFactory().createXSDSchema();
      resourceSet = XSDSchemaImpl.createResourceSet();
      IFile resourceFile = editorInput.getFile();
      structuredModel = StructuredModelManager.getModelManager().getModelForEdit(resourceFile);
      // If the resource is in the workspace....
      // otherwise the user is trying to open an external file
      if (resourceFile != null)
      {
        String pathName = resourceFile.getFullPath().toString();
        xsdResource = resourceSet.getResource(URI.createPlatformResourceURI(pathName), true);
        resourceSet.getResources().add(xsdResource);
        Object obj = xsdResource.getContents().get(0);
        if (obj instanceof XSDSchema)
        {
          xsdSchema = (XSDSchema) obj;
          xsdSchema.setElement(((IDOMModel) structuredModel).getDocument().getDocumentElement());
          model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
        }
        
        // If the input schema is from the WSDL Editor, then use that inline schema
        if (editorInput instanceof XSDFileEditorInput)
        {
          xsdSchema = ((XSDFileEditorInput) editorInput).getSchema();
          model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
        }
        if (xsdSchema.getElement() != null)
          
          // TODO (cs) ... we need to look into performance issues when we add elements
          // seems to be that formatting is causig lots of notification and things get terribly slow
          // I'm specializing the method below to add an isModelStateChanging check that should
          // help here ... but we need to investigate further
          new XSDModelReconcileAdapter(xsdSchema.getElement().getOwnerDocument(), xsdSchema)
          {
            public void handleNotifyChange(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
            {
              if (notifier instanceof NodeImpl)
              {
                NodeImpl nodeImpl = (NodeImpl)notifier;
                if (!nodeImpl.getModel().isModelStateChanging())
                {  
                  super.handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
                }  
              }
            }
          };
        xsdResource.setModified(false);
      }
    }
    catch (StackOverflowError e)
    {
    }
    catch (Exception ex)
    {
    }
    return model;
  }

  public void dispose()
  {
    structuredModel.releaseFromEdit();
    if (fOutlinePage != null)
    {
//      if (fOutlinePage instanceof ConfigurableContentOutlinePage && fOutlineListener != null)
//      {
//        ((ConfigurableContentOutlinePage) fOutlinePage).removeDoubleClickListener(fOutlineListener);
//      }
      if (fOutlineListener != null)
      {
        fOutlinePage.removeSelectionChangedListener(fOutlineListener);
      }
    }
    getSelectionManager().removeSelectionChangedListener(fXSDSelectionListener);
    super.dispose();
  }

  protected void initializeGraphicalViewer()
  {
    RootContentEditPart root = new RootContentEditPart();
    root.setModel(model);
    graphicalViewer.setContents(root);
  }
  
  protected void configureGraphicalViewer()
  {
    super.configureGraphicalViewer();
    // get edit part factory from extension
    EditPartFactory editPartFactory = XSDEditorPlugin.getDefault().getXSDEditorConfiguration().getEditPartFactory();
    if (editPartFactory != null)
    {
      graphicalViewer.setEditPartFactory(editPartFactory);
    }
    else
    {
      // otherwise use default
      graphicalViewer.setEditPartFactory(new XSDEditPartFactory());
    }
  }

  public Object getAdapter(Class type)
  {
    if (type == org.eclipse.ui.views.properties.IPropertySheetPage.class)
    {
      XSDTabbedPropertySheetPage page = new XSDTabbedPropertySheetPage(this);
      return page;
    }
    else if (type == ISelectionProvider.class)
    {
      return selectionManager;
    }  
    else if (type == XSDSchema.class)
    {
      return xsdSchema;
    }
    else if (type == IContentOutlinePage.class)
    {
      Object adapter = super.getAdapter(type);
      if (adapter != null)
      {
        IContentOutlinePage page = (IContentOutlinePage) adapter;
        fOutlineListener = new OutlineTreeSelectionChangeListener();
        page.addSelectionChangedListener(fOutlineListener);
        
//        if (page instanceof ConfigurableContentOutlinePage)
//        {
//          ((ConfigurableContentOutlinePage) page).addDoubleClickListener(fOutlineListener);
//        }
        return page;
      }
    }
    else if (type == XSDElementReferenceEditManager.class)
    {
    	IEditorInput editorInput = getEditorInput();
    	if (editorInput instanceof IFileEditorInput)
    	{
    		IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
    		// TODO (cs) currently we assume the schema editor will only ever edit a
    		// single schema
    		/// but if we want to enable the schema editor to edit wsdl files we
    		// should pass in
    		// an array of schemas
    		// hmm.. perhaps just pass in a ResourceSet
    		XSDSchema[] schemas = {xsdSchema};
    		return new XSDElementReferenceEditManager(fileEditorInput.getFile(), schemas);
    	}
    }
    else if (type == XSDTypeReferenceEditManager.class)
    {
      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof IFileEditorInput)
      {
        IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
        // TODO (cs) currently we assume the schema editor will only ever edit a
        // single schema
        // but if we want to enable the schema editor to edit wsdl files we
        // should pass in
        // an array of schemas
        // hmm.. perhaps just pass in a ResourceSet
        XSDSchema[] schemas = {xsdSchema};
        return new XSDTypeReferenceEditManager(fileEditorInput.getFile(), schemas);
      }
    }
    else if (type == ITextEditor.class)
    {
      return getTextEditor();
    }
    else if (type == ISelectionMapper.class)
    {
      return new XSDSelectionMapper();
    }  
    return super.getAdapter(type);
  }

  public String getContributorId()
  {
    return "org.eclipse.wst.xsd.ui.internal.editor";
  }

  public XSDSchema getXSDSchema()
  {
    return xsdSchema;
  }

  public StructuredTextEditor getTextEditor()
  {
    return structuredTextEditor;
  }

  protected void createSourcePage()
  {
    try
    {
      structuredTextEditor = new StructuredTextEditor();
      int index = addPage(structuredTextEditor, getEditorInput());
      setPageText(index, "Source");
      structuredTextEditor.update();
      structuredTextEditor.setEditorPart(this);
      structuredTextEditor.addPropertyListener(this);
      firePropertyChange(PROP_TITLE);
    }
    catch (PartInitException e)
    {
      ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
    }
  }

  /**
   * Method openOnGlobalReference. The comp argument is a resolved xsd schema
   * object from another file. This is created and called from another schema
   * model to allow F3 navigation to open a new editor and choose the referenced
   * object within that editor context
   * 
   * @param comp
   */
  public void openOnGlobalReference(XSDConcreteComponent comp)
  {
    XSDNamedComponent namedComponent = openOnSelectionHelper.openOnGlobalReference(comp);
    
    if (namedComponent != null)
    {
      XSDBaseAdapter adapter = (XSDBaseAdapter) XSDAdapterFactory.getInstance().adapt(namedComponent);
      getSelectionManager().setSelection(new StructuredSelection(adapter));
      IAction action = getActionRegistry().getAction(SetInputToGraphView.ID);
      if (action != null)
      {
        action.run();
      }
    }
  }
  
  protected OpenOnSelectionHelper openOnSelectionHelper;

  public OpenOnSelectionHelper getOpenOnSelectionHelper()
  {
    return openOnSelectionHelper;
  }

  /**
   * Creates the pages of the multi-page editor.
   */
  protected void createPages()
  {
    model = buildModel((IFileEditorInput) getEditorInput());
    selectionProvider = getSelectionManager();
    getEditorSite().setSelectionProvider(selectionProvider);
    createGraphPage();
    createSourcePage();
    openOnSelectionHelper = new OpenOnSelectionHelper(getTextEditor(), getXSDSchema());
    ISelectionProvider provider = getTextEditor().getSelectionProvider();
    fSourceEditorSelectionListener = new SourceEditorSelectionListener();
    if (provider instanceof IPostSelectionProvider)
    {
      ((IPostSelectionProvider) provider).addPostSelectionChangedListener(fSourceEditorSelectionListener);
    }
    else
    {
      provider.addSelectionChangedListener(fSourceEditorSelectionListener);
    }
    fXSDSelectionListener = new XSDSelectionManagerSelectionListener();
    getSelectionManager().addSelectionChangedListener(fXSDSelectionListener);
  }

  protected void createActions()
  {
    super.createActions();
    ActionRegistry registry = getActionRegistry();
    BaseSelectionAction action = new AddFieldAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new DeleteAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDElementAction(this, AddXSDElementAction.ID, "Add Element", false);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDElementAction(this, AddXSDElementAction.REF_ID, "Add Element Ref", true);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDModelGroupAction(this, XSDCompositor.SEQUENCE_LITERAL, AddXSDModelGroupAction.SEQUENCE_ID);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDModelGroupAction(this, XSDCompositor.CHOICE_LITERAL, AddXSDModelGroupAction.CHOICE_ID);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDModelGroupAction(this, XSDCompositor.ALL_LITERAL, AddXSDModelGroupAction.ALL_ID);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDModelGroupDefinitionAction(this, false);
    action.setId(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITION_ID);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDModelGroupDefinitionAction(this, true);
    action.setId(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITIONREF_ID);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDComplexTypeDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDSimpleTypeDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new AddXSDAttributeDeclarationAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new OpenInNewEditor(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new ShowPropertiesViewAction(this);
    registry.registerAction(action);
    action = new AddXSDAttributeGroupDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new DeleteXSDConcreteComponentAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    
    SetTypeAction setNewComplexTypeAction = new SetTypeAction("New...", SetTypeAction.SET_NEW_TYPE_ID, this);
    setNewComplexTypeAction.setSelectionProvider(getSelectionManager());
    registry.registerAction(setNewComplexTypeAction);
        
    SetTypeAction setExistingTypeAction = new SetTypeAction("Browse...", SetTypeAction.SELECT_EXISTING_TYPE_ID, this);
    setExistingTypeAction.setSelectionProvider(getSelectionManager());
    registry.registerAction(setExistingTypeAction);

    addMultiplicityMenu(registry);
  }
  
  protected void addMultiplicityMenu(ActionRegistry registry)
  {
    SetMultiplicityAction oneMultiplicity = new SetMultiplicityAction(this, "1..1 (" + "Required" + ")", SetMultiplicityAction.REQUIRED_ID);
    oneMultiplicity.setMaxOccurs(1);
    oneMultiplicity.setMinOccurs(1);
    oneMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(oneMultiplicity);

    SetMultiplicityAction zeroOrMoreMultiplicity = new SetMultiplicityAction(this, "0..* (" + "Zero or more" + ")", SetMultiplicityAction.ZERO_OR_MORE_ID);
    zeroOrMoreMultiplicity.setMaxOccurs(-1);
    zeroOrMoreMultiplicity.setMinOccurs(0);
    zeroOrMoreMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(zeroOrMoreMultiplicity);
    
    SetMultiplicityAction zeroOrOneMultiplicity = new SetMultiplicityAction(this, "0..1 (" + "Optional" + ")", SetMultiplicityAction.ZERO_OR_ONE_ID);
    zeroOrOneMultiplicity.setMaxOccurs(1);
    zeroOrOneMultiplicity.setMinOccurs(0);
    zeroOrOneMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(zeroOrOneMultiplicity);

    SetMultiplicityAction oneOrMoreMultiplicity = new SetMultiplicityAction(this, "1..* (" + "One or more" + ")", SetMultiplicityAction.ONE_OR_MORE_ID);
    oneOrMoreMultiplicity.setMaxOccurs(-1);
    oneOrMoreMultiplicity.setMinOccurs(1);
    oneOrMoreMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(oneOrMoreMultiplicity);
    
  }


  /**
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

  /**
   * Returns <code>true</code> if the command stack is dirty
   * 
   * @see org.eclipse.ui.ISaveablePart#isDirty()
   */
  public boolean isDirty()
  {
    super.isDirty();
    return structuredTextEditor.isDirty() || getCommandStack().isDirty();
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
   * Listener on SSE's outline page's selections that converts DOM selections
   * into xsd selections and notifies XSD selection manager
   */
  class OutlineTreeSelectionChangeListener implements ISelectionChangedListener, IDoubleClickListener
  {
    public OutlineTreeSelectionChangeListener()
    {
    }

    private ISelection getXSDSelection(ISelection selection)
    {
      ISelection sel = null;
      if (selection instanceof IStructuredSelection)
      {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Object o = structuredSelection.getFirstElement();
        if (o != null)
        {
          if (o instanceof CategoryAdapter)
          {
            XSDBaseAdapter baseAdapter = (XSDBaseAdapter)XSDAdapterFactory.getInstance().adapt(xsdSchema);
            sel = new StructuredSelection(baseAdapter);
          }
          else
            sel = new StructuredSelection(o);
        }
          
      }
      return sel;
    }

    /**
     * Determines DOM node based on object (xsd node)
     * 
     * @param object
     * @return
     */
    private Object getObjectForOtherModel(Object object)
    {
      Node node = null;
      if (object instanceof Node)
      {
        node = (Node) object;
      }
      else if (object instanceof XSDComponent)
      {
        node = ((XSDComponent) object).getElement();
      }
      else if (object instanceof CategoryAdapter)
      {
        node = ((CategoryAdapter) object).getXSDSchema().getElement();
      }
      else if (object instanceof XSDBaseAdapter)
      {
        if (((XSDBaseAdapter) object).getTarget() instanceof XSDConcreteComponent)
        {
          node = ((XSDConcreteComponent) ((XSDBaseAdapter) object).getTarget()).getElement();
        }
      }
      // the text editor can only accept sed nodes!
      //
      if (!(node instanceof IDOMNode))
      {
        node = null;
      }
      return node;
    }

    public void doubleClick(DoubleClickEvent event)
    {
      /*
       * Selection in outline tree changed so set outline tree's selection into
       * editor's selection and say it came from outline tree
       */
      if (getSelectionManager() != null && getSelectionManager().getEnableNotify())
      {
        ISelection selection = getXSDSelection(event.getSelection());
        if (selection != null)
        {
          getSelectionManager().setSelection(selection, fOutlinePage);
        }
        if (getTextEditor() != null && selection instanceof IStructuredSelection)
        {
          int start = -1;
          int length = 0;
          Object o = ((IStructuredSelection) selection).getFirstElement();
          if (o != null)
            o = getObjectForOtherModel(o);
          if (o instanceof IndexedRegion)
          {
            start = ((IndexedRegion) o).getStartOffset();
            length = ((IndexedRegion) o).getEndOffset() - start;
          }
          if (start > -1)
          {
            getTextEditor().selectAndReveal(start, length);
          }
        }
      }
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
      /*
       * Selection in outline tree changed so set outline tree's selection into
       * editor's selection and say it came from outline tree
       */
      if (getSelectionManager() != null && getSelectionManager().getEnableNotify())
      {
        ISelection selection = getXSDSelection(event.getSelection());
        if (selection != null)
        {
          getSelectionManager().setSelection(selection, fOutlinePage);
        }
      }
    }
  }
  /**
   * Listener on SSE's source editor's selections that converts DOM selections
   * into xsd selections and notifies XSD selection manager
   */
  private class SourceEditorSelectionListener implements ISelectionChangedListener
  {
    /**
     * Determines XSD node based on object (DOM node)
     * 
     * @param object
     * @return
     */
    private Object getXSDNode(Object object)
    {
      // get the element node
      Element element = null;
      if (object instanceof Node)
      {
        Node node = (Node) object;
        if (node != null)
        {
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            element = (Element) node;
          }
          else if (node.getNodeType() == Node.ATTRIBUTE_NODE)
          {
            element = ((Attr) node).getOwnerElement();
          }
        }
      }
      Object o = element;
      if (element != null)
      {
        Object modelObject = getXSDSchema().getCorrespondingComponent(element);
        if (modelObject != null)
        {
          o = modelObject;
          o = XSDAdapterFactory.getInstance().adapt((Notifier) modelObject);
        }
      }
      return o;
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
      if (getSelectionManager().getEnableNotify() && getActivePage() == 1)
      {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection)
        {
          List xsdSelections = new ArrayList();
          for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();)
          {
            Object domNode = i.next();
            Object xsdNode = getXSDNode(domNode);
            if (xsdNode != null)
            {
              xsdSelections.add(xsdNode);
            }
          }
          if (!xsdSelections.isEmpty())
          {
            StructuredSelection xsdSelection = new StructuredSelection(xsdSelections);
            getSelectionManager().setSelection(xsdSelection, getTextEditor().getSelectionProvider());
          }
        }
      }
    }
  }
  /**
   * Listener on XSD's selection manager's selections that converts XSD
   * selections into DOM selections and notifies SSE's selection provider
   */
  private class XSDSelectionManagerSelectionListener implements ISelectionChangedListener
  {
    /**
     * Determines DOM node based on object (xsd node)
     * 
     * @param object
     * @return
     */
    private Object getObjectForOtherModel(Object object)
    {
      Node node = null;
      if (object instanceof Node)
      {
        node = (Node) object;
      }
      else if (object instanceof XSDComponent)
      {
        node = ((XSDComponent) object).getElement();
      }
      else if (object instanceof CategoryAdapter)
      {
        node = ((CategoryAdapter) object).getXSDSchema().getElement();
      }
      else if (object instanceof XSDBaseAdapter)
      {
        if (((XSDBaseAdapter) object).getTarget() instanceof XSDConcreteComponent)
        {
          node = ((XSDConcreteComponent) ((XSDBaseAdapter) object).getTarget()).getElement();
        }
      }
      // the text editor can only accept sed nodes!
      //
      if (!(node instanceof IDOMNode))
      {
        node = null;
      }
      return node;
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
      // do not fire selection in source editor if selection event came
      // from source editor
      if (event.getSource() != getTextEditor().getSelectionProvider())
      {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection)
        {
          List otherModelObjectList = new ArrayList();
          for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();)
          {
            Object modelObject = i.next();
            Object otherModelObject = getObjectForOtherModel(modelObject);
            if (otherModelObject != null)
            {
              otherModelObjectList.add(otherModelObject);
            }
          }
          if (!otherModelObjectList.isEmpty())
          {
            // here's an ugly hack... if we allow text selections to fire during
            // SetInputToGraphView action we screw up the navigation history!
            //            
            //TODO (cs) ... we need to prevent the source editor from messing up the navigation history
            //
            if (getActivePage() == 1)
            {  
              StructuredSelection nodeSelection = new StructuredSelection(otherModelObjectList);
              getTextEditor().getSelectionProvider().setSelection(nodeSelection);
            }  
          }
        }
      }
    }
  }

  public INavigationLocation createEmptyNavigationLocation()
  {
    if (getActivePage() == 0)
    {
      return new DesignViewNavigationLocation(this);
    }
    else
    {
      return new MultiPageEditorTextSelectionNavigationLocation(getTextEditor(), false);
    }
  }

  public INavigationLocation createNavigationLocation()
  {
    if (getActivePage() == 0)
    {
      try
      {
        RootEditPart rootEditPart = graphicalViewer.getRootEditPart();
        EditPart editPart = rootEditPart.getContents();
        if (editPart instanceof RootContentEditPart)
        {
          RootContentEditPart rootContentEditPart = (RootContentEditPart)editPart;
          Object input = rootContentEditPart.getInput();      
          if (input instanceof Adapter)
          {
            XSDConcreteComponent concreteComponent = (XSDConcreteComponent)((Adapter)input).getTarget();
            return new DesignViewNavigationLocation(this, concreteComponent);
          }
        }   
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
    }
    else
    {
      return new MultiPageEditorTextSelectionNavigationLocation(getTextEditor(), true);
    }
  }
}  