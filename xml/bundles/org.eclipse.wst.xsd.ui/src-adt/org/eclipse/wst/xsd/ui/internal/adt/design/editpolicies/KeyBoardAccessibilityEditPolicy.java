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
package org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

public class KeyBoardAccessibilityEditPolicy extends GraphicalEditPolicy
{
  public static String KEY = "KeyBoardAccessibilityEditPolicy";
  
  public static int OUT_TO_PARENT = PositionConstants.ALWAYS_LEFT;
  public static int IN_TO_FIRST_CHILD = PositionConstants.ALWAYS_RIGHT;
  
  public EditPart getRelativeEditPart(EditPart editPart, int direction)
  {
    return null;
  }
}
