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
package org.eclipse.wst.xsd.editor.internal.design.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.wst.xsd.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.editor.internal.design.figures.SpacingFigure;

public class TargetConnectionSpacingFigureEditPart extends BaseEditPart
{
  public TargetConnectionSpacingFigureEditPart()
  {
    super();
  }

  SpacingFigure figure;

  protected IFigure createFigure()
  {
    figure = new SpacingFigure();
    return figure;
  }

  public IFigure getConnectionFigure()
  {
    return figure;
  }

  protected void createEditPolicies()
  {

  }
}
