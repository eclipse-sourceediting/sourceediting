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
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalElementRenamer;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class ElementPropertySource extends BasePropertySource implements IPropertySource
{
  private static final String PROPERTY_NAME = "org.eclipse.wst.xsd.ui.internal.name";
  private String[] blockComboValues =
    { "", "#all", "extension", "restriction", "substitution" };
  private String[] finalComboValues =
    { "", "#all", "extension", "restriction" };
  private String[] substitutionGroupComboValues = { "" };
  private String[] formComboValues =
  {
      "",
      XSDEditorPlugin.getXSDString("_UI_COMBO_UNQUALIFIED"),
      XSDEditorPlugin.getXSDString("_UI_COMBO_QUALIFIED")
  };

  public ElementPropertySource()
  {
    super();
  }

  public ElementPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  
  public ElementPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }

  public void setInput(Element element)
  {
    this.element = element;
    TypesHelper helper = new TypesHelper(xsdSchema);
    List globals = helper.getGlobalElements();
    int size = globals.size() + 1;
    substitutionGroupComboValues = new String[size];
    substitutionGroupComboValues[0] = "";
    if (globals != null)
    {
      for (int k = 0; k < globals.size(); k++)
      {
        substitutionGroupComboValues[k + 1] = (String) globals.get(k);
      }
    }
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
  public Object getEditableValue()
  {
//    return element.getNodeName();
    return null;
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    Object parentNode = element.getParentNode();
    boolean isGlobalElement = XSDDOMHelper.inputEquals(parentNode, XSDConstants.SCHEMA_ELEMENT_TAG, false);
    
    List list = new ArrayList();
    // Create a descriptor and set a category
//  These have been moved to the general tab
//    PropertyDescriptor nameDescriptor =
//      new TextPropertyDescriptor(
//        XSDConstants.NAME_ATTRIBUTE,
//        XSDConstants.NAME_ATTRIBUTE);
//    list.add(nameDescriptor);
//    TypesPropertyDescriptor typeDescriptor = new TypesPropertyDescriptor(
//        XSDConstants.TYPE_ATTRIBUTE,
//        XSDConstants.TYPE_ATTRIBUTE,
//        element, xsdSchema);
//    list.add(typeDescriptor);
    if (isGlobalElement)
    {
      XSDComboBoxPropertyDescriptor abstractDescriptor =
        new XSDComboBoxPropertyDescriptor(
          XSDConstants.ABSTRACT_ATTRIBUTE,
          XSDConstants.ABSTRACT_ATTRIBUTE,
          trueFalseComboValues);
      list.add(abstractDescriptor);
    }
    if (!isGlobalElement)
    {
      PropertyDescriptor minOccursDescriptor =
        new TextPropertyDescriptor(
          XSDConstants.MINOCCURS_ATTRIBUTE,
          XSDConstants.MINOCCURS_ATTRIBUTE);
      list.add(minOccursDescriptor);
    
      PropertyDescriptor maxOccursDescriptor =
        new TextPropertyDescriptor(
          XSDConstants.MAXOCCURS_ATTRIBUTE,
          XSDConstants.MAXOCCURS_ATTRIBUTE);
      list.add(maxOccursDescriptor);
    }
    XSDComboBoxPropertyDescriptor nillableDescriptor =
      new XSDComboBoxPropertyDescriptor(
        XSDConstants.NILLABLE_ATTRIBUTE,
        XSDConstants.NILLABLE_ATTRIBUTE,
        trueFalseComboValues);
    list.add(nillableDescriptor);
    XSDComboBoxPropertyDescriptor blockDescriptor =
      new XSDComboBoxPropertyDescriptor(
        XSDConstants.BLOCK_ATTRIBUTE,
        XSDConstants.BLOCK_ATTRIBUTE,
        blockComboValues);
    list.add(blockDescriptor);
    if (isGlobalElement)
    {      
      XSDComboBoxPropertyDescriptor finalDescriptor =
        new XSDComboBoxPropertyDescriptor(
          XSDConstants.FINAL_ATTRIBUTE,
          XSDConstants.FINAL_ATTRIBUTE,
          finalComboValues);
      list.add(finalDescriptor);
      XSDComboBoxPropertyDescriptor substitutionGroupDescriptor =
        new XSDComboBoxPropertyDescriptor(
          XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE,
          XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE,
          substitutionGroupComboValues);
      list.add(substitutionGroupDescriptor);
    }
    if (!isGlobalElement)
    {
      XSDComboBoxPropertyDescriptor formDescriptor =
        new XSDComboBoxPropertyDescriptor(
          XSDConstants.FORM_ATTRIBUTE,
          XSDConstants.FORM_ATTRIBUTE,
          formComboValues);
      list.add(formDescriptor);
    }

    Attr fixedAttr = element.getAttributeNode(XSDConstants.FIXED_ATTRIBUTE);
    Attr defaultAttr = element.getAttributeNode(XSDConstants.DEFAULT_ATTRIBUTE);
    String str;
    if (fixedAttr != null)
    {
      str = XSDConstants.FIXED_ATTRIBUTE;
    }
    else if (defaultAttr != null)
    {
      str = XSDConstants.DEFAULT_ATTRIBUTE;
    }
    else
    {
      str = XSDConstants.FIXED_ATTRIBUTE + "/" + XSDConstants.DEFAULT_ATTRIBUTE;
    }
    
    FixedOrDefaultTextPropertyDescriptor fixedOrDefaultDescriptor =
      new FixedOrDefaultTextPropertyDescriptor(
        str, 
        str,
        element);
    list.add(fixedOrDefaultDescriptor);

    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
    //    return propertyDescriptors;
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
    {
      String attributeName = (String)id;
      result = element.getAttribute(attributeName);
      if (result == null)
      {
        result = "";
      }
      if (attributeName.equals(XSDConstants.TYPE_ATTRIBUTE))
      {
        boolean isAnonymous = checkForAnonymousType(element);
        if (isAnonymous)
        {
          return "**anonymous**";
        }
        if (result.equals(""))
        {
          result = XSDEditorPlugin.getXSDString("_UI_NO_TYPE");
        }
        return result;
      }
      else if (attributeName.equals(XSDConstants.MAXOCCURS_ATTRIBUTE) 
        || attributeName.equals(XSDConstants.MINOCCURS_ATTRIBUTE)
        || attributeName.equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE)
        || attributeName.equals(XSDConstants.FORM_ATTRIBUTE)
        || attributeName.equals(XSDConstants.ABSTRACT_ATTRIBUTE)
        || attributeName.equals(XSDConstants.NILLABLE_ATTRIBUTE)
        || attributeName.equals(XSDConstants.BLOCK_ATTRIBUTE)
        || attributeName.equals(XSDConstants.FINAL_ATTRIBUTE)
        || attributeName.equals(XSDConstants.FIXED_ATTRIBUTE) 
        || attributeName.equals(XSDConstants.DEFAULT_ATTRIBUTE)
        || attributeName.equals(XSDConstants.NAME_ATTRIBUTE))
      {
        return result;
      }
    }
    return "";
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
  public boolean isPropertySet(Object id)
  {
    return false;
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
  public void resetPropertyValue(Object id)
  {
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
	 *      java.lang.Object)
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
      
      if (attributeName.equals(XSDConstants.MAXOCCURS_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MAXOCCURS_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.MINOCCURS_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MINOCCURS_CHANGE"), element);      
      }
      else if (attributeName.equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_SUBSTITUTIONGROUP_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.FORM_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_FORM_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.ABSTRACT_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_ABSTRACT_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.NILLABLE_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_NILLABLE_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.BLOCK_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_BLOCK_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.FINAL_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_FINAL_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.FIXED_ATTRIBUTE) || attributeName.equals(XSDConstants.DEFAULT_ATTRIBUTE))
      {  
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_VALUE_CHANGE"), element);
      }
      else if (attributeName.equals(XSDConstants.NAME_ATTRIBUTE))
      {
        if (validateName(newValue))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_NAME_CHANGE"), element);
          // now rename any references to this element
          if (xsdSchema != null)
          {
            XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
            if (comp != null && comp instanceof XSDElementDeclaration && comp.getContainer().equals(xsdSchema))
            {
              GlobalElementRenamer renamer = new GlobalElementRenamer((XSDNamedComponent)comp, newValue);
              renamer.visitSchema(xsdSchema);
            }
          }
        }
      }
      else if (attributeName.equals(XSDConstants.TYPE_ATTRIBUTE))
      {
        // put logic in descriptor/cell editor
//          beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_TYPE_CHANGE"), element);
      }
    
      if (newValue.length() > 0)
      {
        element.setAttribute((String) id, (String) value);
      }
      else
      {
        if (!attributeName.equals(XSDConstants.NAME_ATTRIBUTE))
        {
          element.removeAttribute((String) id);
        }
      }
      endRecording(element);
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

  boolean checkForAnonymousType(Element element)
  {
    /* Using Ed's model to check
     boolean isAnonymous = false;

     XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(element);
     if (component instanceof XSDElementDeclaration)
     {
     XSDElementDeclaration xsdElem = (XSDElementDeclaration)component;
     isAnonymous = xsdElem.isSetAnonymousTypeDefinition();
     }
     return isAnonymous;
     */

    boolean isAnonymous = false;

    Node aNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      return true;
    }
    aNode = getDomHelper().getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      isAnonymous = true;
    }
    return isAnonymous;
  }
  
  
