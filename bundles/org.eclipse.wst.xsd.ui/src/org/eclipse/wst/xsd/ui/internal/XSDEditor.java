/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.editors.text.JavaFileEditorInput;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.wst.common.ui.properties.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.internal.document.XMLModelImpl;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphViewer;
import org.eclipse.wst.xsd.ui.internal.util.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

// public class XSDEditor extends StructuredTextMultiPageEditorPart
public class XSDEditor extends XSDMultiPageEditorPart implements ITabbedPropertySheetPageContributor
{
  protected XSDTextEditor textEditor;
  IFile resourceFile;
  XSDSelectionManager xsdSelectionManager;

  private IStructuredModel result;
  private XMLModelImpl model;

  public XSDEditor()
  {
    super();
    xsdSelectionManager = new XSDSelectionManager();
  }

  InternalPartListener partListener = new InternalPartListener(this);
  
  // show outline view - defect 266116
  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
  {
    super.init(site, editorInput);
    
    IWorkbenchWindow dw=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page=dw.getActivePage();
    getSite().getPage().addPartListener(partListener);
    try
    {
      if (page != null)
      {
//      page.showView("org.eclipse.ui.views.ContentOutline");
        page.showView("org.eclipse.ui.views.PropertySheet");
      }
    } catch (PartInitException e) 
    {
//       e.printStackTrace();
    }      
   }
  
  // For team support
  //  protected PropertyDirtyChangeListener propertyChangeListener;

  /**
   * Creates the pages of this multi-page editor.
   * <p>
   * Subclasses of <code>MultiPageEditor</code> must implement this method.
   * </p>
   */
  protected void createPages()
  {
    try
    {
      if (!loadFile())
        return;

      // source page MUST be created before design page, now
      createSourcePage();

      addSourcePage();
      buildXSDModel();

      // comment this line out to hide the graph page 
      // 
      createAndAddGraphPage();

      int pageIndexToShow = getDefaultPageTypeIndex();
      setActivePage(pageIndexToShow);
      
      addCommandStackListener();
      
      XSDEditorPlugin.getPlugin().getPreferenceStore().addPropertyChangeListener(preferenceStoreListener);
    }
    catch (PartInitException exception)
    {
      throw new SourceEditingRuntimeException(SSECorePlugin.getResourceString("An_error_has_occurred_when1_ERROR_")); //$NON-NLS-1$ = "An error has occurred when initializing the input for the the editor's source page."
    }
  }

