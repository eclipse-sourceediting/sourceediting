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
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class SimpleContentPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String DERIVED_BY_ID = "derived by";  // XSDEditorPlugin.getXSDString("_UI_LABEL_DERIVED_BY"); //$NON-NLS-1$ 
  private String BASE_TYPE_ID = "base";  // XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE"); //$NON-NLS-1$
  
  private String derivedByChoicesComboValues[] =
  {
     "",  //$NON-NLS-1$
     XSDConstants.RESTRICTION_ELEMENT_TAG,
     XSDConstants.EXTENSION_ELEMENT_TAG
  };
  
  /**
   * 
   */
  public SimpleContentPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SimpleContentPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public SimpleContentPropertySource(XSDSchema xsdSchema)
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
    
    SimpleContentPropertyDescriptor typeDescriptor = new SimpleContentPropertyDescriptor(
        BASE_TYPE_ID,
        BASE_TYPE_ID,
        element, xsdSchema);
    list.add(typeDescriptor);
    XSDComboBoxPropertyDescriptor derivedByDescriptor =
    new XSDComboBoxPropertyDescriptor(
        DERIVED_BY_ID,
        DERIVED_BY_ID,
        derivedByChoicesComboValues);
    list.add(derivedByDescriptor);
    
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
      if (((String) id).equals(DERIVED_BY_ID))
      {
        String derivedBy = getDomHelper().getDerivedByName(element);
        if (derivedBy == null)
         {
          derivedBy = ""; //$NON-NLS-1$
        }
        return derivedBy;
      }
      else if (((String) id).equals(BASE_TYPE_ID))
      {
        String baseType = getDomHelper().getBaseType(element);
        if (baseType == null)
        {
          baseType = ""; //$NON-NLS-1$
        }
        return baseType;
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
        String origBaseType = getDomHelper().getBaseType(element);
        String derivedBy = getDomHelper().getDerivedByName(element);
        
        if (((String) id).equals(BASE_TYPE_ID))
        {            
          Document doc = element.getOwnerDocument();
          Element childElement = null;
          beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element); //$NON-NLS-1$        
          getDomHelper().setDerivedByBaseType(element, derivedBy, (String)value);
          endRecording(element);
        }
        else if (((String) id).equals(DERIVED_BY_ID))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_DERIVEDBY_CHANGE"), element); //$NON-NLS-1$
          String newDerivedBy = (String)value;
          if (newDerivedBy.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
          {
            String prefix = element.getPrefix();
            String anyType = prefix == null? "anyType" : prefix + ":anyType";  //$NON-NLS-1$ $NON-NLS-2$
            getDomHelper().changeDerivedByType(element, (String)value, anyType);
          }
          else
          {
            Element derivedByElem = getDomHelper().getDerivedByElement(element);
            if (checkForAnonymousType(derivedByElem))
            {            
// KCPort
//            ArrayList message = new ArrayList();
//            ErrorMessage aTask = new ErrorMessage();
//            Node aNode = getDomHelper().getChildNode(derivedByElem, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
////////////// shall we remove the node and its children??
////            getDomHelper().removeNodeAndWhitespace(aNode);
//////////////
////            if (aNode instanceof Element)
////            {
////              Element st = (Element)aNode;
////             if (st instanceof NodeImpl)
////              {
////                aTask.setNode((NodeImpl)st);
////              }
////            }
//            if (derivedByElem instanceof NodeImpl)
//            {
//              aTask.setModelObject(derivedByElem);
//            }
//            aTask.setLocalizedMessage(XSDEditorPlugin.getXSDString("_ERROR_REMOVE_LOCAL_SIMPLETYPE"));
//            message.add(aTask);
//            if (getEditor() != null)
//            {
//              getEditor().createTasksInTaskList(message);
//            }
            }   
            getDomHelper().changeDerivedByType(element, (String)value, origBaseType);
          }

          
          endRecording(element);
//        setInput(element);
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

  boolean checkForAnonymousType(Element element)
  {
    boolean isAnonymous = false;

    Node aNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      return true;
    }
    return isAnonymous;
  }
  
}
