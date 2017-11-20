/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class TypesHelper
{
  XSDSchema xsdSchema;
  Vector list = new Vector();

  public TypesHelper(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }

  private void updateExternalImportGlobals()
  {
    if (xsdSchema != null)
    {
      Iterator contents = xsdSchema.getContents().iterator();
      while (contents.hasNext())
      {
        XSDSchemaContent content = (XSDSchemaContent) contents.next();
        if (content instanceof XSDImportImpl)
        {
          XSDImportImpl anImport = (XSDImportImpl) content;
          try
          {
            if (anImport.getSchemaLocation() != null)
            {
              anImport.importSchema();
            }
          }
          catch (Exception e)
          {
            
          }
        }
      }
    }
  }


  // TODO This method and it's very similar getBuiltInTypeNamesList2 should 
  // be reviewed and combined if possible. Both seem to be used from 
  // XSDModelQueryExtension for content assist purposes.
  
  public java.util.List getBuiltInTypeNamesList()
  {
    List items = new ArrayList();
    if (xsdSchema != null)
    {
      String prefix = xsdSchema.getSchemaForSchemaQNamePrefix();
      if (xsdSchema != null)
      {
        XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        for (Iterator i = schemaForSchema.getSimpleTypeIdMap().values().iterator(); i.hasNext();)
        {
          XSDTypeDefinition td = (XSDTypeDefinition) i.next();  
          String localName = td.getName(); 
          String prefixedName = (prefix != null && prefix.length() > 0) ? prefix + ":" + localName : localName; 
          items.add(prefixedName);        
        }
      }
    }
    return items;
  }

  // issue (cs) do we still need this?  it can likely be remove now
  // was used for content assist but I don't think we really need it
  public java.util.List getBuiltInTypeNamesList2()
  {
    List result = new ArrayList();
    if (xsdSchema != null)
    {
      List prefixes = getPrefixesForNamespace(xsdSchema.getSchemaForSchemaNamespace());
      XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
      for (Iterator i = schemaForSchema.getSimpleTypeIdMap().values().iterator(); i.hasNext();)
      {
        XSDTypeDefinition td = (XSDTypeDefinition) i.next();  
        String localName = td.getName();
        String prefix = prefixes.size() > 0 ? (String)prefixes.get(0) : null;
        String prefixedName = (prefix != null && prefix.length() > 0) ? prefix + ":" + localName : localName; 
        result.add(prefixedName);        
      }
    }
    return result;
  }

  public java.util.List getUserSimpleTypeNamesList()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      Iterator i = xsdSchema.getTypeDefinitions().iterator();
      while (i.hasNext())
      {
        XSDTypeDefinition typeDefinition = (XSDTypeDefinition) i.next();
        if (typeDefinition instanceof XSDSimpleTypeDefinition)
        {
          items.addAll(getPrefixedNames(typeDefinition.getTargetNamespace(), typeDefinition.getName()));
        }
      }
      items = (Vector) sortList(items);
    }
    return items;
  }

  public java.util.List getUserComplexTypeNamesList()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      Iterator i = xsdSchema.getTypeDefinitions().iterator();
      while (i.hasNext())
      {
        XSDTypeDefinition typeDefinition = (XSDTypeDefinition) i.next();
        if (typeDefinition instanceof XSDComplexTypeDefinition)
        {
			    items.addAll(getPrefixedNames(typeDefinition.getTargetNamespace(), typeDefinition.getName()));         
        }
      }
      items = (Vector) sortList(items);
    }
    return items;
  }
  
  public java.util.List getUserSimpleTypes()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      Iterator i = xsdSchema.getTypeDefinitions().iterator();
      while (i.hasNext())
      {
        XSDTypeDefinition typeDefinition = (XSDTypeDefinition) i.next();
        if (typeDefinition instanceof XSDSimpleTypeDefinition)
        {
          items.add(typeDefinition);
          //items.add(typeDefinition.getQName(xsdSchema));
        }
      }
      // We need to add the anyType