  public String[] getPropertyCategories()
  {
    return new String[] { "general", "namespace", "other", "attributes", "documentation", "facets" }; //$NON-NLS-1$
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySheetPageContributor#getContributorId()
	 */ 
  public String getContributorId()
	{
    return "org.eclipse.wst.xsd.ui.internal.XSDEditor";
    //return getSite().getId();
  }

  protected CommandStackListener commandStackListener;
  protected void addCommandStackListener()
  {
    if (commandStackListener == null)
    {
      IStructuredTextUndoManager undoManager = getModel().getUndoManager();
        commandStackListener = new CommandStackListener()
        {
          /**
           * @see org.eclipse.emf.common.command.CommandStackListener#commandStackChanged(EventObject)
           */
          public void commandStackChanged(EventObject event)
          {
            Object obj = event.getSource();
            if (obj instanceof BasicCommandStack)
            {
              BasicCommandStack stack = (BasicCommandStack) obj;
              Command recentCommand = stack.getMostRecentCommand();
              Command redoCommand = stack.getRedoCommand();
              Command undoCommand = stack.getUndoCommand();
              if (recentCommand == redoCommand)
              {
                // there must have been an undo reset info tasks 
                resetInformationTasks();
              }
            }
          }
        };

//TODO WTP Port        undoManager.getCommandStack().addCommandStackListener(commandStackListener);
      
    }
  } 

  protected void pageChange(int arg)
  {
    super.pageChange(arg);
  }

  protected void removeCommandStackListener()
  {
    if (commandStackListener != null)
    {
      IStructuredTextUndoManager undoManager = getModel().getUndoManager();
//TODO WTP Port      undoManager.getCommandStack().removeCommandStackListener(commandStackListener);
    }
  }

  // This is from the IValidateEditEditor interface
/*  public void undoChange()
  {
    StructuredTextUndoManager undoManager = textEditor.getModel().getUndoManager();   
    undoManager.undo();
    // Make the editor clean
    textEditor.getModel().setDirtyState(false);
  } */

  private class PreferenceStoreListener implements IPropertyChangeListener
  {
    /**
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event)
    {
    }
  }

  protected IPropertyChangeListener preferenceStoreListener = new PreferenceStoreListener();

  protected int getDefaultPageTypeIndex()
  {
    int pageIndex = sourcePageIndex;
    
    if (XSDEditorPlugin.getPlugin().getDefaultPage().equals(XSDEditorPlugin.GRAPH_PAGE))
    {
      if (graphPageIndex != -1)
        pageIndex = graphPageIndex;
    }
    
    return pageIndex;
  }
      
	int currentPage = -1;      
	public String getCurrentPageType()
	{
		// should update pref. for valid pages
		if (getActivePage() != -1)
		{
			currentPage = getActivePage();
		}
		if (currentPage == graphPageIndex)
		{
			return XSDEditorPlugin.GRAPH_PAGE;
		}
		else
		{
			return XSDEditorPlugin.SOURCE_PAGE;
    }
	}
	
	public Object getActivePart()
  {
		return getSite().getWorkbenchWindow().getActivePage().getActivePart();	
	}

  public void dispose()
  {
//    propertyChangeListener.dispose();
    removeCommandStackListener();
    
    XSDEditorPlugin.getPlugin().setDefaultPage(getCurrentPageType());
    XSDEditorPlugin.getPlugin().getPreferenceStore().removePropertyChangeListener(preferenceStoreListener);
    
    getSite().getPage().removePartListener(partListener);

    super.dispose();
  }

  protected boolean loadFile()
  {
    Object input = getEditorInput();

    if (input instanceof IFileEditorInput)
    {
      resourceFile = ((IFileEditorInput) input).getFile();
    }
    else if (input instanceof JavaFileEditorInput)
    {
      IPath path = ((JavaFileEditorInput)input).getPath(input);
      String ext = path.getFileExtension();
      if (ext != null && ext.equals("xsd"))
      {
        return true;
      }
      return false;
    }
    else
    {
//      XSDEditorPlugin.getPlugin().getMsgLogger().write("###Error...XSDEditor::createPages() .. Can't find input..Exiting..");
      return false;
    }
    return true;
  }

  /**
   * Method openOnGlobalReference.
   * The comp argument is a resolved xsd schema object from another file.  This is created and called from another
   * schema model to allow F3 navigation to open a new editor and choose the referenced object within that editor context
   * @param comp
   */
  public void openOnGlobalReference(XSDConcreteComponent comp)
  {
    openOnSelectionHelper.openOnGlobalReference(comp);
  }
  
  protected OpenOnSelectionHelper openOnSelectionHelper;

  public OpenOnSelectionHelper getOpenOnSelectionHelper()
  {
    return openOnSelectionHelper;
  }
  
  /**
   * @see org.eclipse.wst.xsd.ui.internal.XSDMultiPageEditorPart#createTextEditor()
   */
  protected StructuredTextEditor createTextEditor()
  {
    return new XSDTextEditor(this);
  }

  /*
   * @see StructuredTextMultiPageEditorPart#createSourcePage()
   */
  protected void createSourcePage() throws PartInitException
  {
    super.createSourcePage();
    
    textEditor = (XSDTextEditor) getTextEditor();

		openOnSelectionHelper = new OpenOnSelectionHelper(textEditor);
  }
 
  int sourcePageIndex = -1;
  /**
   * Adds the source page of the multi-page editor.
   */
  protected void addSourcePage() throws PartInitException {
  
    sourcePageIndex = addPage(textEditor, getEditorInput());
    setPageText(sourcePageIndex, XSDEditorPlugin.getXSDString("_UI_TAB_SOURCE"));

    // defect 223043 ... do textEditor.setModel() here instead of in createSourcePage()
		// the update's critical, to get viewer selection manager and highlighting to work
    IEditorInput editorInput = getEditorInput();
    if (editorInput instanceof IFileEditorInput)
    {
      textEditor.setModel((IFileEditorInput)getEditorInput());
    }
    else
    {
//      textEditor.setModel(editorInput);
    }
    textEditor.update();
		firePropertyChange(PROP_TITLE);
  }                       

  int graphPageIndex = -1;            
  XSDGraphViewer graphViewer;

  /**
   * Creates the graph page and adds it to the multi-page editor.
   */
  protected void createAndAddGraphPage() throws PartInitException 
  {                         
    graphViewer = new XSDGraphViewer(this);                              
    graphViewer.setSchema(xsdSchema);
    Control graphControl = graphViewer.createControl(getContainer());
    graphPageIndex = addPage(graphControl);
    setPageText(graphPageIndex, XSDEditorPlugin.getXSDString("_UI_TAB_GRAPH"));

    // graphViewer.setViewerSelectionManager(textEditor.getViewerSelectionManager());    
    graphViewer.setSelectionManager(getSelectionManager());
    
    // this forces the editor to initially select the top level schema object
    //
    getSelectionManager().setSelection(new StructuredSelection(textEditor.getXSDSchema()));    
  }

  /*
   * @see IAdaptable#getAdapter(Class)
   */
  public Object getAdapter(Class key)
  {
    Object result = null;
    if (key == ISelectionProvider.class)
    {
      result = xsdSelectionManager;
    }
    else 
    {
      result = textEditor.getAdapter(key);
    }
    return result;
  }

  public XSDSelectionManager getSelectionManager()
  {
    return xsdSelectionManager;
  }
 
  /**
   * @see org.eclipse.wst.xsd.ui.internal.XSDMultiPageEditorPart#doSaveAs()
   */
  public void doSaveAs()
  {
    super.doSaveAs();
  }

  public void doSave(org.eclipse.core.runtime.IProgressMonitor monitor)
  {
    super.doSave(monitor);
  }

  protected XSDSchema xsdSchema;
  protected ResourceSet resourceSet;

  public void reparseSchema()
  {
    Document document  = ((XMLModel)getModel()).getDocument();
    createSchema(document.getDocumentElement());
  }
  
  public XSDSchema createSchema(Node node)
  {
    try
    {
      EPackage.Registry reg = EPackage.Registry.INSTANCE; 
      XSDPackage xsdPackage = (XSDPackage)reg.getEPackage(XSDPackage.eNS_URI);
      xsdSchema = xsdPackage.getXSDFactory().createXSDSchema();
      
      // Force the loading of the "meta" schema for schema instance instance.
      //
      String schemaForSchemaNamespace = node.getNamespaceURI();
      XSDSchemaImpl.getSchemaForSchema(schemaForSchemaNamespace);

      resourceSet = XSDSchemaImpl.createResourceSet();
      resourceSet.setURIConverter(new XSDURIConverter(resourceFile));

      String pathName = "";
      // If the resource is in the workspace....
      // otherwise the user is trying to open an external file
      if (resourceFile != null)
      {
        pathName = resourceFile.getFullPath().toString();
        Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(pathName), true);
//      resource.getContents().add(xsdSchema);
        resourceSet.getResources().add(resource);
      
        Object obj = resource.getContents().get(0);
        if (obj instanceof XSDSchema)
        {
          xsdSchema = (XSDSchema)obj;
        }

//      URIConverter uriConverter = resourceSet.getURIConverter();
//      resourceSet.setURIConverter(new XSDURIConverter(resourceFile));
 
        xsdSchema.setElement((Element)node);
        resource.setModified(false);
      }
      else
      {
        xsdSchema.setElement((Element)node);
      }
    }
    catch (StackOverflowError e)
    {
//      XSDEditorPlugin.getPlugin().getMsgLogger().write("Stack overflow encountered.  Possibly an invalid recursive circular schema");
    }
    catch (Exception ex)
    {
//      ex.printStackTrace();
    }
    
    return xsdSchema;
  }

  class XSDDocumentAdapter extends DocumentAdapter
  {
  	INodeNotifier currentNotifier;
  	int currentEventType;
  	
		public XSDDocumentAdapter(Document document)
		{
			super(document);
		}

		boolean handlingNotifyChanged = false;

		public void notifyChanged(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
		{
			if (eventType == INodeNotifier.REMOVE) // don't handle remove events
			{
				return;
			}

			if (!handlingNotifyChanged)
			{
				handlingNotifyChanged = true;
				try
				{
					// delay handle events only in the source view
					if (getCurrentPageType() == XSDEditorPlugin.SOURCE_PAGE &&
							!(getActivePart() instanceof PropertySheet) && 
							!(getActivePart() instanceof org.eclipse.ui.views.contentoutline.ContentOutline)) {
						startDelayedEvent(notifier, eventType, feature, oldValue, newValue, index);
				    //handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
					}
					else // all other views, just handle the events right away
					{
						handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
					}
				}
				catch (Exception e)
				{
//					XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
				}
				handlingNotifyChanged = false;
			}
		}

		public void handleNotifyChange(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
		{
//			System.out.println(eventType + " : HandleNotifyChange " + notifier.hashCode() + " notifier " + notifier);
			switch (eventType)
			{
				case INodeNotifier.ADD:
				{
					if (newValue instanceof Element)
					{
						adapt((Element)newValue);
//	Add			updateParentForDerivation(node, listener);
					}
					break;
				}
				case INodeNotifier.CHANGE:
				{
					Node node = (Node)notifier;
			    XSDConcreteComponent listener = xsdSchema.getCorrespondingComponent(node);
  		    listener.elementAttributesChanged((Element)node);
    	    listener.elementChanged((Element)node);
    	    break;
				}
				case INodeNotifier.STRUCTURE_CHANGED:
				case INodeNotifier.CONTENT_CHANGED:
				{
					Node node = (Node)notifier;
					XSDConcreteComponent listener = xsdSchema.getCorrespondingComponent(node);
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						listener.elementContentsChanged((Element)node);
						break;
					}
					else if (node.getNodeType() == Node.DOCUMENT_NODE)
					{
						Element docElement = ((Document)node).getDocumentElement();
						// Need to add check if doc element is being edited in the source
						if (docElement != null)
					  {
						  String prefix = docElement.getPrefix();
						  String xmlnsString = prefix == null? "xmlns" : "xmlns:" + prefix;
						  Attr attr = docElement.getAttributeNode(xmlnsString);
						  boolean doParse = false;
						  if (attr != null)
						  {
						    if (attr.getValue().equals("http://www.w3.org/2001/XMLSchema") && docElement.getLocalName().equals("schema"))
						    {
						      // We have a viable schema so parse it
						      doParse = true;
						    }
						  }
						  
							if (doParse)
							{
					      adapt(docElement);
 						    xsdSchema.setElement(docElement);
							}
					  }
					}
					break;
				}
			}
		}

		protected DelayedEvent delayedTask;
		protected void startDelayedEvent(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
		{
//			System.out.println("start delayed event");
      // check if there is already a delayed task for the same notifier and eventType
//			if (delayedTask != null)
//			{
//  			Notifier aNotifier = delayedTask.getNotifier();
//	  		int anEventType = delayedTask.getEventType();
//  			if (notifier == aNotifier && anEventType == eventType)
//			  {
//				  // same event, just different data, delay new event
//				  delayedTask.setCancel(true);
//			  }
//			}

			delayedTask = new DelayedEvent();

			delayedTask.setNotifier(notifier);
			delayedTask.setEventType(eventType);
			delayedTask.setFeature(feature);
			delayedTask.setOldValue(oldValue);
			delayedTask.setNewValue(newValue);
			delayedTask.setIndex(index);

			Display.getDefault().timerExec(400,delayedTask);
		}

		class DelayedEvent implements Runnable
		{
		  INodeNotifier notifier;
			int eventType;
			Object feature;
			Object oldValue;
			Object newValue;
			int index;
			boolean cancelEvent = false;

			/*
			 * @see Runnable#run()
			 */
			public void run()
			{
				if (!cancelEvent)
				{
				  handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
				  if (delayedTask == this)
				  {
				  	delayedTask = null;
				  }
				}
			}
			
			public void setCancel(boolean flag)
			{
				cancelEvent = flag;
			}

			public void setNotifier(INodeNotifier notifier)
			{
				this.notifier = notifier;
			}
		
			public void setEventType(int eventType)
			{
				this.eventType = eventType;
			}

			public void setFeature(Object feature)
			{
				this.feature = feature;
			}

			public void setOldValue(Object oldValue)
			{
				this.oldValue = oldValue;			
			}

			public void setNewValue(Object newValue)
			{
				this.newValue = newValue;
			}

			public void setIndex(int index)
			{
				this.index = index;			
			}

			public INodeNotifier getNotifier()
			{
				return notifier;
			}

			public int getEventType()
			{
				return eventType;
			}

			public Object getNewValue()
			{
				return newValue;
			}

			public Object getOldValue()
			{
				return oldValue;
			}

		}
  }

  abstract class DocumentAdapter implements INodeAdapter
  {
    public DocumentAdapter(Document document)
    {
      ((INodeNotifier)document).addAdapter(this);
      adapt(document.getDocumentElement());
    }

    public void adapt(Element element)
    {
      if (((INodeNotifier)element).getExistingAdapter(this) == null)
      {

        ((INodeNotifier)element).addAdapter(this);

        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
          if (child.getNodeType() == Node.ELEMENT_NODE)
          {
            adapt((Element)child);
          }
        }
      }
    }

    public boolean isAdapterForType(Object type)
    {
      return type == this;
    }

    abstract public void notifyChanged
      (INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index);
  }


  /**
   * Method createDefaultSchemaNode.  Should only be called to insert a schema node into an empty document
   */
  public void createDefaultSchemaNode()
  {
    Document document  = ((XMLModel)getModel()).getDocument();
    if (document.getChildNodes().getLength() == 0)
    {
      // if it is a completely empty file, then add the encoding and version processing instruction
//TODO  String encoding = EncodingHelper.getDefaultEncodingTag();
      String encoding = "UTF-8";
      ProcessingInstruction instr = document.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"" + encoding + "\"");
      document.appendChild(instr);
    }

    // Create a default schema tag now

    // String defaultPrefixForTargetNamespace = getFileResource().getProjectRelativePath().removeFileExtension().lastSegment();
    String defaultPrefixForTargetNamespace = "tns";
    String prefixForSchemaNamespace = "";
    String schemaNamespaceAttribute = "xmlns";
    if (XSDEditorPlugin.getPlugin().isQualifyXMLSchemaLanguage())
    {
      // Added this if check before disallowing blank prefixes in the preferences...
      // Can take this out.  See also NewXSDWizard
      if (XSDEditorPlugin.getPlugin().getXMLSchemaPrefix().trim().length() > 0)
      {
        prefixForSchemaNamespace = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix() + ":";
        schemaNamespaceAttribute += ":" + XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
      }
    }
    
    document.appendChild(document.createTextNode("\n"));
    Element element = document.createElement(prefixForSchemaNamespace + XSDConstants.SCHEMA_ELEMENT_TAG);
    
    element.setAttribute(schemaNamespaceAttribute,"http://www.w3.org/2001/XMLSchema");
    
    String defaultTargetURI = XSDEditorPlugin.getPlugin().getXMLSchemaTargetNamespace();
    element.setAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE, defaultTargetURI);
    element.setAttribute("xmlns:" + defaultPrefixForTargetNamespace, defaultTargetURI);
    
    document.appendChild(element);
  }
  
