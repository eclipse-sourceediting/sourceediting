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
package org.eclipse.wst.xsd.ui.internal.graph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.XSDSelectionManager;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.TopLevelComponentEditPart;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
public class XSDGraphViewer implements ISelectionChangedListener
{
  protected PageBook pageBook;
  protected Control componentViewerControl;
  protected Control inheritanceViewerControl;
  protected Control subGroupsViewerControl;
  protected XSDComponentViewer componentViewer;
  protected XSDInheritanceViewer inheritanceViewer;
  protected XSDSubstitutionGroupsViewer subGroupsViewer;
  protected XSDSelectionManager xsdSelectionManager;
  protected XSDSchema schema;
  protected InternalSelectionProvider internalSelectionProvider = new InternalSelectionProvider();
  protected XSDEditor editor;
  protected PrintGraphAction printGraphAction;
  protected SashForm sashForm;
  protected ToolItem backButton;

  public XSDGraphViewer(XSDEditor editor)
  {
    super();
    this.editor = editor;
  }

  public void setSchema(XSDSchema schema)
  {
    this.schema = schema;
  }
  ToolBar graphToolBar; // the toolbar at the top of the graph view
  ToolItem toolItem; // the view tool item
  ViewForm form; // view form for holding all the views
  Composite frameBar; // The composite that contains the toolbar
  Composite c; // temporary blank page. Clean this up when all views completed
  LinkedGraphViewer linkInheritanceViewer;

  protected void createInheritanceViewer(Composite parent)
  {
    linkInheritanceViewer = new LinkedGraphViewer(editor, internalSelectionProvider);
    BaseGraphicalViewer graphViewer = new XSDInheritanceViewer(editor, internalSelectionProvider);
    linkInheritanceViewer.setMajorViewer(graphViewer);
    graphViewer = new XSDComponentViewer(editor, editor.getSelectionManager());
    linkInheritanceViewer.setMinorViewer(graphViewer);
    linkInheritanceViewer.createControl(parent);
  }
  LinkedGraphViewer linkSubstitutionGroupViewer;

  protected void createSubstitutionGroupViewer(Composite parent)
  {
    linkSubstitutionGroupViewer = new LinkedGraphViewer(editor, internalSelectionProvider);
    BaseGraphicalViewer graphViewer = new XSDSubstitutionGroupsViewer(editor, internalSelectionProvider);
    linkSubstitutionGroupViewer.setMajorViewer(graphViewer);
    graphViewer = new XSDComponentViewer(editor, internalSelectionProvider);
    linkSubstitutionGroupViewer.setMinorViewer(graphViewer);
    linkSubstitutionGroupViewer.createControl(parent);
  }
  static private Color dividerColor;

