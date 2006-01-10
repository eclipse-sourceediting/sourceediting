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
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Node;

public class SimpleTypePropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private boolean isAnonymous = false;
  /**
   * 
   */
  public SimpleTypePropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SimpleTypePropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public SimpleTypePropertySource(XSDSchema xsdSchema)
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
    Node parent = element.getParentNode();
    if (XSDDOMHelper.inputEquals(parent, XSDConstants.RESTRICTION_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(parent, XSDConstants.ELEMENT_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(parent, XSDConstants.UNION_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(parent, XSDConstants.LIST_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(parent, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {
      isAnonymous = true;
    }

    List list = new ArrayList();
    if (!isAnonymous)
    {
      // Create a descriptor and set a category
      PropertyDescriptor nameDescriptor =
      new TextPropertyDescriptor(
          XSDConstants.NAME_ATTRIBUTE,
          XSDConstants.NAME_ATTRIBUTE);
      list.add(nameDescriptor);
    }
    else
    {
      PropertyDescriptor readOnly = new PropertyDescriptor(XSDConstants.NAME_ATTRIBUTE, XSDConstants.NAME_ATTRIBUTE);
      list.add(readOnly);
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
      if (isAnonymous)
      {
        result = "**anonymous**";
      }
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
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      String name = (String)value;
      if (validateName(name))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SIMPLETYPE_NAME_CHANGE"), element);
        if (name != null && name.length() > 0)
        {
          // now rename any references to this type
          if (xsdSchema != null)
          {
            XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
            if (comp != null && comp instanceof XSDSimpleTypeDefinition && comp.getContainer().equals(xsdSchema))
            {
//                ((XSDNamedComponent)comp).setName(name);
              // TODO CS : revisit
              //GlobalSimpleOrComplexTypeRenamer renamer = new GlobalSimpleOrComplexTypeRenamer((XSDNamedComponent)comp, name);
              //renamer.visitSchema(xsdSchema);
            }
          }
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, name);
        }
        else
        {
          element.setAttribute(XSDConstants.NAME_ATTRIBUTE, "");
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
}