  public void buildXSDModel()
  {
    try
    {
      Document document  = ((XMLModel)getModel()).getDocument();
      if (document.getChildNodes().getLength() == 0)
      {
        // this is an empty document.  Create a default schema tag now
        createDefaultSchemaNode();
      }

      createSchema(document.getDocumentElement());

			XSDDocumentAdapter documentAdapter =
				new XSDDocumentAdapter(((XMLModel)getModel()).getDocument());
    }
    catch (Exception e)
    {
//      XSDEditorPlugin.getPlugin().getMsgLogger().write("Failed to create Model");
//      XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
//      e.printStackTrace();
    }



//      XSDResourceFactoryImpl.validate(xsdSchema, input.getFile().getContents(true));
  }

//  private void updateParentForDerivation(Node node, XSDConcreteComponent correspondingComponent)
//  {
//    if (XSDDOMHelper.inputEquals(node, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false)||
//        XSDDOMHelper.inputEquals(node,XSDConstants.COMPLEXCONTENT_ELEMENT_TAG,false))
//    {
//      XSDComplexTypeDefinition xsdComplexTypeDefinition =
//        correspondingComponent.getContainer() instanceof XSDComplexTypeDefinition ? 
//          (XSDComplexTypeDefinition)correspondingComponent.getContainer() :
//          null;
//      if (xsdComplexTypeDefinition != null)
//      {
//        xsdComplexTypeDefinition.elementContentsChanged(xsdComplexTypeDefinition.getElement());
//      }
//    }
//  }    

//  private void checkUnion(Node node, XSDConcreteComponent correspondingComponent, int i)
//  {
//    // bug 219967 - union changes to restriction
//    if (XSDDOMHelper.inputEquals(node, XSDConstants.UNION_ELEMENT_TAG, false))
//    {
////      XSDConcreteComponent comp = correspondingComponent.getContainer();
////      if (comp != null)
////      {
////        switch (i)
////        {
////          case 1:
////            comp.elementAttributesChanged((Element)node);
////            break;
////          case 4:
////            comp.elementContentsChanged((Element)node);
////            break;
////        }
////      }
//    }
//    else
//    {
//      switch (i)
//      {
//        case 1:
//        case 4:
//          // do both types of updates since sometimes the attributes have changed indirectly
//          // because the content has changed
//          correspondingComponent.elementAttributesChanged((Element)node);
//          correspondingComponent.elementContentsChanged((Element)node);
//          break;
//      }
//    }
//  }
  
//  private void updateMap(XSDConcreteComponent listener, Element documentElement)
//  {
//    boolean handleChangeInSchema = false;
//    if (listener instanceof XSDSchema)
//    {
//      if (!handleChangeInSchema)
//      {
//        handleChangeInSchema = true;
//        XSDSchema xsdSchema = (XSDSchema)listener;
//        java.util.Map prefixToNameSpaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
//        String targetNamespace = xsdSchema.getTargetNamespace();
////        System.out.println("targetNamespace = " + targetNamespace);
//
//        NamedNodeMap attributes = documentElement.getAttributes();
//        int length = attributes.getLength();
//        
//        ArrayList keyList = new ArrayList();
//        keyList.addAll(prefixToNameSpaceMap.keySet());
//        
//        String key;
//
//
//        // update the map when the prefix is changed
//        CHECK: for (int i = 0; i < length; i++)
//        {
//          Attr attr = (Attr)attributes.item(i);
//          String name = attr.getNodeName();
//          
//          if (isValidXMLNSAttribute(name))
////          if (name.startsWith("xmlns"))
//          {
//            String value = attr.getNodeValue();
//            if (value == null)
//            {
//              break CHECK;
//            }
//            int index = name.indexOf(":");
//            key  = index == -1 ? null : name.substring(index + 1);
////            System.out.println(" Attribute key is " + key + " , value = " + value);
////            System.out.println("   map.get(key) = " + prefixToNameSpaceMap.get(key));
//            if (!prefixToNameSpaceMap.containsKey(key))
//            {
//              for (Iterator iter = keyList.iterator(); iter.hasNext(); )
//              {
//                String aPrefix = (String)iter.next();
////                System.out.println("    --> A Map Prefix is " + aPrefix);
////                System.out.println("    --> model map.get(prefix) " + prefixToNameSpaceMap.get(aPrefix));
//                if (prefixToNameSpaceMap.get(aPrefix) != null)
//                {
//                  if (prefixToNameSpaceMap.get(aPrefix).equals(value))
//                  {
//                    prefixToNameSpaceMap.remove(aPrefix);
//                  }
//                }
//              }
//            }
//            else if (prefixToNameSpaceMap.containsKey(key))
//            {
//              if (prefixToNameSpaceMap.get(key) != null)
//              {
//                if (!prefixToNameSpaceMap.get(key).equals(value))
//                {
//                  Set entrySet = prefixToNameSpaceMap.entrySet();
//                  for (Iterator iter = entrySet.iterator(); iter.hasNext(); )
//                  {
//                    Map.Entry aMapEntry = (Map.Entry)iter.next();
//                    if (  (key != null && (aMapEntry.getKey() != null && aMapEntry.getKey().equals(key)))
//                           || (key == null && (aMapEntry.getKey() == null)))
//                    {
//                      aMapEntry.setValue(value);
//                    }
//                  }
//                }
//              }
//              else
//              {
//                Set entrySet = prefixToNameSpaceMap.entrySet();
//                for (Iterator iter = entrySet.iterator(); iter.hasNext(); )
//                {
//                  Map.Entry aMapEntry = (Map.Entry)iter.next();
//                  if (  (key != null && (aMapEntry.getKey() != null && aMapEntry.getKey().equals(key)))
//                         || (key == null && (aMapEntry.getKey() == null)))
//                  {
//                    aMapEntry.setValue(value);
//                  }
//                }
//              }
//            }
//          }
//        }
//        
//        boolean modelMapPrefixFound = false;
//        for (Iterator iter = keyList.iterator(); iter.hasNext(); )
//        {
//          String aPrefix = (String)iter.next();
//          modelMapPrefixFound = false;
//          attributes = documentElement.getAttributes();
//          length = attributes.getLength();
//
//          for (int i = 0; i < length; i++)
//          {
//            Attr attr = (Attr)attributes.item(i);
//            if (attr != null)
//            {
//              String name = attr.getNodeName();
//            
//              // if (name.startsWith("xmlns"))
//              if (isValidXMLNSAttribute(name))
//              {
//                String value = attr.getNodeValue();
//                int index = name.indexOf(":");
//                key  = index == -1 ? null : name.substring(index + 1);
//                if (aPrefix == null && key == null)
//                {
//                  modelMapPrefixFound = true;
//                }
//                else if (aPrefix != null && (aPrefix.equals(key)))
//                {
//                  modelMapPrefixFound = true;
//                  if ((prefixToNameSpaceMap.get(key) != null && !prefixToNameSpaceMap.get(key).equals(value))
//                       || (prefixToNameSpaceMap.get(key) == null && value != null))
//                  {
//                    if (value != null && value.length() > 0)
//                    {
//                      prefixToNameSpaceMap.put(aPrefix, value);
//                    }
//                  }
//                }
//                else if (key != null && (key.equals(aPrefix)))
//                {
//                  modelMapPrefixFound = true;
//                }
//              }
//            }
//          }
//          if (!modelMapPrefixFound)
//          {
//            prefixToNameSpaceMap.remove(aPrefix);
//          }
//        }
//       
//        // to ensure map is recreated
////        XSDSchemaHelper.updateElement(xsdSchema);        
////        reparseSchema();
//
//        handleChangeInSchema = false;
//
////        System.out.println("XSDeditor Map is " + prefixToNameSpaceMap.values());
////        System.out.println("XSDeditor Map keys are " + prefixToNameSpaceMap.keySet());
//        
//      }
//    }
//  }

