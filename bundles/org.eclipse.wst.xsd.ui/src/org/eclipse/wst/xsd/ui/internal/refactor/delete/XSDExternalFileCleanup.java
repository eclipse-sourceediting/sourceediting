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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDExternalFileCleanup extends BaseCleanup
{
  /**
   * Constructor for XSDExternalFileCleanup.
   */
  public XSDExternalFileCleanup(String oldFilename)
  {
    super();
    this.oldFilename = oldFilename;
  }
  
  protected XSDSchema deletedSchema;
  public XSDExternalFileCleanup(XSDSchema deletedSchema)
  {
    this.deletedSchema = deletedSchema;
  }

  protected String oldFilename;
  
  /**
   * @see XSDVisitor#visitElementDeclaration(XSDElementDeclaration)
   */
  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    boolean addMessage = true;
    String schemaLocation = element.getSchema().getSchemaLocation();
    if (schemaLocation!= null)
    {
      if (!schemaLocation.equals(schema.getSchemaLocation()))
      {
        addMessage = false;
      }
    }
    if (element.isElementDeclarationReference())
    {
      if (isFromDeletedSchema(element.getResolvedElementDeclaration()))
      {
        if (addMessage)
        {
          // String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_ELEMENT_REFERENCE") + " <" + getNamedComponentName(element.getContainer()) + ">";
					String msg = "_INFO_REMOVE_ELEMENT_REFERENCE" + " <" + getNamedComponentName(element.getContainer()) + ">";
          addMessage(msg, element.getContainer());
        }
        childrenToRemove.add(element.getElement());
//        Element domElement = element.getElement();
//        domElement.getParentNode().removeChild(domElement);     
      }
    }
    else if (removeType(element))
    {
      String msg = "";
      if (element.isGlobal())
      {
      	// String pattern = XSDPlugin.getSchemaString("_INFO_RESET_GLOBAL_ELEMENT");
				String pattern = "_INFO_RESET_GLOBAL_ELEMENT";
        Object args[] = {element.getName()};
        msg = MessageFormat.format(pattern, args);
      }
      else
      {
      	// msg = XSDPlugin.getSchemaString("_INFO_RESET_ELEMENT");
				msg = "_INFO_RESET_ELEMENT";
      	// msg += "<" + element.getName() + "> " + XSDPlugin.getSchemaString("_UI_TO_TYPE_STRING");
				msg += "<" + element.getName() + "> " + "_UI_TO_TYPE_STRING";
      }
      if (addMessage)
      {
        addMessage(msg, element);
      }
    }


    super.visitElementDeclaration(element);
  }

  protected void resetTypeToString(Element element)
  {
    String prefix = element.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    
    element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, prefix + "string"); 
  }
  
  protected boolean removeType(XSDElementDeclaration element)
  {
  	if (removeType(element.getTypeDefinition()))
  	{
      resetTypeToString(element.getElement());
  	  return true;
  	}
  	return false;
  }
  
  protected boolean isFromDeletedSchema(XSDConcreteComponent component)
  {
    if (component == null)
    {
      return false;
    }
    XSDConcreteComponent root = component.getRootContainer();

    boolean isFromDeletedSchema = false;
    if (deletedSchema.getContents() != null)
    {
      Iterator contents = deletedSchema.getContents().iterator();
      while (contents.hasNext())
      {
        XSDSchemaContent content = (XSDSchemaContent)contents.next();
        if (content instanceof XSDSchemaDirective)
        {
          XSDSchema aSchema = ((XSDSchemaDirective)content).getResolvedSchema();
          if (root != null && root.equals(aSchema))
          {
            isFromDeletedSchema = true;
          }
        }
      }
    }
    if (root != null && root.equals(deletedSchema))
    {
      isFromDeletedSchema = true;
    }
    return isFromDeletedSchema;
  }
      
  /**
   * Remove the type from the element if it belongs to the external file
   */
  protected boolean removeType(XSDTypeDefinition typeDef)
  {
    if (typeDef == null)
    {
      return false;
    }
    return isFromDeletedSchema(typeDef);
  }

  /**
   * @see org.eclipse.wst.xsd.utility.XSDVisitor#visitComplexTypeDefinition(XSDComplexTypeDefinition)
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
          if (attrDecl.isAttributeDeclarationReference())
          {
            if (isFromDeletedSchema(attrDecl.getResolvedAttributeDeclaration()))
            {
              String name = getNamedComponentName(type);
              // String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_ATTRIBUTE_REFERENCE") +
							String msg = "_INFO_REMOVE_ATTRIBUTE_REFERENCE" +
                       " <" + name + ">";
              addMessage(msg, attrDecl.getContainer());
              
              childrenToRemove.add(attrDecl.getElement());
            }
          }
          // otherwise check the type of the attribute and see if it is from the deleted schema
          else
          {
            if (removeType(attrDecl.getTypeDefinition()))
            {
              // reset the type of the attribute decl to string
              // String msg = XSDPlugin.getSchemaString("_INFO_RESET_ATTRIBUTE") +
							String msg = "_INFO_RESET_ATTRIBUTE" +
                          " <" + attrDecl.getName() + "> " +
                          // XSDPlugin.getSchemaString("_UI_TO_TYPE_STRING");
							            "_UI_TO_TYPE_STRING";
              addMessage(msg, attrDecl);
              resetTypeToString(attrDecl.getElement());

            }
          }
        }
        else if (attrGroupContent instanceof XSDAttributeGroupDefinition)
        {
          XSDAttributeGroupDefinition attrGroupDef = (XSDAttributeGroupDefinition) attrGroupContent;
          
          if (isFromDeletedSchema(attrGroupDef.getResolvedAttributeGroupDefinition()))
          {
          	// remove the attribute group reference
            String name = getNamedComponentName(type);
            // String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_ATTRIBUTE_GROUP_REFERENCE") + " <" + name + ">";
						String msg = "_INFO_REMOVE_ATTRIBUTE_GROUP_REFERENCE" + " <" + name + ">";

            addMessage(msg, attrGroupDef.getContainer());
            
            childrenToRemove.add(attrGroupDef.getElement());
          }
        }
      }
    }

    // For the complex type with simple content case, see the visitComplexTypeDefinition method
    XSDTypeDefinition base = type.getBaseTypeDefinition();
    if (base instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition baseType = (XSDSimpleTypeDefinition)base;
      if (isFromDeletedSchema(baseType))
      {
        // String msg = XSDPlugin.getSchemaString("_INFO_RESET_COMPLEX_TYPE") +
				String msg = "_INFO_RESET_COMPLEX_TYPE" +
                " <" + getNamedComponentName(type) + "> " +
                // XSDPlugin.getSchemaString("_UI_DERIVATION");
				        "_UI_DERIVATION";
        addMessage(msg, type);
      
        type.setBaseTypeDefinition(schema.getSchemaForSchema().resolveSimpleTypeDefinition("string"));
      }
    }
  }

  /**
   * @see org.eclipse.wst.xsd.utility.XSDVisitor#visitModelGroupDefinition(XSDModelGroupDefinition)
   */
  public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroup)
  {
    super.visitModelGroupDefinition(modelGroup);
    if (modelGroup.isModelGroupDefinitionReference())
    {
      if (isFromDeletedSchema(modelGroup.getResolvedModelGroupDefinition()))
      {
        String name = getNamedComponentName(modelGroup);
        // String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_GROUP_REFERENCE") + " <" + name + ">";
				String msg = "_INFO_REMOVE_GROUP_REFERENCE" + " <" + name + ">";
        addMessage(msg, modelGroup.getContainer());
        childrenToRemove.add(modelGroup.getElement());
      }
    }      
  }

  /**
   * @see org.eclipse.wst.xsd.utility.XSDVisitor#visitSimpleTypeDefinition(XSDSimpleTypeDefinition)
   */
  public void visitSimpleTypeDefinition(XSDSimpleTypeDefinition type)
  {
    super.visitSimpleTypeDefinition(type);
    XSDSimpleTypeDefinition baseType = type.getBaseTypeDefinition();
    if (isFromDeletedSchema(baseType))
    {
      // String msg = XSDPlugin.getSchemaString("_INFO_RESET_SIMPLE_TYPE") +
			String msg = "_INFO_RESET_SIMPLE_TYPE" +
              " <" + getNamedComponentName(type) + "> " +
              // XSDPlugin.getSchemaString("_UI_DERIVATION");
			        "_UI_DERIVATION";
      addMessage(msg, type);
      

    // This will set the simple Type base to string 
    // For the complex type with simple content case, see the visitComplexTypeDefinition method

      type.getFacetContents().clear();
      type.getFacets().clear();
      type.setBaseTypeDefinition(schema.getSchemaForSchema().resolveSimpleTypeDefinition("string"));
    }
  }

}
