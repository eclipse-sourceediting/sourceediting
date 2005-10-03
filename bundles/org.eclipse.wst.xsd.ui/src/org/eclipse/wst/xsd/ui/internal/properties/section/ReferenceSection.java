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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ReferenceSection extends AbstractSection
{
  protected CCombo componentNameCombo; 
  Button button;
  IEditorPart editorPart;
  CLabel refLabel;
  
	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

    componentNameCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    componentNameCombo.addSelectionListener(this);
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, 1);
		componentNameCombo.setLayoutData(data);

		refLabel = getWidgetFactory().createCLabel(composite, XSDConstants.REF_ATTRIBUTE + ":");  //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(componentNameCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(componentNameCombo, 0, SWT.CENTER);
		refLabel.setLayoutData(data);
	}

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    
    Object obj = getInput();
    TypesHelper helper = new TypesHelper(xsdSchema);
    List items = new ArrayList();
    if (obj instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)obj;
      if (elementDeclaration.isElementDeclarationReference())
      {
        items = helper.getGlobalElements();
//            minimumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MINIMUM"));
//            WorkbenchHelp.setHelp(minimumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MINIMUM);
//            maximumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MAXIMUM"));
//            WorkbenchHelp.setHelp(maximumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MAXIMUM);
      }
    }
    else if (obj instanceof XSDAttributeDeclaration)
    {
      items = helper.getGlobalAttributes();
    }
    else if (obj instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition group = (XSDModelGroupDefinition)obj;
      if (group.isModelGroupDefinitionReference())
      {
        items = helper.getModelGroups();
          // Need tooltip for Group Ref
//            minimumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MINIMUM"));
//            WorkbenchHelp.setHelp(minimumField, XSDEditorContextIds.XSDE_GROUP_REF_MINIMUM);
//            maximumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MAXIMUM"));
//            WorkbenchHelp.setHelp(maximumField, XSDEditorContextIds.XSDE_GROUP_REF_MAXIMUM);
      }
    }
    else if (obj instanceof XSDNamedComponent)
    {
      XSDNamedComponent namedComponent = (XSDNamedComponent)obj;
      Element element = namedComponent.getElement();
      if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
      {
        items = helper.getGlobalAttributeGroups();
//      WorkbenchHelp.setHelp(client, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_REF_DESIGN_VIEW);
//      WorkbenchHelp.setHelp(refCombo, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_REF_NAME);
      }
      else if (XSDDOMHelper.inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, true))
      {
        items = helper.getGlobalElements();
//          minimumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MINIMUM"));
//          WorkbenchHelp.setHelp(minimumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MINIMUM);
//          maximumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MAXIMUM"));
//          WorkbenchHelp.setHelp(maximumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MAXIMUM);
      }
    }
    else if (obj instanceof XSDAttributeUse)
    {
      XSDAttributeUse attributeUse = (XSDAttributeUse)obj;
      Element element = attributeUse.getElement();
      if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
      {
        items = helper.getGlobalAttributes();
//      WorkbenchHelp.setHelp(client, XSDEditorContextIds.XSDE_ATTRIBUTE_REF_DESIGN_VIEW);
//      WorkbenchHelp.setHelp(refCombo, XSDEditorContextIds.XSDE_ATTRIBUTE_REF_NAME);      
      }
    }      
    items.add(0, "");
    int size = items.size();
    String [] st = new String[size];
    System.arraycopy(items.toArray(), 0, st, 0, size);
    componentNameCombo.setItems(st);
      
    st = null;
  }
	
	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (doRefresh)
    {
  	  setListenerEnabled(false);  
  //	  componentNameCombo.removeListener(SWT.Modify, this);
  	  Object input = getInput();
      if (isReadOnly)
      {
        composite.setEnabled(false);
      }
      else
      {
        composite.setEnabled(true);
      }

  	  if (input instanceof XSDNamedComponent)
  	  {
        XSDNamedComponent namedComponent = (XSDNamedComponent)getInput();
        Element element = namedComponent.getElement();
        if (element != null)
        {
          String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
          if (attrValue == null)
          {
            attrValue = "";
          }
          componentNameCombo.setText(attrValue);
        }
  	  }
      else if (input instanceof XSDParticleContent)
      {
        XSDParticleContent particle = (XSDParticleContent)input;
        Element element = particle.getElement();
        String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
        if (attrValue == null)
        {
          attrValue = "";
        }
        componentNameCombo.setText(attrValue);
      }
  	  else if (input instanceof XSDAttributeUse)
  	  {
  	    XSDAttributeUse attributeUse = (XSDAttributeUse)getInput();
  	    Element element = attributeUse.getElement();
        String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
        if (attrValue == null)
        {
          attrValue = "";
        }
        componentNameCombo.setText(attrValue);
  	  }
  	  
      setListenerEnabled(true);
  //    componentNameCombo.addListener(SWT.Modify, this);
    }
	}

	
  public void widgetSelected(SelectionEvent e)
  {
	  Object input = getInput();
    if (e.widget == componentNameCombo)
    {
      String newValue = componentNameCombo.getText();
		  if (input instanceof XSDNamedComponent)
		  {
	      XSDNamedComponent namedComponent = (XSDNamedComponent)getInput();
	      Element element = namedComponent.getElement();
	      
	      if (namedComponent instanceof XSDElementDeclaration)
	      {
	        beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_REF_CHANGE"), element); //$NON-NLS-1$
	        element.setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
	        endRecording(element);
	      }
			  else if (namedComponent instanceof XSDAttributeDeclaration)
			  {
	        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTEGROUP_REF_CHANGE"), element); //$NON-NLS-1$
	        element.setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
	        endRecording(element);

			  }
	  	  else if (namedComponent instanceof XSDAttributeGroupDefinition)
	  	  {
	        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTEGROUP_REF_CHANGE"), element); //$NON-NLS-1$
	        // element.setAttribute(XSDConstants.REF_ATTRIBUTE, (String) value);
	        XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition)namedComponent;
	        Iterator iter = xsdSchema.getAttributeGroupDefinitions().iterator();
	        while (iter.hasNext())
	        {
	          XSDAttributeGroupDefinition def = (XSDAttributeGroupDefinition)iter.next();
	          if (def.getQName(xsdSchema).equals(componentNameCombo.getText()))
	          {
	            attrGroup.setResolvedAttributeGroupDefinition(def);
	            attrGroup.setName(componentNameCombo.getText());
	            break;
	          }
	        }  
	        endRecording(element);
	  	  }
        else if (namedComponent instanceof XSDModelGroupDefinition)
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_REF_CHANGE"), element); //$NON-NLS-1$
          element.setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
          endRecording(element);
        }
		  }
		  else if (input instanceof XSDAttributeUse)
		  {
		    XSDAttributeUse attributeUse = (XSDAttributeUse)getInput();
		    Element element = attributeUse.getElement();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_USE_CHANGE"), element); //$NON-NLS-1$
        Iterator iter = xsdSchema.getAttributeDeclarations().iterator();
        while (iter.hasNext())
        {
          XSDAttributeDeclaration attr = (XSDAttributeDeclaration)iter.next();
          if (attr.getQName(xsdSchema).equals(newValue))
          {
            attributeUse.setAttributeDeclaration(attr);
            element.setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
            break;
          }
        }  

        endRecording(element);
		  }
    }
  }
  
  public void setEditorPart(IEditorPart editorPart)
  {
    this.editorPart = editorPart;
  }

}
