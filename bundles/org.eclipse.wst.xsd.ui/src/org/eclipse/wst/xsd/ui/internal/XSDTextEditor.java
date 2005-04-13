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

import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.openon.OpenOnAction;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.ui.StructuredTextEditorXML;
import org.eclipse.wst.xsd.ui.internal.properties.section.XSDTabbedPropertySheetPage;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAdapterFactoryLabelProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDContentProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.wst.xsd.ui.internal.refactor.actions.RefactorActionGroup;
import org.eclipse.wst.xsd.ui.internal.util.SelectionAdapter;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class XSDTextEditor extends StructuredTextEditorXML implements INodeSelectionListener, ISelectionChangedListener
{
  protected XSDSelectionManager xsdSelectionManager;
  protected XSDModelAdapterFactoryImpl xsdModelAdapterFactory;
  protected static XSDAdapterFactoryLabelProvider adapterFactoryLabelProvider;
  protected InternalSelectionProvider internalSelectionProvider = new InternalSelectionProvider();

  public XSDTextEditor(XSDEditor xsdEditor)
  {
    super();
    xsdSelectionManager = xsdEditor.getSelectionManager();
    xsdSelectionManager.addSelectionChangedListener(this);

    setHelpContextId(XSDEditorContextIds.XSDE_SOURCE_VIEW);
    
    xsdModelAdapterFactory = XSDModelAdapterFactoryImpl.getInstance();
    adapterFactoryLabelProvider = new XSDAdapterFactoryLabelProvider(xsdModelAdapterFactory);
  }
  
  public void dispose()
  {
    super.dispose();
    xsdSelectionManager.removeSelectionChangedListener(this);
  }
  
  public XSDModelAdapterFactoryImpl getXSDModelAdapterFactory()
  {
    return xsdModelAdapterFactory;
  }

  public static XSDAdapterFactoryLabelProvider getLabelProvider()
  {
    return adapterFactoryLabelProvider;
  }

	public Object getAdapter(Class required) {
	  
		if (IPropertySheetPage.class.equals(required))
    {
	    fPropertySheetPage = new XSDTabbedPropertySheetPage(getXSDEditor());
      
	    ((XSDTabbedPropertySheetPage)fPropertySheetPage).setXSDModelAdapterFactory(xsdModelAdapterFactory);
      ((XSDTabbedPropertySheetPage)fPropertySheetPage).setSelectionManager(getXSDEditor().getSelectionManager());
	    ((XSDTabbedPropertySheetPage)fPropertySheetPage).setXSDSchema(getXSDSchema());

      return fPropertySheetPage;
		}
		else if (IContentOutlinePage.class.equals(required))
		{
			if (fOutlinePage == null || fOutlinePage.getControl() == null || fOutlinePage.getControl().isDisposed())
			{
				XSDContentOutlinePage outlinePage = new XSDContentOutlinePage(this);
        XSDContentProvider xsdContentProvider = new XSDContentProvider(xsdModelAdapterFactory);
        xsdContentProvider.setXSDSchema(getXSDSchema());
	      outlinePage.setContentProvider(xsdContentProvider);
	      outlinePage.setLabelProvider(adapterFactoryLabelProvider);
				outlinePage.setModel(getXSDSchema().getDocument());
				
				// Update outline selection from source editor selection:
	      getViewerSelectionManager().addNodeSelectionListener(this);
	      internalSelectionProvider.addSelectionChangedListener(getViewerSelectionManager());
	      internalSelectionProvider.setEventSource(outlinePage);

				fOutlinePage = outlinePage;
			}
			return fOutlinePage;
		}
	
		return super.getAdapter(required);
	}
  
  XSDModelQueryContributor xsdModelQueryContributor = new XSDModelQueryContributor();

  protected XSDContentOutlinePage outlinePage;

  /*
   * @see StructuredTextEditor#getContentOutlinePage()
   */
  public IContentOutlinePage getContentOutlinePage()
  {
    return fOutlinePage;
  }

  // used to map selections from the outline view to the source view
  // this class thinks of selections in terms of DOM element
  class InternalSelectionProvider extends SelectionAdapter
  {
    protected Object getObjectForOtherModel(Object object)
    {
      Node node = null;

      if (object instanceof Node)
      {
        node = (Node)object;
      }
      else if (object instanceof XSDComponent)
      {
        node = ((XSDComponent)object).getElement();
      }
      else if (object instanceof CategoryAdapter)
      {
        node = ((CategoryAdapter)object).getXSDSchema().getElement();
      }

      // the text editor can only accept sed nodes!
      //
      if (!(node instanceof IDOMNode))
      {
        node = null;
      }
      return node;
    }
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    // here we convert the model selection to a node selection req'd for the source view
    //
    internalSelectionProvider.setSelection(event.getSelection());
  }

  public void nodeSelectionChanged(NodeSelectionChangedEvent event)
  {
    // here we convert an node seleciton to a model selection as req'd by the other views
    //
    if (!event.getSource().equals(internalSelectionProvider) && getXSDEditor().getActiveEditorPage() != null)
    {
      Element element = null;
      List list = event.getSelectedNodes();
      for (Iterator i = list.iterator(); i.hasNext();)
      {
        Node node = (Node)i.next();
        if (node != null)
        {
	        if (node.getNodeType() == Node.ELEMENT_NODE)
	        {
	          element = (Element)node;
	          break;
	        }
	        else if (node.getNodeType() == Node.ATTRIBUTE_NODE)
	        {
	          element = ((Attr)node).getOwnerElement();
	          break;
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
        }
      }

      if (o != null)
      {
        xsdSelectionManager.setSelection(new StructuredSelection(o), internalSelectionProvider);
      }
      else
      {
        xsdSelectionManager.setSelection(new StructuredSelection(), internalSelectionProvider);
      }
    }
  }

  
  /*
   * @see ITextEditor#doRevertToSaved()
   */
  public void doRevertToSaved()
  {
    super.doRevertToSaved();
  }

  /*
   * @see StructuredTextEditor#update()
   */
  public void update()
  {
    super.update();
    if (outlinePage != null)
     outlinePage.setModel(getModel());
  }

  protected Composite client;
  
  protected void addOpenOnSelectionListener()
  {
    getTextViewer().getTextWidget().addKeyListener(new KeyAdapter()
    {
      /**
         * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
         */
      public void keyReleased(KeyEvent arg0)
      {
        if (arg0.keyCode == SWT.F3)
        {
          getXSDEditor().getOpenOnSelectionHelper().openOnSelection();
        }
      }

    });
  }

 // private static Color dividerColor;

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {

		fAnnotationAccess= createAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		StructuredTextViewer sourceViewer = createStructedTextViewer(parent, verticalRuler, styles);
		initSourceViewer(sourceViewer);

    // end of super createSourceViewer
		
    //StructuredAnnotationAccess annotationAccess = new StructuredAnnotationAccess();
	 // DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		
	//  ISharedTextColors sharedColors = getTextColorsCache();
////  fOverviewRuler = new OverviewRuler(annotationAccess, OVERVIEW_RULER_WIDTH, sharedColors);
	//  fOverviewRuler = new OverviewRuler(createAnnotationAccess(), 12, sharedColors);
	  
//	  fOverviewRuler.addHeaderAnnotationType(StructuredAnnotationType.ERROR);
//	  fOverviewRuler.addHeaderAnnotationType(StructuredAnnotationType.WARNING);
	  
	 // fSourceViewerDecorationSupport = new SourceViewerDecorationSupport(sourceViewer, fOverviewRuler, annotationAccess, sharedColors);
    //configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);    

// The following method was removed
//    sourceViewer.setEditor(this);

		return sourceViewer;
	}

  /*
   * @see StructuredTextEditor#setModel(IFileEditorInput)
   */
  public void setModel(IFileEditorInput input)
  {                     
    super.setModel(input);
    if (getModel() instanceof IDOMModel)
    {
      xsdModelQueryContributor.setModel((IDOMModel)getModel());
    }
    file = input.getFile();
  }

  protected IFile file;


  /**
   * Gets the xsdSchema.
   * @return Returns a XSDSchema
   */
  public XSDSchema getXSDSchema()
  {
    return ((XSDEditor)getEditorPart()).getXSDSchema();
  }
  
  public XSDEditor getXSDEditor()
  {
    return (XSDEditor)getEditorPart();
  }
 
  /**
   * @see org.eclipse.ui.texteditor.AbstractTextEditor#safelySanityCheckState(IEditorInput)
   */
	public void safelySanityCheckState(IEditorInput input)
  {
    super.safelySanityCheckState(input);
  }

  protected class WrappedOpenFileAction extends OpenOnAction
  {
    /**
     * Constructor for WrappedAction.
     * @param bundle
     * @param prefix
     * @param editor
     */
    public WrappedOpenFileAction(
      ResourceBundle bundle,
      String prefix,
      ITextEditor editor)
    {
      super(bundle, prefix, editor);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
      if (!getXSDEditor().getOpenOnSelectionHelper().openOnSelection())
      {
        super.run();
      }
    }
  }
  
  protected WrappedOpenFileAction wrappedAction;
  private static final String DOT = "."; //$NON-NLS-1$
  private ActionGroup fRefactorMenuGroup;
  
  /**
   * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
   */
  protected void createActions()
  {
    super.createActions();
    addOpenOnSelectionListener();
    ResourceBundle resourceBundle = Platform.getResourceBundle(XSDEditorPlugin.getPlugin().getBundle());
    
    wrappedAction = new WrappedOpenFileAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE + DOT, this);
    setAction(StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE, wrappedAction);
	fRefactorMenuGroup = new RefactorActionGroup(this.getXSDEditor().getSelectionManager(), getXSDSchema(), ITextEditorActionConstants.GROUP_EDIT
);

  }
  /* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.StructuredTextEditor#addContextMenuActions(org.eclipse.jface.action.IMenuManager)
	 */
	protected void addContextMenuActions(IMenuManager menu) {
		
		super.addContextMenuActions(menu);
		ActionContext context= new ActionContext(getSelectionProvider().getSelection());
		fRefactorMenuGroup.setContext(context);
		fRefactorMenuGroup.fillContextMenu(menu);
		fRefactorMenuGroup.setContext(null);
	}
    

  class XSDModelQueryContributor extends AbstractXSDModelQueryContributor
  {
    public AbstractXSDDataTypeValueExtension createXSDDataTypeValueExtension(ModelQuery modelQuery)
    {
      return new XSDDataTypeValueExtension(modelQuery);
    }
  }
 

  class XSDDataTypeValueExtension extends AbstractXSDDataTypeValueExtension
  {                             
    public XSDDataTypeValueExtension(ModelQuery modelQuery)
    {
      super(modelQuery);
    }

    public String getId()
    {
      return "XSDDataTypeValueExtension";
    }
     
    protected XSDSchema getEnclosingXSDSchema(Element element)
    {
      return getXSDSchema();
    }   
  }

}

