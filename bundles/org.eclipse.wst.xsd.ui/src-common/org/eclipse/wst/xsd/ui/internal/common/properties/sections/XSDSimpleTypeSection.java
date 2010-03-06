/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xerces.util.XMLChar;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.XSDComplexTypeBaseTypeEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDTypeReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.util.StringTokenizer;

public class XSDSimpleTypeSection extends RefactoringSection
{
  protected Text nameText;
  CCombo varietyCombo;
  CCombo typesCombo;
  CLabel typesLabel;

  XSDSimpleTypeDefinition memberTypeDefinition, itemTypeDefinition, baseTypeDefinition;

  public XSDSimpleTypeSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);

    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // NameLabel
    // ------------------------------------------------------------------

    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel nameLabel = factory.createCLabel(composite, Messages.UI_LABEL_NAME);
    nameLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // NameText
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    nameText.setLayoutData(data);
    applyAllListeners(nameText);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
    		XSDEditorCSHelpIds.GENERAL_TAB__SIMPLE_TYPE__NAME);
    

    // ------------------------------------------------------------------
    // Refactor/rename hyperlink 
    // ------------------------------------------------------------------
    createRenameHyperlink(composite);

    // Variety Label
    CLabel label = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_VARIETY")); //$NON-NLS-1$

    // Variety Combo
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    label.setLayoutData(data);

    varietyCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    List list = XSDVariety.VALUES;
    Iterator iter = list.iterator();
    while (iter.hasNext())
    {
      varietyCombo.add(((XSDVariety) iter.next()).getName());
    }
    varietyCombo.addSelectionListener(this);
    varietyCombo.setLayoutData(data);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(varietyCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__SIMPLE_TYPE__VARIETY);


    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // Types Label
    // ------------------------------------------------------------------
    typesLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // Types Combo
    // ------------------------------------------------------------------
    typesCombo = getWidgetFactory().createCCombo(composite);
    typesCombo.setEditable(false);
    typesCombo.setLayoutData(data);
    typesCombo.addSelectionListener(this);
    typesCombo.addListener(SWT.Traverse, this);

    
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    typesCombo.setLayoutData(data);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(typesCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__SIMPLE_TYPE__BASE_TYPE);

  }

  protected void relayout()
  {
    Composite parentComposite = composite.getParent();
    parentComposite.getParent().setRedraw(false);

    if (parentComposite != null && !parentComposite.isDisposed())
    {
      Control[] children = parentComposite.getChildren();
      for (int i = 0; i < children.length; i++)
      {
        children[i].dispose();
      }
    }

    // Now initialize the new handler
    createContents(parentComposite);
    parentComposite.getParent().layout(true, true);

    // Now turn painting back on
    parentComposite.getParent().setRedraw(true);
    refresh();
  }

  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);
    showLink(!hideHyperLink);

    nameText.setText(""); //$NON-NLS-1$
    varietyCombo.setText(""); //$NON-NLS-1$
    typesCombo.setText(""); //$NON-NLS-1$
    fillTypesCombo();
    typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON")); //$NON-NLS-1$

    if (input instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) input;
      String simpleTypeName = st.getName();
      if (simpleTypeName != null)
      {
        nameText.setText(simpleTypeName);
        nameText.setEditable(true);
      }
      else
      {
        nameText.setText("**anonymous**"); //$NON-NLS-1$);
        nameText.setEditable(false);
      }
      
      String variety = st.getVariety().getName();
      int intVariety = st.getVariety().getValue();
      XSDDOMHelper domHelper = new XSDDOMHelper();
      if(domHelper.getDerivedByElementFromSimpleType(st.getElement()) != null) {
	      if (variety != null)
	      {
	        varietyCombo.setText(variety);
	        if (intVariety == XSDVariety.ATOMIC)
	        {
	          baseTypeDefinition = st.getBaseTypeDefinition();
	          String name = ""; //$NON-NLS-1$
	          if (baseTypeDefinition != null)
	          {
	            name = baseTypeDefinition.getName();
	            if (name == null) name = "";
	          }
	          typesCombo.setText(name);
	          typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON")); //$NON-NLS-1$
	        }
	        else if (intVariety == XSDVariety.LIST)
	        {
	          itemTypeDefinition = st.getItemTypeDefinition();
	          String name = ""; //$NON-NLS-1$
	          if (itemTypeDefinition != null)
	          {
	            name = itemTypeDefinition.getName();
	            if (name == null) name = "";
	          }
	          typesCombo.setText(name);
	          typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_ITEM_TYPE")); //$NON-NLS-1$
	        }
	        else if (intVariety == XSDVariety.UNION)
	        {
	          List memberTypesList = st.getMemberTypeDefinitions();
	          StringBuffer sb = new StringBuffer();
	          for (Iterator i = memberTypesList.iterator(); i.hasNext();)
	          {
	            XSDSimpleTypeDefinition typeObject = (XSDSimpleTypeDefinition) i.next();
	            String name = typeObject.getQName();
	            if (name != null)
	            {
	              sb.append(name);
	              if (i.hasNext())
	              {
	                sb.append(" "); //$NON-NLS-1$
	              }
	            }
	          }
	          String memberTypes = sb.toString();
	          typesCombo.setText(memberTypes);
	          typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$
	        }
	      }
	    }
    }
    setListenerEnabled(true);

  }

  public void doWidgetDefaultSelected(SelectionEvent e)
  {
    if (e.widget == typesCombo)
    {
      String selection = typesCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == typesCombo)
    {
      String selection = typesCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  private void handleWidgetSelection(SelectionEvent e)
  {
    if (e.widget == typesCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDComplexTypeBaseTypeEditManager.class);    

      String selection = typesCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_COMBO_BROWSE))
      {
        dialog = manager.getBrowseDialog();
        ((XSDSearchListDialogDelegate) dialog).showComplexTypes(false);
      }
      else if ( selection.equals(Messages._UI_COMBO_NEW))
      {
        dialog = manager.getNewDialog();
        ((NewTypeDialog) dialog).allowComplexType(false);
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          manager.modifyComponentReference(input, newValue);
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else if (e.widget == varietyCombo)
    {
      if (input != null)
      {
        if (input instanceof XSDSimpleTypeDefinition)
        {
          XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) input;
          Element parent = st.getElement();

          String variety = varietyCombo.getText();
          if (variety.equals(XSDVariety.ATOMIC_LITERAL.getName()))
          {
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON")); //$NON-NLS-1$
            st.setVariety(XSDVariety.ATOMIC_LITERAL);
            addCreateElementActionIfNotExist(XSDConstants.RESTRICTION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_RESTRICTION"), parent, null); //$NON-NLS-1$
          }
          else if (variety.equals(XSDVariety.UNION_LITERAL.getName()))
          {
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$
            st.setVariety(XSDVariety.UNION_LITERAL);
            addCreateElementActionIfNotExist(XSDConstants.UNION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_UNION"), parent, null); //$NON-NLS-1$
          }
          else if (variety.equals(XSDVariety.LIST_LITERAL.getName()))
          {
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_ITEM_TYPE")); //$NON-NLS-1$
            st.setVariety(XSDVariety.LIST_LITERAL);
            addCreateElementActionIfNotExist(XSDConstants.LIST_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_LIST"), parent, null); //$NON-NLS-1$
          }
        }
      }
    }
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  // issue (cs) this method seems to be utilizing 'old' classes, can we reimplement?
  // (e.g. ChangeElementAction, XSDDOMHelper, etc)
  protected boolean addCreateElementActionIfNotExist(String elementTag, String label, Element parent, Node relativeNode)
  {
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) input;
    List attributes = new ArrayList();
    String reuseType = null;

    // beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_VARIETY_CHANGE"),
    // parent); //$NON-NLS-1$
    if (elementTag.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
    {
      Element listNode = getFirstChildNodeIfExists(parent, XSDConstants.LIST_ELEMENT_TAG, false);
      if (listNode != null)
      {
        if (listNode.hasAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE))
        	reuseType = listNode.getAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
        XSDDOMHelper.removeNodeAndWhitespace(listNode);
      }

      Element unionNode = getFirstChildNodeIfExists(parent, XSDConstants.UNION_ELEMENT_TAG, false);
      if (unionNode != null)
      {
        
        if (unionNode.hasAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE))
        {
          String memberAttr = unionNode.getAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
          StringTokenizer stringTokenizer = new StringTokenizer(memberAttr);
          reuseType = stringTokenizer.nextToken();
        }
        XSDDOMHelper.removeNodeAndWhitespace(unionNode);
      }

      if (reuseType == null)
      {
        reuseType = getBuiltInStringQName();
      }
      attributes.add(new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, reuseType));
      st.setItemTypeDefinition(null);
    }
    else if (elementTag.equals(XSDConstants.LIST_ELEMENT_TAG))
    {
      Element restrictionNode = getFirstChildNodeIfExists(parent, XSDConstants.RESTRICTION_ELEMENT_TAG, false);
      if (restrictionNode != null)
      {
        reuseType = restrictionNode.getAttribute(XSDConstants.BASE_ATTRIBUTE);
        XSDDOMHelper.removeNodeAndWhitespace(restrictionNode);
      }
      Element unionNode = getFirstChildNodeIfExists(parent, XSDConstants.UNION_ELEMENT_TAG, false);
      if (unionNode != null)
      {
        String memberAttr = unionNode.getAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
        if (memberAttr != null)
        {
          StringTokenizer stringTokenizer = new StringTokenizer(memberAttr);
          reuseType = stringTokenizer.nextToken();
        }
        XSDDOMHelper.removeNodeAndWhitespace(unionNode);
      }
      attributes.add(new DOMAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, reuseType));
    }
    else if (elementTag.equals(XSDConstants.UNION_ELEMENT_TAG))
    {
      Element listNode = getFirstChildNodeIfExists(parent, XSDConstants.LIST_ELEMENT_TAG, false);
      if (listNode != null)
      {
        reuseType = listNode.getAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
        XSDDOMHelper.removeNodeAndWhitespace(listNode);
      }
      Element restrictionNode = getFirstChildNodeIfExists(parent, XSDConstants.RESTRICTION_ELEMENT_TAG, false);
      if (restrictionNode != null)
      {
        reuseType = restrictionNode.getAttribute(XSDConstants.BASE_ATTRIBUTE);
        XSDDOMHelper.removeNodeAndWhitespace(restrictionNode);
      }
      attributes.add(new DOMAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, reuseType));
      st.setItemTypeDefinition(null);
    }

    if (getFirstChildNodeIfExists(parent, elementTag, false) == null)
    {
      Action action = addCreateElementAction(elementTag, label, attributes, parent, relativeNode);
      action.run();
    }

    st.setElement(parent);
    st.updateElement();
    // endRecording(parent);
    return true;
  }

  protected Action addCreateElementAction(String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateElementAction action = new CreateElementAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    return action;
  }

  protected Element getFirstChildNodeIfExists(Node parent, String elementTag, boolean isRef)
  {
    NodeList children = parent.getChildNodes();
    Element targetNode = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child != null && child instanceof Element)
      {
        if (XSDDOMHelper.inputEquals(child, elementTag, isRef))
        {
          targetNode = (Element) child;
          break;
        }
      }
    }
    return targetNode;
  }

  protected String getBuiltInStringQName()
  {
    String stringName = "string"; //$NON-NLS-1$

    if (xsdSchema != null)
    {
      String schemaForSchemaPrefix = xsdSchema.getSchemaForSchemaQNamePrefix();
      if (schemaForSchemaPrefix != null && schemaForSchemaPrefix.length() > 0)
      {
        String prefix = xsdSchema.getSchemaForSchemaQNamePrefix();
        if (prefix != null && prefix.length() > 0)
        {
          stringName = prefix + ":" + stringName; //$NON-NLS-1$
        }
      }
    }
    return stringName;
  }

  // TODO: Common this up with element declaration
  public void doHandleEvent(Event event) 
  {
    if (event.type == SWT.Traverse) {
      if (event.detail == SWT.TRAVERSE_ARROW_NEXT || event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
        isTraversing = true;
        return;
      }
    }
    if (event.widget == nameText)
    {
      if (!nameText.getEditable())
        return;

      String newValue = nameText.getText().trim();
      if (input instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent)input;
        if (!validateSection())
          return;

        Command command = null;

        // Make sure an actual name change has taken place
        String oldName = namedComponent.getName();
        if (!newValue.equals(oldName))
        {
          command = new UpdateNameCommand(Messages._UI_ACTION_RENAME, namedComponent, newValue);
        }

        if (command != null && getCommandStack() != null)
        {
          getCommandStack().execute(command);
        }

      }
    }
  }
  
  protected boolean validateSection()
  {
    if (nameText == null || nameText.isDisposed())
      return true;

    setErrorMessage(null);

    String name = nameText.getText().trim();

    // validate against NCName
    if (name.length() < 1 || !XMLChar.isValidNCName(name))
    {
      setErrorMessage(Messages._UI_ERROR_INVALID_NAME);
      return false;
    }

    return true;
  }
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    setListenerEnabled(false);
    if (input instanceof XSDSimpleTypeDefinition)
    {
    	XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) input;
      hideHyperLink = !(simpleType.getContainer() instanceof XSDSchema);
      
    }    
    // Don't have to call relayout() here
    setListenerEnabled(true);
  }
  
  private void fillTypesCombo()
  {
    typesCombo.removeAll();
    
    IEditorPart editor = getActiveEditor();
    ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);
    if (manager != null)
    {
      ComponentSpecification[] items = manager.getQuickPicks();

      typesCombo.add(Messages._UI_COMBO_BROWSE);
      typesCombo.add(Messages._UI_COMBO_NEW);

      for (int i = 0; i < items.length; i++)
      {
        typesCombo.add(items[i].getName());
      }

      // Add the current Type of this attribute if needed
      XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) input;
      XSDTypeDefinition baseType = simpleType.getBaseType();
      if (baseType != null && baseType.getQName() != null)
      {
        String currentTypeName = baseType.getQName(xsdSchema); // no prefix
        ComponentSpecification ret = getComponentSpecFromQuickPickForValue(currentTypeName, manager);
        if (ret == null && currentTypeName != null) // not in quickPick
        {
          typesCombo.add(currentTypeName);
        }
      }
    }
  }

  // TODO: common this up with XSDElementDeclarationSection
  private ComponentSpecification getComponentSpecFromQuickPickForValue(String value, ComponentReferenceEditManager editManager)
  {
    if (editManager != null)
    {  
      ComponentSpecification[] quickPicks = editManager.getQuickPicks();
      if (quickPicks != null)
      {
        for (int i=0; i < quickPicks.length; i++)
        {
          ComponentSpecification componentSpecification = quickPicks[i];
          if (value != null && value.equals(componentSpecification.getName()))
          {
            return componentSpecification;
          }                
        }  
      }
    }
    return null;
  }
  
  public void dispose()
  {
    if (typesCombo != null && !typesCombo.isDisposed())
      typesCombo.removeListener(SWT.Traverse, this);
    super.dispose();
  }
}
