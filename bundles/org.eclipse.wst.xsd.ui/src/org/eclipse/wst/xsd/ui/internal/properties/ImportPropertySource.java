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
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorContextIds;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.XSDExternalFileCleanup;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class ImportPropertySource
  extends SchemaDirectiveHelperPropertySource
  implements IPropertySource
{
  boolean isSetNamespace = false;
  /**
   * @param viewer
   * @param xsdSchema
   */
  public ImportPropertySource(Viewer viewer, XSDSchema xsdSchema, IFile currentIFile)
  {
    super(viewer, xsdSchema, false);
    this.currentIFile = currentIFile;
//     WorkbenchHelp.setHelp(controlsContainer, XSDEditorContextIds.XSDE_IMPORT_DESIGN_VIEW);
//WorkbenchHelp.setHelp(selectButton, XSDEditorContextIds.XSDE_INCLUDE_HELPER_SELECT);
//WorkbenchHelp.setHelp(prefixField, XSDEditorContextIds.XSDE_IMPORT_PREFIX);
  }
  /**
   * @param xsdSchema
   */
  public ImportPropertySource(XSDSchema xsdSchema, IFile currentIFile)
  {
    super(xsdSchema, false);
    this.currentIFile = currentIFile;
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
    
    SchemaLocationPropertyDescriptor schemaLocationDescriptor =
    new SchemaLocationPropertyDescriptor(
      XSDConstants.SCHEMALOCATION_ATTRIBUTE,
      XSDConstants.SCHEMALOCATION_ATTRIBUTE
      );
    schemaLocationDescriptor.setHelpContextIds(XSDEditorContextIds.XSDE_INCLUDE_HELPER_SELECT);
    list.add(schemaLocationDescriptor);
    
    if (isSetNamespace)
    {
      PropertyDescriptor prefixDescriptor = 
      new TextPropertyDescriptor(
        "Prefix",
        "Prefix");
      prefixDescriptor.setHelpContextIds(XSDEditorContextIds.XSDE_IMPORT_PREFIX);
      list.add(prefixDescriptor);
    }
    else
    {
      PropertyDescriptor prefixDescriptor = 
      new PropertyDescriptor(
        "Prefix",
        "Prefix");
      prefixDescriptor.setHelpContextIds(XSDEditorContextIds.XSDE_IMPORT_PREFIX);
      list.add(prefixDescriptor);
    }

    PropertyDescriptor namespaceDescriptor = 
    new PropertyDescriptor(
        XSDConstants.NAMESPACE_ATTRIBUTE,
        XSDConstants.NAMESPACE_ATTRIBUTE);
    list.add(namespaceDescriptor);  

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

      if (attributeName.equals("Prefix"))
       {
        TypesHelper helper = new TypesHelper(xsdSchema);
        String aPrefix = helper.getPrefix(element.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE), false);
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
      TypesHelper typesHelper = new TypesHelper(xsdSchema);
      String namespace = element.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE);
      String oldPrefixValue = typesHelper.getPrefix(namespace, false);
      
      String schemaLocation = element.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE);
      if (((String) id).equals("Prefix"))
      {
        if (validatePrefix(newValue) &&  schemaLocation.length() > 0)
        {
          Map map = xsdSchema.getQNamePrefixToNamespaceMap();

          if (map.containsKey(newValue))
          {
//          setErrorMessage(XSDEditorPlugin.getXSDString("_ERROR_LABEL_PREFIX_EXISTS"));
          }
          else
          {
            beginRecording(XSDEditorPlugin.getXSDString("_UI_PREFIX_CHANGE"), element);
            
            Element schemaElement = xsdSchema.getElement();
            map.remove(oldPrefixValue);
            map.put(newValue, namespace);
            XSDSchemaHelper.updateElement(xsdSchema);

            endRecording(element);  
          }
        }
      }
      else if (((String) id).equals(XSDConstants.SCHEMALOCATION_ATTRIBUTE))
      { 
        updateExternalModel((String)value, selectedIFile, selectedNamespace, selectedXSDSchema);
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
    this.element = element;
    String namespace = element.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE);
    if (namespace != null && namespace.trim().length() > 0)
    {
      isSetNamespace = true;
    }    
    
  }

  protected void updateExternalModel(String newLocation, IFile newFile, String namespace, XSDSchema externalSchema)
  {
    if (xsdSchema == null) // in case we have a bad schema
     {
      return;
    }
    Element importElement = element;
    if (namespace == null)
    {
      namespace = "";
    }        
    
    XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
    if (comp instanceof XSDImport)
    {
      beginRecording(XSDEditorPlugin.getXSDString("_UI_IMPORT_CHANGE"), importElement);
      java.util.Map map = xsdSchema.getQNamePrefixToNamespaceMap();
      
      // Referential integrity on old import
      // How can we be sure that if the newlocation is the same as the oldlocation
      // the file hasn't changed
      
      XSDSchema referencedSchema = ((XSDSchemaDirective)comp).getResolvedSchema();
      if (referencedSchema != null)
      {
        XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
        cleanHelper.visitSchema(xsdSchema);
      }

      Element schemaElement = xsdSchema.getElement();
     
//      String oldPrefix = prefixField.getText();
//
//      // Use the existing xmlns if available
//      if (!map.containsValue(namespace))
//      {
//        if (oldPrefix.length() > 0)
//        {
//          schemaElement.removeAttribute("xmlns:"+oldPrefix);
//          map.remove(oldPrefix);
//        }
//      }

      XSDImport xsdImport = (XSDImport)comp;
//      xsdImport.setSchemaLocation(null);
//      xsdImport.setResolvedSchema(externalSchema);

      // update the xmlns in the schema element first, and then update the import element next
      // so that the last change will be in the import element.  This keeps the selection
      // on the import element
      TypesHelper helper = new TypesHelper(externalSchema);
      String prefix = helper.getPrefix(namespace, false);
      
      boolean prefixAlreadyExists = false;
      if (map.containsKey(prefix))
      {
        prefixAlreadyExists = true;
      }
      
      if (prefix == null || (prefix !=null && prefix.length() == 0) || prefixAlreadyExists)
      {
        prefix = "pref";

        int prefixExtension = 1;
        while (map.containsKey(prefix) && prefixExtension < 100)
         {
          prefix = prefix + String.valueOf(prefixExtension);
          prefixExtension++;
        }
      }

      if (namespace.length() > 0)
       {
        // if ns already in map, use its corresponding prefix
        if (map.containsValue(namespace))
         {
          TypesHelper typesHelper = new TypesHelper(xsdSchema);
          prefix = typesHelper.getPrefix(namespace, false);
        }
        else // otherwise add to the map
        {
//          prefixMap.put(prefix, newLocation);
          schemaElement.setAttribute("xmlns:"+prefix, namespace);
        }
      }

      // Now update the import element's attributes
      importElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, newLocation);
      
      if (!namespace.equals(""))
       {
        importElement.setAttribute(XSDConstants.NAMESPACE_ATTRIBUTE, namespace);
      }
      else
       {
        importElement.removeAttribute(XSDConstants.NAMESPACE_ATTRIBUTE);
      }
      
//      if (getEditor() != null)
//       {
//        getEditor().reparseSchema();
//        getEditor().getGraphViewer().setSchema(getXSDSchema());
//      }

      endRecording(importElement);
    }
  }
}
