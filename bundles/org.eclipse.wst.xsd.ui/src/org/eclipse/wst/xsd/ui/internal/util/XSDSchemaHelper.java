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
package org.eclipse.wst.xsd.ui.internal.util;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xsd.XSDConcreteComponent;

// TODO: KC remove this

public class XSDSchemaHelper
{
  /**
   * Constructor for XSDSchemaHelper.
   */
  public XSDSchemaHelper()
  {
    super();
  }
  
  public static void updateElement(XSDConcreteComponent concreteComp)
  {
    try
    {
      concreteComp.updateElement();
    }
    catch (Exception e)
    {
      for (Iterator containments = concreteComp.eClass().getEAllReferences().iterator(); containments.hasNext(); )
      {
        EReference eReference = (EReference)containments.next();
        if (eReference.isContainment())
        {
          if (eReference.isMany())
          {
            for (Iterator objects = ((Collection)concreteComp.eGet(eReference)).iterator(); objects.hasNext(); )
            {
              XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)objects.next();
              try
              {
                xsdConcreteComponent.updateElement();
              }
              catch (Exception ex) {}
            }
          }
          else
          {
            XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)concreteComp.eGet(eReference);
            if (xsdConcreteComponent != null)
            {
              try
              {
                xsdConcreteComponent.updateElement();
              }
              catch (Exception ex) {}
            }
          }
        }
      }
    }
  }    
}
