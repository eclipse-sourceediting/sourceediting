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
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalSimpleOrComplexTypeRenamer;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ComplexTypePropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String DERIVED_BY_ID = "derived by"; // XSDEditorPlugin.getXSDString("_UI_LABEL_DERIVED_BY");  //$NON-NLS-1$ 
  private String BASE_TYPE_ID = "base type"; //  XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE");  //$NON-NLS-1$
  
  private String[] blockOrFinalComboValues = 
  {
    "",    	//$NON-NLS-1$
    "#all",  //$NON-NLS-1$
    "extension", //$NON-NLS-1$
    "restriction" //$NON-NLS-1$
  };
  
  private String derivedByChoicesComboValues[] =
  {
        "", //$NON-NLS-1$
        XSDConstants.RESTRICTION_ELEMENT_TAG,
        XSDConstants.EXTENSION_ELEMENT_TAG
  };
  
  /**
   * 
   */
  public ComplexTypePropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public ComplexTypePropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public ComplexTypePropertySource(XSDSchema xsdSchema)
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
    boolean isAnonymousType = checkForAnonymousType(element);
// The three properties name, base type and derived by have been moved to the general tab
//    if (isAnonymousType)
//    {
//      PropertyDescriptor nameDescriptor =
//      new PropertyDescriptor(
//          XSDConstants.NAME_ATTRIBUTE,
//          XSDConstants.NAME_ATTRIBUTE);
//      list.add(nameDescriptor);
//    }
//    else
//    {
//      PropertyDescriptor nameDescriptor =
//      new TextPropertyDescriptor(
//          XSDConstants.NAME_ATTRIBUTE,
//          XSDConstants.NAME_ATTRIBUTE);
//      list.add(nameDescriptor);
//    }

    Element contentModelElement = getDomHelper().getContentModelFromParent(element);
