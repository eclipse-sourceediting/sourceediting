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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.TargetNamespaceChangeHandler;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SchemaPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String oldPrefix = "";
  private String oldNamespace = "";
  
  private String[] attributeFormDefaultComboValues =
  { 
    "",
    XSDEditorPlugin.getXSDString("_UI_COMBO_UNQUALIFIED"),
    XSDEditorPlugin.getXSDString("_UI_COMBO_QUALIFIED")
  };
  
  private String[] elementFormDefaultComboValues =
  {
    "",
    XSDEditorPlugin.getXSDString("_UI_COMBO_UNQUALIFIED"),
    XSDEditorPlugin.getXSDString("_UI_COMBO_QUALIFIED")
  };

  private String[] blockDefaultComboValues =
  {
    "",
    "#all",
    "extension",
    "restriction",
    "substitution"
  };

  private String[] finalDefaultComboValues =
  {
    "",
    "#all",
    "extension",
    "restriction"
  };
  
  /**
   * 
   */
  public SchemaPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SchemaPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public SchemaPropertySource(XSDSchema xsdSchema)
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
// These have been moved to the General tab    
//    PropertyDescriptor prefixDescriptor = 
//    new TextPropertyDescriptor(
//        "prefix",
//        "prefix");
//    list.add(prefixDescriptor);
//    prefixDescriptor.setCategory("Namespace");
//
//    PropertyDescriptor targetNamespaceDescriptor = 
//    new TextPropertyDescriptor(
//        XSDConstants.TARGETNAMESPACE_ATTRIBUTE,
//        XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
//    list.add(targetNamespaceDescriptor);
//    targetNamespaceDescriptor.setCategory("Namespace");
    
    PropertyDescriptor versionDescriptor = 
    new TextPropertyDescriptor(
        XSDConstants.VERSION_ATTRIBUTE,
        XSDConstants.VERSION_ATTRIBUTE);
