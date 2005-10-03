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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDFactoryImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SimpleRestrictPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String [] whiteSpaceComboChoices = { "", "preserve", "replace", "collapse" };
  /**
   * 
   */
  public SimpleRestrictPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SimpleRestrictPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public SimpleRestrictPropertySource(XSDSchema xsdSchema)
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
    
    SimpleContentPropertyDescriptor typeDescriptor = new SimpleContentPropertyDescriptor(
        XSDConstants.BASE_ATTRIBUTE,
        XSDConstants.BASE_ATTRIBUTE,
        (Element)element.getParentNode(), xsdSchema);  // get the parent node!
    list.add(typeDescriptor);
    
    Iterator facets = xsdSimpleType.getValidFacets().iterator();
    
    while(facets.hasNext())
    {
      String aValidFacet = (String)facets.next();
      if (!(aValidFacet.equals(XSDConstants.PATTERN_ELEMENT_TAG) || aValidFacet.equals(XSDConstants.ENUMERATION_ELEMENT_TAG)))
      {
        if (aValidFacet.equals(XSDConstants.WHITESPACE_ELEMENT_TAG))
        {
          XSDComboBoxPropertyDescriptor whitespaceDescriptor = new XSDComboBoxPropertyDescriptor(
              aValidFacet, aValidFacet, whiteSpaceComboChoices);
          list.add(whitespaceDescriptor);
        }
        else
        {  
          list.add(new TextPropertyDescriptor(aValidFacet, aValidFacet));
        }
      }
    }
    
    
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
      if (((String) id).equals(XSDConstants.BASE_ATTRIBUTE))
      {
        String baseType = element.getAttribute(XSDConstants.BASE_ATTRIBUTE);
        if (baseType == null)
        {
          baseType = "";
        }
        return baseType;
      }
      else
      {
        String aFacet = (String)id;
        Iterator facets = xsdSimpleType.getFacets().iterator();
        
        while(facets.hasNext())
        {
          XSDFacet aValidFacet = (XSDFacet)facets.next();
          if (aValidFacet.getFacetName().equals(aFacet))
          {
            result = aValidFacet.getLexicalValue();
            if (result == null)
            {
              result = "";
            }
            return result;
          }
        }
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
    if (value != null)
     {
      if (value instanceof String)
      {
        String newValue = (String)value;
        
        if (((String) id).equals(XSDConstants.BASE_ATTRIBUTE))
        {            
          beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element);        

          Element parent = (Element)element.getParentNode();
          if (XSDDOMHelper.inputEquals(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
          {
//            updateElementToNotAnonymous(element);
          }
          getDomHelper().setSimpleContentType(element, newValue);
          endRecording(element);
        }
        else
        {
          Element simpleTypeElement = xsdSimpleType.getElement();
          XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
          Element derivedByElement = xsdDOMHelper.getDerivedByElement(simpleTypeElement);
          beginRecording(XSDEditorPlugin.getXSDString("_UI_FACET_CHANGE"), simpleTypeElement);
          String prefix = simpleTypeElement.getPrefix();
          prefix = (prefix == null) ? "" : (prefix + ":");

          String aFacet = (String)id;
          XSDFactoryImpl factory = new XSDFactoryImpl();

          Element childNodeElement = null;
          DOMAttribute valueAttr = null;
          XSDFacet facet = null;
          if (aFacet.equals(XSDConstants.TOTALDIGITS_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getTotalDigitsFacet();
          }
          else if (aFacet.equals(XSDConstants.FRACTIONDIGITS_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getFractionDigitsFacet();
          }
          else if (aFacet.equals(XSDConstants.WHITESPACE_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getWhiteSpaceFacet();
          }
          else if (aFacet.equals(XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMaxExclusiveFacet();
          }
          else if (aFacet.equals(XSDConstants.MAXINCLUSIVE_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMaxInclusiveFacet();
          }
          else if (aFacet.equals(XSDConstants.MINEXCLUSIVE_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMinExclusiveFacet();
          }
          else if (aFacet.equals(XSDConstants.MININCLUSIVE_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMinInclusiveFacet();
          }
          else if (aFacet.equals(XSDConstants.LENGTH_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getLengthFacet();
          }
          else if (aFacet.equals(XSDConstants.MAXLENGTH_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMaxLengthFacet();
          }
          else if (aFacet.equals(XSDConstants.MINLENGTH_ELEMENT_TAG))
          {
            facet = xsdSimpleType.getMinLengthFacet();
          }
          
          if (facet != null)
          {
            facet.setLexicalValue(newValue);
          }
          else
          {
            facet = (XSDFacet)factory.createXSDTotalDigitsFacet();
            childNodeElement = (derivedByElement.getOwnerDocument()).createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + aFacet);
            valueAttr = new DOMAttribute(XSDConstants.VALUE_ATTRIBUTE, "");
            childNodeElement.setAttribute(valueAttr.getName(), valueAttr.getValue());
            valueAttr.setValue(newValue);
            childNodeElement.setAttribute(valueAttr.getName(), valueAttr.getValue());  
            element.appendChild(childNodeElement);
          //formatChild(childNodeElement, hasChildrenElements);    
          }
          XSDSchemaHelper.updateElement(xsdSimpleType);
          if (facet != null)
          {
            XSDSchemaHelper.updateElement(facet);
          }
        }
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

  protected boolean isAnonymous;
  protected XSDSimpleTypeDefinition xsdSimpleType;

  public void setInput(Element element)
  {
    this.element = element;
    if (xsdSchema == null)
    {
      return;
    }

    isAnonymous = checkForAnonymousType(element);
    
    if (XSDDOMHelper.inputEquals(element, XSDConstants.RESTRICTION_ELEMENT_TAG, false))
    {
      Element parent = (Element)element.getParentNode();
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        XSDConcreteComponent component = null;
        if (parent != null)
        {
          component = xsdSchema.getCorrespondingComponent(parent);
        }
        if (component instanceof XSDSimpleTypeDefinition)
        {
          xsdSimpleType = (XSDSimpleTypeDefinition)component;
        }
      }

      XSDConcreteComponent xsdConcreteComponent = null;
      if (element.getParentNode() != null)
      {
        xsdConcreteComponent = xsdSchema.getCorrespondingComponent(element.getParentNode());
      }

      if (xsdConcreteComponent instanceof XSDSimpleTypeDefinition)
      {
        xsdSimpleType = (XSDSimpleTypeDefinition)xsdConcreteComponent;
      }
    }
  }

  boolean checkForAnonymousType(Element element)
  {
    boolean isAnonymous = false;

    Node aNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
    {
      return true;
    }
    return isAnonymous;
  }

}