//    SimpleContentPropertyDescriptor typeDescriptor = new SimpleContentPropertyDescriptor(
//        BASE_TYPE_ID,
//        BASE_TYPE_ID,
//        contentModelElement, xsdSchema);
//    list.add(typeDescriptor);
//    XSDComboBoxPropertyDescriptor derivedByDescriptor =
//    new XSDComboBoxPropertyDescriptor(
//        DERIVED_BY_ID,
//        DERIVED_BY_ID,
//        derivedByChoicesComboValues);
//    list.add(derivedByDescriptor);
    
    XSDComboBoxPropertyDescriptor abstractDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.ABSTRACT_ATTRIBUTE,
        XSDConstants.ABSTRACT_ATTRIBUTE,
        trueFalseComboValues);
    list.add(abstractDescriptor);

    XSDComboBoxPropertyDescriptor mixedDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.MIXED_ATTRIBUTE,
        XSDConstants.MIXED_ATTRIBUTE,
        trueFalseComboValues);
    list.add(mixedDescriptor);
        
    XSDComboBoxPropertyDescriptor blockDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.BLOCK_ATTRIBUTE,
        XSDConstants.BLOCK_ATTRIBUTE,
        blockOrFinalComboValues);
    list.add(blockDescriptor);
    XSDComboBoxPropertyDescriptor finalDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.FINAL_ATTRIBUTE,
        XSDConstants.FINAL_ATTRIBUTE,
        blockOrFinalComboValues);
    list.add(finalDescriptor);
    
    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
  }

  public void setInput(Element element)
  {
    this.element = element;
    
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
    {
      String attributeName = (String)id;
      result = element.getAttribute((String) id);
      Element contentModelElement = getDomHelper().getContentModelFromParent(element);    
      String baseType = getDomHelper().getBaseType(contentModelElement);
      
      if (result == null)
      {
        result = ""; //$NON-NLS-1$
      }
      
      if (attributeName.equals(DERIVED_BY_ID))
      {
        return getDomHelper().getDerivedByName(contentModelElement);
      }
      else if (attributeName.equals(BASE_TYPE_ID))
      {
        if (baseType != null)
        {
          return baseType;
        }
        else
        {
          return ""; //$NON-NLS-1$
        }
      }
      else if (attributeName.equals(XSDConstants.NAME_ATTRIBUTE))
      {
        String name = element.getAttribute(XSDConstants.NAME_ATTRIBUTE);
        
        boolean isAnonymousType = checkForAnonymousType(element);
        if (isAnonymousType)
        {
          return "**anonymous**";  //$NON-NLS-1$
        }
        else
        {
          return name;
        }
      }
        
      return result;
//      if (((String) id).equals(XSDConstants.ABSTRACT_ATTRIBUTE)
//          || ((String) id).equals(XSDConstants.MIXED_ATTRIBUTE))
//      {
//      }
//      else if (((String) id).equals(XSDConstants.BLOCK_ATTRIBUTE))
//       {
//      }
//      else if (((String) id).equals(XSDConstants.FINAL_ATTRIBUTE))
//      {
//      }
    }
    return ""; //$NON-NLS-1$
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
      value = ""; //$NON-NLS-1$
    }
    if (value instanceof String)
    {
      String newValue = (String)value;
      String attributeName = (String)id;
      
      if (attributeName.equals(XSDConstants.NAME_ATTRIBUTE))
      {
        if (validateName(newValue))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_NAME_CHANGE"), element); //$NON-NLS-1$
          if (newValue.length() > 0)
          {
            // now rename any references to this type
            if (xsdSchema != null)
            {
              XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(element);
              if (comp != null && comp instanceof XSDComplexTypeDefinition && comp.getContainer().equals(xsdSchema))
              {
//                XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)comp;
//                ct.setName(newValue);
                GlobalSimpleOrComplexTypeRenamer renamer = new GlobalSimpleOrComplexTypeRenamer((XSDNamedComponent)comp, newValue);
                renamer.visitSchema(xsdSchema);
              }
            }
            element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
          }
          else
          {
            element.removeAttribute(XSDConstants.NAME_ATTRIBUTE);
          }
          endRecording(element);
        }
      }
      else if (attributeName.equals(DERIVED_BY_ID))
      {
        Element contentModelElement = getDomHelper().getContentModelFromParent(element);
        String baseType = getDomHelper().getBaseType(contentModelElement);
        beginRecording(XSDEditorPlugin.getXSDString("_UI_DERIVEDBY_CHANGE"), element); //$NON-NLS-1$
        Element derivedByElem = getDomHelper().getDerivedByElement(element);
        getDomHelper().changeDerivedByType(contentModelElement, newValue, baseType);
        endRecording(element);
      }
      else if (attributeName.equals(BASE_TYPE_ID))
      {
        String derivedBy = getDomHelper().getDerivedByName(element);
        
        SetBaseTypeAction setBaseTypeAction = new SetBaseTypeAction(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_BASE_TYPE")); //$NON-NLS-1$
        setBaseTypeAction.setXSDSchema(xsdSchema);
        setBaseTypeAction.setComplexTypeElement(element);
        setBaseTypeAction.setType(newValue);
        setBaseTypeAction.setDerivedBy(derivedBy);
        setBaseTypeAction.performAction();

//        handleBaseTypeComboChange(newValue);
        
      }
      else
      {
        if (attributeName.equals(XSDConstants.ABSTRACT_ATTRIBUTE))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_ABSTRACT_CHANGE"), element); //$NON-NLS-1$
        }
        else if (attributeName.equals(XSDConstants.MIXED_ATTRIBUTE))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_MIXED_CHANGE"), element); //$NON-NLS-1$
        }
        else if (attributeName.equals(XSDConstants.BLOCK_ATTRIBUTE))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_BLOCK_CHANGE"), element); //$NON-NLS-1$
        }
        else if (attributeName.equals(XSDConstants.FINAL_ATTRIBUTE))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_COMPLEXTYPE_FINAL_CHANGE"), element); //$NON-NLS-1$
        }
        
        if (newValue.length() > 0)
        {
          element.setAttribute((String) id,  (String)value);
        }
        else
        {
          element.removeAttribute((String) id);
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

  boolean checkForAnonymousType(Element element)
  {
    Object parentElement = (Object)element.getParentNode();
    boolean isAnonymous = false;
    if (parentElement != null)
    {
      if (XSDDOMHelper.inputEquals(parentElement, XSDConstants.ELEMENT_ELEMENT_TAG, false))
      {
        isAnonymous = true; 
      }
    }
    return isAnonymous;
  }
  
  
//  private void handleBaseTypeComboChange(String newType)
//  {
//    String tempChoice = newType;
//    TypesHelper helper = new TypesHelper(xsdSchema);
//    if (helper.getBuiltInTypeNamesList().contains(tempChoice) ||
//        helper.getUserSimpleTypeNamesList().contains(tempChoice))
//    {
//      derivedByCombo.setText(XSDConstants.EXTENSION_ELEMENT_TAG);
//      derivedByCombo.setEnabled(false);
//    }
//    else if (helper.getUserComplexTypeNamesList().contains(tempChoice))
//     {
//      Element contentModelElement = getDomHelper().getContentModelFromParent(element);    
//      String derivedByString = getDomHelper().getDerivedByName(contentModelElement);
//      derivedByCombo.setText(derivedByString);
//      derivedByCombo.setEnabled(true); 
//    }
//    else
//     {
//      derivedByCombo.setText("");
//      derivedByCombo.setEnabled(false); 
//    }
//  }
  
}
