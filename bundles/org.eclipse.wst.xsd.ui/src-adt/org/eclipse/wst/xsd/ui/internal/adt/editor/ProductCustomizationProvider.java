/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.wst.xsd.ui.internal.adt.outline.ExtensibleContentOutlinePage;

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

  public ExtensibleContentOutlinePage getProductContentOutlinePage()
  {
    return null;
  }
}
