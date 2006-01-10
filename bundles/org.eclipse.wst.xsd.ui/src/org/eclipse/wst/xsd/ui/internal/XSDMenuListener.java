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
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.actions.AddEnumsAction;
import org.eclipse.wst.xsd.ui.internal.actions.AddModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.actions.BackAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateAnnotationAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateGroupAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateIdentityConstraintsAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateLocalComplexTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateLocalSimpleTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateSimpleContentAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateSimpleTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.actions.OpenSchemaAction;
import org.eclipse.wst.xsd.ui.internal.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.actions.SetTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.XSDEditNamespacesAction;
import org.eclipse.wst.xsd.ui.internal.graph.model.Category;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSDMenuListener implements IMenuListener
{
  protected ISelectionProvider selectionProvider;
  protected DeleteAction deleteAction;
  protected CreateElementAction addComplexTypeAction;
  protected XSDSchema xsdSchema;
  protected boolean isReadOnly;
  protected Object sourceContext;
  //private RefactorActionGroup fRefactorMenuGroup;

  /**
   * Constructor for XSDMenuListener.
   */
  public XSDMenuListener(ISelectionProvider selectionProvider)
  {
    super();
    this.selectionProvider = selectionProvider;

    deleteAction = new DeleteAction(XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE"), null, getXSDSchema());
    deleteAction.setSelectionProvider(selectionProvider);
    selectionProvider.addSelectionChangedListener(deleteAction);
  }

  public void setSourceContext(Object sourceContext)
  {
    this.sourceContext = sourceContext;
  }

  public void setSelectionProvider(ISelectionProvider selectionProvider)
  {
    this.selectionProvider = selectionProvider;
  }

  protected XSDSchema getXSDSchema()
  {
    return xsdSchema;
  }

  protected Object getSelectedElement()
  {
    ISelection selection = selectionProvider.getSelection();
    if (selection.isEmpty())
    {
      return null;
    }
    return ((IStructuredSelection) selection).getFirstElement();
  }
  
  protected boolean isSchemaReadOnly()
  {
	  XSDSchema editorSchema = ((XSDEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).getXSDSchema();
	  if (editorSchema == xsdSchema) {
		  return false;
	  }

	  return true; 
  }
  
  protected XSDSchema getCurrentSchemaInEditor()
  {
    return ((XSDEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).getXSDSchema();
  }
    
  protected void updateXSDSchema()
  {
    isReadOnly = false;

    Object object = getSelectedElement();
    if (object instanceof XSDConcreteComponent)
    {
      xsdSchema = ((XSDConcreteComponent) object).getSchema();
      isReadOnly = isSchemaReadOnly();
    }
    else if (object instanceof Category)
    {
      Category cg = (Category) object;
      xsdSchema = cg.getXSDSchema();
    }
    else if (object instanceof CategoryAdapter)
    {
      CategoryAdapter category = (CategoryAdapter) object;
      xsdSchema = category.getXSDSchema();
    }
  }
  
  /*
   * @see IMenuListener#menuAboutToShow(IMenuManager)
   */
  public void menuAboutToShow(IMenuManager manager)
  {
    isReadOnly = false;
    updateXSDSchema();
    if (xsdSchema == null)
    {
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      return;
    }
    
    deleteAction.setXSDSchema(xsdSchema);
    deleteAction.setEnabled(!isReadOnly);
    
    BackAction backAction = new BackAction(XSDEditorPlugin.getXSDString("_UI_ACTION_BACK_TO_SCHEMA_VIEW")); //$NON-NLS-1$
    backAction.setXSDSchema(getCurrentSchemaInEditor());
    backAction.setSelectionProvider(selectionProvider);
    
    Object selectedElementObj = getSelectedElement();
    
    if (selectedElementObj instanceof XSDSchema || selectedElementObj instanceof Category || selectedElementObj instanceof CategoryAdapter)
    {
      backAction.setEnabled(false);
    }
    manager.add(backAction);
    manager.add(new Separator());
//    if (undoAction == null && textEditor != null)
//    {
//      undoAction = textEditor.getAction(org.eclipse.ui.texteditor.ITextEditorActionConstants.UNDO);
//      redoAction = textEditor.getAction(org.eclipse.ui.texteditor.ITextEditorActionConstants.REDO);
//    }
    //    Element selectedElement = getSelectedElement();

    Element selectedElement = null;
    
    if (selectedElementObj instanceof Element)
    {
      selectedElement = (Element) selectedElementObj;
    }
    else if (selectedElementObj instanceof XSDConcreteComponent)
    {
      selectedElement = ((XSDConcreteComponent) selectedElementObj).getElement();
    }
    else if (selectedElementObj instanceof Category || selectedElementObj instanceof CategoryAdapter)
    {
      int groupType = -1;
      if (selectedElementObj instanceof Category)
      {
        Category category = (Category) selectedElementObj;
        groupType = category.getGroupType();
      }
      // todo... We need to ensure we eliminate the need for
      // this case. The XSDMenuListener class should not have
      // view dependant code. We need to do some work to ensure all
      // views utilize the 'Category' model object
      else if (selectedElementObj instanceof CategoryAdapter)
      {
        CategoryAdapter categoryAdapter = (CategoryAdapter) selectedElementObj;
        groupType = categoryAdapter.getGroupType();
      }
      ArrayList attributes = null;
      Element parent = getXSDSchema().getElement();
      
      if (parent == null)
      {
        return;
      }
      
      Node relativeNode = null;
      switch (groupType)
      {
        case Category.TYPES : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("ComplexType")));
          Action action = addCreateElementAction(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_TYPE"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("SimpleType")));
          Action action2 = addCreateSimpleTypeAction(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SIMPLE_TYPE"), attributes, parent, relativeNode);
          ((CreateElementAction) action2).setIsGlobal(true);
          break;
        }
        case Category.ELEMENTS : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ELEMENT_ELEMENT_TAG, "GlobalElement")));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          Action action = addCreateElementAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ELEMENT"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
        case Category.GROUPS : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.GROUP_ELEMENT_TAG, "Group")));
          CreateGroupAction groupAction = addCreateGroupAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GROUP"), attributes, parent, relativeNode);
          groupAction.setIsGlobal(true);
          break;
        }
        case Category.ATTRIBUTES : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ATTRIBUTE_ELEMENT_TAG, "GlobalAttribute")));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          Action action = addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GLOBAL_ATTRIBUTE"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
        case Category.ATTRIBUTE_GROUPS : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, "AttributeGroup")));
          Action action = addCreateElementAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
        case Category.NOTATIONS : {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.NOTATION_ELEMENT_TAG, "Notation")));
          attributes.add(new DOMAttribute(XSDConstants.PUBLIC_ATTRIBUTE, ""));
          Action action = addCreateElementAction(manager, XSDConstants.NOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_NOTATION"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
        case Category.DIRECTIVES : {
          boolean b = true;
          NodeList children = parent.getChildNodes();
          int length = children.getLength();
          Node effectiveRelativeNode = parent.getFirstChild();
          for (int i = 0; i < length && b; i++)
          {
            Node child = children.item(i);
            if (child != null && child instanceof Element)
            {
              if (XSDDOMHelper.inputEquals((Element) child, XSDConstants.INCLUDE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.IMPORT_ELEMENT_TAG, false)
                  || XSDDOMHelper.inputEquals((Element) child, XSDConstants.REDEFINE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
              {
                effectiveRelativeNode = child;
              }
              else
              {
                b = false;
              }
            }
          }
          relativeNode = effectiveRelativeNode != null ? XSDDOMHelper.getNextElementNode(effectiveRelativeNode) : null;
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, ""));
          Action action = addCreateElementAction(manager, XSDConstants.INCLUDE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_INCLUDE"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          action = addCreateElementAction(manager, XSDConstants.IMPORT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_IMPORT"), null, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          action = addCreateElementAction(manager, XSDConstants.REDEFINE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_REDEFINE"), attributes, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
        case Category.ANNOTATIONS : {
          Action action = addCreateElementAction(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), null, parent, relativeNode);
          ((CreateElementAction) action).setIsGlobal(true);
          break;
        }
      }
//      manager.add(new Separator());
//      if (undoAction != null)
//      {
//        manager.add(undoAction);
//        manager.add(redoAction);
//      }
      
      // insertion point for popupMenus extension
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));    

      return;
    }
    if (selectedElement != null)
    {
      addContextItems(manager, selectedElement, null);
      manager.add(new Separator());
    }
    
    manager.add(new Separator());
    if (deleteAction != null)
    {
      manager.add(deleteAction);
    }
    
    // insertion point for popupMenus extension
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));    
  }

  protected String getBuiltInStringQName()
  {
    String stringName = "string";
    if (getXSDSchema() != null)
    {
      String schemaForSchemaPrefix = getXSDSchema().getSchemaForSchemaQNamePrefix();
      if (schemaForSchemaPrefix != null && schemaForSchemaPrefix.length() > 0)
      {
        String prefix = getXSDSchema().getSchemaForSchemaQNamePrefix();
        if (prefix != null && prefix.length() > 0)
        {
          stringName = prefix + ":" + stringName;
        }
      }
    }
    return stringName;
  }

  /**
   * Method addContextItems.
   * 
   * @param manager
   * @param parent -
   *          menu items should be context sensitive to this node
   * @param relativeNode -
   *          anything inserted, should be inserted before this node (which is a
   *          child of the parent node. A value of null means add to the end
   */
  protected void addContextItems(IMenuManager manager, Element parent, Node relativeNode)
  {
    ArrayList attributes = null;
    if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
    { //
      addSchemaElementItems(manager, parent, relativeNode);
      manager.add(new Separator());
      boolean b = true;
      NodeList children = parent.getChildNodes();
      Node effectiveRelativeNode = parent.getFirstChild();
      for (int i = 0; i < children.getLength() && b; i++)
      {
        Node child = children.item(i);
        if (child != null && child instanceof Element)
        {
          if (XSDDOMHelper.inputEquals((Element) child, XSDConstants.INCLUDE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.IMPORT_ELEMENT_TAG, false)
              || XSDDOMHelper.inputEquals((Element) child, XSDConstants.REDEFINE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
          {
            effectiveRelativeNode = child;
          }
          else
          {
            b = false;
          }
        }
      }
      relativeNode = effectiveRelativeNode != null ? effectiveRelativeNode.getNextSibling() : null;
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, ""));
      addCreateElementAction(manager, XSDConstants.INCLUDE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_INCLUDE"), attributes, parent, relativeNode);
      addCreateElementAction(manager, XSDConstants.IMPORT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_IMPORT"), null, parent, relativeNode);
      addCreateElementAction(manager, XSDConstants.REDEFINE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_REDEFINE"), attributes, parent, relativeNode);
      attributes = null;
      addCreateAnnotationAction(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), null, parent, relativeNode);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
    { //
      addCreateElementAction(manager, XSDConstants.DOCUMENTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_DOC"), attributes, parent, null);
      addCreateElementAction(manager, XSDConstants.APPINFO_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_APP_INFO"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
      Element parentNode = (Element) parent.getParentNode();
      boolean isGlobalElement = false;
      if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        isGlobalElement = true;
      }
      boolean annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      boolean simpleTypeExists = elementExists(XSDConstants.SIMPLETYPE_ELEMENT_TAG, parent);
      boolean complexTypeExists = elementExists(XSDConstants.COMPLEXTYPE_ELEMENT_TAG, parent);
      manager.add(new Separator());
      if (annotationExists)
      {
        Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
        if (!(simpleTypeExists || complexTypeExists) && annotationNode != null)
        {
          manager.add(new Separator());
        }
      }
      else
      {
        if (concreteComponent != null)
        {
          AddModelGroupAction addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.SEQUENCE_LITERAL);
          addModelGroupAction.setEnabled(!isReadOnly);
          manager.add(addModelGroupAction);
          
          addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.CHOICE_LITERAL);
          addModelGroupAction.setEnabled(!isReadOnly);
          manager.add(addModelGroupAction);
          
          addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.ALL_LITERAL);
          addModelGroupAction.setEnabled(!isReadOnly);
          manager.add(addModelGroupAction);
          
          manager.add(new Separator());
        }
      }
      XSDDOMHelper domHelper = new XSDDOMHelper();
      Element anonymousType = (Element) domHelper.getChildNode(parent, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
      if (anonymousType != null)
      {
        manager.add(new Separator());
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("ComplexType")));
        addMoveAnonymousGlobal(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, anonymousType, null);
        attributes = null;
      }
      anonymousType = (Element) domHelper.getChildNode(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      if (anonymousType != null)
      {
        manager.add(new Separator());
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("SimpleType")));
        addMoveAnonymousGlobal(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, anonymousType, null);
        attributes = null;
      }
      
      manager.add(new Separator());
      MenuManager setTypeCascadeMenu = new MenuManager(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"));
      manager.add(setTypeCascadeMenu);

      SetTypeAction setNewComplexTypeAction = new SetTypeAction(XSDEditorPlugin.getXSDString("_UI_LABEL_NEW_COMPLEX_TYPE"), ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDComplexType.gif"), concreteComponent);
      setNewComplexTypeAction.setTypeKind(XSDConstants.COMPLEXTYPE_ELEMENT);
      setNewComplexTypeAction.setEnabled(!isReadOnly);
      setTypeCascadeMenu.add(setNewComplexTypeAction);

      SetTypeAction setNewSimpleTypeAction = new SetTypeAction(XSDEditorPlugin.getXSDString("_UI_LABEL_NEW_SIMPLE_TYPE"), ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/XSDSimpleType.gif"), concreteComponent);
      setNewSimpleTypeAction.setTypeKind(XSDConstants.SIMPLETYPE_ELEMENT);
      setNewSimpleTypeAction.setEnabled(!isReadOnly);
      setTypeCascadeMenu.add(setNewSimpleTypeAction);
      
      SetTypeAction setExistingTypeAction = new SetTypeAction(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_EXISTING_TYPE"), concreteComponent);
      setExistingTypeAction.setTypeKind(0);
      setExistingTypeAction.setEnabled(!isReadOnly);
      setTypeCascadeMenu.add(setExistingTypeAction);
      manager.add(new Separator());

      if (!isGlobalElement)
      {
        addMultiplicityMenu(concreteComponent, manager);
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.SEQUENCE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals(parent, XSDConstants.CHOICE_ELEMENT_TAG, false))
    { //
      XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
      
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      manager.add(new Separator());
      addCreateElementAction(manager, XSDConstants.CHOICE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CHOICE"), attributes, parent, null);
      addCreateElementAction(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SEQUENCE"), attributes, parent, null);
      addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, null);
      manager.add(new Separator());
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ELEMENT_ELEMENT_TAG, "Element", false)));
      attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
      addCreateElementAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ELEMENT"), attributes, parent, null);
      addCreateElementRefAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ELEMENT_REF"), parent, null);
      manager.add(new Separator());
      attributes = null;
      addCreateElementAction(manager, XSDConstants.ANY_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ELEMENT"), attributes, parent, null);
      
      if (concreteComponent.getContainer() instanceof XSDParticle)
      {
        addMultiplicityMenu(concreteComponent, manager);
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ALL_ELEMENT_TAG, false))
    { //
      XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
      
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      manager.add(new Separator());
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ELEMENT_ELEMENT_TAG, "Element", false)));
      attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
      addCreateElementAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ELEMENT"), attributes, parent, null);
      addCreateElementRefAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ELEMENT_REF"), parent, null);

      if (concreteComponent.getContainer() instanceof XSDParticle)
      {
        addMultiplicityMenu(concreteComponent, manager);
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false))
    { //
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      boolean anyAttributeExists = elementExists(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, parent);
      Node anyAttributeNode = null;
      manager.add(new Separator());
      if (anyAttributeExists)
      {
        anyAttributeNode = getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false);
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
        attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
        addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
        attributes = null;
        addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
        addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
      }
      else
      {
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
        attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
        addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
        attributes = null;
        addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
        addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
      }
      attributes = null;
      addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.NOTATION_ELEMENT_TAG, false))
    { //
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
    { //
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      boolean restrictionExists = elementExists(XSDConstants.RESTRICTION_ELEMENT_TAG, parent);
      boolean unionExists = elementExists(XSDConstants.UNION_ELEMENT_TAG, parent);
      boolean listExists = elementExists(XSDConstants.LIST_ELEMENT_TAG, parent);
      if (!(restrictionExists || unionExists || listExists))
      {
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, getBuiltInStringQName()));
        addCreateElementActionIfNotExist(manager, XSDConstants.RESTRICTION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_RESTRICTION"), attributes, parent, null);
        attributes = null;
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, getBuiltInStringQName()));
        addCreateElementActionIfNotExist(manager, XSDConstants.UNION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_UNION"), attributes, parent, null);
        attributes = null;
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, getBuiltInStringQName()));
        addCreateElementActionIfNotExist(manager, XSDConstants.LIST_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LIST"), attributes, parent, null);
        attributes = null;
      }
      if (XSDDOMHelper.inputEquals(parent.getParentNode(), XSDConstants.ELEMENT_ELEMENT_TAG, false))
      {
        manager.add(new Separator());
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("SimpleType")));
        addMoveAnonymousGlobal(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, parent, null);
        attributes = null;
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.GROUP_ELEMENT_TAG, false))
    { //
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      addCreateElementActionIfNotExist(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
    { //
      boolean annotationExists = false;
      boolean contentExists = false;
      Node annotationNode = null;
      NodeList children = parent.getChildNodes();
      for (int i = 0; i < children.getLength(); i++)
      {
        Node child = children.item(i);
        if (child != null && child instanceof Element)
        {
          if (XSDDOMHelper.inputEquals((Element) child, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
          {
            annotationNode = child;
            annotationExists = true;
          }
          else if (XSDDOMHelper.inputEquals((Element) child, XSDConstants.SEQUENCE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.ALL_ELEMENT_TAG, false)
              || XSDDOMHelper.inputEquals((Element) child, XSDConstants.CHOICE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.GROUP_ELEMENT_TAG, true)
              || XSDDOMHelper.inputEquals((Element) child, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals((Element) child, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
          {
            contentExists = true;
          }
        }
      }
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      manager.add(new Separator());
      addSetBaseTypeAction(manager, parent);
      XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
      if (annotationExists)
      {
        if (!contentExists)
        {
          if (concreteComponent != null)
          {
            AddModelGroupAction addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.SEQUENCE_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);
            
            addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.CHOICE_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);
            
            addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.ALL_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);

          }
          addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, annotationNode.getNextSibling());
          attributes = null;
        }
      }
      else
      {
        if (!contentExists)
        {
          if (concreteComponent != null)
          {
            AddModelGroupAction addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.SEQUENCE_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);
            
            addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.CHOICE_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);
            
            addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.ALL_LITERAL);
            addModelGroupAction.setEnabled(!isReadOnly);
            manager.add(addModelGroupAction);

          }
          addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, parent.getFirstChild());
          attributes = null;
        }
      }
      manager.add(new Separator());
      if (XSDDOMHelper.inputEquals(parent.getParentNode(), XSDConstants.ELEMENT_ELEMENT_TAG, false))
      {
        manager.add(new Separator());
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("ComplexType")));
        addMoveAnonymousGlobal(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, parent, null);
        attributes = null;
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
    { //
      XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
      Element derivedByNode = xsdDOMHelper.getDerivedByElement(parent);
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      if (derivedByNode == null)
      {
        TypesHelper typesHelper = new TypesHelper(getXSDSchema());
        String firstType = "";
        List listOfCT = typesHelper.getUserComplexTypeNamesList();
        if (listOfCT.size() > 0)
        {
          firstType = (String) (listOfCT).get(0);
        }
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, firstType));
        addCreateElementActionIfNotExist(manager, XSDConstants.RESTRICTION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_RESTRICTION"), attributes, parent, null);
        addCreateElementActionIfNotExist(manager, XSDConstants.EXTENSION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_EXTENSION"), attributes, parent, null);
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
    { //
      XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
      Element derivedByNode = xsdDOMHelper.getDerivedByElement(parent);
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      if (derivedByNode == null)
      {
        TypesHelper typesHelper = new TypesHelper(getXSDSchema());
        String firstType = "";
        List listOfCT = typesHelper.getUserComplexTypeNamesList();
        if (listOfCT.size() > 0)
        {
          firstType = (String) (listOfCT).get(0);
        }
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, firstType));
        addCreateElementActionIfNotExist(manager, XSDConstants.RESTRICTION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_RESTRICTION"), attributes, parent, null);
        addCreateElementActionIfNotExist(manager, XSDConstants.EXTENSION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_EXTENSION"), attributes, parent, null);
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.RESTRICTION_ELEMENT_TAG, false))
    {
      Element parentNode = (Element) parent.getParentNode();
      // <simpleContent>
      //    <restriction>
      //      ...
      if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        boolean annotationExists = false;
        boolean anyAttributeExists = false;
        Node anyAttributeNode = null;
        anyAttributeExists = elementExists(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, parent);
        annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent
            .getFirstChild());
        if (annotationExists)
        {
          getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
        }

        manager.add(new Separator());
        if (anyAttributeExists)
        {
          anyAttributeNode = getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false);
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
        }
        else
        {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
        }
        attributes = null;
        addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, parent.getLastChild());
      }
      // <simpleType>
      //    <restriction>
      //      ...
      else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        boolean annotationExists = false;
        attributes = null;
        annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent
            .getFirstChild());
        if (annotationExists)
        {
          Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
          addCreateLocalSimpleTypeActionIfNotExist(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, annotationNode
              .getNextSibling());
        }
        else
        {
          addCreateLocalSimpleTypeActionIfNotExist(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, parent
              .getFirstChild());
        }
        manager.add(new Separator());
        addCreateElementAction(manager, XSDConstants.PATTERN_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_PATTERN"), attributes, parent, null);
        addCreateElementAction(manager, XSDConstants.ENUMERATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUM"), attributes, parent, null);
        addEnumsAction(manager, XSDConstants.ENUMERATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUMS"), attributes, parent, null);
      }
      // <complexContent>
      //    <restriction>
      //      ...
      else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        boolean annotationExists = false;
        boolean anyAttributeExists = false;
        Node anyAttributeNode = null;
        boolean sequenceExists = elementExists(XSDConstants.SEQUENCE_ELEMENT_TAG, parent);
        boolean choiceExists = elementExists(XSDConstants.CHOICE_ELEMENT_TAG, parent);
        boolean allExists = elementExists(XSDConstants.ALL_ELEMENT_TAG, parent);
        boolean groupExists = elementExists(XSDConstants.GROUP_ELEMENT_TAG, parent);
        anyAttributeExists = elementExists(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, parent);
        annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent
            .getFirstChild());
        manager.add(new Separator());
        if (annotationExists)
        {
          if (!(sequenceExists || choiceExists || allExists || groupExists))
          {
            Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
            addCreateElementActionIfNotExist(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, annotationNode
                .getNextSibling());
            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, annotationNode.getNextSibling());
          }
        }
        else
        {
          if (!(sequenceExists || choiceExists || allExists || groupExists))
          {
            addCreateElementActionIfNotExist(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, parent.getFirstChild());
            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, parent.getFirstChild());
          }
        }
        manager.add(new Separator());
        if (anyAttributeExists)
        {
          anyAttributeNode = getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false);
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
        }
        else
        {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
        }
        attributes = null;
        addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, parent.getLastChild());
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.EXTENSION_ELEMENT_TAG, false))
    {
      Element parentNode = (Element) parent.getParentNode();
      // <simpleContent>
      //    <extension>
      //      ...
      if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        boolean anyAttributeExists = false;
        Node anyAttributeNode = null;
        anyAttributeExists = elementExists(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, parent);
        addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent
            .getFirstChild());
        manager.add(new Separator());
        if (anyAttributeExists)
        {
          anyAttributeNode = getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false);
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
        }
        else
        {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
        }
        attributes = null;
        addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, parent.getLastChild());
      }
      // <complexContent>
      //    <extension>
      //      ...
      else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        boolean annotationExists = false;
        boolean anyAttributeExists = false;
        Node anyAttributeNode = null;
        boolean sequenceExists = elementExists(XSDConstants.SEQUENCE_ELEMENT_TAG, parent);
        boolean choiceExists = elementExists(XSDConstants.CHOICE_ELEMENT_TAG, parent);
        boolean allExists = elementExists(XSDConstants.ALL_ELEMENT_TAG, parent);
        boolean groupExists = elementExists(XSDConstants.GROUP_ELEMENT_TAG, parent);
        anyAttributeExists = elementExists(XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, parent);
        annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent
            .getFirstChild());
        manager.add(new Separator());
        if (annotationExists)
        {
          if (!(sequenceExists || choiceExists || allExists || groupExists))
          {
            Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
            addCreateElementActionIfNotExist(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, annotationNode
                .getNextSibling());
            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, annotationNode.getNextSibling());
          }
        }
        else
        {
          if (!(sequenceExists || choiceExists || allExists || groupExists))
          {
            addCreateElementActionIfNotExist(manager, XSDConstants.SEQUENCE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CONTENT_MODEL"), attributes, parent, parent.getFirstChild());
            addCreateElementRefAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ADD_GROUP_REF"), parent, parent.getFirstChild());
          }
        }
        manager.add(new Separator());
        if (anyAttributeExists)
        {
          anyAttributeNode = getFirstChildNodeIfExists(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false);
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, anyAttributeNode);
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, anyAttributeNode);
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, anyAttributeNode);
        }
        else
        {
          attributes = new ArrayList();
          attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewName(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, "Attribute", false)));
          attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
          addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE"), attributes, parent, parent.getLastChild());
          attributes = null;
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_REFERENCE"), parent, parent.getLastChild());
          addCreateElementRefAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP_REF"), parent, parent.getLastChild());
        }
        attributes = null;
        addCreateElementActionIfNotExist(manager, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANY_ATTRIBUTE"), attributes, parent, parent.getLastChild());
      }
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.REDEFINE_ELEMENT_TAG, false))
    { //
      addCreateElementAction(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, null);
      addCreateElementAction(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SIMPLE_TYPE"), attributes, parent, null);
      addCreateElementAction(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_TYPE"), attributes, parent, null);
      addCreateGroupAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GROUP"), attributes, parent, null);
      addCreateElementAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP"), attributes, parent, null);
      manager.add(new Separator());
      addOpenSchemaAction(manager, XSDEditorPlugin.getXSDString("_UI_ACTION_OPEN_SCHEMA"), parent);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.LIST_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      addCreateLocalSimpleTypeActionIfNotExist(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.UNION_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      addCreateLocalSimpleTypeAction(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.UNIQUE_ELEMENT_TAG, false))
    {
      boolean annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      if (annotationExists)
      {
        Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, annotationNode.getNextSibling());
      }
      else
      {
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, parent.getFirstChild());
      }
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      addCreateElementAction(manager, XSDConstants.FIELD_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_FIELD"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.KEYREF_ELEMENT_TAG, false))
    {
      boolean annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      if (annotationExists)
      {
        Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, annotationNode.getNextSibling());
      }
      else
      {
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, parent.getFirstChild());
      }
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      addCreateElementAction(manager, XSDConstants.FIELD_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_FIELD"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.KEY_ELEMENT_TAG, false))
    {
      boolean annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
          parent.getFirstChild());
      manager.add(new Separator());
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      if (annotationExists)
      {
        Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, annotationNode.getNextSibling());
      }
      else
      {
        addCreateElementActionIfNotExist(manager, XSDConstants.SELECTOR_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SELECTOR"), attributes, parent, parent.getFirstChild());
      }
      attributes = new ArrayList();
      attributes.add(new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, ""));
      addCreateElementAction(manager, XSDConstants.FIELD_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_FIELD"), attributes, parent, null);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.IMPORT_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      manager.add(new Separator());
      addOpenSchemaAction(manager, XSDEditorPlugin.getXSDString("_UI_ACTION_OPEN_SCHEMA"), parent);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.SELECTOR_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.FIELD_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.INCLUDE_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      manager.add(new Separator());
      addOpenSchemaAction(manager, XSDEditorPlugin.getXSDString("_UI_ACTION_OPEN_SCHEMA"), parent);
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ANY_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      // need to add adapters for the ANY element.  I'd rather not provide this menu
      // and let users see that the graph view doesn't update
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {
      addCreateElementActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
      addCreateLocalSimpleTypeActionIfNotExist(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, null);
    }
    // Facets all have optional annotation nodes
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MINEXCLUSIVE_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MININCLUSIVE_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MAXINCLUSIVE_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.TOTALDIGITS_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.FRACTIONDIGITS_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.LENGTH_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MINLENGTH_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.MAXLENGTH_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ENUMERATION_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.WHITESPACE_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.PATTERN_ELEMENT_TAG, false))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
    {
      addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent, parent.getFirstChild());
    }
    else if (XSDDOMHelper.inputEquals(parent, XSDConstants.ELEMENT_ELEMENT_TAG, true))
    {
      XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)getXSDSchema().getCorrespondingComponent(parent);
      if (xsdConcreteComponent instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdConcreteComponent;
        XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
        if (resolvedElementDeclaration.getRootContainer() == xsdSchema)
        {
          parent = resolvedElementDeclaration.getElement(); 
        
          boolean annotationExists = addCreateAnnotationActionIfNotExist(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), attributes, parent,
              parent.getFirstChild());
          boolean simpleTypeExists = elementExists(XSDConstants.SIMPLETYPE_ELEMENT_TAG, parent);
          boolean complexTypeExists = elementExists(XSDConstants.COMPLEXTYPE_ELEMENT_TAG, parent);
          manager.add(new Separator());
          if (annotationExists)
          {
            Node annotationNode = getFirstChildNodeIfExists(parent, XSDConstants.ANNOTATION_ELEMENT_TAG, false);
            if (!(simpleTypeExists || complexTypeExists) && annotationNode != null)
            {
              manager.add(new Separator());
            }
          }
          else
          {
            XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
            if (concreteComponent != null)
            {
              AddModelGroupAction addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.SEQUENCE_LITERAL);
              addModelGroupAction.setEnabled(!isReadOnly);
              manager.add(addModelGroupAction);
              
              addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.CHOICE_LITERAL);
              addModelGroupAction.setEnabled(!isReadOnly);
              manager.add(addModelGroupAction);
              
              addModelGroupAction = new AddModelGroupAction(concreteComponent, XSDCompositor.ALL_LITERAL);
              addModelGroupAction.setEnabled(!isReadOnly);
              manager.add(addModelGroupAction);
              
              manager.add(new Separator());
            }
          }
          XSDDOMHelper domHelper = new XSDDOMHelper();
          Element anonymousType = (Element) domHelper.getChildNode(parent, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
          if (anonymousType != null)
          {
            manager.add(new Separator());
            attributes = new ArrayList();
            attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("ComplexType")));
            addMoveAnonymousGlobal(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, anonymousType, null);
            attributes = null;
          }
          anonymousType = (Element) domHelper.getChildNode(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
          if (anonymousType != null)
          {
            manager.add(new Separator());
            attributes = new ArrayList();
            attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("SimpleType")));
            addMoveAnonymousGlobal(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"), attributes, anonymousType, null);
            attributes = null;
          }
        }
        addMultiplicityMenu(xsdConcreteComponent, manager);
      }
    }
   
    XSDConcreteComponent concreteComponent = getXSDSchema().getCorrespondingComponent(parent);
    if (concreteComponent instanceof XSDNamedComponent)
    {
      addRefactorMenuGroup(manager);
    }
  }

  protected void addContextInsertItems(IMenuManager manager, Element parent, Element currentElement, Node relativeNode)
  {
    ArrayList attributes = null;
    if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.INCLUDE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals(currentElement, XSDConstants.IMPORT_ELEMENT_TAG, false)
        || XSDDOMHelper.inputEquals(currentElement, XSDConstants.REDEFINE_ELEMENT_TAG, false) || XSDDOMHelper.inputEquals(currentElement, XSDConstants.ANNOTATION_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        attributes = new ArrayList();
        attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, ""));
        addCreateElementAction(manager, XSDConstants.INCLUDE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_INCLUDE"), attributes, parent, relativeNode);
        addCreateElementAction(manager, XSDConstants.IMPORT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_IMPORT"), null, parent, relativeNode);
        addCreateElementAction(manager, XSDConstants.REDEFINE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_REDEFINE"), attributes, parent, relativeNode);
        attributes = null;
        addCreateElementAction(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), null, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
      else if (XSDDOMHelper.inputEquals(parent, XSDConstants.UNION_ELEMENT_TAG, false))
      {
        addCreateLocalSimpleTypeAction(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LOCAL_SIMPLE_TYPE"), attributes, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.GROUP_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.NOTATION_ELEMENT_TAG, false))
    {
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        addSchemaElementItems(manager, parent, relativeNode);
      }
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.DOCUMENTATION_ELEMENT_TAG, false))
    {
      addCreateElementAction(manager, XSDConstants.DOCUMENTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_DOC"), attributes, parent, relativeNode);
      addCreateElementAction(manager, XSDConstants.APPINFO_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_APP_INFO"), attributes, parent, relativeNode);
    }
    else if (XSDDOMHelper.inputEquals(currentElement, XSDConstants.APPINFO_ELEMENT_TAG, false))
    {
      addCreateElementAction(manager, XSDConstants.DOCUMENTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_DOC"), attributes, parent, relativeNode);
      addCreateElementAction(manager, XSDConstants.APPINFO_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_APP_INFO"), attributes, parent, relativeNode);
    }
  }

  protected String getNewGlobalName(String elementTag, String description)
  {
    return getNewGlobalName(elementTag, description, false);
  }

  protected String getNewGlobalTypeName(String description)
  {
    return getNewGlobalName(null, description, true);
  }

  protected String getNewGlobalName(String elementTag, String description, boolean isSimpleOrComplexType)
  {  
     return getNewName(getXSDSchema().getDocument(), elementTag, description, isSimpleOrComplexType);
  }

  // TODO.. .we need to rewrite this code to me model driven... not document driven
  //
  protected String getNewName(Node parentNode, String elementTag, String description, boolean isSimpleOrComplexType)
  {
    NodeList list = null;
    NodeList typeList2 = null;
    // if the global name is for a simple or complex type, we ignore the
    // elementTag and populate 2 lists
    // one to look for all simple types and the other to look for all complex
    // types
    if (isSimpleOrComplexType)
    {
      if (parentNode instanceof Document)
      {
        list = ((Document) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
        typeList2 = ((Document) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      }
      else if (parentNode instanceof Element)
      {
        list = ((Element) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
        typeList2 = ((Element) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      }
    }
    else
    {
      if (parentNode instanceof Document)
      {
        list = ((Document) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, elementTag);
      }
      else if (parentNode instanceof Element)
      {
        list = ((Element) parentNode).getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, elementTag);
      }
    }
    String name = "New" + description;
    if (list == null || list.getLength() == 0 && (typeList2 != null && typeList2.getLength() == 0))
    {
      return name;
    }
    for (int i = 1; i < 100; i++)
    {
      boolean newName = false;
      for (int j = 0; j < list.getLength(); j++)
      {
        String currName = ((Element) list.item(j)).getAttribute(XSDConstants.NAME_ATTRIBUTE);
        if (currName == null || currName.length() == 0)
        {
          continue;
        }
        if (currName.equals(name))
        {
          name = "New" + description + String.valueOf(i);
          newName = true;
          break;
        }
      }
      // if there is another type list and we haven't created a new name, then
      // check the type list
      if (typeList2 != null && !newName)
      {
        for (int j = 0; j < typeList2.getLength(); j++)
        {
          String currName = ((Element) typeList2.item(j)).getAttribute(XSDConstants.NAME_ATTRIBUTE);
          if (currName == null || currName.length() == 0)
          {
            continue;
          }
          if (currName.equals(name))
          {
            name = "New" + description + String.valueOf(i);
            break;
          }
        }
      }
    }
    return name;
  }

  protected String getFirstGlobalElementTagName(String elementTag)
  {
    //XMLModel model = getXMLModel();
    //if (model != null)
    {
      XSDSchema schema = getXSDSchema();
      TypesHelper helper = new TypesHelper(schema);
      String prefix = "";
      if (schema != null)
      {
        prefix = helper.getPrefix(schema.getTargetNamespace(), true);
      }
      // get the schema node
      NodeList slist = schema.getDocument().getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.SCHEMA_ELEMENT_TAG);
      Node schemaNode = null;
      if (slist != null && slist.getLength() > 0)
      {
        schemaNode = slist.item(0);
      }
      NodeList list = null;
      // get the schema's direct children - hence, globals
      if (schemaNode != null)
      {
        list = schemaNode.getChildNodes();
      }
      String name = null;
      if (list != null)
      {
        for (int i = 0; i < list.getLength(); i++)
        {
          if (list.item(i) instanceof Element)
          {
            if (list.item(i).getLocalName().equals(elementTag))
            {
              name = ((Element) list.item(i)).getAttribute(XSDConstants.NAME_ATTRIBUTE);
              if (name != null && name.length() > 0)
              {
                return prefix + name;
              }
            }
          }
        }
      }
      if (elementTag.equals(XSDConstants.ELEMENT_ELEMENT_TAG))
      {
        return helper.getGlobalElement(schema);
      }
      else if (elementTag.equals(XSDConstants.ATTRIBUTE_ELEMENT_TAG))
      {
        return helper.getGlobalAttribute(schema);
      }
      else if (elementTag.equals(XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG))
      {
        return helper.getGlobalAttributeGroup(schema);
      }
      else if (elementTag.equals(XSDConstants.GROUP_ELEMENT_TAG))
      {
        return helper.getModelGroup(schema);
      }
    }
    return null;
  }

  protected void addSchemaElementItems(IMenuManager manager, Element parent, Node relativeNode)
  {
    ArrayList attributes = null;
    // Add Edit Namespaces menu action
    XSDEditNamespacesAction nsAction = new XSDEditNamespacesAction(XSDEditorPlugin.getXSDString("_UI_ACTION_EDIT_NAMESPACES"), parent, relativeNode, getXSDSchema());
    manager.add(nsAction);
    manager.add(new Separator());
    DOMAttribute nameAttribute = new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("ComplexType"));
    attributes = new ArrayList();
    attributes.add(nameAttribute);
    Action action = addCreateElementAction(manager, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_TYPE"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalTypeName("SimpleType")));
    action = addCreateElementAction(manager, XSDConstants.SIMPLETYPE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SIMPLE_TYPE"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ELEMENT_ELEMENT_TAG, "GlobalElement")));
    attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
    action = addCreateElementAction(manager, XSDConstants.ELEMENT_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GLOBAL_ELEMENT"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ATTRIBUTE_ELEMENT_TAG, "GlobalAttribute")));
    attributes.add(new DOMAttribute(XSDConstants.TYPE_ATTRIBUTE, getBuiltInStringQName()));
    action = addCreateElementAction(manager, XSDConstants.ATTRIBUTE_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GLOBAL_ATTRIBUTE"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, "AttributeGroup")));
    action = addCreateElementAction(manager, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ATTRIBUTE_GROUP"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.GROUP_ELEMENT_TAG, "Group")));
    CreateGroupAction groupAction = addCreateGroupAction(manager, XSDConstants.GROUP_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_GROUP"), attributes, parent, relativeNode);
    groupAction.setIsGlobal(true);
    attributes = new ArrayList();
    attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, getNewGlobalName(XSDConstants.NOTATION_ELEMENT_TAG, "Notation")));
    attributes.add(new DOMAttribute(XSDConstants.PUBLIC_ATTRIBUTE, ""));
    action = addCreateElementAction(manager, XSDConstants.NOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_NOTATION"), attributes, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
    action = addCreateElementAction(manager, XSDConstants.ANNOTATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ANNOTATION"), null, parent, relativeNode);
    ((CreateElementAction) action).setIsGlobal(true);
  }

  // returns whether element exists already
  protected boolean addCreateElementActionIfNotExist(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    if (getFirstChildNodeIfExists(parent, elementTag, false) == null)
    {
      addCreateElementAction(manager, elementTag, label, attributes, parent, relativeNode);
      return false;
    }
    return true;
  }

  protected Action addCreateElementAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateElementAction action = new CreateElementAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    action.setSourceContext(sourceContext);
    manager.add(action);
    return action;
  }

  protected void addCreateElementAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode, boolean isEnabled)
  {
    Action action = addCreateElementAction(manager, elementTag, label, attributes, parent, relativeNode);
    action.setEnabled(isEnabled);
  }

  protected void addCreateElementRefAction(IMenuManager manager, String elementTag, String label, Element parent, Node relativeNode)
  {
    ArrayList attributes = new ArrayList();
    String ref = getFirstGlobalElementTagName(elementTag);
    attributes.add(new DOMAttribute(XSDConstants.REF_ATTRIBUTE, ref));
    Action action = addCreateElementAction(manager, elementTag, label, attributes, parent, relativeNode);
    action.setEnabled(ref != null && !isReadOnly);
  }

  protected void addCreateSimpleContentAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    if (getFirstChildNodeIfExists(parent, elementTag, false) == null)
    {
      CreateSimpleContentAction action = new CreateSimpleContentAction(label, getXSDSchema());
      action.setElementTag(elementTag);
      action.setAttributes(attributes);
      action.setParentNode(parent);
      action.setRelativeNode(relativeNode);
      action.setEnabled(!isReadOnly);
      manager.add(action);
    }
  }

  protected CreateGroupAction addCreateGroupAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateGroupAction action = new CreateGroupAction(label, getXSDSchema());
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    action.setSourceContext(sourceContext);
    manager.add(action);
    return action;
  }

  protected void addCreateIdentityConstraintsAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateIdentityConstraintsAction action = new CreateIdentityConstraintsAction(label, getXSDSchema());
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected void addEnumsAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    AddEnumsAction action = new AddEnumsAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setDescription(XSDEditorPlugin.getXSDString("_UI_ENUMERATIONS_DIALOG_TITLE"));
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected Action addCreateSimpleTypeAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateSimpleTypeAction action = new CreateSimpleTypeAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    action.setSourceContext(sourceContext);
    manager.add(action);
    return action;
  }

  protected boolean addCreateLocalSimpleTypeActionIfNotExist(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    if (getFirstChildNodeIfExists(parent, elementTag, false) == null)
    {
      addCreateLocalSimpleTypeAction(manager, elementTag, label, attributes, parent, relativeNode);
      return false;
    }
    return true;
  }

  protected void addSetBaseTypeAction(IMenuManager manager, Element element)
  {
    SetBaseTypeAction action = new SetBaseTypeAction(XSDEditorPlugin.getXSDString("_UI_ACTION_SET_BASE_TYPE"));// +
    // "...");
    action.setComplexTypeElement(element);
    action.setXSDSchema(getXSDSchema());
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected void addCreateLocalSimpleTypeAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateLocalSimpleTypeAction action = new CreateLocalSimpleTypeAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected boolean addCreateLocalComplexTypeActionIfNotExist(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    if (getFirstChildNodeIfExists(parent, elementTag, false) == null)
    {
      addCreateLocalComplexTypeAction(manager, elementTag, label, attributes, parent, relativeNode);
      return false;
    }
    return true;
  }

  protected void addCreateLocalComplexTypeAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateLocalComplexTypeAction action = new CreateLocalComplexTypeAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getXSDSchema());
    action.setSelectionProvider(selectionProvider);
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected boolean addCreateAnnotationActionIfNotExist(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    return false;
  }

  protected void addOpenSchemaAction(IMenuManager manager, String label, Element parent)
  {
    OpenSchemaAction openAction = new OpenSchemaAction(label, getXSDSchema().getCorrespondingComponent(parent));
    manager.add(openAction);
  }

  protected void addMoveAnonymousGlobal(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
//    MakeAnonymousGlobal action = new MakeAnonymousGlobal(label, parent, getXSDSchema());
//    action.setElementTag(elementTag);
//    action.setAttributes(attributes);
//    action.setParentNode(getXSDSchema().getElement());
//    action.setRelativeNode(relativeNode);
//    action.setEnabled(!isReadOnly);
//    //manager.add(action);
//    addRefactorMenuGroup(manager);
//    fRefactorMenuGroup.addAction(action);
  }
  
  protected void addRefactorMenuGroup(IMenuManager manager){
    /*
	  	fRefactorMenuGroup = new RefactorActionGroup(selectionProvider, getXSDSchema());
     	ActionContext context= new ActionContext(selectionProvider.getSelection());
    	fRefactorMenuGroup.setContext(context);
    	fRefactorMenuGroup.fillContextMenu(manager);
    	fRefactorMenuGroup.setContext(null);
    */    
  }

  protected void addCreateAnnotationAction(IMenuManager manager, String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateAnnotationAction action = new CreateAnnotationAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setEnabled(!isReadOnly);
    manager.add(action);
  }

  protected Node getFirstChildNodeIfExists(Node parent, String elementTag, boolean isRef)
  {
    if (parent == null)
      return null;
    NodeList children = parent.getChildNodes();
    Node targetNode = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child != null && child instanceof Element)
      {
        if (XSDDOMHelper.inputEquals((Element) child, elementTag, isRef))
        {
          targetNode = child;
          break;
        }
      }
    }
    return targetNode;
  }

  protected boolean elementExists(String elementTag, Element parent)
  {
    if (!(parent.getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, elementTag).getLength() > 0))
    {
      return false;
    }
    return true;
  }

  /**
   * Returns the deleteAction.
   * 
   * @return DeleteAction
   */
  public DeleteAction getDeleteAction()
  {
    return deleteAction;
  }
  
  protected void addMultiplicityMenu(XSDConcreteComponent concreteComponent, IMenuManager manager)
  {
    SetMultiplicityAction oneMultiplicity = new SetMultiplicityAction(concreteComponent, "1");
    oneMultiplicity.setMaxOccurs(1);
    oneMultiplicity.setMinOccurs(1);
    oneMultiplicity.setEnabled(!isReadOnly);
    SetMultiplicityAction zeroOrMoreMultiplicity = new SetMultiplicityAction(concreteComponent, "0..* (" + XSDEditorPlugin.getXSDString("_ZERO_OR_MORE") + ")");
    zeroOrMoreMultiplicity.setMaxOccurs(-1);
    zeroOrMoreMultiplicity.setMinOccurs(0);
    zeroOrMoreMultiplicity.setEnabled(!isReadOnly);
    SetMultiplicityAction zeroOrOneMultiplicity = new SetMultiplicityAction(concreteComponent, "0..1 (" + XSDEditorPlugin.getXSDString("_ZERO_OR_ONE") + ")");
    zeroOrOneMultiplicity.setMaxOccurs(1);
    zeroOrOneMultiplicity.setMinOccurs(0);
    zeroOrOneMultiplicity.setEnabled(!isReadOnly);
    SetMultiplicityAction oneOrMoreMultiplicity = new SetMultiplicityAction(concreteComponent, "1..* (" + XSDEditorPlugin.getXSDString("_ONE_OR_MORE") + ")");
    oneOrMoreMultiplicity.setMaxOccurs(-1);
    oneOrMoreMultiplicity.setMinOccurs(1);
    oneOrMoreMultiplicity.setEnabled(!isReadOnly);
    
    MenuManager multiplicityMenu = new MenuManager(XSDEditorPlugin.getXSDString("_UI_ACTION_SET_MULTIPLICITY"));
    manager.add(multiplicityMenu);
    multiplicityMenu.add(oneMultiplicity);
    multiplicityMenu.add(zeroOrOneMultiplicity);
    multiplicityMenu.add(zeroOrMoreMultiplicity);
    multiplicityMenu.add(oneOrMoreMultiplicity);    
  }
}