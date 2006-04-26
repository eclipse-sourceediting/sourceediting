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
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;


public class StructureFigure extends BoxFigure implements IStructureFigure
{
  public void editPartAttached(EditPart owner)
  {
    // nothing to do here :-)
  }

  public void addSelectionFeedback()
  {
    LineBorder boxFigureLineBorder = (LineBorder)getBorder();
    boxFigureLineBorder.setWidth(2);
    // TODO (cs) need to fix this
    //boxFigureLineBorder.setColor(getComplexType().isReadOnly() ? ColorConstants.darkGray : ColorConstants.darkBlue);  
    getHeadingFigure().setSelected(true);
    repaint();
  }

  public void removeSelectionFeedback()
  {
    LineBorder boxFigureLineBorder = (LineBorder)getBorder();
    boxFigureLineBorder.setWidth(1);
    getHeadingFigure().setSelected(false);
    repaint();
  }  
  
  public boolean hitTestHeader(Point location)
  {
    IFigure target = getHeadingFigure();
    Rectangle b = target.getBounds().getCopy();
    target.translateToAbsolute(b);  
    return b.contains(location);
  }
  
  public void refreshVisuals(Object model)
  {
    IStructure structure = (IStructure)model;
    getNameLabel().setText(structure.getName());
    getHeadingFigure().setIsReadOnly(structure.isReadOnly());
  }
}
