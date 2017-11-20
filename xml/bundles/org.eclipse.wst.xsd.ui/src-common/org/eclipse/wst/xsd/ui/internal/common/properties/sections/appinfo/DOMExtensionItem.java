/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.tabletree.TreeContentHelper;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTextValueCommand;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
class DOMExtensionItem extends ExtensionItem
{
  private final static int KIND_ELEMENT_ATTR = 1;
  private final static int KIND_ELEMENT_TEXT = 2;
  private final static int KIND_ELEMENT_CMATTRIBUTE = 3;
  private final static int KIND_ATTR_TEXT = 4;
  int kind;
  Attr attribute;
  Element parent;
  CMNode cmNode;

  private DOMExtensionItem(int kind, Element parent, Attr node, CMNode cmNode)
  {
    this.kind = kind;
    this.parent = parent;
    this.attribute = node;
    this.cmNode = cmNode;
  }
  
  public boolean isTextValue()
  {
    return kind == KIND_ELEMENT_TEXT || kind == KIND_ATTR_TEXT;
  }

  static DOMExtensionItem createItemForElementText(Element parent)
  {
    return new DOMExtensionItem(KIND_ELEMENT_TEXT, parent, null, null);
  }

  static DOMExtensionItem createItemForElementAttribute(Element parent, Attr attribute)
  {
    return new DOMExtensionItem(KIND_ELEMENT_ATTR, parent, attribute, null);
  }

  static DOMExtensionItem createItemForElementAttribute(Element parent, CMAttributeDeclaration ad)
  {
    if (ad == null)
    {
      System.out.println("null!");
    }
    return new DOMExtensionItem(KIND_ELEMENT_CMATTRIBUTE, parent, null, ad);
  }

  static DOMExtensionItem createItemForAttributeText(Element parent, Attr attribute)
  {
    return new DOMExtensionItem(KIND_ATTR_TEXT, parent, attribute, null);
  }

  public String getName()
  {
    String result = null;
    switch (kind)
    {
      case KIND_ATTR_TEXT : {
        result = "value";
        break;
      }
      case KIND_ELEMENT_ATTR : {
        result = attribute.getName();
        break;
      }
      case KIND_ELEMENT_CMATTRIBUTE : {
        CMAttributeDeclaration ad = (CMAttributeDeclaration) cmNode;
        result = ad.getNodeName();
        break;
      }
      case KIND_ELEMENT_TEXT : {
        result = "text value";
        break;
      }
    }
    return result != null ? result : "";
  }

  public String getValue()
  {
    switch (kind)
    {
      case KIND_ATTR_TEXT :
      case KIND_ELEMENT_ATTR : {
        // note intentional fall-thru!!
        return attribute.getNodeValue();
      }
      case KIND_ELEMENT_CMATTRIBUTE : {
        // CS : one would think that we'd just need to return "" here
        // but after editing a item of this kind and giving it value
        // the list of item's doesn't get recomputed.. so we need to trick
        // one of these items to behave like the KIND_ELEMENT_ATTR case
        //
        String value = parent.getAttribute(cmNode.getNodeName());
        return (value != null) ? value : "";
      }
      case KIND_ELEMENT_TEXT : {
        return new TreeContentHelper().getElementTextValue(parent);         
      }
    }
    return "";
  }
  

  public String[] getPossibleValues()
  {
    String[] result = {};
     
    switch (kind)
    {
      case KIND_ATTR_TEXT :
      case KIND_ELEMENT_ATTR : {
        // note intentional fall-thru!!
        ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
        if (modelQuery != null)
        {            
          CMAttributeDeclaration ad = modelQuery.getCMAttributeDeclaration(attribute);        
          if (ad != null)
          {
            result = modelQuery.getPossibleDataTypeValues(parent, ad);
          }
        }
        break;
      }
      case KIND_ELEMENT_CMATTRIBUTE : {
        ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
        if (modelQuery != null && cmNode != null)
        {
          result = modelQuery.getPossibleDataTypeValues(parent, cmNode);
        }
        break;
      }
      case KIND_ELEMENT_TEXT : {
        ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
        if (modelQuery != null)
        {
          CMElementDeclaration ed = modelQuery.getCMElementDeclaration(parent);
          if (ed != null)
          { 
            result = modelQuery.getPossibleDataTypeValues(parent, ed);             
          }  
        }
        break;
      }
    }
    return result;
  }

  public Command getUpdateValueCommand(String newValue)
  {
    switch (kind)
    {
      case KIND_ATTR_TEXT :
      case KIND_ELEMENT_ATTR : {
        // note intentional fall-thru!!               
        return new UpdateAttributeValueCommand(parent, attribute.getNodeName(), newValue, true);
      }
      case KIND_ELEMENT_CMATTRIBUTE : {
        final CMAttributeDeclaration ad = (CMAttributeDeclaration) cmNode;
        return new UpdateAttributeValueCommand(parent, ad.getAttrName(), newValue, true);
      }
      case KIND_ELEMENT_TEXT : {
        return new UpdateTextValueCommand(parent, newValue);
      }
    }
    return null;
  }

  public String getNamespace()
  {
    String namespace = null;
    if (kind == KIND_ATTR_TEXT)
    {
      namespace = attribute.getNamespaceURI();
    }
    else if (parent != null)
    {
      namespace = parent.getNamespaceURI();
    }
    return namespace;
  }

  public Node getParentNode()
  {
    Node parentNode = null;
    if (attribute != null)
    {
      parentNode = attribute.getOwnerElement();
    }
    else if (parent != null)
    {
      parentNode = parent;
    }
    return parentNode;
  }

  public String getParentName()
  {
    Node parentNode = getParentNode();
    return parentNode != null ? parentNode.getLocalName() : "";
  }

  public Node getNode()
  {
    return attribute;
  }
}
