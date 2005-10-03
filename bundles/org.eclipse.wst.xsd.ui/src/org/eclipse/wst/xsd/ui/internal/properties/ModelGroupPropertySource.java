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
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ModelGroupPropertySource  // all or sequence or choice
  extends BasePropertySource
  implements IPropertySource
{
  private String[] modelGroupComboValues = { "sequence", "choice", "all" };
  /**
   * 
   */
  public ModelGroupPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public ModelGroupPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public ModelGroupPropertySource(XSDSchema xsdSchema)
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

    XSDComboBoxPropertyDescriptor modelGroupDescriptor =
    new XSDComboBoxPropertyDescriptor(
        "model group",
        "model group",
        modelGroupComboValues);
    list.add(modelGroupDescriptor);
    
    PropertyDescriptor minOccursDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.MINOCCURS_ATTRIBUTE,
        XSDConstants.MINOCCURS_ATTRIBUTE);
    list.add(minOccursDescriptor);
    PropertyDescriptor maxOccursDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.MAXOCCURS_ATTRIBUTE,
        XSDConstants.MAXOCCURS_ATTRIBUTE);
    list.add(maxOccursDescriptor);
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
      String attributeName = (String)id;
      if (result == null)
       {
        result = "";
      }
      if (attributeName.equals("model group"))
      {
        result = element.getLocalName();
        return result;
      }
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
    String property = (String)id;
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      String newValue = (String)value;
      if (property.equals("model group"))
       {
        Element parent = (Element)element.getParentNode();
        String prefix = element.getPrefix();
        prefix = prefix == null ? "" : prefix + ":";
        beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_SCOPE_CHANGE"), parent);
        changeContentModel(parent, newValue);
        endRecording(parent);
        XSDDOMHelper domHelper = new XSDDOMHelper();
        setInput(domHelper.getContentModelFromParent(parent));
      }
      else if (property.equals(XSDConstants.MAXOCCURS_ATTRIBUTE))
      {
        String max = (String)value;
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MAXOCCURS_CHANGE"), element);
        if (max.length() > 0)
        {
          element.setAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE, max);
        }
        else
         {
          element.removeAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.MINOCCURS_ATTRIBUTE))
       {
        String min = (String)value;
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MINOCCURS_CHANGE"), element);
        if (min.length() > 0)
        {
          element.setAttribute(XSDConstants.MINOCCURS_ATTRIBUTE, min);
        }
        else
        {
          element.removeAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
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

  public void setInput(Element element)
  {
    super.setInput(element);
    
    if (element != null)
    {
      boolean parentIsSequence = false;
      boolean parentIsChoice = false;
     
      Object parent = element.getParentNode();
      
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SEQUENCE_ELEMENT_TAG, false))
       {
        parentIsSequence = true;
      }
      else if (XSDDOMHelper.inputEquals(parent, XSDConstants.CHOICE_ELEMENT_TAG, false))
       {
        parentIsChoice = true;
      }
      
      if (parentIsChoice || parentIsSequence)
      {
        modelGroupComboValues = new String[2];
        modelGroupComboValues[0] = XSDConstants.SEQUENCE_ELEMENT_TAG;
        modelGroupComboValues[1] = XSDConstants.CHOICE_ELEMENT_TAG;
      }
      else
      {
        modelGroupComboValues = new String[3];
        modelGroupComboValues[0] = XSDConstants.SEQUENCE_ELEMENT_TAG;
        modelGroupComboValues[1] = XSDConstants.CHOICE_ELEMENT_TAG;
        modelGroupComboValues[2] = XSDConstants.ALL_ELEMENT_TAG;
      }
    }      
  }
  
  private void changeContentModel(Element parent, String contentModel)
  {
    Document doc = parent.getOwnerDocument();
    XSDDOMHelper domHelper = new XSDDOMHelper();
  
    String prefix = parent.getPrefix();
    prefix = prefix == null ? "" : prefix + ":";
    
    Element contentModelElement = domHelper.getContentModelFromParent(parent);
  
    if (contentModelElement.getLocalName().equals(contentModel))
    {
      return; // it's already the content model 
    }
  
    Element newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + contentModel);
  
    if (contentModelElement.hasChildNodes())
    {        
      NodeList nodes = contentModelElement.getChildNodes();
      // use clones so we don't have a refresh problem
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Node node = nodes.item(i);
        newNode.appendChild(node.cloneNode(true)); 
      }
    }
    parent.replaceChild(newNode, contentModelElement);
  }

}
