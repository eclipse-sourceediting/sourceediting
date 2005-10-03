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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDSetTypeHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SimpleTypeSection extends AbstractSection
{
  CCombo varietyCombo;
  Text typesText;
  CLabel typesLabel;
  Button button;

  /**
   * 
   */
  public SimpleTypeSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		varietyCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		CLabel label = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_VARIETY")); //$NON-NLS-1$
    
		List list = XSDVariety.VALUES;	
		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
		  varietyCombo.add(((XSDVariety)iter.next()).getName());
		}
    varietyCombo.addSelectionListener(this);

		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, 0);
		varietyCombo.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(varietyCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(varietyCombo, 0, SWT.CENTER);
		label.setLayoutData(data);
    
    typesText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    typesLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$
    button = getWidgetFactory().createButton(composite, "", SWT.PUSH); //$NON-NLS-1$
    button.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
    
    typesText.addListener(SWT.Modify, this);

    data = new FormData();
    data.left = new FormAttachment(0, 100);
    data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(button, 0, SWT.CENTER);
    typesText.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(typesText, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(button, 0, SWT.CENTER);
    typesLabel.setLayoutData(data);

    button.addSelectionListener(this);
    data = new FormData();
    data.left = new FormAttachment(typesText, 0);
    data.right = new FormAttachment(100,0);
    data.top = new FormAttachment(varietyCombo, +ITabbedPropertyConstants.VSPACE);
    button.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  setListenerEnabled(false);
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
	  Object input = getInput();
	  varietyCombo.setText(""); //$NON-NLS-1$
    typesText.setText(""); //$NON-NLS-1$
    typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON")); //$NON-NLS-1$
	  if (input != null)
	  {
	    if (input instanceof XSDSimpleTypeDefinition)
	    {
	      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
        
        Element simpleTypeElement = st.getElement();
        Element element = null;
	      String variety = st.getVariety().getName();
        
        int intVariety = st.getVariety().getValue();
        
	      if (variety != null)
	      {
          varietyCombo.setText(variety);
          if (intVariety == XSDVariety.ATOMIC)
          {
            element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.RESTRICTION_ELEMENT_TAG);
//            if (element == null)
//            {
//              element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.EXTENSION_ELEMENT_TAG);
//              if (element == null) return;
//            }
            
            if (element == null)
            {
              varietyCombo.setText(XSDEditorPlugin.getXSDString("_UI_NO_TYPE")); // "Select a simple type variety"); //$NON-NLS-1$            
            }
            else
            {
              String result = element.getAttribute(XSDConstants.BASE_ATTRIBUTE);
              if (result == null)
              {
                typesText.setText("**anonymous**"); //$NON-NLS-1$
              }
              else
              {
                typesText.setText(result);
              }
            }
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON")); //$NON-NLS-1$
          }
          else if (intVariety == XSDVariety.LIST)
          {
            element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.LIST_ELEMENT_TAG);
            if (element != null)
            {
              String result = element.getAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
              if (result == null)
              {
                typesText.setText("**anonymous**"); //$NON-NLS-1$
              }
              else
              {
                typesText.setText(result);
              }
            }
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_ITEM_TYPE")); //$NON-NLS-1$
          }
          else if (intVariety == XSDVariety.UNION)
          {
            Element unionElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.UNION_ELEMENT_TAG);
            if (unionElement != null)
            {
              String memberTypes = unionElement.getAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
              if (memberTypes != null)
              {
                typesText.setText(memberTypes);
              }
            }
            typesLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$
          }
	      }
	    }
	  }
	  setListenerEnabled(true);
	}

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
	  Object input = getInput();
    if (e.widget == varietyCombo)
    {
  	  if (input != null)
  	  {
  	    if (input instanceof XSDSimpleTypeDefinition)
  	    {
  	      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
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
    else if (e.widget == button)
    {
      Shell shell = Display.getCurrent().getActiveShell();
      Element element = ((XSDConcreteComponent)input).getElement();
      Dialog dialog = null;
      String property = "";
      Element secondaryElement = null;
      
      IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();

      XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, xsdSchema);
      dialog = new XSDComponentSelectionDialog(shell, XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"), provider);
      provider.setDialog((XSDComponentSelectionDialog) dialog);
      
      if (input instanceof XSDSimpleTypeDefinition)
      {
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
        Element simpleTypeElement = st.getElement();
        if (st.getVariety() == XSDVariety.LIST_LITERAL)
        {
          Element listElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.LIST_ELEMENT_TAG);
//          dialog = new TypesDialog(shell, listElement, XSDConstants.ITEMTYPE_ATTRIBUTE, xsdSchema);
//          dialog.showComplexTypes = false;
          provider.showComplexTypes(false);
          
          secondaryElement = listElement;
          property = XSDConstants.ITEMTYPE_ATTRIBUTE;
        }
        else if (st.getVariety() == XSDVariety.ATOMIC_LITERAL)
        {
          Element derivedByElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.RESTRICTION_ELEMENT_TAG);
          if (derivedByElement == null)
          {
            derivedByElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.EXTENSION_ELEMENT_TAG);
            if (derivedByElement == null) return;
          }
          if (derivedByElement != null)
          {
//            dialog = new TypesDialog(shell, derivedByElement, XSDConstants.BASE_ATTRIBUTE, xsdSchema);
//            dialog.showComplexTypes = false;
              provider.showComplexTypes(false);

              secondaryElement = derivedByElement;
              property = XSDConstants.BASE_ATTRIBUTE;
          }
          else
          {
            return;
          }
        }
        else if (st.getVariety() == XSDVariety.UNION_LITERAL)
        {
          SimpleContentUnionMemberTypesDialog unionDialog = new SimpleContentUnionMemberTypesDialog(shell, st);
          unionDialog.setBlockOnOpen(true);
          unionDialog.create();
          
          int result = unionDialog.open();
          if (result == Window.OK)
          {
            String newValue = unionDialog.getResult();
            beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES_CHANGE"), element); //$NON-NLS-1$
            Element unionElement = (Element)domHelper.getChildNode(element, XSDConstants.UNION_ELEMENT_TAG);
            unionElement.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, newValue);

            if (newValue.length() > 0)
            {
              unionElement.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, newValue);
            }
            else
            {
              unionElement.removeAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);  
            }
            endRecording(unionElement);
            if (doRefresh)
            {
              refresh();
            }
          }
          return;
        }
        else
        {
//          dialog = new TypesDialog(shell, element, "type", xsdSchema); //$NON-NLS-1$
            property = "type";
        }
      }
      else
      {
//        dialog = new TypesDialog(shell, element, "type", xsdSchema); //$NON-NLS-1$
          property = "type";
      }
      beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element); //$NON-NLS-1$
      dialog.setBlockOnOpen(true);
      dialog.create();
      int result = dialog.open();
      
      if (result == Window.OK)
      {
          if (secondaryElement == null) {
              secondaryElement = element;
          }
          XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, xsdSchema);
          helper.setType(secondaryElement, property, ((XSDComponentSelectionDialog) dialog).getSelection());          

        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
        st.setElement(element);
        updateSimpleTypeFacets();
      }
      endRecording(element);
    }
    if (doRefresh)
    {
      refresh();
    }
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  
  protected boolean addCreateElementActionIfNotExist(String elementTag, String label, Element parent, Node relativeNode)
  {
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
    List attributes = new ArrayList();
    String reuseType = null;
    
    beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_VARIETY_CHANGE"), parent); //$NON-NLS-1$
    if (elementTag.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
    {
      Element listNode = getFirstChildNodeIfExists(parent, XSDConstants.LIST_ELEMENT_TAG, false);
      if (listNode != null)
      {
        reuseType = listNode.getAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
        XSDDOMHelper.removeNodeAndWhitespace(listNode);
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
      Action action = addCreateElementAction(elementTag,label,attributes,parent,relativeNode);
      action.run();
    }

    st.setElement(parent);
//    st.updateElement();
    endRecording(parent);
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
    for (int i=0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child != null && child instanceof Element)
      {
        if (XSDDOMHelper.inputEquals((Element)child, elementTag, isRef))
        {
          targetNode = (Element)child;
          break;
        }
      }
    }
    return targetNode;
  }

  protected String getBuiltInStringQName()
  {
    String stringName = "string"; //$NON-NLS-1$
    
    if (getSchema() != null)
    {
      String schemaForSchemaPrefix = getSchema().getSchemaForSchemaQNamePrefix();
      if (schemaForSchemaPrefix != null && schemaForSchemaPrefix.length() > 0)
      {
        String prefix = getSchema().getSchemaForSchemaQNamePrefix();
        if (prefix != null && prefix.length() > 0)
        {
          stringName = prefix + ":" + stringName; //$NON-NLS-1$
        }
      }
    }
    return stringName;
  }

  private void updateSimpleTypeFacets()
  {
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
    Element simpleTypeElement = st.getElement();
    Element derivedByElement = getDomHelper().getDerivedByElement(simpleTypeElement);
    if (derivedByElement != null)
    {
      List nodesToRemove = new ArrayList();
      NodeList childList = derivedByElement.getChildNodes();
      int length = childList.getLength();
      for (int i = 0; i < length; i++)
      {
        Node child = childList.item(i);
        if (child instanceof Element)
        {
          Element elementChild = (Element)child;
          if (!(elementChild.getLocalName().equals("pattern") || elementChild.getLocalName().equals("enumeration") || //$NON-NLS-1$
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ANNOTATION_ELEMENT_TAG, false) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true) ||
               XSDDOMHelper.inputEquals(elementChild, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false)
               ))
          {
            nodesToRemove.add(child);
          }
        }
      }
      Iterator iter = nodesToRemove.iterator();
      while (iter.hasNext())
      {
        Element facetToRemove = (Element)iter.next();
        String facetName = facetToRemove.getLocalName();
        Iterator it = st.getValidFacets().iterator();
        boolean doRemove = true;
        while (it.hasNext())
        {
          String aValidFacet = (String)it.next();
          if (aValidFacet.equals(facetName))
          {
            doRemove = false;
            break;
          }
        }
        if (doRemove)
        {
          XSDDOMHelper.removeNodeAndWhitespace(facetToRemove);
        }
      }
    }
  }

}