  /**
   * Returns the xsdSchema.
   * @return XSDSchema
   */
  public XSDSchema getXSDSchema()
  {
    return xsdSchema;
  }


  /**
   * Returns the resourceFile.
   * @return IFile
   */
  public IFile getFileResource()
  {
    return resourceFile;
  }

  /**
   * Get the IDocument from the text viewer
   */
  public IDocument getEditorIDocument()
  {
    IDocument document = textEditor.getTextViewer().getDocument();
    return document;
  }

  /**
   * Create ref integrity tasks in task list
   */
  public void createTasksInTaskList(ArrayList messages)
  {
//    DisplayErrorInTaskList tasks = new DisplayErrorInTaskList(getEditorIDocument(), getFileResource(), messages);
//    tasks.run();
  }

  public void resetInformationTasks()
  {
// DisplayErrorInTaskList.removeInfoMarkers(getFileResource());
  }
  
  public XSDGraphViewer getGraphViewer()
  {
    return graphViewer;
  }

//  /**
//   * @see org.eclipse.ui.part.MultiPageEditorPart#handlePropertyChange(int)
//   */
//  protected void handlePropertyChange(int propertyId)
//  {
//    super.handlePropertyChange(propertyId);
//    
//    if (propertyId == IEditorPart.PROP_INPUT)
//    {
//      setInput(textEditor.getEditorInput());
//      resourceFile = ((IFileEditorInput) getEditorInput()).getFile();
//      setTitle(resourceFile.getName());
////      outline.setModel(getModel());
//
//      // even though we've set title etc., several times already!
//      // only now is all prepared for it.
//      firePropertyChange(IWorkbenchPart.PROP_TITLE);
//      firePropertyChange(PROP_DIRTY);
//    }
//    else if (propertyId == IEditorPart.PROP_TITLE)
//    {
//      if (getEditorInput() != textEditor.getEditorInput())
//      {
//        setInput(textEditor.getEditorInput());
//      }
//    }    
//  }

  public IEditorPart getActiveEditorPage()
  {
    return getActiveEditor();
  }
  
  public XSDTextEditor getXSDTextEditor()
	{
    return textEditor;
  }

	class InternalPartListener implements IPartListener
	{
	  XSDEditor editor;
	  public InternalPartListener(XSDEditor editor)
	  {
	    this.editor = editor; 
	  }
	  
		public void partActivated(IWorkbenchPart part)
		{
			if (part == editor)
			{
        ISelection selection = getSelectionManager().getSelection();
        if (selection != null)
        {
          getSelectionManager().setSelection(selection);
        }
			}
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
		}

   
		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
		}
	}
  
}
