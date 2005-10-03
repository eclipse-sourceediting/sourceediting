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
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class NameSectionDescriptor extends AbstractSectionDescriptor implements ISectionDescriptor
{
  /**
   * 
   */
  public NameSectionDescriptor()
  {
    super();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return "org.eclipse.wst.xsdeditor.section.name";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getInputTypes()
   */
  public List getInputTypes()
  {
    List list = new ArrayList();
    list.add(XSDNamedComponent.class);
    list.add(XSDAttributeUse.class);
    return list;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getSectionClass()
   */
  public ISection getSectionClass()
  {
    return new NameSection();
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
      if (object instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent)object;
        Element element = namedComponent.getElement();
        
        if (inputEquals(element, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false) ||
            inputEquals(element, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
        {
          return false;
        }
        
        // don't want to show editable name section for ref's
        // need to show ref section with a combobox
        
        if (namedComponent instanceof XSDElementDeclaration)
        {
          if (((XSDElementDeclaration)namedComponent).isElementDeclarationReference())
          {
            return false;
          }
          else
          {
            return true;
          }
        }
        else if (namedComponent instanceof XSDAttributeDeclaration)
        {
          if (((XSDAttributeDeclaration)namedComponent).isAttributeDeclarationReference())
          {
            return false;
          }
          else
          {
            return true;
          }
        }

        if (element != null)
        {
          if (inputEquals(element, element.getLocalName(), true))
          {
            return false;
          }
          else
          {
            return true;
          }
        }
      }
//      else if (object instanceof XSDParticle)
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
//          return false;
//        }
//      }
      else if (object instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)object;
        Element element = attributeUse.getElement();
        if (inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
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
