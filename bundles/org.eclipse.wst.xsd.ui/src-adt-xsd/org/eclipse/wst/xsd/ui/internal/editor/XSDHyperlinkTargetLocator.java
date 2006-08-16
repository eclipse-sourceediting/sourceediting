/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.List;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSwitch;

/**
 * A custom XSDSwitch used to locate the "referenced" component. Used by the
 * hyperlink/F3 navigation mechanism. Made a separate class because it is used
 * from the WSDL editor as well.
 */
public class XSDHyperlinkTargetLocator extends XSDSwitch
{
  /**
   * Holds the attribute name if the cursor/mouse is over an attribute.
   */
  private String attributeName;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDAttributeDeclaration(org.eclipse.xsd.XSDAttributeDeclaration)
   */
  public Object caseXSDAttributeDeclaration(XSDAttributeDeclaration attributeDeclaration)
  {
    XSDConcreteComponent target = null;

    if (attributeDeclaration.isAttributeDeclarationReference())
    {
      target = attributeDeclaration.getResolvedAttributeDeclaration();
    }
    else if (attributeDeclaration.getAnonymousTypeDefinition() == null)
    {
      target = attributeDeclaration.getTypeDefinition();

      // Avoid navigating to built in data types.

      if (isFromSchemaForSchema(target))
      {
        target = null;
      }
    }
    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDAttributeGroupDefinition(org.eclipse.xsd.XSDAttributeGroupDefinition)
   */
  public Object caseXSDAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroupDefinition)
  {
    XSDConcreteComponent target = null;

    if (attributeGroupDefinition.isAttributeGroupDefinitionReference())
    {
      target = attributeGroupDefinition.getResolvedAttributeGroupDefinition();
    }

    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDElementDeclaration(org.eclipse.xsd.XSDElementDeclaration)
   */
  public Object caseXSDElementDeclaration(XSDElementDeclaration elementDeclaration)
  {
    XSDConcreteComponent target = null;

    if (elementDeclaration.isElementDeclarationReference())
    {
      target = elementDeclaration.getResolvedElementDeclaration();
    }
    else
    {
      XSDConcreteComponent typeDefinition = null;

      if (elementDeclaration.getAnonymousTypeDefinition() == null)
      {
        typeDefinition = elementDeclaration.getTypeDefinition();
      }

      XSDConcreteComponent substitutionGroupAffiliation = elementDeclaration.getSubstitutionGroupAffiliation();

      if (typeDefinition != null && substitutionGroupAffiliation != null)
      {
        // There are 2 things we can navigate to: if the cursor is anywhere on
        // the
        // substitution attribute then jump to that, otherwise just go to the
        // base type.

        if (XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE.equals(attributeName))
        {
          target = substitutionGroupAffiliation;
        }
        else
        {
          target = typeDefinition;
        }
      }
      else
      {
        target = typeDefinition != null ? typeDefinition : substitutionGroupAffiliation;
      }

      if (isFromSchemaForSchema(target))
      {
        target = null;
      }
    }

    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDIdentityConstraintDefinition(org.eclipse.xsd.XSDIdentityConstraintDefinition)
   */
  public Object caseXSDIdentityConstraintDefinition(XSDIdentityConstraintDefinition idConstraintDefinition)
  {
    Object target = null;

    XSDIdentityConstraintDefinition referencedKey = idConstraintDefinition.getReferencedKey();

    if (referencedKey != null)
    {
      target = referencedKey;
    }

    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDModelGroupDefinition(org.eclipse.xsd.XSDModelGroupDefinition)
   */
  public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition modelGroupDefinition)
  {
    XSDConcreteComponent target = null;

    if (modelGroupDefinition.isModelGroupDefinitionReference())
    {
      target = modelGroupDefinition.getResolvedModelGroupDefinition();
    }

    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDSchemaDirective(org.eclipse.xsd.XSDSchemaDirective)
   */
  public Object caseXSDSchemaDirective(XSDSchemaDirective directive)
  {
    XSDSchema schema = directive.getResolvedSchema();

    return schema;
  }

  public Object caseXSDSimpleTypeDefinition(XSDSimpleTypeDefinition typeDefinition)
  {
    XSDConcreteComponent target = null;

    // Simple types can be one of: atomic, list or union.

    XSDVariety variety = typeDefinition.getVariety();
    int varietyType = variety.getValue();

    switch (varietyType)
    {
      case XSDVariety.ATOMIC:
        {
          target = typeDefinition.getBaseTypeDefinition();
        }
        break;
      case XSDVariety.LIST:
        {
          target = typeDefinition.getItemTypeDefinition();
        }
        break;
      case XSDVariety.UNION:
        {
          List memberTypes = typeDefinition.getMemberTypeDefinitions();
          if (memberTypes != null && memberTypes.size() > 0)
          {
            // ISSUE: What if there are more than one type?
            // This could be a case for multiple hyperlinks at the same
            // location.
            target = (XSDConcreteComponent) memberTypes.get(0);
          }
        }
        break;
    }

    // Avoid navigating to built in data types.

    if (isFromSchemaForSchema(target))
    {
      target = null;
    }

    return target;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.xsd.util.XSDSwitch#caseXSDTypeDefinition(org.eclipse.xsd.XSDTypeDefinition)
   */
  public Object caseXSDTypeDefinition(XSDTypeDefinition typeDefinition)
  {
    XSDConcreteComponent target = null;

    XSDTypeDefinition baseType = typeDefinition.getBaseType();

    if (baseType != null)
    {
      target = baseType;
    }

    // Avoid navigating to built in data types.

    if (isFromSchemaForSchema(target))
    {
      target = null;
    }

    return target;
  }

  /**
   * Detects if a given schema component is from the schema for schema (built in
   * data types). Used to avoid navigating to this type of components as they
   * don't have an accessible physical location.
   * 
   * @param component the component to check.
   * @return true if the component is from the schema for schema namespace,
   *         false otherwise.
   */
  public boolean isFromSchemaForSchema(XSDConcreteComponent component)
  {
    if (component == null)
    {
      return false;
    }

    XSDSchema schema = component.getSchema();

    if (schema != null && schema.equals(schema.getSchemaForSchema()))
    {
      return true;
    }

    return false;
  }

  /**
   * Locates the target component - for example the element declaration pointed
   * to by an element reference, etc.
   * 
   * @param component the current component.
   * @param attributeName the attribute name if the cursor/mouse is over an
   *        attribute. This is used to provide fine grained navigation for
   *        components with more than one "active" attribute.
   * @return the referenced XSD concrete component or null if none is found.
   */
  public XSDConcreteComponent locate(XSDConcreteComponent component, String attributeName)
  {
    this.attributeName = attributeName;

    return (XSDConcreteComponent) doSwitch(component);
  }
}
