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
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class KeyrefPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String[] refComboValues = { "" };
  /**
   * 
   */
  public KeyrefPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public KeyrefPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public KeyrefPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  
  public void setInput(Element element)
  {
    this.element = element;
    java.util.List items = new ArrayList();

    
    if (xsdSchema != null)
    {
      Iterator iter = xsdSchema.getIdentityConstraintDefinitions().iterator();
      String name = element.getAttribute(XSDConstants.NAME_ATTRIBUTE);
      while (iter.hasNext())
      {
        XSDIdentityConstraintDefinition constraint = (XSDIdentityConstraintDefinition)iter.next();
        if (name != null && !name.equals(""))
         {
          if (constraint.getName() != null)
           {
            if (!name.equals(constraint.getQName(xsdSchema)))
            {
              items.add(constraint.getQName(xsdSchema));
            }
          }
        }
        else
         {
          if (constraint.getName() != null)
          {
            items.add(constraint.getQName(xsdSchema));
          }
        }
      }
    }   
    
    int size = items.size() + 1;
    refComboValues = new String[size];
    refComboValues[0] = "";
    if (items != null)
     {
      for (int i = 0; i < items.size(); i++)
       {
        refComboValues[i + 1] = (String) items.get(i);
      }
    }
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
// This property is moved to the General Tab
//    PropertyDescriptor nameDescriptor =
//    new TextPropertyDescriptor(
//        XSDConstants.NAME_ATTRIBUTE,
//        XSDConstants.NAME_ATTRIBUTE);
//    list.add(nameDescriptor);
    
    XSDComboBoxPropertyDescriptor refDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.REFER_ATTRIBUTE,
        XSDConstants.REFER_ATTRIBUTE,
        refComboValues);
    list.add(refDescriptor);
    

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
        result = "";
      }
      return result;
//      if (((String) id).equals(XSDConstants.REFER_ATTRIBUTE))
//      {
//        return result;
//      }
    }
    return "";
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
      value = "";
    }
    if (value instanceof String)
    {
      String newValue = (String)value;
      if (((String) id).equals(XSDConstants.NAME_ATTRIBUTE))
      {  
        if (validateName(newValue))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_KEYREF_NAME_CHANGE"), element);
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
          endRecording(element);
        }         
      }
      else if (((String) id).equals(XSDConstants.REFER_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_KEYREF_REFER_CHANGE"), element);
        element.setAttribute((String) id, newValue);
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
}