  public Control createControl(Composite parent)
  {
    pageBook = new PageBook(parent, 0);
    
    //componentViewer = new XSDComponentViewer(editor, internalSelectionProvider);
    componentViewer = new XSDComponentViewer(editor, editor.getSelectionManager());
    ViewUtility util = new ViewUtility();
    final Composite client;
    String designLayoutPosition = XSDEditorPlugin.getPlugin().getDesignLayoutPosition();
    form = new ViewForm(pageBook, SWT.NONE);
    frameBar = new Composite(form, SWT.NONE);
    org.eclipse.swt.layout.GridLayout frameLayout = new org.eclipse.swt.layout.GridLayout();
    frameLayout.marginWidth = 0;
    frameLayout.marginHeight = 0;
    frameBar.setLayout(frameLayout);
    graphToolBar = new ToolBar(frameBar, SWT.FLAT);
    graphToolBar.addTraverseListener(new TraverseListener()
    {
      public void keyTraversed(TraverseEvent e)
      {
        if (e.detail == SWT.TRAVERSE_MNEMONIC)
          e.doit = false;
      }
    });
    backButton = new ToolItem(graphToolBar, SWT.PUSH);
    backButton.setImage(XSDEditorPlugin.getXSDImage("icons/back.gif")); //$NON-NLS-1$
    backButton.setToolTipText(XSDEditorPlugin.getXSDString("_UI_HOVER_BACK_TO_SCHEMA_VIEW")); //$NON-NLS-1$
    backButton.setEnabled(false);
    backButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        editor.getGraphViewer().setInput(editor.getXSDSchema());
        // internalSelectionProvider.setSelection(new StructuredSelection(editor.getXSDSchema()));
        editor.getSelectionManager().setSelection(new StructuredSelection(editor.getXSDSchema()));
      }
    });
    // TEMPORARILY REMOVE DIFFERENT VIEWS
    //    toolItem = new ToolItem(graphToolBar, SWT.DROP_DOWN);
    //
    //    // set default to containment
    //// toolItem.setText(XSDEditorPlugin.getXSDString("_UI_CONTAINMENT"));
    //    toolItem.addSelectionListener(new SelectionAdapter()
    //    {
    //      public void widgetSelected(SelectionEvent e)
    //      {
    //        Menu menu = new Menu(graphToolBar);
    //        if (menu != null)
    //        {
    //          if (!showGraphMenu(menu))
    //          {
    //            frameBar.setVisible(false);
    //            return;
    //          }
    //          Rectangle b = toolItem.getBounds();
    //          org.eclipse.swt.graphics.Point p = toolItem.getParent().toDisplay(new
    // org.eclipse.swt.graphics.Point(b.x, b.y + b.height));
    //          menu.setLocation(p.x, p.y);
    //          menu.setVisible(true);
    //        }
    //      }
    //    });
    form.setTopLeft(frameBar);
    //    createInheritanceViewer(form);
    //    createSubstitutionGroupViewer(form);
    componentViewerControl = componentViewer.createControl(form);
    //inheritanceViewerControl = inheritanceViewer.createControl(form);
    //subGroupsViewerControl = subGroupsViewer.createControl(form);
    c = ViewUtility.createComposite(form, 1);
    form.setContent(componentViewerControl);
    // componentViewerControl.setData("layout ratio", new Float(0.65));
    form.setData("layout ratio", new Float(0.65));
    if (dividerColor == null)
    {
      dividerColor = new Color(componentViewerControl.getDisplay(), 143, 141, 138);
    }
    //KCPort
    //    client.addPaintListener(new PaintListener()
    //    {
    //      /**
    //       * @see org.eclipse.swt.events.PaintListener#paintControl(PaintEvent)
    //       */
    //      public void paintControl(PaintEvent e)
    //      {
    //        Object source = e.getSource();
    //        if (source instanceof Composite)
    //        {
    //          Composite comp = (Composite)source;
    //          Rectangle boundary = comp.getClientArea();
    //          e.gc.setForeground(dividerColor);
    //          e.gc.drawLine(boundary.x, boundary.y, boundary.x + boundary.width,
    // boundary.y);
    //          editor.setDesignWeights(sashForm.getWeights(), true);
    //        }
    //      }
    //    });
    // KCPort
    //    designView = new DesignViewer(editor);
    //    final Control design = designView.createControl(client);
    //    design.setLayoutData(ViewUtility.createFill());
    //    client.setData("layout ratio", new Float(0.35));
    //    enableDesignView(editor.isCombinedDesignAndSourceView());
    //    pageBook.showPage(sashForm);
    pageBook.showPage(form);
    componentViewer.addSelectionChangedListener(internalSelectionProvider);
    //inheritanceViewer.addSelectionChangedListener(this);
    // Temporarily remove graph tool bar
    //    linkInheritanceViewer.addSelectionChangedListener(this);
    //    linkSubstitutionGroupViewer.addSelectionChangedListener(this);
    printGraphAction = new PrintGraphAction(componentViewer);
    return pageBook;
    //    return form;
  }

  private boolean showGraphMenu(Menu menu)
  {
    MenuItem containmentMenuItem = new MenuItem(menu, SWT.RADIO);
    containmentMenuItem.setText(XSDEditorPlugin.getXSDString("_UI_CONTAINMENT"));
    containmentMenuItem.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        toolItem.setText(XSDEditorPlugin.getXSDString("_UI_CONTAINMENT"));
        frameBar.layout(true);
        graphToolBar.layout(true);
        form.setContent(componentViewerControl);
        // retrieve latest input which could have changed...from designView ??
        // get it directly?
        // KCPort
        //        setInput(designView.getInput());
      }
    });
    MenuItem inheritanceMenuItem = new MenuItem(menu, SWT.RADIO);
    inheritanceMenuItem.setText(XSDEditorPlugin.getXSDString("_UI_INHERITANCE"));
    inheritanceMenuItem.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        toolItem.setText(XSDEditorPlugin.getXSDString("_UI_INHERITANCE"));
        frameBar.layout(true);
        graphToolBar.layout(true);
        //        form.setContent(inheritanceViewerControl);
        form.setContent(linkInheritanceViewer.getControl());
        if (linkInheritanceViewer.getInput() == null)
        {
          linkInheritanceViewer.setInput(schema);
        }
      }
    });
    MenuItem subGroupsMenuItem = new MenuItem(menu, SWT.RADIO);
    subGroupsMenuItem.setText(XSDEditorPlugin.getXSDString("_UI_SUBSTITUTION_GROUPS"));
    subGroupsMenuItem.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        toolItem.setText(XSDEditorPlugin.getXSDString("_UI_SUBSTITUTION_GROUPS"));
        frameBar.layout(true);
        graphToolBar.layout(true);
        form.setContent(linkSubstitutionGroupViewer.getControl());
        // retrieve latest input which could have changed...from designView ??
        // get it directly?
        //        setInput(designView.getInput());
        if (linkSubstitutionGroupViewer.getInput() == null)
        {
          linkSubstitutionGroupViewer.setInput(schema);
        }
      }
    });
    if (toolItem.getText().equals(XSDEditorPlugin.getXSDString("_UI_CONTAINMENT")))
    {
      containmentMenuItem.setSelection(true);
    }
    else if (toolItem.getText().equals(XSDEditorPlugin.getXSDString("_UI_INHERITANCE")))
    {
      inheritanceMenuItem.setSelection(true);
    }
    else if (toolItem.getText().equals(XSDEditorPlugin.getXSDString("_UI_SUBSTITUTION_GROUPS")))
    {
      subGroupsMenuItem.setSelection(true);
    }
    return true;
  }

  public Action getPrintGraphAction()
  {
    return printGraphAction;
  }

  public void updateDesignLayout(int newLayout)
  {
    sashForm.setOrientation(newLayout);
    sashForm.layout();
  }

  public SashForm getSashForm()
  {
    return sashForm;
  }

  public void enableDesignView(boolean enable)
  {
  }

  public XSDComponentViewer getComponentViewer()
  {
    return componentViewer;
  }
  
  public void setBackButtonEnabled(boolean state)
  {
    backButton.setEnabled(state);
  }

  public void setInput(Object object)
  {
    if (object instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent xsdComp = (XSDConcreteComponent) object;
      if (xsdComp instanceof XSDSchema)
      {
        setBackButtonEnabled(false);
      }
      else
      {
        setBackButtonEnabled(true);
      }
      componentViewer.setInput(xsdComp);
      componentViewer.setSelection(xsdComp);
    }   
  }

  protected boolean isDeleted(XSDConcreteComponent component)
  {
    boolean result = false;
    if (component != null && component.getElement() != null)
    {
      result = component.getElement().getParentNode() == null;
    }
    return result;
  }

  public void setSelectionManager(XSDSelectionManager newSelectionManager)
  {
    // disconnect from old one
    if (xsdSelectionManager != null)
    {
      xsdSelectionManager.removeSelectionChangedListener(this);
      internalSelectionProvider.removeSelectionChangedListener(xsdSelectionManager);
    }
    xsdSelectionManager = newSelectionManager;
    // connect to new one
    if (xsdSelectionManager != null)
    {
      xsdSelectionManager.addSelectionChangedListener(this);
      internalSelectionProvider.addSelectionChangedListener(xsdSelectionManager);
    }
  }

  // this method is called by the SelectionManager to notify the graph view
  // that the editor selection has changed
  //
  public void selectionChanged(SelectionChangedEvent event)
  {
    // here we check the selection source to ensure that this selection came
    // from a different view (not from the graph view)
    if (event.getSource() != getComponentViewer())
    { 
      handleSelection(event, true);
    }
  }

  protected XSDConcreteComponent getTopLevelComponent(XSDConcreteComponent component)
  {
    XSDConcreteComponent prev = component;
    XSDConcreteComponent container = component;
    while ( container != null && !(container instanceof XSDSchema))
    {
      prev = container;     
      container = container.getContainer();
    }
    return container != null ? prev : null;
  }
  
  // TODO.. we need to clean this method up and add comments to clarify what's going on
  //
  protected void handleSelection(SelectionChangedEvent event, boolean isSelectionRequired)
  {
    StructuredSelection s = (StructuredSelection)event.getSelection();
    Object obj = s.getFirstElement();
    if (obj instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent selectedComponent = (XSDConcreteComponent)obj;
      Object currentInput = getComponentViewer().getInput();
      
      // in this case we're looking at a top level view
      // so if the selection is a 'non-top-level' component we need to do a set input
      XSDSchema topLevelSchema = null;
      if (currentInput instanceof XSDSchema)
      {
        if (selectedComponent instanceof XSDSchema || 
            selectedComponent.getContainer() instanceof XSDSchema)
        {
          topLevelSchema = (XSDSchema)currentInput;
        }
      }
      if (selectedComponent instanceof XSDSchemaDirective || 
          selectedComponent instanceof XSDNotationDeclaration)      
      {
        topLevelSchema = selectedComponent.getSchema();
      }
      else if (selectedComponent instanceof XSDAttributeGroupContent ||
               selectedComponent instanceof XSDWildcard)
      {
        if (selectedComponent.getContainer() instanceof XSDAttributeGroupDefinition)
        {
          topLevelSchema = selectedComponent.getSchema();
        }
      }
      else if (selectedComponent instanceof XSDSimpleTypeDefinition)
      {
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)selectedComponent;
        EditPart editPart = componentViewer.getEditPart(componentViewer.getRootEditPart(), st);
        if (editPart == null)
        {
          if (st.getContainer() == editor.getXSDSchema())
          {
            topLevelSchema = selectedComponent.getSchema();
          }
        }
      }
      
      if (topLevelSchema != null)
      {
        setInput(topLevelSchema);
        // TODO... this is not 'quite' right
        // it should be possible to view in
      }  
      else
      { 
        EditPart editPart = getComponentViewer().getEditPart(getComponentViewer().getRootEditPart(), obj);
        if (editPart == null)
        {  
          XSDConcreteComponent topLevelComponent = getTopLevelComponent(selectedComponent);
          if (topLevelComponent != null)
          {
            setInput(topLevelComponent);  
          }
        }  
      }  
      // now we handle the selection
      //
      if (isSelectionRequired)
      {  
        EditPart editPart = getComponentViewer().getRootEditPart();        
        EditPart newSelectedEditPart = getComponentViewer().getEditPart(editPart, obj);
        if (newSelectedEditPart != null)
        {
          if (newSelectedEditPart instanceof TopLevelComponentEditPart)
          {
            ((TopLevelComponentEditPart)newSelectedEditPart).setScroll(true);
          }
          getComponentViewer().setSelection(new StructuredSelection(newSelectedEditPart));
        }
      }      
    }
    else if (obj instanceof CategoryAdapter)
    {
      setInput(((CategoryAdapter)obj).getXSDSchema());  
    }
  }

  protected Element getElementNode(Node node)
  {
    while (!(node instanceof Element))
    {
      if (node instanceof Attr)
      {
        node = ((Attr) node).getOwnerElement();
      }
      else if (node instanceof Text)
      {
        Node sibling = node.getNextSibling();
        if (sibling == null)
        {
          break;
        }
        node = sibling;
      }
      else
      {
        Node parent = node.getParentNode();
        if (parent == null)
        {
          break;
        }
        node = node.getParentNode();
      }
    }
    return node instanceof Element ? (Element) node : null;
  }



  // This class listens to the graph view and converts edit part selection
  // events
  // into XSD component selection events that can be 'fired' to the
  // selectionManager
  //
  class InternalSelectionProvider implements ISelectionProvider, ISelectionChangedListener
  {
    protected List listenerList = new ArrayList();
    protected ISelection selection = new StructuredSelection();

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
      listenerList.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
      listenerList.remove(listener);
    }

    public ISelection getSelection()
    {
      return selection;
    }

    protected void notifyListeners(SelectionChangedEvent event)
    {
      for (Iterator i = listenerList.iterator(); i.hasNext();)
      {
        ISelectionChangedListener listener = (ISelectionChangedListener) i.next();
        listener.selectionChanged(event);
      }
    }

    public StructuredSelection convertSelectionFromEditPartToModel(ISelection editPartSelection)
    {
      List selectedModelObjectList = new ArrayList();
      if (editPartSelection instanceof IStructuredSelection)
      {
        for (Iterator i = ((IStructuredSelection) editPartSelection).iterator(); i.hasNext();)
        {
          Object obj = i.next();
          Object model = null;
          if (obj instanceof EditPart)
          {
            EditPart editPart = (EditPart) obj;
            model = editPart.getModel();
            // Convert category to XSDSchema
            // if (editPart instanceof CategoryEditPart)
            // {
            //   model = ((Category)((CategoryEditPart)editPart).getModel()).getXSDSchema();
            // }
          }
          else if (obj instanceof XSDConcreteComponent)
          {
            //cs .. not sure why would we'd ever hit this case?
            model = obj;
          }
          if (model != null)//&& model instanceof XSDConcreteComponent)
          {
            selectedModelObjectList.add(model);
          }
        }
      }
      return new StructuredSelection(selectedModelObjectList);
    }

    public void setSelection(ISelection selection)
    {
      this.selection = selection;
    }

    // This method gets called when an edit part in the graph view is selected
    //
    public void selectionChanged(SelectionChangedEvent event)
    {
      ISelection newSelection = convertSelectionFromEditPartToModel(event.getSelection());
      this.selection = newSelection;
      SelectionChangedEvent newEvent = new SelectionChangedEvent(getComponentViewer(), newSelection);
      notifyListeners(newEvent);
    }
  }
}