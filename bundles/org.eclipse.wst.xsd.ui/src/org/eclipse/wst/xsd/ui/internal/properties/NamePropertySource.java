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
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

public class NamePropertySource
  extends BasePropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public NamePropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public NamePropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public NamePropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);

// From attribute group    
//    WorkbenchHelp.setHelp(client, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_DESIGN_VIEW);
//    WorkbenchHelp.setHelp(nameField, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_NAME);

// From unique
//    WorkbenchHelp.setHelp(nameField, XSDEditorContextIds.XSDE_UNIQUE_BASE_NAME);

// From key
//    WorkbenchHelp.setHelp(nameField, XSDEditorContextIds.XSDE_UNIQUE_BASE_NAME);
    
// From group
//     WorkbenchHelp.setHelp(controlsContainer, XSDEditorContextIds.XSDE_GROUP_DESIGN_VIEW);
//     WorkbenchHelp.setHelp(nameField, XSDEditorContextIds.XSDE_GROUP_NAME);
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
    PropertyDescriptor nameDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.NAME_ATTRIBUTE,
        XSDConstants.NAME_ATTRIBUTE);
    list.add(nameDescriptor);

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
    }
    if (result == null)
    {
      result = "";
    }
    return result;
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
    if (value != null)
    {
      if (value instanceof String)
      {
        String newValue = (String)value;
        if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false))
        {  
          beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTEGROUP_NAME_CHANGE"), element);

          // now rename any references to this element
          if (xsdSchema != null)
          {
            XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
            
            // TODO cs : revisit
            //if (comp != null && comp instanceof XSDAttributeGroupDefinition && comp.getContainer().equals(xsdSchema))
            //{
            //  GlobalAttributeGroupRenamer renamer = new GlobalAttributeGroupRenamer((XSDNamedComponent)comp, (String)value);
            //  renamer.visitSchema(xsdSchema);
            //}
          }
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, (String)value);
          endRecording(element);
        }
        else if (XSDDOMHelper.inputEquals(element, XSDConstants.UNIQUE_ELEMENT_TAG, false))
        {
          if (validateName(newValue))
           {
            beginRecording(XSDEditorPlugin.getXSDString("_UI_UNIQUE_NAME_CHANGE"), element);
            if (newValue.length() > 0)
            {
              element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
            }
            else
            {
              element.setAttribute(XSDConstants.NAME_ATTRIBUTE, "");
            }
            endRecording(element);
          }
        }
        else if (XSDDOMHelper.inputEquals(element, XSDConstants.KEY_ELEMENT_TAG, false))
        {
          if (validateName(newValue))
          {
            beginRecording(XSDEditorPlugin.getXSDString("_UI_KEY_NAME_CHANGE"), element);
            if (newValue.length() > 0)
            {
              element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
            }
            else
            {
              element.setAttribute(XSDConstants.NAME_ATTRIBUTE, "");
            }
            endRecording(element);
          }
        }
        else if (XSDDOMHelper.inputEquals(element, XSDConstants.GROUP_ELEMENT_TAG, false))
        {
          if (validateName(newValue))
           {
            beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_NAME_CHANGE"), element);
            // now rename any references to this element
            if (xsdSchema != null)
             {
              // TODO cs : revisit
              //XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
              //if (comp != null && comp instanceof XSDModelGroupDefinition && comp.getContainer().equals(xsdSchema))
              //{
              //  GlobalGroupRenamer renamer = new GlobalGroupRenamer((XSDNamedComponent)comp, newValue);
              //  renamer.visitSchema(xsdSchema);
              //}
            }
            element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
            endRecording(element);
          }
          
        }
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
