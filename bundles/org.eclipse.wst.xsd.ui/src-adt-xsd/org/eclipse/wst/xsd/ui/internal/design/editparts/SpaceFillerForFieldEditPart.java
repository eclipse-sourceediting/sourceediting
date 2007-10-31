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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class SpaceFillerForFieldEditPart extends BaseFieldEditPart
{
  Label space;
  public SpaceFillerForFieldEditPart()
  {
    super();
  }

  protected IFigure createFigure()
  {
    space = new Label(""); //$NON-NLS-1$
    space.setIcon(XSDEditorPlugin.getXSDImage("icons/Dot.gif")); //$NON-NLS-1$
    space.setBorder(new MarginBorder(3, 0, 3, 0));
    return space;
  }

  protected void refreshVisuals()
  {
  }

  protected void createEditPolicies()
  {

  }
  
  public boolean isSelectable()
  {
    return false;
  }
}
