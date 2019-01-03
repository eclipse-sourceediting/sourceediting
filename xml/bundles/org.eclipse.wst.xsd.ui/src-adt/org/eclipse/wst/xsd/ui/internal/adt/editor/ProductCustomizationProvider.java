/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.NavigationLocation;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ExtensibleContentOutlinePage;
import org.eclipse.xsd.XSDConcreteComponent;

public abstract class ProductCustomizationProvider
{
  public abstract boolean isEditorModeApplicable(String id);
  public abstract String getEditorModeDisplayName(String id);
 
  public String getProductString(String id)
  {
    return "";
  }
  
  public String getProductString(String id, Object[] args)
  {
    return "";
  }
  
  public void handleAction(String actionId)
  {    
  }
  
  public NavigationLocation getNavigationLocation(IEditorPart part, XSDConcreteComponent component, BaseEditPart editPart)
  {
    return null;
  }
  
  public ExtensibleContentOutlinePage getProductContentOutlinePage()
  {
    return null;
  }
}
