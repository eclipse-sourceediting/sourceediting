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
package org.eclipse.wst.xsd.ui.internal.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * @author ernest
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XSDSubstitutionGroupChildUtility
{

  /**
   * @param declaration
   * @return List
   */
  public static List getModelChildren(XSDElementDeclaration declaration)
  {
    ArrayList children = new ArrayList();
    List substitutionGroup = declaration.getSubstitutionGroup();
    for (int i = 0, size = substitutionGroup.size(); i < size; i++)
    {
      XSDElementDeclaration element = (XSDElementDeclaration) substitutionGroup.get(i);
      if (declaration.equals(element.getSubstitutionGroupAffiliation()))
      {
        children.add(element);
      }
    }
    return children;
    
  }
}
