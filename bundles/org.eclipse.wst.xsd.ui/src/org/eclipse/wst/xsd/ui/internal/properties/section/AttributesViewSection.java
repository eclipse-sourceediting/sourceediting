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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import java.util.ArrayList;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.XSDMenuListener;
import org.eclipse.wst.xsd.ui.internal.actions.AddAttributeAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateAttributeAndRequired;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.properties.XSDPropertySourceProvider;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttributesViewSection extends AbstractSection implements ISelectionChangedListener
{
  AttributeTableTreeViewer viewer;
  AttributesPropertySheetPage propertySheetPage;

  /**
   * 
   */
  public AttributesViewSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent,	TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		
		composite =	getWidgetFactory().createFlatFormComposite(parent);
    SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);

    FormData data = new FormData();
    data.top = new FormAttachment(0, 0);
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.bottom = new FormAttachment(100, 0);
    sashForm.setLayoutData(data);
    
    viewer = new AttributeTableTreeViewer(sashForm);
    propertySheetPage = new AttributesPropertySheetPage();
    propertySheetPage.createControl(sashForm);

		AttributesViewContentProvider provider = new AttributesViewContentProvider(getActiveEditor(), viewer); 
		viewer.setContentProvider(provider);

		viewer.setLabelProvider(provider);
		viewer.addSelectionChangedListener(this);

    propertySheetPage.setPropertySourceProvider(new XSDPropertySourceProvider());
	}

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)input;
      if (elementDeclaration.isElementDeclarationReference())
      {
        input = elementDeclaration.getResolvedElementDeclaration();
        
        isReadOnly = (!(elementDeclaration.getResolvedElementDeclaration().getRootContainer() == xsdSchema));
      }
    }
  }
  
	public void selectionChanged(SelectionChangedEvent event)
	{
	  propertySheetPage.selectionChanged(part, event.getSelection());
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
	}

  public void aboutToBeShown()
  {
		refresh();
  }
	
	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
    setListenerEnabled(false);
	  if (viewer != null)
	  {
      viewer.setInput(getInput());
      viewer.refresh();
	  }
    setListenerEnabled(true);
	}
	
  public void dispose()
  {
//    if (propertySheetPage != null)
//    {
//      propertySheetPage.dispose();
//      propertySheetPage = null;
//    }
//    if (viewer != null)
//    {
//      viewer = null;
//    }
  }

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

	
	class AttributeTableTreeViewer extends TreeViewer // ExtendedTableTreeViewer
	{
	  public AttributeTableTreeViewer(Composite c)
	  {
	    super(c);
	    
	    MenuManager menuManager = new MenuManager("#popup");//$NON-NLS-1$
	    menuManager.setRemoveAllWhenShown(true);
	    Menu menu = menuManager.createContextMenu(getTree());
	    getTree().setMenu(menu);

	    XSDAttributeMenuListener menuListener = new XSDAttributeMenuListener(this);
	    menuManager.addMenuListener(menuListener);

	  }
	  
	  public class XSDAttributeMenuListener extends XSDMenuListener
	  {
	    public XSDAttributeMenuListener(TreeViewer viewer)
	    {
	      super(viewer);
        selectionProvider = viewer;
       
	      deleteAction = new DeleteAction(XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE"), AttributesViewSection.this.getActiveEditor(), getXSDSchema());
        deleteAction.setSelectionProvider(selectionProvider);
        selectionProvider.addSelectionChangedListener(deleteAction);
	    }
	    
	    protected XSDSchema getXSDSchema()
	    {
        return xsdSchema;
//        return getSchema();
//	      XSDConcreteComponent xsdInput = (XSDConcreteComponent)AttributesViewSection.this.getInput();
//	      return xsdInput.getSchema();
	    }
	    
	    protected Object getSelectedElement()
	    {
        XSDComponent xsdInput = (XSDComponent)AttributesViewSection.this.getInput();
        
        if (xsdInput instanceof XSDElementDeclaration)
        {
          XSDElementDeclaration xsdElement = (XSDElementDeclaration)xsdInput;
          XSDTypeDefinition xsdType = xsdElement.getType();
          if (xsdType instanceof XSDComplexTypeDefinition)
          {
            XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)xsdType;
            return ct;
          }
          return xsdElement;
        }
        else if (xsdInput instanceof XSDComplexTypeDefinition)
        {
          return xsdInput;
        }

        return null;
	    }
      
      public void menuAboutToShow(IMenuManager manager)
      {
        updateXSDSchema();
        if (xsdSchema == null)
        {
          return;
        }

        Object selectedElementObj = getSelectedElement();
        
        Element selectedElement = null;

        if (selectedElementObj instanceof XSDComplexTypeDefinition)
        {
          selectedElement = ((XSDComplexTypeDefinition)selectedElementObj).getElement();
        }
        
        addContextItems(manager, selectedElement, null);

        if (!selectionProvider.getSelection().isEmpty())
        {
          // Add context menu items for selected element
//          addContextItems(manager, selectedElement, null);
          
          manager.add(new Separator());
          if (deleteAction != null)
          {
            deleteAction.setXSDSchema(getXSDSchema());
            manager.add(deleteAction);
          }
        }
      }
	    
	    protected void addContextItems(IMenuManager manager, Element parent, Node relativeNode)
	    {
	      ArrayList attributes = null;
	      if (XSDDOMHelper.inputEquals(parent, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
	      { //
	        boolean annotationExists = false;
	        boolean contentExists = false;
	        boolean complexOrSimpleContentExists = false;
	        boolean anyAttributeExists = false;
	        Node annotationNode = null;
	        Element contentNode = null;
	        Node anyAttributeNode = null;
	        NodeList children = parent.getChildNodes();
	        
	        for (int i=0; i < children.getLength(); i++)
	        {
	          Node child = children.item(i);
	          if (child != null && child instanceof Element)
	          {
	            if (XSDDOMHelper.inputEquals((Element)child, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
	            {
	              annotationNode = child;
	              annotationExists = true;
	            }
	            else if (XSDDOMHelper.inputEquals((Element)child, XSDConstants.SEQUENCE_ELEMENT_TAG, false) ||
	                     XSDDOMHelper.inputEquals((Element)child, XSDConstants.ALL_ELEMENT_TAG, false) ||
	                     XSDDOMHelper.inputEquals((Element)child, XSDConstants.CHOICE_ELEMENT_TAG, false) ||
	                     XSDDOMHelper.inputEquals((Element)child, XSDConstants.GROUP_ELEMENT_TAG, true) ||
	                     XSDDOMHelper.inputEquals((Element)child, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false) ||
	                     XSDDOMHelper.inputEquals((Element)child, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
	            {
	              contentExists = true;
	              contentNode = (Element)child;

	              if (XSDDOMHelper.inputEquals((Element)child, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false) ||
	                  XSDDOMHelper.inputEquals((Element)child, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
	              {
	                complexOrSimpleContentExists = true;
	              }
	            } 
	            else if (XSDDOMHelper.inputEquals((Element)child, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false))
	            {
	              anyAttributeExists = true;
	              anyAttributeNode = child;
	            }
	          }
	        }
//	        addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
//	        manager.add(new Separator());
//	        addSetBaseTypeAction(manager, parent);
//	        if (annotationExists)
//	        {
//	          if (!contentExists)
//	          {
//	            addCreateElementAction(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, annotationNode.getNextSibling());
//	            addCreateSimpleContentAction(manager, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SIMPLE_CONTENT"), attributes, parent, annotationNode.getNextSibling());
//	            addCreateSimpleContentAction(manager, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_CONTENT"), attributes, parent, annotationNode.getNextSibling());
//	            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, annotationNode.getNextSibling());
//	            attributes = null;
//	          }
//	        }
//	        else
//	        {
//	          if (!contentExists)
//	          {
//	            addCreateElementAction(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, parent.getFirstChild());
//	            addCreateSimpleContentAction(manager, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SIMPLE_CONTENT"), attributes, parent, parent.getFirstChild());
//	            addCreateSimpleContentAction(manager, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_CONTENT"), attributes, parent, parent.getFirstChild());
//	            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, parent.getFirstChild());
//	            attributes = null;
//	          }
//	        }
//
//	        manager.add(new Separator());

	        if (anyAttributeExists)
	        {
	          if (!complexOrSimpleContentExists)
	          {
	            attributes = new ArrayList();
	            attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE,
	                                            getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
	            attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
	            addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
	            attributes = null;
//	   ARE ATTRIBUTE GROUPS ALLOWED ?
//	            addCreateElementAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, "_UI_ACTION_ADD_ATTRIBUTE_GROUP", attributes, parent, anyAttributeNode);
	            addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
	            addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
	          }
	        }
	        else
	        {
	          if (!complexOrSimpleContentExists)
	          {
	            attributes = new ArrayList();
	            attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE,
	                                            getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
	            attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
	            addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
	            attributes = null;
	            addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
	            addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
	            attributes = null;
	            addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, parent.getLastChild());
	          }
            else
            {
              // new model based add attribute action
              XSDComplexTypeDefinition xsdCT = (XSDComplexTypeDefinition)getXSDSchema().getCorrespondingComponent(parent);
              manager.add(new AddAttributeAction(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), xsdCT));

              Element derivedByElement = domHelper.getDerivedByElement(contentNode);
              if (derivedByElement != null)
              {
                attributes = null;
                addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), derivedByElement, derivedByElement.getLastChild());
                addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), derivedByElement, derivedByElement.getLastChild());
                attributes = null;
                addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, derivedByElement, derivedByElement.getLastChild());
              }
            }
	        }
	      }
	      else if (parent == null) {	      
	      	XSDElementDeclaration ed = (XSDElementDeclaration)input;      
	      	if (ed.getTypeDefinition() != null) 
	      	{
	      		XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
	      	}
	      
	      	// Add Attribute
	      	attributes = new ArrayList();
	      	attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE,
                                          getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
	      	attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
	      	manager.add(new CreateAttributeAndRequired(XSDConstants.ATTRIBUTE_ELEMENT_TAG,
					   XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"),
					   attributes,
					   getXSDSchema(),
					   selectionProvider,
					   ed));
	      	
	      	// Add Attribute Reference
	      	attributes = null;
	      	attributes = new ArrayList();
	        String ref = getFirstGlobalElementTagName(XSDConstants.ATTRIBUTE_ELEMENT_TAG);
	        attributes.add(new DOMAttribute(XSDConstants.REF_ATTRIBUTE, ref));
	      
	        Action action = new CreateAttributeAndRequired(XSDConstants.ATTRIBUTE_ELEMENT_TAG,
					   XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"),
					   attributes,
					   getXSDSchema(),
					   selectionProvider,
					   ed);
	        manager.add(action);
	        action.setEnabled(ref != null);

	        // Add Attribute Group Reference
	        attributes = null;
	      	attributes = new ArrayList();
	        ref = getFirstGlobalElementTagName(XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG);
	        attributes.add(new DOMAttribute(XSDConstants.REF_ATTRIBUTE, ref));
	        
	        action = new CreateAttributeAndRequired(XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG,
					   XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"),
					   attributes,
					   getXSDSchema(),
					   selectionProvider,
					   ed);

	      	manager.add(action);
	      	action.setEnabled(ref != null);
	      	
	      	// Add Any Attribute
	      	attributes = null;
	      	if (getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false) == null)
	        {
	      		action = new CreateAttributeAndRequired(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG,
						   XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"),
						   attributes,
						   getXSDSchema(),
						   selectionProvider,
						   ed);
	      		manager.add(action);
	         }
	      }
	    }
	  }
	}
	
	class AttributesPropertySheetPage extends PropertySheetPage implements INotifyChangedListener
	{
	  public AttributesPropertySheetPage()
	  {
	    super();
	  }
	  
	  public void notifyChanged(Notification notification)
	  {
	    System.out.println("Notification");
	  }
	}
}
