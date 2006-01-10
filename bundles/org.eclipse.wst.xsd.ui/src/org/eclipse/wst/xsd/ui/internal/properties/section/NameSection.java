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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class NameSection extends AbstractSection
{
  /**
   * 
   */
  public NameSection()
  {
    super();
  }
  
	Text nameText;

	
  public void doHandleEvent(Event event) 
  {
    if (event.widget == nameText)
	  {
			Object input = getInput();
      String newValue = nameText.getText();
	    if (input instanceof XSDNamedComponent)
	    {
	      XSDNamedComponent namedComponent = (XSDNamedComponent)input;
	      if (newValue.length() > 0)
	      {
	        namedComponent.setName(newValue);
          doReferentialIntegrityCheck(namedComponent, newValue);
	      }
	      else
	      {
          // TODO: Show error message
	      }
	    }
//	    else if (input instanceof XSDParticle)
//	    {
//	      XSDParticle xsdParticle = (XSDParticle)input;
//	      if (newValue.length() > 0)
//	      {
//	        doReferentialIntegrityCheck(xsdParticle, newValue);
//	      }
//	      else
//	      {
//	        // TODO: Show error message
//	      }
//	    }
      else if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration attribute = (XSDAttributeDeclaration)input;
	      if (newValue.length() > 0)
	      {
	        doReferentialIntegrityCheck(attribute, newValue);
	        attribute.setName(newValue);
	      }
	      else
	      {
	        // TODO: Show error message
	      }
      }
	    else if (input instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)input;
        XSDAttributeDeclaration attribute = attributeUse.getAttributeDeclaration();
	      if (newValue.length() > 0)
	      {
	        doReferentialIntegrityCheck(attribute, newValue);
	        attribute.setName(newValue);
	        attributeUse.setAttributeDeclaration(attribute);
	      }
	      else
	      {
          // TODO: Show error message
	      }
      }
	  }
	}

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite =	getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		// data.right = new FormAttachment(95, 0);
    data.right = new FormAttachment(100, -rightMarginSpace -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, 0);
		nameText.setLayoutData(data);
		nameText.addListener(SWT.Modify, this);

		CLabel nameLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_NAME")); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(nameText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(nameText, 0, SWT.CENTER);
		nameLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (nameText.isFocusControl())
    {
      return;
    }
    setListenerEnabled(false);
    nameText.setEditable(true);
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
	  Object input = getInput();
	  nameText.setText(""); //$NON-NLS-1$
	  if (input != null)
	  {
	    if (input instanceof XSDComplexTypeDefinition || input instanceof XSDSimpleTypeDefinition)
	    {
	      XSDTypeDefinition type = (XSDTypeDefinition)input;
	      
	      Element element = type.getElement();
        String name = element.getAttribute(XSDConstants.NAME_ATTRIBUTE);
        if (name == null) name = "";
        
        boolean isAnonymousType = checkForAnonymousType(element);
        if (isAnonymousType)
        {
          nameText.setText("**anonymous**"); //$NON-NLS-1$
          nameText.setEditable(false);
        }
        else
        {
          nameText.setText(name);
          nameText.setEditable(true);
        }
	    }
	    else if (input instanceof XSDNamedComponent)
	    {
	      XSDNamedComponent namedComponent = (XSDNamedComponent)input;
          String name = namedComponent.getName();
	      if (name != null)
	      {
	        nameText.setText(name);
	      }
	    }
      else if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration attribute = (XSDAttributeDeclaration)input;
        //String name = attribute.getName();
        Element element = attribute.getElement();
        String name = element.getAttribute(XSDConstants.NAME_ATTRIBUTE);
        if (name != null)
	      {
	        nameText.setText(name);
	      }
      }
	    else if (input instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)input;
        XSDAttributeDeclaration attribute = attributeUse.getAttributeDeclaration();
        String name = attribute.getName();
        if (name != null)
	      {
	        nameText.setText(name);
	      }
      }
//	    else if (input instanceof Element)
//	    {
//	      String name = ((Element)input).getAttribute(XSDConstants.NAME_ATTRIBUTE);
//	      if (name == null) name = "";
//	      nameText.setText(name);
//	    }
	  }
	  setListenerEnabled(true);
	}

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

	private void doReferentialIntegrityCheck(XSDComponent xsdComponent, String newValue)
	{
    }
