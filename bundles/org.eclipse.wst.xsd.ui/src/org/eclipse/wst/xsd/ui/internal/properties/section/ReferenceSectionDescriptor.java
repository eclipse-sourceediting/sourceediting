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
import org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ReferenceSectionDescriptor extends AbstractSectionDescriptor implements ISectionDescriptor
{
  ReferenceSection referenceSection;
  
  /**
   * 
   */
  public ReferenceSectionDescriptor()
  {
    super();
    this.referenceSection = new ReferenceSection();
  }


  /**
   * 
   */

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return "org.eclipse.wst.wsdleditor.section.reference";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getInputTypes()
   */
  public List getInputTypes()
  {
    List list = new ArrayList();
    list.add(XSDNamedComponent.class);
    list.add(XSDElementDeclaration.class);
    list.add(XSDModelGroupDefinition.class);
    list.add(XSDAttributeDeclaration.class);
    list.add(XSDAttributeUse.class);
    return list;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getSectionClass()
   */
  public ISection getSectionClass()
  {
    return referenceSection;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getTargetTab()
   */
  public String getTargetTab()
  {
    return "com.ibm.xmlwebservices.general";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#appliesTo(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public boolean appliesTo(IWorkbenchPart part, ISelection selection)
  {
    if (part != null)
    {
      referenceSection.setEditorPart(part.getSite()
            .getWorkbenchWindow()
              .getActivePage()
                .getActiveEditor());
    }
    Object object = null;
    if (selection instanceof StructuredSelection)
    {
      StructuredSelection structuredSelection = (StructuredSelection)selection;
      object = structuredSelection.getFirstElement();
      if (object instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent)object;
        Element element = namedComponent.getElement();
        if (element == null)
        {
          return false;
        }
        if (inputEquals(element, element.getLocalName(), true))
        {
          return true;
        }
        else
        {
          return false;
        }
      }
      else if (object instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)object;
        if (elementDeclaration.isElementDeclarationReference())
        {
          return true;
        }
        else
        {
          return false;
        }
      }
      else if (object instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration attr = (XSDAttributeDeclaration)object;
        if (attr.isAttributeDeclarationReference())
        {
          return true;
        }
        else
        {
          return false;
        }
      }
      else if (object instanceof XSDModelGroupDefinition)
      {
        XSDModelGroupDefinition group = (XSDModelGroupDefinition)object;
        if (group.isModelGroupDefinitionReference())
        {
          return true;
        }
        else
        {
          return false;
        }
      }
      else if (object instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)object;
        XSDAttributeDeclaration attribute = attributeUse.getAttributeDeclaration();
        Element element = attributeUse.getElement();
        if (inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
        {
          return true;
        }
        else
        {
          return false;
        }
      }
      
    }
    return false;
  }

}