//  void updateElementToAnonymous(Element element, String xsdType)
//  {
//    String prefix = element.getPrefix();
//    prefix = (prefix == null) ? "" : (prefix + ":");
//
//    updateElementToNotAnonymous(element);
//    boolean hasChildrenElements = hasElementChildren(element);
//
//    Element childNode = null;
//    if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
//     {
//      childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
//    }
//    else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG))
//     {
//      childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
//    }
//
//    element.appendChild(childNode);
//    formatChild(childNode, hasChildrenElements);    
//
//
//    /* Using Ed's model to do the above
//     XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(element);
//     if (component instanceof XSDElementDeclaration)
//     {
//     XSDElementDeclaration xsdElem = (XSDElementDeclaration)component;
//     XSDFactoryImpl factory = new XSDFactoryImpl();
//     XSDComplexTypeDefinition complex = factory.createXSDComplexTypeDefinition();
//     XSDSimpleTypeDefinition simple = factory.createXSDSimpleTypeDefinition();
//
//     Node child = element.getFirstChild();
//     if (XSDDOMHelper.inputEquals(child, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false) ||
//     XSDDOMHelper.inputEquals(child, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
//     {
//     element.removeChild(child);
//     }
//     
//     FormatProcessor formatProcessor = new FormatProcessor();
//     if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
//     {
//     xsdElem.setAnonymousTypeDefinition(complex);
//     Element elem = complex.getElement();
//     formatProcessor.formatWithSiblingIndent((XMLNode)elem);
//     }
//     else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG))
//     {
//     xsdElem.setAnonymousTypeDefinition(simple);
//     Element elem = simple.getElement();  
//     formatProcessor.formatWithSiblingIndent((XMLNode)elem);
//     }
//     }
//     component.updateElement();
//     */
//  }
//
//  boolean isSTAnonymous(Element element)
//  {
//    Node aNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
//    if (aNode != null)
//     {
//      if (XSDDOMHelper.inputEquals(aNode, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
//       {
//        return true;
//      }
//    }
//    return false;
//  }  
//
//  boolean isCTAnonymous(Element element)
//  {
//    Node aNode = getDomHelper().getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
//    if (aNode != null)
//     {
//      if (XSDDOMHelper.inputEquals(aNode, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
//       {
//        return true;
//      }
//    }
//    return false;
//  }  
//
//  XSDTypeDefinition getAnonymousTypeDefinition(Element element)
//  {
//    Node typeDefinitionNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
//    if (typeDefinitionNode == null)
//     {
//      typeDefinitionNode = getDomHelper().getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
//    }
//    if (typeDefinitionNode != null)
//     {
//      XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(typeDefinitionNode);
//      if (component instanceof XSDTypeDefinition)
//       {
//        return (XSDTypeDefinition)component;
//      }
//    }
//    return null;    
//
//    /*    XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(element);
//     if (component instanceof XSDElementDeclaration)
//     {
//     XSDElementDeclaration xsdElem = (XSDElementDeclaration)component;
//
//     return xsdElem.getAnonymousTypeDefinition();
//     }
//     return null;
//     */
//  }
//
//  void updateElementToNotAnonymous(Element element)
//  {
//    NodeList children = element.getChildNodes();
//    if (children != null)
//     {
//      for (int i = 0; i < children.getLength(); i++)
//       {
//        Node node = (Node)children.item(i);
//        if (node instanceof Element)
//         {
//          if (node.getLocalName().equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG) ||
//              node.getLocalName().equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
//           {
//            XSDDOMHelper.removeNodeAndWhitespace(node);
//            i=0;
//          }
//        }
//      }
//    }
//    /*    XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(element);
//     if (component instanceof XSDElementDeclaration)
//     {
//     XSDElementDeclaration xsdElem = (XSDElementDeclaration)component;
//     if (xsdElem.isSetAnonymousTypeDefinition())
//     {
//     xsdElem.unsetAnonymousTypeDefinition();
//     xsdElem.setAnonymousTypeDefinition(null);
//     }
//     }
//     component.updateElement();
//     */
//  }
  
}
