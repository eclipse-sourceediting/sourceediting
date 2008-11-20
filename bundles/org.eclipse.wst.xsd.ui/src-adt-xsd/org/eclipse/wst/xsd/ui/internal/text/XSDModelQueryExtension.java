/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeFilter;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
public class XSDModelQueryExtension extends ModelQueryExtension
{
  public XSDModelQueryExtension()
  {
  }

  public String[] getAttributeValues(Element e, String namespace, String name)
  {
    List list = new ArrayList();
    String currentElementName = e.getLocalName();
    Node parentNode = e.getParentNode();
    String parentName = parentNode != null ? parentNode.getLocalName() : "";
    if (checkName(name, "type"))
    {
      if (checkName(currentElementName, "attribute"))
      {
        list = getTypesHelper(e).getBuiltInTypeNamesList2();
        list.addAll(getTypesHelper(e).getUserSimpleTypeNamesList());
      }
      else if (checkName(currentElementName, "element"))
      {
        list = getTypesHelper(e).getBuiltInTypeNamesList2();
        list.addAll(getTypesHelper(e).getUserSimpleTypeNamesList());
        list.addAll(getTypesHelper(e).getUserComplexTypeNamesList());
      }
    }
    else if (checkName(name, "minOccurs"))
    {
      list.add("0");
    }
    else if (checkName(name, "itemType"))
    {
      if (checkName(currentElementName, "list"))
      {
        if (checkName(parentName, "simpleType"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserSimpleTypeNamesList());
        }
      }
    }
    else if (checkName(name, "memberTypes"))
    {
      if (checkName(currentElementName, "union"))
      {
        if (checkName(parentName, "simpleType"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserSimpleTypeNamesList());
        }
      }
    }
    else if (checkName(name, "base"))
    {
      if (checkName(currentElementName, "restriction"))
      {
        if (checkName(parentName, "simpleType"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserSimpleTypeNamesList());
        }
        else if (checkName(parentName, "simpleContent"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserComplexTypeNamesList());
        }
        else if (checkName(parentName, "complexContent"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserComplexTypeNamesList());
        }
      }
      else if (checkName(currentElementName, "extension"))
      {
        if (checkName(parentName, "simpleContent"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserComplexTypeNamesList());
        }
        else if (checkName(parentName, "complexContent"))
        {
          list = getTypesHelper(e).getBuiltInTypeNamesList();
          list.addAll(getTypesHelper(e).getUserComplexTypeNamesList());
        }
      }
    }
    else if (checkName(name, "ref"))
    {
      if (checkName(currentElementName, "element"))
      {
        list = getTypesHelper(e).getGlobalElements();
      }
      else if (checkName(currentElementName, "attribute"))
      {
        list = getTypesHelper(e).getGlobalAttributes();
      }
      else if (checkName(currentElementName, "attributeGroup"))
      {
        list = getTypesHelper(e).getGlobalAttributeGroups();
      }
      else if (checkName(currentElementName, "group"))
      {
        list = getTypesHelper(e).getModelGroups();
      }
    }
    else if (checkName(name, "substitutionGroup"))
    {
      if (checkName(currentElementName, "element"))
      {
        list = getTypesHelper(e).getGlobalElements();
      }
    }
    String[] result = new String[list.size()];
    list.toArray(result);
    return result;
  }

  public boolean isApplicableChildElement(Node parentNode, String namespace, String name)
  {
    if (XSDConstants.APPINFO_ELEMENT_TAG.equals(parentNode.getNodeName()) &&
        XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespace))
    {
        return false;
    }
   
    // we assume that other nodes don't want schema nodes as extension elements
    // TODO... cs: we really need to define custimizations for filtering based on parent/child
    // namespace pairs to accurately handle this    
    String parentElementNamespaceURI = parentNode.getNamespaceURI();
    if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(parentElementNamespaceURI) &&
        XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespace))
    {
       return false;   
    }
    
    if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespace))
    {  
      NodeFilter filter = XSDEditorPlugin.getPlugin().getNodeCustomizationRegistry().getNodeFilter(namespace);
      if (filter != null)
      {
        return filter.isApplicableContext(parentNode, Node.ELEMENT_NODE, namespace, name);
      }     
    }
     
    return true;
  }

  protected XSDSchema lookupOrCreateSchema(Document document)
  {
    return XSDModelAdapter.lookupOrCreateSchema(document);
  }

  /**
   * @deprecated
   */
  protected XSDSchema lookupOrCreateSchemaForElement(Element element)
  {
    return lookupOrCreateSchema(element.getOwnerDocument());
  }

  protected TypesHelper getTypesHelper(Element element)
  {
    XSDSchema schema = lookupOrCreateSchema(element.getOwnerDocument());
    return new TypesHelper(schema);
  }

  protected boolean checkName(String localName, String token)
  {
    if (localName != null && localName.trim().equals(token))
    {
      return true;
    }
    return false;
  }
}