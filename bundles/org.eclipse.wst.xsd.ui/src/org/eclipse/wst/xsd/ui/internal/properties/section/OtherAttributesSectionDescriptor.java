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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.internal.provisional.ISection;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

public class OtherAttributesSectionDescriptor extends AbstractSectionDescriptor
{
  OtherAttributesSection otherAttributesSection;
  /**
   * 
   */
  public OtherAttributesSectionDescriptor()
  {
    super();
    otherAttributesSection = new OtherAttributesSection();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return "org.eclipse.wst.xsdeditor.section.otherattributes";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getInputTypes()
   */
  public List getInputTypes()
  {
    List list = new ArrayList();
    list.add(XSDConcreteComponent.class);
    return list;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getSectionClass()
   */
  public ISection getSectionClass()
  {
    return otherAttributesSection;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getTargetTab()
   */
  public String getTargetTab()
  {
    return "org.eclipse.wst.xmlwebservices.other";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#appliesTo(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public boolean appliesTo(IWorkbenchPart part, ISelection selection)
  {
    Object object = null;
    if (selection instanceof StructuredSelection)
    {
      StructuredSelection structuredSelection = (StructuredSelection)selection;
      object = structuredSelection.getFirstElement();
      if (object instanceof XSDConcreteComponent)
      {
      	if (object instanceof XSDAttributeGroupDefinition ||
      	    object instanceof XSDAttributeUse ||
      	    object instanceof XSDAttributeDeclaration ||
      	    object instanceof XSDEnumerationFacet ||
      	    object instanceof XSDPatternFacet ||
      	    object instanceof XSDSimpleTypeDefinition ||
      	    object instanceof XSDAnnotation ||
            object instanceof XSDWildcard ||
            object instanceof XSDSchemaDirective)
      	{
     	    return false;
      	}
        else if (object instanceof XSDModelGroup)
        {
          return false;
        }
        else if (object instanceof XSDElementDeclaration)
        {
//        Remove this to fix bug 3870 Element references should have the same properties as elements 
//          if (((XSDElementDeclaration)object).isElementDeclarationReference())
//          {
//            return false;
//          }
          return true;
        }
        else if (object instanceof XSDModelGroupDefinition)
        {
          if (((XSDModelGroupDefinition)object).isModelGroupDefinitionReference())
          {
            return false;
          }
          return false;
        }
        else if (object instanceof XSDIdentityConstraintDefinition)
        {
          XSDIdentityConstraintDefinition constraint = (XSDIdentityConstraintDefinition)object;
          XSDIdentityConstraintCategory category = constraint.getIdentityConstraintCategory();
          if (category.getValue() == XSDIdentityConstraintCategory.KEYREF)
          {
            return true;
          }
          else
          {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
}
