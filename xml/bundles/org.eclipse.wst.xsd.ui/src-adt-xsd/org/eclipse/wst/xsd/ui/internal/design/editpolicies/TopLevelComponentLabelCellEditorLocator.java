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
package org.eclipse.wst.xsd.ui.internal.design.editpolicies;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.LabelCellEditorLocator;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.INamedEditPart;

public class TopLevelComponentLabelCellEditorLocator extends LabelCellEditorLocator
{
  public TopLevelComponentLabelCellEditorLocator(INamedEditPart namedEditPart, Point cursorLocation)
  {
    super(namedEditPart, cursorLocation);
  }

  public void relocate(CellEditor celleditor)
  {
    Text text = (Text) celleditor.getControl();

    Label label = namedEditPart.getNameLabelFigure();
    
    if (text.getBounds().x <= 0)
    {
      super.relocate(celleditor);  
    }
    else
    {
      org.eclipse.swt.graphics.Point sel = text.getSelection();
      org.eclipse.swt.graphics.Point pref = text.computeSize(-1, -1);
      Rectangle rect = label.getTextBounds().getCopy();
      label.translateToAbsolute(rect);
      text.setBounds(rect.x, rect.y-1, rect.width, pref.y+1);
      text.setSelection(0);
      text.setSelection(sel); 
    }
  }
}