//    versionDescriptor.setDescription("Version attribute"); // XSDEditorPlugin.getXSDString("_UI_TOOLTIP_VERSION"));
    list.add(versionDescriptor);
    PropertyDescriptor xmlLangDescriptor = 
    new TextPropertyDescriptor(
        "xml:lang",
        "xml:lang");
    list.add(xmlLangDescriptor);
    
    XSDComboBoxPropertyDescriptor attributeFormDefaultDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.ATTRIBUTEFORMDEFAULT_ATTRIBUTE,
        XSDConstants.ATTRIBUTEFORMDEFAULT_ATTRIBUTE,
        attributeFormDefaultComboValues);
    list.add(attributeFormDefaultDescriptor);
    
    
    XSDComboBoxPropertyDescriptor elementFormDefaultDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.ELEMENTFORMDEFAULT_ATTRIBUTE,
        XSDConstants.ELEMENTFORMDEFAULT_ATTRIBUTE,
        elementFormDefaultComboValues);
    list.add(elementFormDefaultDescriptor);
    
    XSDComboBoxPropertyDescriptor blockDefaultDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.BLOCKDEFAULT_ATTRIBUTE,
        XSDConstants.BLOCKDEFAULT_ATTRIBUTE,
        blockDefaultComboValues);
    list.add(blockDefaultDescriptor);
    XSDComboBoxPropertyDescriptor finalDefaultDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.FINALDEFAULT_ATTRIBUTE,
        XSDConstants.FINALDEFAULT_ATTRIBUTE,
        finalDefaultComboValues);
    list.add(finalDefaultDescriptor);

    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
    //    return propertyDescriptors;
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

      if (attributeName.equals("prefix"))
      {
        TypesHelper helper = new TypesHelper(xsdSchema);
        String aPrefix = helper.getPrefix(element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE), false);
        // System.out.println("schema targetNS is " + xsdSchema.getTargetNamespace());
        if (aPrefix != null && aPrefix.length() > 0)
        {
          return aPrefix;
        }
        return "";        
      }
      else
      {
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
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      String newValue = (String)value;
      String attributeName = (String)id;
      if (attributeName.equals("prefix"))
      {
        updatePrefix(newValue); 
      }
      else if (attributeName.equals(XSDConstants.TARGETNAMESPACE_ATTRIBUTE))
      {
        updateTargetNamespace(newValue);
      }
      else if (attributeName.equals("xml:lang"))
      {
        validateLanguage(newValue);
        // return;  // we will accept the value even though it does not conform
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_LANG_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.VERSION_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_VERSION_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.ATTRIBUTEFORMDEFAULT_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_ATTRIBUTEFORMDEFAULT_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.ELEMENTFORMDEFAULT_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_ELEMENTFORMDEFAULT_CHANGE"), element); 
      }
      else if (attributeName.equals(XSDConstants.BLOCKDEFAULT_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_BLOCKDEFAULT_CHANGE"), element); 
      }
      else if (attributeName.equals(XSDConstants.FINALDEFAULT_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_SCHEMA_FINALDEFAULT_CHANGE"), element);
      }

      if (!attributeName.equals("prefix") && !attributeName.equals(XSDConstants.TARGETNAMESPACE_ATTRIBUTE))
      {        
        if (newValue.equals(""))
        {
          element.removeAttribute(attributeName); 
        }
        else
        {
          element.setAttribute(attributeName, newValue);
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
    
    oldNamespace = "";
    oldPrefix = "";
    
    if (element!= null)
    {
      String targetNamespace = element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
      oldNamespace = targetNamespace;

      TypesHelper helper = new TypesHelper(xsdSchema);
      String aPrefix = helper.getPrefix(targetNamespace, false);
      if (aPrefix != null && aPrefix.length() > 0)
      {
        oldPrefix = aPrefix;
      }
    }

//  For debugging
//    System.out.println("old Prefix is " + oldPrefix);
//    System.out.println("old NS is " + oldNamespace);
    if (xsdSchema != null)
    {
      Map map = xsdSchema.getQNamePrefixToNamespaceMap();
//      System.out.println("SetInput: Prefixes: " + map.values());
//      System.out.println("SetInput: NS:" + map.keySet());
    }
    
  }
  
  
  private void updatePrefix(String newPrefix)
  {
    updateNamespaceInfo(newPrefix, oldNamespace);  
  }
  
  private void updateTargetNamespace(String newTargetNamespace)
  {
    updateNamespaceInfo(oldPrefix, newTargetNamespace);
  }
  
  private void updateNamespaceInfo(String newPrefix, String newTargetNamespace)
  {
//    System.out.println("\nold Prefix is " + oldPrefix);
//    System.out.println("old NS is " + oldNamespace);
//    System.out.println("new Prefix is " + newPrefix);
//    System.out.println("new NS is " + newTargetNamespace);
    
    DocumentImpl doc = (DocumentImpl)element.getOwnerDocument();

    String modelTargetNamespace = xsdSchema.getTargetNamespace();
//    System.out.println("Model TargetNS is " + modelTargetNamespace);
    if (modelTargetNamespace == null)
    {
      modelTargetNamespace = "";
    }
        
    String targetNamespace = newTargetNamespace.trim(); 
    String prefix = newPrefix.trim();

    if (!validatePrefix(prefix) || !validateTargetNamespace(targetNamespace))
    {
      return;
    }
        
    if (prefix.length() > 0 && targetNamespace.length() == 0)
    {
       // can't have blank targetnamespace and yet specify a prefix
       return;
    }

    doc.getModel().beginRecording(this, "Target Namespace Change");
    String xsdForXSDPrefix = xsdSchema.getSchemaForSchemaQNamePrefix();
    Map map = xsdSchema.getQNamePrefixToNamespaceMap();

// For debugging
//        System.out.println("1. SW Map is " + map.values());
//        System.out.println("1. SW Map keys are " + map.keySet());

    // Check if prefix is blank
    // if it is, then make sure we have a prefix 
    // for schema for schema
    if (prefix.length() == 0)
    {
      // if prefix for schema for schema is blank
      // then set it to value specified in preference
      // and update ALL nodes with this prefix
      if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
      {
        // get preference prefix
        xsdForXSDPrefix = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
        // get a unique prefix by checking what's in the map

        xsdForXSDPrefix = getUniqueSchemaForSchemaPrefix(xsdForXSDPrefix, map);
        element.setAttribute("xmlns:" + xsdForXSDPrefix, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);

        updateAllNodes(element, xsdForXSDPrefix);
            
        // remove the old xmlns attribute for the schema for schema
        if (element.getAttribute("xmlns") != null &&
            element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
        {
          element.removeAttribute("xmlns");
        }
      }
    }

    if (targetNamespace.length() > 0 ||
       (targetNamespace.length() == 0 && prefix.length() == 0))
    {
      // clean up the old prefix for this schema
      if (oldPrefix != null && oldPrefix.length() > 0)
      {
        element.removeAttribute("xmlns:"+oldPrefix);
//            element.setAttribute("xmlns:" + prefix, targetNamespace);
//            java.util.Map prefixToNameSpaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
//            prefixToNameSpaceMap.remove(oldPrefix);
      }
      else // if no prefix
      {
        if (element.getAttribute("xmlns") != null)
        {
          if (!element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
           {
            element.removeAttribute("xmlns");
          }
        }
      }
    }

    if (targetNamespace.length() > 0)
    {
      if (!modelTargetNamespace.equals(targetNamespace))
      {
        element.setAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE, targetNamespace);
      }
      // now set the new xmlns:prefix attribute
      if (prefix.length() > 0)
      {
        element.setAttribute("xmlns:" + prefix, targetNamespace);
      }
      else
      {
        element.setAttribute("xmlns", targetNamespace);
      }
      // set the targetNamespace attribute
    }
    else // else targetNamespace is blank
    {
      if (prefix.length() == 0)
      {
        element.removeAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
      }
    }

//    System.out.println("1.5 SW Map is " + map.values());
//    System.out.println("1.5 SW Map keys are " + map.keySet());
    
    // do our own referential integrity
    TargetNamespaceChangeHandler targetNamespaceChangeHandler = new TargetNamespaceChangeHandler(xsdSchema, oldNamespace, targetNamespace);
    targetNamespaceChangeHandler.resolve();

    oldPrefix = prefix;
    oldNamespace = targetNamespace;
    
    XSDSchemaHelper.updateElement(xsdSchema);
    
    doc.getModel().endRecording(this);

// For debugging
        map = xsdSchema.getQNamePrefixToNamespaceMap();
//        System.out.println("2. SW Map is " + map.values());
//        System.out.println("2. SW Map keys are " + map.keySet());
  }


  private String getUniqueSchemaForSchemaPrefix(String xsdForXSDPrefix, Map map)
  {
    if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
     {       
      xsdForXSDPrefix = "xsd";
    }
    // ensure prefix is unique
    int prefixExtension = 1;
    while (map.containsKey(xsdForXSDPrefix) && prefixExtension < 100)
     {
      xsdForXSDPrefix = xsdForXSDPrefix + String.valueOf(prefixExtension);
      prefixExtension++;
    }
    return xsdForXSDPrefix;
  }

  private void updateAllNodes(Element element, String prefix)
  {
    element.setPrefix(prefix);
    NodeList list = element.getChildNodes();
    if (list != null)
    {
      for (int i=0; i < list.getLength(); i++)
      {
        Node child = list.item(i);
        if (child != null && child instanceof Element)
        {
          child.setPrefix(prefix);
          if (child.hasChildNodes())
          {
            updateAllNodes((Element)child, prefix);
          }
        }
      }
    }   
  }

  protected boolean validateTargetNamespace(String ns)
  {
    // will allow blank namespace !!
    if (ns.equals(""))
     {
      return true;
    }
    
    String errorMessage = null;
    try
    {
      URI testURI = new URI(ns);
    }
    catch (URISyntaxException e)
    {
      errorMessage = XSDEditorPlugin.getXSDString("_WARN_INVALID_TARGET_NAMESPACE");
    }
    
    if (errorMessage == null || errorMessage.length() == 0)
     {
      return true;
    }
    return false;
  }
  
}

