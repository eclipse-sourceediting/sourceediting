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
package org.eclipse.wst.xsd.ui.internal.adt.design.directedit;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.INamedEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;

public class LabelCellEditorLocator implements CellEditorLocator
{
  protected INamedEditPart namedEditPart;
  protected Point cursorLocation;

  public LabelCellEditorLocator(INamedEditPart namedEditPart, Point cursorLocation)
  {
    this.namedEditPart = namedEditPart;
    this.cursorLocation = cursorLocation;
  }

  public void relocate(CellEditor celleditor)
  {
    Text text = (Text) celleditor.getControl();

    Label label = namedEditPart.getNameLabelFigure();
    if (text.getBounds().x <= 0)
    {
      int widthToRemove = 0;
      // HACK 
      if (namedEditPart instanceof BaseFieldEditPart)
      {
        BaseFieldEditPart field = (BaseFieldEditPart)namedEditPart;
        IFieldFigure fieldFigure = field.getFieldFigure();     
        widthToRemove = fieldFigure.getTypeLabel().getBounds().width;       
        //TODO: !! perhaps the IFieldFigure should just have a method to compute this?
        //Label typeAnnotationLabel = ((FieldFigure) field.getFigure()).getTypeAnnotationLabel();
        //Label nameAnnotationLabel = ((FieldFigure) field.getFigure()).getNameAnnotationLabel();
        //widthToRemove = typeLabel.getBounds().width + typeAnnotationLabel.getBounds().width + nameAnnotationLabel.getBounds().width;
      }
     
      Rectangle boundingRect = label.getTextBounds();

      // Reduce the width by the amount we shifted along the x-axis
      int delta = Math.abs(boundingRect.x - label.getParent().getBounds().x);

      label.getParent().translateToAbsolute(boundingRect);
      org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
      boundingRect.translate(trim.x, trim.y);
      boundingRect.height = boundingRect.height - trim.y;

      boundingRect.width = label.getParent().getBounds().width - delta - widthToRemove;
      text.setBounds(boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height);

      // Translate point
      if (cursorLocation != null) {
    	  Point translatedPoint = new Point(cursorLocation.x - boundingRect.x, cursorLocation.y - boundingRect.y);

    	  // Calculate text offset corresponding to the translated point
    	  text.setSelection(0, 0);
    	  int xCaret = text.getCaretLocation().x;
    	  int offset = text.getCaretPosition();
    	  while (xCaret < translatedPoint.x)
    	  {
    		  text.setSelection(offset + 1, offset + 1);
    		  xCaret = text.getCaretLocation().x;
    		  int newOffset = text.getCaretPosition();
    		  if (newOffset == offset)
    		  {
    			  break;
    		  }
    		  offset++;
    	  }
    	  text.setSelection(offset, offset);
      }
    }
  }
}
