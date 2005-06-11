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
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDWildcard;

public class MinMaxSectionDescriptor extends AbstractSectionDescriptor
{
  /**
   * 
   */
  public MinMaxSectionDescriptor()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return "org.eclipse.wst.xsdeditor.section.minmax";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getInputTypes()
   */
  public List getInputTypes()
  {
    List list = new ArrayList();
    list.add(XSDElementDeclaration.class);
    list.add(XSDModelGroup.class);
    list.add(XSDWildcard.class);
    return list;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getSectionClass()
   */
  public ISection getSectionClass()
  {
    return new MinMaxSection();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getTargetTab()
   */
  public String getTargetTab()
  {
    return "org.eclipse.wst.xmlwebservices.general";
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
//      if (object instanceof XSDElementDeclaration)
//      {
//        Element element = ((XSDElementDeclaration)object).getElement();
//        Object parentNode = element.getParentNode();
//        // minOccurs and maxOccurs apply to non-global elements
//        boolean isGlobalElement = XSDDOMHelper.inputEquals(parentNode, XSDConstants.SCHEMA_ELEMENT_TAG, false);
//        return !isGlobalElement;
//      }
//      if (object instanceof XSDParticle)
//      {
//        XSDParticle particle = (XSDParticle)object;
//        Element element = particle.getElement();
//        if (inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, false))
//        {
//          return true;
//        }
//        else if (inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, true))
//        {
//          return false;
//        }
//        else
//        {
//          return true;
//        }
//      }
      if (object instanceof XSDModelGroup)
      {
        return true;
      }
      else if (object instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)object;
        if (xsdElementDeclaration.isGlobal())
        {
          return false;
        }
        else
        {
          return true;
        }
      }
      else if (object instanceof XSDWildcard)
      {
        XSDWildcard wildcard = (XSDWildcard)object;
        if (wildcard.getContainer() instanceof XSDComplexTypeDefinition ||
            wildcard.getContainer() instanceof XSDAttributeGroupDefinition)
        {
          return false;
        }
        else
        {
          return true;
        }
      }
    }
    return false;
  }
  
  public String getAfterSection()
  {
    return "org.eclipse.wst.wsdleditor.section.reference";
  }

}
