/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.nsedit;

import java.util.Iterator;
import java.util.List;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class SchemaPrefixChangeHandler
{
  String newPrefix;
  XSDSchema xsdSchema;
  String namespace;

  public SchemaPrefixChangeHandler(XSDSchema xsdSchema, String newPrefix)
  {
    this.xsdSchema = xsdSchema;
    this.newPrefix= newPrefix;
    namespace = XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001; 
  }

  public SchemaPrefixChangeHandler(XSDSchema xsdSchema, String newPrefix, String namespace)
  {
    this.xsdSchema = xsdSchema;
    this.newPrefix= newPrefix;
    this.namespace = namespace;
  }
  
  public void resolve()
  {
    XSDSchemaPrefixRenamer xsdSchemaPrefixRenamer = new XSDSchemaPrefixRenamer();
    xsdSchemaPrefixRenamer.visitSchema(xsdSchema);
  }

  public String getNewQName(XSDTypeDefinition comp, String value, String newXSDPrefix)
  {
    String qName = null;
    if (value != null)
    {
      qName = newXSDPrefix;
      if (qName != null && qName.length() > 0)
      {
        qName += ":" + value;
      }
      else
      {
        qName = value; 
      }
    }
    else
    {
      qName = value;
    }
    
    return qName;
  }

  
  class XSDSchemaPrefixRenamer extends XSDVisitor
  {
    public XSDSchemaPrefixRenamer()
    {
      super();
    }
    
    public void visitElementDeclaration(XSDElementDeclaration element)
    {
      super.visitElementDeclaration(element);
      XSDTypeDefinition type = element.getType();
      if (type != null)
      {
        String ns = type.getTargetNamespace();
        if (ns == null) ns = "";
        if (ns.equals(namespace))        
        {
          Element domElement = element.getElement();
          if (domElement != null && domElement instanceof IDOMNode)
          {
            Attr typeAttr = domElement.getAttributeNode(XSDConstants.TYPE_ATTRIBUTE);
            if (typeAttr != null)
            {
              element.getElement().setAttribute(XSDConstants.TYPE_ATTRIBUTE, getNewQName(type, type.getName(), newPrefix));            
            }
          }
        }
      }
    }
    
    public void visitSimpleTypeDefinition(XSDSimpleTypeDefinition simpleType)
    {
      super.visitSimpleTypeDefinition(simpleType);
      XSDTypeDefinition baseType = simpleType.getBaseTypeDefinition();
      
      if (baseType != null)
      {
        String ns = baseType.getTargetNamespace();
        if (ns == null) ns = "";
        if (ns.equals(namespace))
        {
          XSDDOMHelper domHelper = new XSDDOMHelper();
          Element derivedBy = domHelper.getDerivedByElement(simpleType.getElement());
          if (derivedBy != null && derivedBy instanceof IDOMNode)
          {
            Attr typeAttr = derivedBy.getAttributeNode(XSDConstants.BASE_ATTRIBUTE);
            if (typeAttr != null)
            {
              derivedBy.setAttribute(XSDConstants.BASE_ATTRIBUTE, getNewQName(baseType, baseType.getName(), newPrefix));
            }
          }
        }
      }
      
      XSDSimpleTypeDefinition itemType = simpleType.getItemTypeDefinition();
      if (itemType != null)
      {
        String ns = itemType.getTargetNamespace();
        if (ns == null) ns = "";
        if (ns.equals(namespace))
        {
          XSDDOMHelper domHelper = new XSDDOMHelper();
          Node listNode = domHelper.getChildNode(simpleType.getElement(), XSDConstants.LIST_ELEMENT_TAG);
          if (listNode != null && listNode instanceof Element)
          {
            Element listElement = (Element)listNode;          
            if (listElement instanceof IDOMNode)
            {
              Attr typeAttr = listElement.getAttributeNode(XSDConstants.ITEMTYPE_ATTRIBUTE);
              if (typeAttr != null)
              {
                listElement.setAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, getNewQName(itemType, itemType.getName(), newPrefix));
              }
            }
          }
        }
      }
      
      List memberTypes = simpleType.getMemberTypeDefinitions();
      if (memberTypes.size() > 0)
      {
        XSDDOMHelper domHelper = new XSDDOMHelper();
        Node unionNode = domHelper.getChildNode(simpleType.getElement(), XSDConstants.UNION_ELEMENT_TAG);
        if (unionNode != null && unionNode instanceof Element)
        {
          Element unionElement = (Element)unionNode;          
          if (unionElement instanceof IDOMNode)
          {
            StringBuffer sb = new StringBuffer("");
            for (Iterator i = memberTypes.iterator(); i.hasNext(); )
            {
              XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)i.next();
              String ns = st.getTargetNamespace();
              if (ns == null) ns = "";
              if (ns.equals(namespace))
              {
                sb.append(getNewQName(st, st.getName(), newPrefix));                
              }
              else
              {
                sb.append(st.getQName(xsdSchema));
              }
              if (i.hasNext())
              {
                sb.append(" ");
              }
            }
            unionElement.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, sb.toString());
          }
        }
      }
    }

    public void visitAttributeDeclaration(XSDAttributeDeclaration attr)
    {
      super.visitAttributeDeclaration(attr);
      XSDTypeDefinition type = attr.getType();
      if (type != null)
      {
        String ns = type.getTargetNamespace();
        if (ns == null) ns = "";
        if (ns.equals(namespace))
        {
          Element domElement = attr.getElement();
          if (domElement != null && domElement instanceof IDOMNode)
          {
            Attr typeAttr = domElement.getAttributeNode(XSDConstants.TYPE_ATTRIBUTE);
            if (typeAttr != null)
            {
              attr.getElement().setAttribute(XSDConstants.TYPE_ATTRIBUTE, getNewQName(type, type.getName(), newPrefix));            
            }
          }
        }
      }
    }
  }    
}
