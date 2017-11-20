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
package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.RedefineCategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDRedefineAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.AddFieldAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseDirectEditAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DesignSelectAll;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.design.IKeyboardDrag;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.adt.editor.EditorMode;
import org.eclipse.wst.xsd.ui.internal.adt.editor.EditorModeManager;
import org.eclipse.wst.xsd.ui.internal.adt.editor.IADTEditorInput;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ProductCustomizationProvider;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlinePage;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.TypeVizEditorMode;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyAttributeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDComplexTypeDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedAttributeGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedComplexTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedSimpleTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDSchemaDirectiveAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDSimpleTypeDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.DeleteXSDConcreteComponentAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.IDocumentChangedNotifier;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.navigation.DesignViewNavigationLocation;
import org.eclipse.wst.xsd.ui.internal.navigation.MultiPageEditorTextSelectionNavigationLocation;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.wst.xsd.ui.internal.utils.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InternalXSDMultiPageEditor extends ADTMultiPageEditor implements ITabbedPropertySheetPageContributor, INavigationLocationProvider
{
  // IModel model;
  IStructuredModel structuredModel;
  XSDSchema xsdSchema;
  XSDModelAdapter schemaNodeAdapter;
  private OutlineTreeSelectionChangeListener fOutlineListener;
  private SourceEditorSelectionListener fSourceEditorSelectionListener;
  private XSDSelectionManagerSelectionListener fXSDSelectionListener;
  private InternalDocumentChangedNotifier internalDocumentChangedNotifier = new InternalDocumentChangedNotifier();
  private static final String XSD_EDITOR_MODE_EXTENSION_ID = "org.eclipse.wst.xsd.ui.editorModes"; //$NON-NLS-N$ 
  private XSDPreferenceStoreListener xsdPreferenceStoreListener;
  
  class InternalDocumentChangedNotifier implements IDocumentChangedNotifier
  {
    List list = new ArrayList();
    
    public void addListener(INodeAdapter adapter)
    {
      list.add(adapter);
    }

    public void removeListener(INodeAdapter adapter)
    {
      list.remove(adapter);
    }
    
    public void notifyListeners(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
    {
      List clone = new ArrayList(list.size());     
      clone.addAll(list);
      for (Iterator i = clone.iterator(); i.hasNext(); )
      {
        INodeAdapter adapter = (INodeAdapter)i.next();
        adapter.notifyChanged(notifier, eventType, changedFeature, oldValue, newValue, pos);
      }
    }
  }
  
  protected void setInputToGraphicalViewer(IDocument newDocument)
  {
    IStructuredModel structuredModel = null;
    try
    {
      // only set input if structured model already exists
      // (meaning source editor is already managing the model)
      structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(newDocument);
      if ((structuredModel != null) && (structuredModel instanceof IDOMModel))
      {
        Document doc = ((IDOMModel) structuredModel).getDocument();
        if (doc != null)
        {
          XSDModelAdapter modelAdapter = XSDModelAdapter.lookupOrCreateModelAdapter(doc);
          if (modelAdapter != null) // Assert should not be null
          {
            modelAdapter.setSchema(xsdSchema);
            xsdSchema = modelAdapter.resetSchema(doc);
            model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
          }
        }
      }
    }
    catch (Exception e)
    {
    }
    finally
    {
      if (structuredModel != null)
      {
        structuredModel.releaseFromRead();
      }
    }
  }
  
 
  public IModel buildModel()
  {
    try
    {      
      IEditorInput editorInput = getEditorInput();
      
      // If the input schema is from the WSDL Editor, then use that inline schema
      if (editorInput instanceof IADTEditorInput)
      {
        xsdSchema = ((IADTEditorInput) editorInput).getSchema();
        if (xsdSchema != null)
          model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
      }
      
      Document document = null;
      IDocument doc = structuredTextEditor.getDocumentProvider().getDocument(getEditorInput());
      if (doc instanceof IStructuredDocument)
      {
        IStructuredModel model = null;
        try
        {
        // TODO: for StorageEditorInputs, should be forRead
          model = StructuredModelManager.getModelManager().getExistingModelForEdit(doc);
          if (model == null)
          {
            model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) doc);
          }
          document = ((IDOMModel) model).getDocument();
        }
        finally
        {
          if (model != null)
          {
            model.releaseFromEdit();
          }
        }
      }
      Assert.isNotNull(document);

      if (model != null)
        return model;
      
      xsdSchema = XSDModelAdapter.lookupOrCreateSchema(document);
      model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);              
    }
    catch (Exception e)
    {

    }
    
