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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

public class SimpleTypeListPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public SimpleTypeListPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SimpleTypeListPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public SimpleTypeListPropertySource(XSDSchema xsdSchema)
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
    TypesPropertyDescriptor typeDescriptor = new TypesPropertyDescriptor(
          XSDConstants.ITEMTYPE_ATTRIBUTE,
          XSDConstants.ITEMTYPE_ATTRIBUTE,
          element, xsdSchema);
        
    typeDescriptor.setLabelProvider(new LabelProvider()
    {
      public String getText(Object element)
      {
        return (String) element;
      }
    });
    list.add(typeDescriptor);
    
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
      if (((String) id).equals(XSDConstants.ITEMTYPE_ATTRIBUTE))
      {
        result = element.getAttribute((String) id);
        if (result == null)
        {
          result = "**anonymous**"; //$NON-NLS-1$
        }
        return result;
      }
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
    if (value != null)
     {
      if (value instanceof String)
      {
        if (((String) id).equals(XSDConstants.ITEMTYPE_ATTRIBUTE))
        { 
          beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_ITEM_TYPE_CHANGE"), element); //$NON-NLS-1$
          element.setAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, (String)value);
          endRecording(element);
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
