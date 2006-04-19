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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;

public class AbstractSectionDescriptor implements ISectionDescriptor
{
  /**
   * 
   */
  public AbstractSectionDescriptor()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
   */
  public String getId()
  {
    return ""; //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getFilter()
   */
  public IFilter getFilter()
  {
    return null;
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
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getTargetTab()
   */
  public String getTargetTab()
  {
    return null;
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
      if (object instanceof XSDConcreteComponent || object instanceof Element)
      {
        return true;
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getAfterSection()
   */
  public String getAfterSection()
  {
    return ""; //$NON-NLS-1$
  }

  
  public int getEnablesFor()
  {
	return ENABLES_FOR_ANY;
  }
}
