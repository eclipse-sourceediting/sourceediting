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
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;


public class DocumentationPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  public static String CONTENT = "Content";
  
  /**
   * 
   */
  public DocumentationPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public DocumentationPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);

  }
  /**
   * @param xsdSchema
   */
  public DocumentationPropertySource(XSDSchema xsdSchema)
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
    PropertyDescriptor languageDescriptor =
    new TextPropertyDescriptor(
        "xml:lang",
        "xml:lang"
        );
    list.add(languageDescriptor);
    PropertyDescriptor sourceDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.SOURCE_ATTRIBUTE,
        XSDConstants.SOURCE_ATTRIBUTE);
    list.add(sourceDescriptor);
    AnyContentPropertyDescriptor contentDescriptor =
    new AnyContentPropertyDescriptor(
        CONTENT,
        CONTENT,
        element);
    list.add(contentDescriptor);

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
      if (((String) id).equals(CONTENT))
       {
        try
        {
          if (element.hasChildNodes())
           {
            // if the element is Text
            Node node = element.getFirstChild();
            if (node instanceof CharacterData)
             {
              return ((CharacterData)node).getData();
            }
          }
          else
           {
            return "";
          }
        }
        catch (Exception e)
        {
          
        }

      }
      else
       {
        result = element.getAttribute((String) id);
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
    if (value != null)
    {
      if (value instanceof String)
      {
        String newValue = (String)value;
        if (((String)id).equals("xml:lang"))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_DOCUMENTATION_LANG_CHANGE"), element);
          if (newValue.length() > 0)
          {
            if (validateLanguage(newValue))
            {
              element.setAttribute("xml:lang", newValue);
            }
          }
          else
          {
            // clearErrorMessage();
            element.removeAttribute("xml:lang");
          }
          endRecording(element);
        }
        else if (((String)id).equals(XSDConstants.SOURCE_ATTRIBUTE))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_DOCUMENTATION_SOURCE_CHANGE"), element);
          if (newValue.length() > 0)
          {
            element.setAttribute(XSDConstants.SOURCE_ATTRIBUTE, newValue);
          }
          else
          {
            element.removeAttribute(XSDConstants.SOURCE_ATTRIBUTE);
          }
          endRecording(element);
        }
        else if (((String)id).equals(CONTENT))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_DOCUMENTATION_COMMENT_CHANGE"), element);
          try
          {
            if (element.hasChildNodes())
            {
              // if the element is Text
              Node node = element.getFirstChild();
              if (node instanceof CharacterData)
              {
                ((CharacterData)node).setData(newValue);
              }
            }
            else
            {
              if (newValue.length() > 0)
              {
                Node childNode = element.getOwnerDocument().createTextNode(newValue);
                element.appendChild(childNode);
              }
            }
            endRecording(element);
          }
          catch (Exception e)
          {
            
          }
        }
        else  // shouldn't be here
        {
          element.setAttribute((String) id, newValue);
        }
      }
      else if (value instanceof Integer)
      {
      }
    }
    else
    {
      element.removeAttribute((String) id);
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