//    try
//    {
//      EPackage.Registry reg = EPackage.Registry.INSTANCE;
//      XSDPackage xsdPackage = (XSDPackage) reg.getEPackage(XSDPackage.eNS_URI);
//      xsdSchema = xsdPackage.getXSDFactory().createXSDSchema();
//      resourceSet = XSDSchemaImpl.createResourceSet();
//      IFile resourceFile = editorInput.getFile();
//      structuredModel = StructuredModelManager.getModelManager().getModelForEdit(resourceFile);
//      // If the resource is in the workspace....
//      // otherwise the user is trying to open an external file
//      if (resourceFile != null)
//      {
//        String pathName = resourceFile.getFullPath().toString();
//        xsdResource = resourceSet.getResource(URI.createPlatformResourceURI(pathName), true);
//        resourceSet.getResources().add(xsdResource);
//        Object obj = xsdResource.getContents().get(0);
//        if (obj instanceof XSDSchema)
//        {
//          xsdSchema = (XSDSchema) obj;
//          xsdSchema.setElement(((IDOMModel) structuredModel).getDocument().getDocumentElement());
//          model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
//        }
//        
//        // If the input schema is from the WSDL Editor, then use that inline schema
//        if (editorInput instanceof XSDFileEditorInput)
//        {
//          xsdSchema = ((XSDFileEditorInput) editorInput).getSchema();
//          model = (IModel) XSDAdapterFactory.getInstance().adapt(xsdSchema);
//        }
//        if (xsdSchema.getElement() != null)
//          
//          // TODO (cs) ... we need to look into performance issues when we add elements
//          // seems to be that formatting is causig lots of notification and things get terribly slow
//          // I'm specializing the method below to add an isModelStateChanging check that should
//          // help here ... but we need to investigate further
//          new XSDModelReconcileAdapter(xsdSchema.getElement().getOwnerDocument(), xsdSchema)
//          {
//            public void handleNotifyChange(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
//            {
//              if (notifier instanceof NodeImpl)
//              {
//                NodeImpl nodeImpl = (NodeImpl)notifier;
//                if (!nodeImpl.getModel().isModelStateChanging())
//                {             
//                  super.handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
//                  internalDocumentChangedNotifier.notifyListeners(notifier, eventType, feature, oldValue, newValue, index);
//                }  
//              }
//            }
//          };
//        xsdResource.setModified(false);
//      }
//    }
//    catch (StackOverflowError e)
//    {
//    }
//    catch (Exception ex)
//    {
//    }
    return model;
  }

  public void dispose()
  {
    IStructuredModel structuredModel = null;
    XSDModelAdapter modelAdapter = null;
    IDOMDocument doc = null;
    IDocument idoc = structuredTextEditor.getDocumentProvider().getDocument(getEditorInput());
    if (idoc != null)
    {
      structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(idoc);
      if ((structuredModel != null) && (structuredModel instanceof IDOMModel))
      {
        try
        {
          if ((structuredModel != null) && (structuredModel instanceof IDOMModel))
          {
            doc = ((IDOMModel) structuredModel).getDocument();
            if (doc != null)
            {
              modelAdapter = (XSDModelAdapter) doc.getExistingAdapter(XSDModelAdapter.class);
              if (modelAdapter != null)
              {
                doc.getModel().removeModelStateListener(modelAdapter.getModelReconcileAdapter());
                doc.removeAdapter(modelAdapter.getModelReconcileAdapter());
                doc.removeAdapter(modelAdapter);
                modelAdapter.clear();
                modelAdapter = null;
              }
            }
          }
        }
        finally
        {
          structuredModel.releaseFromRead();
        }
      }
    }
   


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
    XSDEditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(xsdPreferenceStoreListener);
    xsdPreferenceStoreListener = null;
    super.dispose();
  }

  protected void initializeGraphicalViewer()
  {
    RootContentEditPart root = new RootContentEditPart();
    if (!(getEditorInput() instanceof IADTEditorInput))
    {
      root.setModel(model);
    }
    graphicalViewer.setContents(root);
  }
  
  protected void configureGraphicalViewer()
  {
    super.configureGraphicalViewer();
    graphicalViewer.getKeyHandler().put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
    // get edit part factory from extension
    EditPartFactory editPartFactory = getEditorModeManager().getCurrentMode().getEditPartFactory();
    graphicalViewer.setEditPartFactory(editPartFactory);   
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
      return getSelectionManager();
    }  
    else if (type == XSDSchema.class)
    {
      return xsdSchema;
    }
    else if (type == IKeyboardDrag.class)
    {
      return new KeyboardDragImpl();
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
    else if (type == XSDAttributeReferenceEditManager.class)
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
    		return new XSDAttributeReferenceEditManager(fileEditorInput.getFile(), schemas);
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
    else if (type == XSDComplexTypeBaseTypeEditManager.class)
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
        return new XSDComplexTypeBaseTypeEditManager(fileEditorInput.getFile(), schemas);
      }
    }
    else if (type == XSDSubstitutionGroupEditManager.class)
    {
      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof IFileEditorInput)
      {
        IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
        XSDSchema[] schemas = {xsdSchema};
        return new XSDSubstitutionGroupEditManager(fileEditorInput.getFile(), schemas);
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
    else if (type == IDocumentChangedNotifier.class)
    {
      return internalDocumentChangedNotifier;
    }  
    else if (type == ProductCustomizationProvider.class)
    {
      return XSDEditorPlugin.getPlugin().getProductCustomizationProvider();
    }  
    return super.getAdapter(type);
  }

  public String getContributorId()
  {
    return "org.eclipse.wst.xsd.ui.internal.editor"; //$NON-NLS-1$
  }

  public XSDSchema getXSDSchema()
  {
    return xsdSchema;
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
    XSDConcreteComponent namedComponent = openOnSelectionHelper.openOnGlobalReference(comp);
    
    if (namedComponent == null)
    {
      namedComponent = getXSDSchema();
    }
    XSDBaseAdapter adapter = (XSDBaseAdapter) XSDAdapterFactory.getInstance().adapt(namedComponent);
    getSelectionManager().setSelection(new StructuredSelection(adapter));
    IAction action = getActionRegistry().getAction(SetInputToGraphView.ID);
    if (action != null)
    {
      action.run();
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
    super.createPages();
    
//    selectionProvider = getSelectionManager();
//    getEditorSite().setSelectionProvider(selectionProvider);
//    
//    structuredTextEditor = new StructuredTextEditor();
//    model = buildModel((IFileEditorInput) getEditorInput());
//    createGraphPage();
//    createSourcePage();

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
    
    xsdPreferenceStoreListener = new XSDPreferenceStoreListener();
    XSDEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(xsdPreferenceStoreListener);
  }

  protected class XSDPreferenceStoreListener implements IPropertyChangeListener
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      String property = evt.getProperty();
      if (XSDEditorPlugin.CONST_SHOW_EXTERNALS.equals(property))
      {
        ((GraphicalEditPart) graphicalViewer.getContents()).getFigure().invalidateTree();
        graphicalViewer.getContents().refresh();
      }
    }
  }

  public boolean isReadOnly()
  {
    IEditorInput editorInput = getEditorInput();
    return !(editorInput instanceof IFileEditorInput || editorInput instanceof FileStoreEditorInput);
  }
  
  protected void createActions()
  {
    super.createActions();
    
    ActionRegistry registry = getActionRegistry();
    
    // add an isReadOnly method to the editorPart instead
    if (!isReadOnly())
    {
    BaseSelectionAction action = new AddFieldAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    action = new DeleteAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    
    action = new DesignSelectAll(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    
    action = new AddXSDRedefinedComplexTypeAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, Messages._UI_IMAGE_COMPLEX_TYPE));
    registry.registerAction(action);
    
    action = new AddXSDRedefinedSimpleTypeAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, Messages._UI_IMAGE_SIMPLE_TYPE));
    registry.registerAction(action);
    
    action = new AddXSDRedefinedAttributeGroupAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, Messages._UI_IMAGE_ATTRIBUTE_GROUP));
    registry.registerAction(action);
    
    action = new AddXSDRedefinedModelGroupAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, Messages._UI_IMAGE_MODEL_GROUP));
    registry.registerAction(action);
    
    action = new SetBaseTypeAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);
    
    action = new AddXSDElementAction(this, AddXSDElementAction.ID, Messages._UI_ACTION_ADD_ELEMENT, false);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDElement.gif"));
    registry.registerAction(action);

    action = new AddXSDElementAction(this, AddXSDElementAction.BEFORE_SELECTED_ID, Messages._UI_ACTION_BEFORE, false);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDElement.gif"));
    registry.registerAction(action);       
    
    action = new AddXSDElementAction(this, AddXSDElementAction.AFTER_SELECTED_ID, Messages._UI_ACTION_AFTER, false);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDElement.gif"));
    registry.registerAction(action);      
    
    action = new AddXSDElementAction(this, AddXSDElementAction.REF_ID, Messages._UI_ACTION_ADD_ELEMENT_REF, true);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDElementRef.gif"));
    registry.registerAction(action);

    action = new AddXSDModelGroupAction(this, XSDCompositor.SEQUENCE_LITERAL, AddXSDModelGroupAction.SEQUENCE_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSequence.gif"));
    registry.registerAction(action);

    action = new AddXSDModelGroupAction(this, XSDCompositor.CHOICE_LITERAL, AddXSDModelGroupAction.CHOICE_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDChoice.gif"));
    registry.registerAction(action);
    
    action = new AddXSDModelGroupAction(this, XSDCompositor.ALL_LITERAL, AddXSDModelGroupAction.ALL_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAll.gif"));
    registry.registerAction(action);

    action = new AddXSDModelGroupDefinitionAction(this, false);
    action.setId(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITION_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDGroup.gif"));
    registry.registerAction(action);

    action = new AddXSDModelGroupDefinitionAction(this, true);
    action.setId(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITIONREF_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/obj16/XSDGroupRef.gif"));
    registry.registerAction(action);

    action = new AddXSDComplexTypeDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDComplexType.gif"));
    registry.registerAction(action);

    action = new AddXSDSimpleTypeDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSimpleType.gif"));
    registry.registerAction(action);

    action = new AddXSDAttributeDeclarationAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttribute.gif"));
    registry.registerAction(action);

    action = new AddXSDAttributeDeclarationAction(this, AddXSDAttributeDeclarationAction.BEFORE_SELECTED_ID, Messages._UI_ACTION_BEFORE, false);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttribute.gif"));
    registry.registerAction(action);
    
    action = new AddXSDAttributeDeclarationAction(this, AddXSDAttributeDeclarationAction.AFTER_SELECTED_ID, Messages._UI_ACTION_AFTER, false);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttribute.gif"));
    registry.registerAction(action);
    
    action = new AddXSDAttributeDeclarationAction(this, AddXSDAttributeDeclarationAction.REF_ID, Messages._UI_ACTION_ADD_ATTRIBUTE_REF, true);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttributeRef.gif"));
    registry.registerAction(action);

    action = new OpenInNewEditor(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);

    action = new ShowPropertiesViewAction(this);
    registry.registerAction(action);

    action = new AddXSDAttributeGroupDefinitionAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttributeGroup.gif"));
    registry.registerAction(action);

    action = new AddXSDAttributeGroupDefinitionAction(this, AddXSDAttributeGroupDefinitionAction.REF_ID);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAttributeGroupRef.gif"));
    registry.registerAction(action);

    action = new DeleteXSDConcreteComponentAction(this);
    action.setSelectionProvider(getSelectionManager());
    registry.registerAction(action);

    action = new AddXSDAnyElementAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAny.gif"));
    registry.registerAction(action);
    
    action = new AddXSDAnyAttributeAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDAnyAttribute.gif"));
    registry.registerAction(action);
    
    action = new AddXSDSchemaDirectiveAction(this, AddXSDSchemaDirectiveAction.INCLUDE_ID, Messages._UI_ACTION_ADD_INCLUDE);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDInclude.gif"));
    registry.registerAction(action);

    action = new AddXSDEnumerationFacetAction(this);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSimpleEnum.gif"));
    registry.registerAction(action);
    
    action = new AddXSDEnumerationFacetAction(this, AddXSDEnumerationFacetAction.BEFORE_SELECTED_ID, Messages._UI_ACTION_BEFORE);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSimpleEnum.gif"));
    registry.registerAction(action);       

    action = new AddXSDEnumerationFacetAction(this, AddXSDEnumerationFacetAction.AFTER_SELECTED_ID, Messages._UI_ACTION_AFTER);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSimpleEnum.gif"));
    registry.registerAction(action);       

    action = new AddXSDSchemaDirectiveAction(this, AddXSDSchemaDirectiveAction.IMPORT_ID, Messages._UI_ACTION_ADD_IMPORT);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDImport.gif"));
    registry.registerAction(action);

    action = new AddXSDSchemaDirectiveAction(this, AddXSDSchemaDirectiveAction.REDEFINE_ID, Messages._UI_ACTION_ADD_REDEFINE);
    action.setSelectionProvider(getSelectionManager());
    action.setImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDRedefine.gif"));
    registry.registerAction(action);
    
    SetTypeAction setNewComplexTypeAction = new SetTypeAction(Messages._UI_ACTION_NEW, SetTypeAction.SET_NEW_TYPE_ID, this);
    setNewComplexTypeAction.setSelectionProvider(getSelectionManager());
    
    registry.registerAction(setNewComplexTypeAction);
        
    SetTypeAction setExistingTypeAction = new SetTypeAction(Messages._UI_ACTION_BROWSE, SetTypeAction.SELECT_EXISTING_TYPE_ID, this);
    setExistingTypeAction.setSelectionProvider(getSelectionManager());
    registry.registerAction(setExistingTypeAction);

    addMultiplicityMenu(registry);
    
    PrintAction printAction = new PrintAction(this);
    registry.registerAction(printAction);
    
    BaseDirectEditAction directEditAction = new BaseDirectEditAction(this);
    directEditAction.setSelectionProvider(getSelectionManager());
    registry.registerAction(directEditAction);
    
    }
    else
    {
      BaseSelectionAction action = new OpenInNewEditor(this);
      action.setSelectionProvider(getSelectionManager());
      registry.registerAction(action);

      action = new ShowPropertiesViewAction(this);
      registry.registerAction(action);
      
      PrintAction printAction = new PrintAction(this);
      registry.registerAction(printAction);
    }
  }
  
  protected void addMultiplicityMenu(ActionRegistry registry)
  {
    SetMultiplicityAction oneMultiplicity = new SetMultiplicityAction(this, "1..1 (" + Messages._UI_LABEL_REQUIRED + ")", SetMultiplicityAction.REQUIRED_ID); //$NON-NLS-1$ //$NON-NLS-2$
    oneMultiplicity.setMaxOccurs(1);
    oneMultiplicity.setMinOccurs(1);
    oneMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(oneMultiplicity);

    SetMultiplicityAction zeroOrMoreMultiplicity = new SetMultiplicityAction(this, "0..* (" + Messages._UI_LABEL_ZERO_OR_MORE + ")", SetMultiplicityAction.ZERO_OR_MORE_ID); //$NON-NLS-1$ //$NON-NLS-2$
    zeroOrMoreMultiplicity.setMaxOccurs(-1);
    zeroOrMoreMultiplicity.setMinOccurs(0);
    zeroOrMoreMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(zeroOrMoreMultiplicity);
    
    SetMultiplicityAction zeroOrOneMultiplicity = new SetMultiplicityAction(this, "0..1 (" + Messages._UI_LABEL_OPTIONAL + ")", SetMultiplicityAction.ZERO_OR_ONE_ID); //$NON-NLS-1$ //$NON-NLS-2$
    zeroOrOneMultiplicity.setMaxOccurs(1);
    zeroOrOneMultiplicity.setMinOccurs(0);
    zeroOrOneMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(zeroOrOneMultiplicity);

    SetMultiplicityAction oneOrMoreMultiplicity = new SetMultiplicityAction(this, "1..* (" + Messages._UI_LABEL_ONE_OR_MORE + ")", SetMultiplicityAction.ONE_OR_MORE_ID); //$NON-NLS-1$ //$NON-NLS-2$
    oneOrMoreMultiplicity.setMaxOccurs(-1);
    oneOrMoreMultiplicity.setMinOccurs(1);
    oneOrMoreMultiplicity.setSelectionProvider(getSelectionManager());
    registry.registerAction(oneOrMoreMultiplicity);
    
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
      else if (object instanceof RedefineCategoryAdapter)
      {
        node = ((RedefineCategoryAdapter) object).getXSDRedefine().getElement();
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
          else if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
          {
            return model; 
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
            if (xsdNode instanceof XSDRedefineAdapter)
            	return;
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
      else if (object instanceof RedefineCategoryAdapter)
      {
        RedefineCategoryAdapter category = (RedefineCategoryAdapter)object;
        node = category.getXSDRedefine().getElement();
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
      else if (object instanceof String)
      {
        // This case was added to make the F3/hyperlink navigation work when an
        // inline schema from a WSDL document is opened in the schema editor.
        // The string is expected to be a URI fragment used to identify an XSD
        // component in the context of the enclosing WSDL resource.

        String uriFragment = (String) object;
        Resource resource = xsdSchema.eResource();
        EObject modelObject = resource.getEObject(uriFragment);

        if (modelObject != null && modelObject instanceof XSDComponent)
        {
          XSDComponent component = (XSDComponent) modelObject;
          node = component.getElement();
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
		// do not fire selection in source editor if the current active page is the InternalXSDMultiPageEditor (source)
		// We only want to make source selections if the active page is either the outline or properties (a modify
		// has been done via the outline or properties and not the source view).  We don't want to be selecting
		// and unselecting things in the source when editing in the source!!
    	boolean makeSelection = true;
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page.getActivePart() instanceof InternalXSDMultiPageEditor) {
				if (getActiveEditor() instanceof StructuredTextEditor) {
					makeSelection = false;
				}
			}
		}
    	
      // do not fire selection in source editor if selection event came
      // from source editor
      if (event.getSource() != getTextEditor().getSelectionProvider() && makeSelection)
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
            if (getActivePage() == SOURCE_PAGE_INDEX)
            {  
              StructuredSelection nodeSelection = new StructuredSelection(otherModelObjectList);
              getTextEditor().getSelectionProvider().setSelection(nodeSelection);
            }  
          }
        }
      }
    }
    
    public void doSetSelection()
    {
      ISelection iSelection = getSelectionManager().getSelection();
      if (iSelection != null)
      {
        Object firstElement = ((StructuredSelection)iSelection).getFirstElement();
        Object otherModelObject = getObjectForOtherModel(firstElement);
        if (otherModelObject != null)
        {
          StructuredSelection nodeSelection = new StructuredSelection(otherModelObject);
          getTextEditor().getSelectionProvider().setSelection(nodeSelection);
        }
      }
    }
  }
  
  // Bug 145590.  Workaround to update source position when flipping to the source page.
  // Unfortunately, this will still add an entry to the navigation history, but this
  // behaviour is an improvement than without this change
  boolean doUpdateSourceLocation = false;
  protected void pageChange(int newPageIndex)
  {
    super.pageChange(newPageIndex);
    doUpdateSourceLocation = newPageIndex == SOURCE_PAGE_INDEX;
    if (doUpdateSourceLocation && fXSDSelectionListener != null)
      fXSDSelectionListener.doSetSelection();

  }
  
  public void propertyChanged(Object source, int propId)
  {
    switch (propId)
    {
      // when refactor rename while file is open in editor, need to reset
      // editor contents to reflect new document
      case IEditorPart.PROP_INPUT:
      {
        if (source == structuredTextEditor && xsdSchema != null)
        {
          IStructuredModel structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
          try
          {
            if (structuredModel instanceof IDOMModel)
            {
              Document schemaDocument = xsdSchema.getDocument();
              Document domModelDocument = ((IDOMModel)structuredModel).getDocument();
              // if dom documents are not the same, they need to be reset
              if (schemaDocument != domModelDocument)
              {
                XSDModelAdapter modelAdapter = null;
                if (schemaDocument instanceof IDOMDocument)
                {
                  // save this model adapter for cleanup later
                  modelAdapter = (XSDModelAdapter) ((IDOMDocument)schemaDocument).getExistingAdapter(XSDModelAdapter.class);
                }
                
                // update multipage editor with new editor input
                IEditorInput editorInput = structuredTextEditor.getEditorInput();
                setInput(editorInput);
                setPartName(editorInput.getName());
                getCommandStack().markSaveLocation();
                
                // Now do the clean up model adapter
                if (modelAdapter != null)
                {
                  modelAdapter.clear();
                  modelAdapter = null;
                }
              }
            }
          }
          finally
          {
            if (structuredModel != null)
              structuredModel.releaseFromRead();
          }
        }
        break;
      }
    }
    super.propertyChanged(source, propId);
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
    if (getActivePage() == DESIGN_PAGE_INDEX)
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
            
            Object object = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(ProductCustomizationProvider.class);
            if (object instanceof ProductCustomizationProvider)
            {
              ProductCustomizationProvider productCustomizationProvider = (ProductCustomizationProvider)object;
              if (productCustomizationProvider != null)
              {
                return productCustomizationProvider.getNavigationLocation(this, concreteComponent, rootContentEditPart);
              }
            }
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
  
  
  public void editorModeChanged(EditorMode newEditorMode)
  {
    //if (isInitializing)
    //  return;
    
    EditPartFactory editPartFactory = newEditorMode.getEditPartFactory();
    if (editPartFactory != null)
    {  
      graphicalViewer.setEditPartFactory(editPartFactory);
      if (graphicalViewer instanceof DesignViewGraphicalViewer)
      {  
        DesignViewGraphicalViewer viewer = (DesignViewGraphicalViewer)graphicalViewer;  
        IADTObject input = viewer.getInput();
        viewer.setInput(null);
        //viewer.getRootEditPart().refresh();
       // viewer.getRootEditPart().getContents().refresh();
        viewer.setInput(input);
        
        floatingToolbar.setEditPartFactory(editPartFactory);
        floatingToolbar.setModel(getModel());
        floatingToolbar.refresh(!(input instanceof IModel));
        
        Control control = graphicalViewer.getControl();
        if (control instanceof Composite)
        {
          Composite parent = ((Composite)control).getParent();
          parent.layout();
        }
      }
    }  
    IContentProvider provider = newEditorMode.getOutlineProvider();
    if (provider != null)
    {
      ADTContentOutlinePage outline = (ADTContentOutlinePage)getContentOutlinePage();
      if (outline != null)
      {
        TreeViewer treeViewer = outline.getTreeViewer();
        if (treeViewer != null)
        {      
          outline.getTreeViewer().setContentProvider(provider);
          outline.getTreeViewer().refresh();
        }
      }  
    }  
  }  
  
  private static final String DEFAULT_EDITOR_MODE_ID = "org.eclipse.wst.xsd.ui.defaultEditorModeId"; //$NON-NLS-1$
  //private boolean isInitializing = false;
  protected EditorModeManager createEditorModeManager()
  {
    final ProductCustomizationProvider productCustomizationProvider = (ProductCustomizationProvider)getAdapter(ProductCustomizationProvider.class);
    EditorModeManager manager = new EditorModeManager(XSD_EDITOR_MODE_EXTENSION_ID)
    {
      public void init()
      {
        if (productCustomizationProvider == null || 
            productCustomizationProvider.isEditorModeApplicable(TypeVizEditorMode.ID))
        {  
          addMode(new TypeVizEditorMode());
        }  
        super.init();                
      }       
      
      protected EditorMode getDefaultMode()
      {
        String defaultModeId = XSDEditorPlugin.getPlugin().getPreferenceStore().getString(DEFAULT_EDITOR_MODE_ID);
        if (defaultModeId != null)
        {
          EditorMode editorMode = getEditorMode(defaultModeId);
          if (editorMode != null)
          {
            return editorMode;
          }  
        }               
        return super.getDefaultMode();
      }
    };
    manager.setProductCustomizationProvider(productCustomizationProvider);
    return manager;
  }
  
  protected void storeCurrentModePreference(String id)
  {
    XSDEditorPlugin.getPlugin().getPreferenceStore().setValue(DEFAULT_EDITOR_MODE_ID, id);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  public void doSave(IProgressMonitor monitor)
  {
    XSDDirectivesManager.removeUnusedXSDImports(((XSDSchema)getAdapter(XSDSchema.class)));
    super.doSave(monitor);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  public void doSaveAs()
  {
    // When performing a save as, the document changes.   Our model state listeners should listen
    // to the new document.
    
    // First get the current document
    IDocument currentDocument = getDocument();
    XSDModelAdapter modelAdapter = null;
    IDOMDocument doc = null;
    if (currentDocument != null)
    {
      IStructuredModel structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(currentDocument);
      if (structuredModel != null)
      {
        try
        {
          if ((structuredModel != null) && (structuredModel instanceof IDOMModel))
          {
            // Get the associated IDOMDocument model
            doc = ((IDOMModel) structuredModel).getDocument();
            // and now get our adapter that listens to DOM changes
            if (doc != null)
            {
              modelAdapter = (XSDModelAdapter) doc.getExistingAdapter(XSDModelAdapter.class);
            }
          }
        }
        finally
        {
          structuredModel.releaseFromRead();
        }
      }
    }
    
    IEditorInput editorInput = structuredTextEditor.getEditorInput();
    // perform save as
    structuredTextEditor.doSaveAs();
    // if saveAs cancelled then don't setInput because the input hasn't change
    // See AbstractDecoratedTextEditor's performSaveAs
    if (editorInput != structuredTextEditor.getEditorInput())
    {
      setInput(structuredTextEditor.getEditorInput());
      setPartName(structuredTextEditor.getEditorInput().getName());
    
      getCommandStack().markSaveLocation();
   
      // Now do the clean up on the old document
      if (modelAdapter != null)
      {
        // clear out model adapter
        modelAdapter.clear();
        modelAdapter = null;
      }
    }
  }
  
  protected void doPostEditorOpenTasks() 
  {
		// Bug 204868: do the selection after the editor part is created, so that 
		// AbstractTextEditor.markInNavigationHistory() won't cause the creation 
        // of its own editor part.
		// Select the schema to show the properties
		getSelectionManager().setSelection(new StructuredSelection(getModel()));
  }
}  