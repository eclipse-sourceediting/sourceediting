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
import org.eclipse.wst.common.ui.properties.ISection;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;

public class AttributesViewSectionDescriptor extends AbstractSectionDescriptor
{
  /**
   * 
   */
  public AttributesViewSectionDescriptor()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return "com.ibm.xsdeditor.section.attributes";
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISectionDescriptor#getInputTypes()
   */
  public List getInputTypes()
  {
    List list = new ArrayList();
    list.add(XSDElementDeclaration.class);
    return list;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISectionDescriptor#getSectionClass()
   */
  public ISection getSectionClass()
  {
    return new AttributesViewSection();
  }

  public boolean appliesTo(IWorkbenchPart part, ISelection selection)
  {
    Object object = null;
    if (selection instanceof StructuredSelection)
    {
      StructuredSelection structuredSelection = (StructuredSelection)selection;
      object = structuredSelection.getFirstElement();
      if (object instanceof XSDElementDeclaration)
      {
// Remove this to fix bug 3870 Element references should have the same properties as elements 
//        XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)object;
//        if (elementDeclaration.isElementDeclarationReference())
//        {
//          return false;
//        }
        return true;
      }
      else if (object instanceof XSDComplexTypeDefinition)
      {
        return true;
      }
    }
    return false;
  }

  
  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISectionDescriptor#getTargetTab()
   */
  public String getTargetTab()
  {
    return "com.ibm.xmlwebservices.attributes";
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISectionDescriptor#getAfterSection()
   */
  public String getAfterSection()
  {
    return "";
  }

}
