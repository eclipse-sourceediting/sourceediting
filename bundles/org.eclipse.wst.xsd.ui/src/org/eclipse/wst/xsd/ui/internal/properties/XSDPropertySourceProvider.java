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

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class XSDPropertySourceProvider implements IPropertySourceProvider
{
  XSDSchema xsdSchema;
  /**
   * 
   * @todo Generated comment
   */
  public XSDPropertySourceProvider()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
   */
  public IPropertySource getPropertySource(Object object)
  {
    if (object == null) return null;
    
    if (object instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent component = (XSDConcreteComponent)object;
      
      xsdSchema = component.getSchema();
      
      if (component instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)component;
        if (elementDeclaration.isElementDeclarationReference())
        {
          component = elementDeclaration.getResolvedElementDeclaration();
        }
      }
      
      Element input = component.getElement(); 
      
      BasePropertySource bps = (BasePropertySource)getXSDPropertySource(input);
      if (bps == null) return null;
      
      bps.setInput(input);
      return bps;
    }
    return null;
  }
  
  boolean showParent = false;
  
  public IPropertySource getXSDPropertySource(Object object)
  {
    Element input = (Element)object;

    showParent = false;
  
    if (inputEquals(input, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      return new ElementPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ELEMENT_ELEMENT_TAG, true))
    {
      return new GroupRefPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.SEQUENCE_ELEMENT_TAG, false) ||
              inputEquals(input, XSDConstants.CHOICE_ELEMENT_TAG, false) ||
              inputEquals(input, XSDConstants.ALL_ELEMENT_TAG, false))
    {
      return new ModelGroupPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {
      return new AttributePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
    {
      return new AttributeGroupRefPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, false))
    {
      return new NamePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
    {
      return new AttributeGroupRefPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.NOTATION_ELEMENT_TAG, false))
    {
      return new NotationPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
    {
      return new SimpleTypePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.GROUP_ELEMENT_TAG, false))
    {
      return new NamePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.GROUP_ELEMENT_TAG, true))
    {
      return new GroupRefPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.SCHEMA_ELEMENT_TAG, false))
    {
      return new SchemaPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
    {
      return new ComplexTypePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.DOCUMENTATION_ELEMENT_TAG, false))
    {
      return new DocumentationPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.APPINFO_ELEMENT_TAG, false))
    {
      return new AppInfoPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
    {
      if (input != null && input instanceof Element)
      {
        Element parent = (Element)input;
        XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
        Element derivedByNode = xsdDOMHelper.getDerivedByElement(parent);
        if (derivedByNode != null)
        {
          if (inputEquals(derivedByNode, XSDConstants.RESTRICTION_ELEMENT_TAG, false) || 
              inputEquals(derivedByNode, XSDConstants.EXTENSION_ELEMENT_TAG, false))
          {
            return new SimpleContentPropertySource(xsdSchema);
          }
        }
        else
        {
          return null;
        }
      }
    }
    else if (inputEquals(input, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
    {
      if (input != null && input instanceof Element)
      {
        Element parent = (Element)input;
        XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
        Element derivedByNode = xsdDOMHelper.getDerivedByElement(parent);
        if (derivedByNode != null)
        {
          return new SimpleContentPropertySource(xsdSchema);
        }
        else
        {
          return null;
        }
      }
    }
    else if (inputEquals(input, XSDConstants.LIST_ELEMENT_TAG, false))
    {
      return new SimpleTypeListPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.UNION_ELEMENT_TAG, false))
    {
      return new SimpleTypeUnionPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.RESTRICTION_ELEMENT_TAG, false))
    {
      return createRestrictWindow(input, xsdSchema);
    }
    else if (XSDDOMHelper.isFacet(input))
    {
      if (input != null && input instanceof Element)
      {
        Node parent = ((Element)input).getParentNode();
        if (inputEquals(parent, XSDConstants.RESTRICTION_ELEMENT_TAG, false))
        {
          return createRestrictWindow(input, xsdSchema);
        } 
      }
    }
    else if (inputEquals(input, XSDConstants.EXTENSION_ELEMENT_TAG, false))
    {
      if (input != null && input instanceof Element)
      {
        Node parent = ((Element)input).getParentNode();
        if (inputEquals(parent, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false)
            || inputEquals(parent, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
        {
          showParent = true;
          return new SimpleContentPropertySource(xsdSchema);
        }
      }
    }
    else if (inputEquals(input, XSDConstants.PATTERN_ELEMENT_TAG, false))
    {
      return new PatternPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ENUMERATION_ELEMENT_TAG, false))
    {
      return new EnumerationPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ANY_ELEMENT_TAG, false))
    {
      return new AnyElementPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false))
    {
      return new AnyAttributePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.UNIQUE_ELEMENT_TAG, false))
    {
      return new NamePropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.KEYREF_ELEMENT_TAG, false))
    {
      return new KeyrefPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.SELECTOR_ELEMENT_TAG, false))
    {
      return new XPathPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.FIELD_ELEMENT_TAG, false))
    {
      return new XPathPropertySource(xsdSchema);
    }
    else if (inputEquals(input, XSDConstants.KEY_ELEMENT_TAG, false))
    {
      return new NamePropertySource(xsdSchema);
    }
    else
    {
      return null;
    }
    return null;
  }

  protected IPropertySource createRestrictWindow(Object input, XSDSchema xsdSchema)
  {
    // special case where SimpleType restriction is different than SimpleContent restriction

    if (input != null && input instanceof Element)
    {
      Node parent = ((Element)input).getParentNode();
      if (inputEquals(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        return new SimpleRestrictPropertySource(xsdSchema);
      }
      else if (inputEquals(parent, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        return new SimpleRestrictPropertySource(xsdSchema);
      }
      else if (inputEquals(parent, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        showParent = true;
        return new SimpleContentPropertySource(xsdSchema);
      }
    }
    return null;
  }


  protected boolean inputEquals(Object input, String tagname, boolean isRef)
  {
    return XSDDOMHelper.inputEquals(input, tagname, isRef);
  }
}
