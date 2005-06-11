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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.impl.XSDSchemaImpl;

public class NamespaceSectionDescriptor extends AbstractSectionDescriptor
{
	  NamespaceSection namespaceSection;
	  /**
	   * 
	   */
	  public NamespaceSectionDescriptor()
	  {
	    super();
	    namespaceSection = new NamespaceSection();
	  }

	  /* (non-Javadoc)
	   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
	   */
	  public String getId()
	  {
	    return "org.eclipse.wst.xsdeditor.section.namespaceSection";
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
	    return namespaceSection;
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
	      if (object instanceof XSDSchemaImpl) {
	      	return true;
	      }
	    }
	    
	    return false;
	  }
}