//      items.add(getPrefix(xsdSchema.getSchemaForSchemaNamespace(), true) + "anyType");
      
      //      items = addExternalImportedUserSimpleTypes(items);
      //items = (Vector) sortList(items);
    }
    return items;
  }

  public String getPrefix(String ns, boolean withColon)
  {
    String key = "";

    if (xsdSchema != null)
    {
      Map map = xsdSchema.getQNamePrefixToNamespaceMap();
      Iterator iter = map.keySet().iterator();
      while (iter.hasNext())
      {
        Object keyObj = iter.next();
        Object value = map.get(keyObj);
        if (value != null && value.toString().equals(ns))
        {
          if (keyObj != null)
          {
            key = keyObj.toString();
          }
          else
          {
            key = "";
          }
          break;
        }
      }
      if (!key.equals(""))
      {
        if (withColon)
        {
          key = key + ":";
        }
      }
    }
    return key;
  }

  public java.util.List getGlobalElements()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      if (xsdSchema.getElementDeclarations() != null)
      {
        Iterator i = xsdSchema.getElementDeclarations().iterator();
        while (i.hasNext())
        {
          XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) i.next();
          String name = elementDeclaration.getQName(xsdSchema);
          if (name != null)
          {
            items.add(name);
          }
        }
      }
      //      items = addExternalImportedGlobalElements(items);
      items = (Vector) sortList(items);
    }
    return items;
  }

  public java.util.List getGlobalAttributes()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      if (xsdSchema.getAttributeDeclarations() != null)
      {
        Iterator i = xsdSchema.getAttributeDeclarations().iterator();
        while (i.hasNext())
        {
          XSDAttributeDeclaration attributeDeclaration = (XSDAttributeDeclaration) i.next();
          if (attributeDeclaration.getTargetNamespace() == null || (attributeDeclaration.getTargetNamespace() != null && !attributeDeclaration.getTargetNamespace().equals(XSDConstants.SCHEMA_INSTANCE_URI_2001)))
          {
            String name = attributeDeclaration.getQName(xsdSchema);
            if (name != null)
            {
              items.add(name);
            }
          }
        }
      }
      //      items = addExternalImportedAttributes(items);
      items = (Vector) sortList(items);
    }
    return items;
  }

  public java.util.List getGlobalAttributeGroups()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      if (xsdSchema.getAttributeGroupDefinitions() != null)
      {
        Iterator i = xsdSchema.getAttributeGroupDefinitions().iterator();
        while (i.hasNext())
        {
          XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) i.next();
          String name = attributeGroupDefinition.getQName(xsdSchema);
          if (name != null)
          {
            items.add(name);
          }
        }
      }
      //      items = addExternalImportedAttributeGroups(items);
      items = (Vector) sortList(items);
    }
    return items;
  }

  public java.util.List getModelGroups()
  {
    Vector items = new Vector();
    if (xsdSchema != null)
    {
      updateExternalImportGlobals();
      if (xsdSchema.getModelGroupDefinitions() != null)
      {
        Iterator i = xsdSchema.getModelGroupDefinitions().iterator();
        while (i.hasNext())
        {
          XSDModelGroupDefinition modelGroupDefinition = (XSDModelGroupDefinition) i.next();
          String name = modelGroupDefinition.getQName(xsdSchema);
          if (name != null)
          {
            items.add(name);
          }
        }
      }
      //      items = addExternalImportedGroups(items);
      items = (Vector) sortList(items);
    }
    return items;
  }

  // issue (cs) ssems like a rather goofy util method?
  public static java.util.List sortList(java.util.List types)
  {
    try
    {
      java.util.Collections.sort(types); // performance?  n*log(n)
    }
    catch (Exception e)
    {
//      XSDEditorPlugin.getPlugin().getMsgLogger().write("Sort failed");
    }
    return types;
  }

  // issue (cs) do we still need this?
  public void updateMapAfterDelete(XSDImport deletedNode)
  {
    String ns = deletedNode.getNamespace();
    if (ns != null)
    {
      String prefix = getPrefix(ns, false);
      if (prefix != null)
      {
        prefix = prefix.trim();
      }
      String xmlnsAttr = (prefix == "") ? "xmlns" : "xmlns:" + prefix;

      if (prefix == "")
      {
        prefix = null;
      }

      if (xsdSchema != null)
      {
        Map map = xsdSchema.getQNamePrefixToNamespaceMap();
        map.remove(prefix);
        Element schemaElement = xsdSchema.getElement();
        schemaElement.removeAttribute(xmlnsAttr);
      }
    }
  }

  public List getPrefixedNames(String namespace, String localName)
  {
    List list = new ArrayList();
    if (namespace == null)
    {
      namespace = "";    			
    }
    if (xsdSchema != null && localName != null)
    {
      List prefixes = getPrefixesForNamespace(namespace);
      for (Iterator i = prefixes.iterator(); i.hasNext(); )
      {
      	String prefix = (String)i.next();
      	if (prefix == null) prefix = "";
        String prefixedName = prefix.length() > 0 ? prefix + ":" + localName : localName;
        list.add(prefixedName);               
      }
      if (prefixes.size() == 0)
      {
        list.add(localName);
      }
    }
    return list;
  }
  
  protected List getPrefixesForNamespace(String namespace)
  {
    List list = new ArrayList();
    Map map = xsdSchema.getQNamePrefixToNamespaceMap();
    for (Iterator iter = map.keySet().iterator(); iter.hasNext();)
    {
      String prefix = (String) iter.next();
      Object value = map.get(prefix);
      if (value != null && value.toString().equals(namespace))
      {
       list.add(prefix);
      }
    }
    return list;
  }
}
