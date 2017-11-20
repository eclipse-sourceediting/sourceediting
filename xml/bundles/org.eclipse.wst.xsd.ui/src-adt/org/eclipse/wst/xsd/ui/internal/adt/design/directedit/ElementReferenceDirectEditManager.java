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
package org.eclipse.wst.xsd.ui.internal.adt.design.directedit;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.editor.XSDElementReferenceEditManager;

public class ElementReferenceDirectEditManager extends ReferenceDirectEditManager
{
  public ElementReferenceDirectEditManager(IField parameter, AbstractGraphicalEditPart source, Label label)
  {
    super(parameter, source, label);
  }
  
  protected ComponentReferenceEditManager getComponentReferenceEditManager()
  {
    ComponentReferenceEditManager result = null;
    IEditorPart editor = getActiveEditor();
    if (editor != null)
    {
      result = (ComponentReferenceEditManager)editor.getAdapter(XSDElementReferenceEditManager.class);
    }  
    return result;
  }
}
