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
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;


/**
 * Base class for rename helpers of various named root components.  This is to be used when
 * the user renames a global element, group, attribute, attribute group, complex/simple type etc
 */
public abstract class BaseRenamer extends XSDVisitor
{

  
  /**
   * Method BaseRenamer.
   * @param globalComponent - this is the component (who's parent is the schema) that has been renamed
   */
  public BaseRenamer(XSDNamedComponent globalComponent, String newName)
  {
    this.globalComponent = globalComponent;
    this.newName = newName;
  }
  
  public String getNewQName()
  {
    String qName = null;
    if (newName != null)
    {
      qName = XSDConstants.lookupQualifier(globalComponent.getElement(), globalComponent.getTargetNamespace());
      if (qName != null && qName.length() > 0)
      {
        qName += ":" + newName;
      }
      else
      {
        qName = newName; 
      }
    }
    else
    {
      qName = newName;
    }
    
    return qName;
  }
  
  protected String newName;
  protected XSDNamedComponent globalComponent;
}
