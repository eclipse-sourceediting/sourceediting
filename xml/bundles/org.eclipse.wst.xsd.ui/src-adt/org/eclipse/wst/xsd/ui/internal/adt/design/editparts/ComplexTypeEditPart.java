/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.util.Iterator;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Font;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;

public class ComplexTypeEditPart extends StructureEditPart
{
  private Font italicFont;

  protected boolean shouldDrawConnection()
  {
    if (getParent().getModel() instanceof FocusTypeColumn)
    {  
      IComplexType complexType = (IComplexType)getModel();
      return complexType.getSuperType() != null;
    } 
    return false;
  }
  
  
  private EditPart getTargetEditPart(IType type)
  {
    ColumnEditPart columnEditPart = null;
    for (EditPart editPart = this; editPart != null; editPart = editPart.getParent())
    {
      if (editPart instanceof ColumnEditPart)
      {
        columnEditPart = (ColumnEditPart)editPart;
        break;
      }  
    }     
    if (columnEditPart != null)
    {
      for (Iterator i = columnEditPart.getChildren().iterator(); i.hasNext(); )
      {
        EditPart child = (EditPart)i.next();
        if (child.getModel() == type)
        {
          return child;
        }         
      }  
    }
    return null;
  }
  
  public TypeReferenceConnection createConnectionFigure()
  {
    connectionFigure = null;
    IComplexType complexType = (IComplexType)getModel();
    IType type = complexType.getSuperType();
    if (type != null)
    {      
      AbstractGraphicalEditPart referenceTypePart = (AbstractGraphicalEditPart)getTargetEditPart(type);
      if (referenceTypePart != null)
      {
        connectionFigure = new TypeReferenceConnection(true);
        // draw a line out from the top         
        connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(getFigure(), CenteredConnectionAnchor.TOP, 1));
        
        // TODO (cs) need to draw the target anchor to look like a UML inheritance relationship
        // adding a label to the connection would help to
        connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(referenceTypePart.getFigure(), CenteredConnectionAnchor.BOTTOM, 0, 0));
        connectionFigure.setConnectionRouter(new ManhattanConnectionRouter());
        ((CenteredConnectionAnchor)connectionFigure.getSourceAnchor()).setOther((CenteredConnectionAnchor)connectionFigure.getTargetAnchor());
        connectionFigure.setHighlight(false);
      }
    }    
    return connectionFigure;
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    Label label = getNameLabelFigure();
    IComplexType complexType = (IComplexType)getModel();
    if (complexType.isAbstract())
    {
      if (italicFont == null)
      {
        Font font = label.getFont();
        italicFont = getItalicFont(font);
      }
      if (italicFont != null)
      {
        label.setFont(italicFont);
      }
    }
    else
    {
      label.setFont(label.getParent().getFont());
    }
  }
  
  public void deactivate()
  {
    if (italicFont != null)
    {
      italicFont.dispose();
      italicFont = null;
    }
    super.deactivate();
  }
}