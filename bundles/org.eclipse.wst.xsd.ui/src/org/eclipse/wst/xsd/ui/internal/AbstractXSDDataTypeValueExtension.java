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
package org.eclipse.wst.xsd.ui.internal;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.DataTypeValueExtension;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


  /**
   * This class is used to extend the ModelQuery behaviour so that we can contribute our own
   * 'allowed values' for attributes or elements (e.g. the 'type' attribute).
   */
  public abstract class AbstractXSDDataTypeValueExtension implements DataTypeValueExtension
  { 
    protected ModelQuery modelQuery;

    public int getType()
    {
      return DATA_TYPE_VALUE_EXTENSION;
    }   

    public abstract String getId();

    public AbstractXSDDataTypeValueExtension(ModelQuery modelQuery)
    {
      this.modelQuery = modelQuery;
      if (modelQuery != null && modelQuery.getExtensionManager() != null)
      {
        modelQuery.getExtensionManager().addExtension(this);
      }
    }  

    public void dispose()
    {
      if (modelQuery != null && modelQuery.getExtensionManager() != null)
      {
        modelQuery.getExtensionManager().removeExtension(this);
      }
    }

    protected abstract XSDSchema getEnclosingXSDSchema(Element element);  


	protected TypesHelper createTypesHelper(XSDSchema schema)
	{ 
		return new TypesHelper(schema);		
	}
	
    public java.util.List getDataTypeValues(Element element, CMNode cmNode)
    {
      java.util.List list = new Vector();
      if (cmNode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION)
      {
        TypesHelper typesHelper = createTypesHelper(getEnclosingXSDSchema(element));
        String name = cmNode.getNodeName();
        String currentElementName = element.getLocalName();
        Node parentNode = element.getParentNode();
        String parentName = "";
        if (parentNode != null)
        {
          parentName = parentNode.getLocalName();
        }

        if (checkName(name, "type"))
        {
          if (checkName(currentElementName, "attribute"))
          {
            list = typesHelper.getBuiltInTypeNamesList();
            list.addAll(typesHelper.getUserSimpleTypeNamesList());
          }
          else if (checkName(currentElementName, "element"))
          {
            list = typesHelper.getBuiltInTypeNamesList2();
            list.addAll(typesHelper.getUserSimpleTypeNamesList());
            list.addAll(typesHelper.getUserComplexTypeNamesList());
          }
        }
        else if (checkName(name, "itemType"))
        {
          if (checkName(currentElementName, "list"))
          {
            if (checkName(parentName, "simpleType"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserSimpleTypeNamesList());
            }
          }
        }
        else if (checkName(name, "memberTypes"))
        {
          if (checkName(currentElementName, "union"))
          {
            if (checkName(parentName, "simpleType"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserSimpleTypeNamesList());
            }
          }
        }
        else if (checkName(name, "base"))
        {
          if (checkName(currentElementName, "restriction"))
          {
            if (checkName(parentName, "simpleType"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserSimpleTypeNamesList());
            }
            else if (checkName(parentName, "simpleContent"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserComplexTypeNamesList());
            }
            else if (checkName(parentName, "complexContent"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserComplexTypeNamesList());
            }
          }
          else if (checkName(currentElementName, "extension"))
          {
            if (checkName(parentName, "simpleContent"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserComplexTypeNamesList());
            }
            else if (checkName(parentName, "complexContent"))
            {
              list = typesHelper.getBuiltInTypeNamesList();
              list.addAll(typesHelper.getUserComplexTypeNamesList());
            }
          }
        }
        else if (checkName(name, "ref"))
        {
          if (checkName(currentElementName, "element"))
          {
            list = typesHelper.getGlobalElements();
          }
          else if (checkName(currentElementName, "attribute"))
          {
            list = typesHelper.getGlobalAttributes();
          }
          else if (checkName(currentElementName, "attributeGroup"))
          {
            list = typesHelper.getGlobalAttributeGroups();
          }
          else if (checkName(currentElementName, "group"))
          {
            list = typesHelper.getModelGroups();
          }
        }
        else if (checkName(name, "substitutionGroup"))
        {
          if (checkName(currentElementName, "element"))
          {
            list = typesHelper.getGlobalElements();
          }
        }
/*        else if (checkName(name, "refer"))
        {
          if (checkName(currentElementName, "keyref"))
          {
            list = typesHelper.getKeys();
          }
        } */


      }
      return list;
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