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
package org.eclipse.wst.xsd.ui.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDTypeDefinition;

public class AddAttributeDeclarationCommand extends AbstractCommand
{
  XSDAttributeDeclaration refAttribute = null;
  /**
   * @param parent
   */
  public AddAttributeDeclarationCommand(XSDConcreteComponent parent)
  {
    super(parent);
  }

  public AddAttributeDeclarationCommand(XSDConcreteComponent parent, XSDAttributeDeclaration ref)
  {
    super(parent);
    this.refAttribute = ref;
  }


  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#run()
   */
  public void run()
  {
    XSDConcreteComponent parent = getParent();
    if (parent instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)parent;

      XSDAttributeDeclaration attribute = XSDFactory.eINSTANCE.createXSDAttributeDeclaration();
      attribute.setName(getNewName("Attribute")); //$NON-NLS-1$
      attribute.setTypeDefinition(ct.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$
      
      XSDAttributeUse attributeUse = XSDFactory.eINSTANCE.createXSDAttributeUse();
      attributeUse.setAttributeDeclaration(attribute);
      attributeUse.setContent(attribute);
      
      if (ct.getAttributeContents() != null)
      {
        ct.getAttributeContents().add(attributeUse);
        formatChild(attribute.getElement());
      }
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#adopt(org.eclipse.xsd.XSDConcreteComponent)
   */
  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }
  
  ArrayList names;
  
  protected String getNewName(String description)
  {
    String candidateName = "New" + description; //$NON-NLS-1$
    StringBuffer candidateNameSB = new StringBuffer("New" + description); //$NON-NLS-1$
    XSDConcreteComponent parent = getParent();
    names = new ArrayList();
    int i = 1;
    if (parent instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)parent;
      walkUpInheritance(ct);

      boolean ready = false;
      while (!ready)
      {
        ready = true;
        for (Iterator iter = names.iterator(); iter.hasNext(); )
        {
          String attrName = (String)iter.next();
          if (candidateName.equals(attrName))
          {
            ready = false;
            candidateName = "New" + description + String.valueOf(i); //$NON-NLS-1$
            i++;
          }
        }
      }
    }
    return candidateName;
  }
  
  private void walkUpInheritance(XSDComplexTypeDefinition ct)
  {
    updateNames(ct);
    XSDTypeDefinition typeDef = ct.getBaseTypeDefinition();
    if (ct != ct.getRootType())
    {
      if (typeDef instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition ct2 = (XSDComplexTypeDefinition)typeDef;
        walkUpInheritance(ct2);
      }
    }
  }

  private void updateNames(XSDComplexTypeDefinition ct)
  {
    Iterator iter = ct.getAttributeContents().iterator();
    while (iter.hasNext())
    {
      Object obj = iter.next();
      if (obj instanceof XSDAttributeUse)
      {
        XSDAttributeUse use = (XSDAttributeUse)obj;
        XSDAttributeDeclaration attr = use.getAttributeDeclaration();
        String attrName = attr.getName();
        if (attrName != null)
        {
          names.add(attrName);
        }
      }
    }

  }
}
