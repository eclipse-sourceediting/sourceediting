/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.viewers.IFilter;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public class XSDFacetSectionFilter implements IFilter
{
  public boolean select(Object toTest)
  {
    if (toTest instanceof XSDFeature)
    {
      XSDTypeDefinition type = ((XSDFeature)toTest).getResolvedFeature().getType();
      if (type instanceof XSDSimpleTypeDefinition)
      {
        return true;
      }
    }
    else if (toTest instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)toTest;
      if (st.eContainer() instanceof XSDSchema ||
          st.eContainer() instanceof XSDFeature)
      {
        return true;
      }
    }
    return false;
  }
}
