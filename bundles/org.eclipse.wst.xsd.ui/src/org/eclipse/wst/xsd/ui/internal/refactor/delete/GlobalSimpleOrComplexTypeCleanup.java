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
package org.eclipse.wst.xsd.ui.internal.refactor.delete;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class GlobalSimpleOrComplexTypeCleanup extends BaseGlobalCleanup
{
  /**
   * Constructor for GlobalSimpleOrComplexTypeCleanup.
   * @param deletedItem
   */
  public GlobalSimpleOrComplexTypeCleanup(XSDConcreteComponent deletedItem)
  {
    super(deletedItem);
  }

  /**
   * @see XSDVisitor#visitElementDeclaration(XSDElementDeclaration)
   */
  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    if (!element.isElementDeclarationReference() &&
        deletedItem.equals(element.getTypeDefinition()))
    {
      resetTypeToString(element.getElement());
      
      String msg = "";
      if (element.isGlobal())
      {
        // KCPort String pattern = XSDPlugin.getSchemaString("_INFO_RESET_GLOBAL_ELEMENT");
				String pattern = "_INFO_RESET_GLOBAL_ELEMENT";
        Object args[] = {element.getName()};
        msg = MessageFormat.format(pattern, args);
      }
      else
      {
        // KCPort msg = XSDPlugin.getSchemaString("_INFO_RESET_ELEMENT");
				msg = "_INFO_RESET_ELEMENT";
        // KCPort msg += "<" + element.getName() + "> " + XSDPlugin.getSchemaString("_UI_TO_TYPE_STRING");
				msg += "<" + element.getName() + "> " + "_UI_TO_TYPE_STRING";
      }
      addMessage(msg, element);
    }


    super.visitElementDeclaration(element);
  }

  /**
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitComplexTypeDefinition(XSDComplexTypeDefinition)
   */
  public void visitComplexTypeDefinition(XSDComplexTypeDefinition type)
  {
    super.visitComplexTypeDefinition(type);
    if (type.getAttributeContents() != null)
    {
      for (Iterator iter = type.getAttributeContents().iterator(); iter.hasNext(); )
      {
        XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent) iter.next();
        if (attrGroupContent instanceof XSDAttributeUse)
        {
          XSDAttributeUse attrUse = (XSDAttributeUse) attrGroupContent;
          XSDAttributeDeclaration attrDecl = attrUse.getContent();
          
          // now is this a reference?
          if (attrDecl != null &&
              !attrDecl.isAttributeDeclarationReference() &&
              deletedItem.equals(attrDecl.getTypeDefinition()))
          {
              resetTypeToString(attrDecl.getElement());
              // reset the type of the attribute decl to string
              // KCPort String msg = XSDPlugin.getSchemaString("_INFO_RESET_ATTRIBUTE") +
						  String msg = "_INFO_RESET_ATTRIBUTE" +
                          " <" + attrDecl.getName() + "> " +
                          // KCPort XSDPlugin.getSchemaString("_UI_TO_TYPE_STRING");
						               "_UI_TO_TYPE_STRING"; 
              addMessage(msg, attrDecl);
              resetTypeToString(attrDecl.getElement());
          }
        }
      }
    }
    XSDTypeDefinition base = type.getBaseTypeDefinition();
    if (base != null && base.equals(deletedItem))
    {
      XSDDOMHelper helper = new XSDDOMHelper();
      Element derivedByNode = helper.getDerivedByElement(type.getElement());

      // KCPort String msg = XSDPlugin.getSchemaString("_INFO_RESET_COMPLEX_TYPE") +
			String msg = "_INFO_RESET_COMPLEX_TYPE" +
              " <" + getNamedComponentName(type) + "> " +
              // KCPort XSDPlugin.getSchemaString("_UI_DERIVATION");
			        "_UI_DERIVATION";
      addMessage(msg, type);
      
      type.setBaseTypeDefinition(null);

      java.util.List listOfCT = schema.getTypeDefinitions();
      XSDTypeDefinition typeDefinition = null;
      if (listOfCT.size() > 0)
      {
        typeDefinition = (XSDTypeDefinition)(listOfCT).get(0);
        type.setBaseTypeDefinition(typeDefinition);
      }

//      type.setBaseTypeDefinition(schema.getSchemaForSchema().resolveSimpleTypeDefinition("string"));
    }
  }



  protected void resetTypeToString(Element element)
  {
    String prefix = element.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    
    element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, prefix + "string"); 
  }
  
}
