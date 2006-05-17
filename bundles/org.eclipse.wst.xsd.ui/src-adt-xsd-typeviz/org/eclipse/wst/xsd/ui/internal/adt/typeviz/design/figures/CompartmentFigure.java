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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewerGraphicConstants;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.StructureEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.ICompartmentFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.layouts.RowLayout;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;

public class CompartmentFigure extends Figure implements ICompartmentFigure
{
  public Label nodeNameLabel;
  protected Figure contentPane;
  protected Figure annotationArea;
  public Figure rowFigure;

  public CompartmentFigure()
  {
    super();

    rowFigure = new Figure();
    add(rowFigure);

    annotationArea = new Figure() {
      
      public void paint(Graphics graphics)
      {
        super.paint(graphics);
        try
        {
          graphics.pushState();  
          graphics.setForegroundColor(ColorConstants.blue);
          graphics.setFont(DesignViewerGraphicConstants.smallFont);
          List children = getChildren();
          for (Iterator i = children.iterator(); i.hasNext(); )
          {
            Figure object = (Figure)i.next();
            traverse(object, graphics);          
          }
        }
        finally
        {
          graphics.popState();
        }
      }
      
      private void traverse(Figure figure, Graphics graphics)
      {
        List children = figure.getChildren();
        for (Iterator i = children.iterator(); i.hasNext(); )
        {
          Figure object = (Figure)i.next();
          
          if (object instanceof GenericGroupFigure)
          {
            GenericGroupFigure fig = (GenericGroupFigure) object;
            if (fig.hasText())
              graphics.drawText(fig.getText(), fig.getTextCoordinates());
          }
          traverse(object, graphics);
        }
        
      }
      
    };
    ToolbarLayout annotationLayout = new ToolbarLayout(false);
    annotationLayout.setStretchMinorAxis(true);
    annotationArea.setLayoutManager(annotationLayout);

    // Need this to show content model structure on the left side of the figure
    rowFigure.add(annotationArea);

    contentPane = new Figure()
    {
      public void paint(Graphics graphics)
      {
        super.paint(graphics);
        graphics.pushState();
        try
        {
          boolean isFirst = true;
          Color oldColor = graphics.getForegroundColor();
          graphics.setForegroundColor(ColorConstants.lightGray);
          for (Iterator i = getChildren().iterator(); i.hasNext();)
          {
            Figure figure = (Figure) i.next();
            Rectangle r = figure.getBounds();
//            if (figure instanceof FieldFigure)
//            {
//               Rectangle rChild = ((FieldFigure)figure).getNameFigure().getBounds();
//               graphics.drawLine(rChild.right(), rChild.y, rChild.right(), rChild.bottom());
//               graphics.setForegroundColor(ColorConstants.darkGray);
//            }
            if (isFirst)
            {
              isFirst = false;
//               graphics.drawLine(r.x, r.y, r.x, r.y + r.height);
            }
            else
            {
              graphics.setForegroundColor(ColorConstants.white);
              graphics.setBackgroundColor(ColorConstants.lightGray);              
              graphics.fillGradient(r.x, r.y, r.width, 1, false);    
//              graphics.drawLine(r.x, r.y, r.x + r.width, r.y);
//            graphics.drawLine(r.x, r.y, r.x, r.y + r.height);
            }
          }
          graphics.setForegroundColor(oldColor);
        }
        finally
        {
          graphics.popState();
        }
      }
    };
    contentPane.setLayoutManager(new ToolbarLayout());
    rowFigure.add(contentPane);

    RowLayout rowLayout = new RowLayout();
    rowFigure.setLayoutManager(rowLayout);
    rowLayout.setConstraint(annotationArea, "annotation");
    rowLayout.setConstraint(contentPane, "contentPane");
  }

  public IFigure getContentPane()
  {
    return contentPane;
  }

  public IFigure getAnnotationPane()
  {
    return annotationArea;
  }
  
  public void editPartAttached(EditPart owner)
  {   
    StructureEditPart structureEditPart = null;
    for (EditPart parent = owner.getParent(); parent != null; parent = parent.getParent())
    {
      if (parent instanceof StructureEditPart)
      {
        structureEditPart = (StructureEditPart) parent;
        break;
      }
    }
    RowLayout rowLayout = (RowLayout)rowFigure.getLayoutManager();
    IStructureFigure typeFigure = structureEditPart.getStructureFigure();    
    Assert.isTrue(typeFigure instanceof StructureFigure, "Expected object of type StructureFigure");    
    rowLayout.setColumnData(((StructureFigure)typeFigure).getColumnData());            
  }

  public void addSelectionFeedback()
  {
  }

  public void removeSelectionFeedback()
  {
  }   
  
  public void refreshVisuals(Object model)
  {
    // TODO Auto-generated method stub
    
  }
}