/*    
    Element element = xsdComponent.getElement();
    if (XSDDOMHelper.inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      if (validateName(newValue))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_NAME_CHANGE"), element); //$NON-NLS-1$
        element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
        if (xsdComponent instanceof XSDNamedComponent)
        {
          ((XSDNamedComponent)xsdComponent).setName(newValue);
        }
        
        // now rename any references to this element
       
        if (xsdSchema != null)
        {
          XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
          if (comp != null && comp instanceof XSDElementDeclaration && comp.getContainer().equals(xsdSchema))
          {
            GlobalElementRenamer renamer = new GlobalElementRenamer((XSDNamedComponent)comp, newValue);
            renamer.visitSchema(xsdSchema);
          }
        }        
        endRecording(element);
      }
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
    {
      if (validateName(newValue))
      {
	      beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_NAME_CHANGE"), element); //$NON-NLS-1$
	      if (newValue.length() > 0)
	      {
	        // now rename any references to this type
	        if (xsdSchema != null)
	        {
	          XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
	          if (comp != null && comp instanceof XSDComplexTypeDefinition && comp.getContainer().equals(xsdSchema))
	          {
	            GlobalSimpleOrComplexTypeRenamer renamer = new GlobalSimpleOrComplexTypeRenamer((XSDNamedComponent)comp, newValue);
	            renamer.visitSchema(xsdSchema);
	          }
	        }
	        element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
	        ((XSDNamedComponent)xsdComponent).setName(newValue);
	      }
	      else
	      {
	        element.removeAttribute(XSDConstants.NAME_ATTRIBUTE);
	      }
      endRecording(element);
      }
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
    {
      beginRecording(XSDEditorPlugin.getXSDString("_UI_SIMPLETYPE_NAME_CHANGE"), element);
      if (validateName(newValue))
      {
        // now rename any references to this type
        if (newValue.length() > 0)
        {
          if (xsdSchema != null)
          {
            XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
            if (comp != null && comp instanceof XSDSimpleTypeDefinition && comp.getContainer().equals(xsdSchema))
            {
              GlobalSimpleOrComplexTypeRenamer renamer = new GlobalSimpleOrComplexTypeRenamer((XSDNamedComponent)comp, newValue);
              renamer.visitSchema(xsdSchema);
            }
          }
//          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
        }
        else
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, "");
        }
      }
      endRecording(element);

    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {  
      beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_NAME_CHANGE"), element); //$NON-NLS-1$
      // now rename any references to this element
      if (xsdSchema != null)
       {
        XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
        if (comp != null && comp instanceof XSDAttributeDeclaration && comp.getContainer().equals(xsdSchema))
        {
          GlobalAttributeRenamer renamer = new GlobalAttributeRenamer((XSDNamedComponent)comp, newValue);
          renamer.visitSchema(xsdSchema);
        }
      }
      // element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
      endRecording(element);
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false))
    {  
      beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTEGROUP_NAME_CHANGE"), element); //$NON-NLS-1$
      ((XSDNamedComponent)xsdComponent).setName(newValue);
      // now rename any references to this element
      if (xsdSchema != null)
      {
        XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
        if (comp != null && comp instanceof XSDAttributeGroupDefinition && comp.getContainer().equals(xsdSchema))
        {
          GlobalAttributeGroupRenamer renamer = new GlobalAttributeGroupRenamer((XSDNamedComponent)comp, newValue);
          renamer.visitSchema(xsdSchema);
        }
      }
      endRecording(element);
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.UNIQUE_ELEMENT_TAG, false))
    {
      if (validateName(newValue))
       {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_UNIQUE_NAME_CHANGE"), element); //$NON-NLS-1$
        if (newValue.length() > 0)
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
        }
        else
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, ""); //$NON-NLS-1$
        }
        endRecording(element);
      }
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.KEY_ELEMENT_TAG, false))
    {
      if (validateName(newValue))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_KEY_NAME_CHANGE"), element); //$NON-NLS-1$
        if (newValue.length() > 0)
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
        }
        else
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, ""); //$NON-NLS-1$
        }
        endRecording(element);
      }
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.GROUP_ELEMENT_TAG, false))
    {
      if (validateName(newValue))
       {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_NAME_CHANGE"), element); //$NON-NLS-1$
        // now rename any references to this element
        if (xsdSchema != null)
         {
          XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
          if (comp != null && comp instanceof XSDModelGroupDefinition && comp.getContainer().equals(xsdSchema))
          {
            GlobalGroupRenamer renamer = new GlobalGroupRenamer((XSDNamedComponent)comp, newValue);
            renamer.visitSchema(xsdSchema);
          }
        }
        element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
        endRecording(element);
      }
      
    }
    


	}*/

  boolean checkForAnonymousType(Element element)
  {
    Object parentElement = (Object)element.getParentNode();
    boolean isAnonymous = false;
    if (parentElement != null)
    {
      if (XSDDOMHelper.inputEquals(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
      {
        if (XSDDOMHelper.inputEquals(parentElement, XSDConstants.ELEMENT_ELEMENT_TAG, false))
        {
          isAnonymous = true; 
        }
      }
      else if (XSDDOMHelper.inputEquals(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        if (XSDDOMHelper.inputEquals(parentElement, XSDConstants.RESTRICTION_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(parentElement, XSDConstants.ELEMENT_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(parentElement, XSDConstants.UNION_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(parentElement, XSDConstants.LIST_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(parentElement, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
        {
          isAnonymous = true;
        }
      }
    }
    return isAnonymous;
  }

}
