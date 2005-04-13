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
package org.eclipse.wst.xsd.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalAttributeRenamer;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AttributePropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String[] useComboValues =
  {
        "", //$NON-NLS-1$
        "prohibited",   // XSDEditorPlugin.getXSDString("_UI_COMBO_BOX_PROHIBITED"), //$NON-NLS-1$ 
        "optional",   // XSDEditorPlugin.getXSDString("_UI_COMBO_BOX_OPTIONAL"), //$NON-NLS-1$
        "required"  // XSDEditorPlugin.getXSDString("_UI_COMBO_BOX_REQUIRED") //$NON-NLS-1$
  };

  protected String formComboValues[] =
  {
        "", //$NON-NLS-1$
        XSDEditorPlugin.getXSDString("_UI_COMBO_UNQUALIFIED"), //$NON-NLS-1$
        XSDEditorPlugin.getXSDString("_UI_COMBO_QUALIFIED") //$NON-NLS-1$
  };
  
  
  /**
   * 
   */
  public AttributePropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public AttributePropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public AttributePropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  public Object getEditableValue()
  {
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    List list = new ArrayList();
    // Create a descriptor and set a category
    PropertyDescriptor nameDescriptor = new TextPropertyDescriptor(XSDConstants.NAME_ATTRIBUTE, XSDConstants.NAME_ATTRIBUTE);
    list.add(nameDescriptor);
//    nameDescriptor.setCategory(XSDEditorPlugin.getXSDString("_UI_LABEL_GENERAL"));
    TypesPropertyDescriptor typeDescriptor = new TypesPropertyDescriptor(
      XSDConstants.TYPE_ATTRIBUTE,
      XSDConstants.TYPE_ATTRIBUTE,
      element, xsdSchema);
    list.add(typeDescriptor);
//    typeDescriptor.setCategory(XSDEditorPlugin.getXSDString("_UI_LABEL_GENERAL"));

    Attr fixedAttr = element.getAttributeNode(XSDConstants.FIXED_ATTRIBUTE);
    Attr defaultAttr = element.getAttributeNode(XSDConstants.DEFAULT_ATTRIBUTE);
    String str;
    if (fixedAttr != null)
     {
      str = XSDConstants.FIXED_ATTRIBUTE;
    }
    else if (defaultAttr != null)
     {
      str = XSDConstants.DEFAULT_ATTRIBUTE;
    }
    else
     {
      str = XSDConstants.FIXED_ATTRIBUTE + "/" + XSDConstants.DEFAULT_ATTRIBUTE; //$NON-NLS-1$
    }
    
    FixedOrDefaultTextPropertyDescriptor fixedOrDefaultDescriptor =
    new FixedOrDefaultTextPropertyDescriptor(
        str, 
        str,
        element);
    list.add(fixedOrDefaultDescriptor);
//    fixedOrDefaultDescriptor.setCategory(XSDEditorPlugin.getXSDString("_UI_LABEL_OTHER"));
    
    Object parentNode = element.getParentNode();
    if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.SCHEMA_ELEMENT_TAG, false))
    {
    }
    else
    {
      XSDComboBoxPropertyDescriptor useDescriptor =
      new XSDComboBoxPropertyDescriptor(
          XSDConstants.USE_ATTRIBUTE,
          XSDConstants.USE_ATTRIBUTE,
          useComboValues);
      list.add(useDescriptor);
//      useDescriptor.setCategory(XSDEditorPlugin.getXSDString("_UI_LABEL_OTHER"));
      XSDComboBoxPropertyDescriptor formDescriptor =
      new XSDComboBoxPropertyDescriptor(
          XSDConstants.FORM_ATTRIBUTE,
          XSDConstants.FORM_ATTRIBUTE,
          formComboValues);
      list.add(formDescriptor);
//      formDescriptor.setCategory(XSDEditorPlugin.getXSDString("_UI_LABEL_OTHER"));
    }
    
    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
    {
      result = element.getAttribute((String) id);
      if (result == null)
      {
        result = ""; //$NON-NLS-1$
      }
      if (((String) id).equals(XSDConstants.TYPE_ATTRIBUTE))
      {
        if (result.equals("")) //$NON-NLS-1$
        {
          if (checkForAnonymousType(element))
          {
            return "**anonymous**"; //$NON-NLS-1$
          }
          else
          {
            return XSDEditorPlugin.getXSDString("_UI_NO_TYPE"); //$NON-NLS-1$ 
          }
        }
        else
         {
          return result;
        }
      }
      return result;
    }
    return ""; //$NON-NLS-1$
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  public boolean isPropertySet(Object id)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  public void resetPropertyValue(Object id)
  {
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  public void setPropertyValue(Object id, Object value)
  {
    if (value == null)
    {
      value = ""; //$NON-NLS-1$
    }
    if (value instanceof String)
    {
      if (((String) id).equals(XSDConstants.TYPE_ATTRIBUTE))
      {
//        beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element);
//        element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, (String)value);
//        updateElementToNotAnonymous(element);
//        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.NAME_ATTRIBUTE))
      { 
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_NAME_CHANGE"), element); //$NON-NLS-1$
        // now rename any references to this element
        if (xsdSchema != null)
         {
          XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
          if (comp != null && comp instanceof XSDAttributeDeclaration && comp.getRootContainer().equals(xsdSchema))
          {
            XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration)comp;
            xsdAttributeDeclaration.setName((String)value);
            GlobalAttributeRenamer renamer = new GlobalAttributeRenamer(xsdAttributeDeclaration, (String)value);
            renamer.visitSchema(xsdSchema);
          }
        }
        element.setAttribute(XSDConstants.NAME_ATTRIBUTE, (String)value);
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.FIXED_ATTRIBUTE) || ((String) id).equals(XSDConstants.DEFAULT_ATTRIBUTE))
      {            
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_VALUE_CHANGE"), element); //$NON-NLS-1$
        if (((String)value).equals("")) //$NON-NLS-1$
         {
          element.removeAttribute((String)id);
        }
        else
        {  
          element.setAttribute((String) id, (String) value);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.USE_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_USE_CHANGE"), element); //$NON-NLS-1$
        if (((String)value).equals("")) //$NON-NLS-1$
        {
          element.removeAttribute(XSDConstants.USE_ATTRIBUTE);
        }
        else
        {  
          element.setAttribute((String) id, (String)value);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.FORM_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTE_FORM_CHANGE"), element); //$NON-NLS-1$
        if (((String)value).equals("")) //$NON-NLS-1$
        {
          element.removeAttribute(XSDConstants.FORM_ATTRIBUTE);
        }
        else
        {
          element.setAttribute(XSDConstants.FORM_ATTRIBUTE, (String)value);
        }
        endRecording(element);
      }
    }
    Runnable delayedUpdate = new Runnable()
    {
      public void run()
      {
        if (viewer != null)
          viewer.refresh();
      }
    };
    Display.getCurrent().asyncExec(delayedUpdate);
    
  }
  
  boolean checkForAnonymousType(Element element)
  {
    NodeList list = element.getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (list.getLength() > 0)
     {
      return true;
    }
    return false;
  }  
}
