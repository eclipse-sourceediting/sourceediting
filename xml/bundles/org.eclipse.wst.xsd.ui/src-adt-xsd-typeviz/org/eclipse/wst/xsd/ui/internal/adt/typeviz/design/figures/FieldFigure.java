/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.StructureEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.layouts.RowLayout;

public class FieldFigure extends Figure implements IFieldFigure
{
  // TODO: put this color is some common class
  public static final Color cellColor = new Color(null, 224, 233, 246);

// For fix to https://bugs.eclipse.org/bugs/show_bug.cgi?id=161940
//  public static final Color leftOuterBorderColor = new Color(null, 253, 196, 88);
//  public static final Color leftInnerBorderColor = new Color(null, 253, 226, 172);
//  public static final Color rightOuterBorderColor = new Color(null, 150, 179, 224);
//  public static final Color rightInnerBorderColor = new Color(null, 49, 106, 197);
  
  // Formatting constraints
  public static final int TOP_MARGIN = 2; // pixels
  public static final int BOTTOM_MARGIN = TOP_MARGIN + 1; // extra pixel for the
                                                          // footer line
  public static final int LEFT_MARGIN = 2;
  public static final int RIGHT_MARGIN = LEFT_MARGIN;
  public static final int RIGHT_SIDE_PADDING = 6;

  // States requiring decorators, and their icons
  // protected static final Image errorIcon = ICON_ERROR;

  // Labels which handle presentation of name and type
  public Figure rowFigure;
  protected Label nameLabel;
  protected Label nameAnnotationLabel;  // for occurrence text, or error icons
  protected Label typeLabel;
  protected Label typeAnnotationLabel;  // for occurrence text, or error icons
  protected Label toolTipLabel;
  
  public boolean hasFocus = false;
  
  public FieldFigure()
  {
    super();
    setLayoutManager(new ToolbarLayout());
    rowFigure = new Figure();
    RowLayout rowLayout = new RowLayout();
    rowFigure.setLayoutManager(rowLayout);

    add(rowFigure);

    nameLabel = new Label();
    nameLabel.setBorder(new MarginBorder(3, 5, 3, 5));
    nameLabel.setLabelAlignment(PositionConstants.LEFT);
    nameLabel.setOpaque(true);
    rowFigure.add(nameLabel);
    
    nameAnnotationLabel = new Label();
    nameAnnotationLabel.setBorder(new MarginBorder(3, 5, 3, 5));
    nameAnnotationLabel.setLabelAlignment(PositionConstants.LEFT);
    nameAnnotationLabel.setOpaque(true);
    rowFigure.add(nameAnnotationLabel);
    
    toolTipLabel = new Label();
//  Don't show tooltip for now.  Annoying vertical line shows up.  Safe fix.
//    nameLabel.setToolTip(toolTipLabel);
    typeLabel = new Label();
    
    // cs : we need to add some additional padding to the right
    // so that when we edit the field there's room for the combobox's arrow
    // and the type name won't be partially obscured
    //
    typeLabel.setBorder(new MarginBorder(3, 5, 3, 20));
    typeLabel.setLabelAlignment(PositionConstants.LEFT);
    typeLabel.setOpaque(true);
    rowFigure.add(typeLabel);

    typeAnnotationLabel = new Label() {
      
      public Dimension getPreferredSize(int wHint, int hHint)
      {
        if (getText() == null || getText().equals(""))
        {
          return new Dimension(0, 0);
        }
        return super.getPreferredSize(wHint, hHint);
      }
    };
    typeAnnotationLabel.setBorder(new MarginBorder(3, 5, 3, 5));
    typeAnnotationLabel.setLabelAlignment(PositionConstants.LEFT);
    typeAnnotationLabel.setOpaque(true);
    rowFigure.add(typeAnnotationLabel);
// Don't show tooltip for now.  Annoying vertical line shows up.  Safe fix.
//    typeAnnotationLabel.setToolTip(toolTipLabel);
    
    rowLayout.setConstraint(nameLabel, "name");
    rowLayout.setConstraint(nameAnnotationLabel, "nameAnnotation");
    rowLayout.setConstraint(typeLabel, "type");
    rowLayout.setConstraint(typeAnnotationLabel, "typeAnnotation");
  }

  /**
   * @return Returns the "name" string used by this figure.
   */
  public String getName()
  {
    return nameLabel.getText();
  }

  /**
   * @return Returns the figure representing the attribute name
   */
  public Label getNameLabel()
  {
    return nameLabel;
  }

  /**
   * @return Returns the "type" string used by this figure.
   */
  public String getType()
  {
    return typeLabel.getText();
  }

  /**
   * @return Returns the figure representing the attribute's type
   */
  public Label getTypeLabel()
  {
    return typeLabel;
  }

  /**
   * @param name
   *          Set the "name" string used by this figure.
   */
  public void setName(String name)
  {
    nameLabel.setText(name);
  }

  /**
   * @param type
   *          Set the "type" string used by this figure.
   */
  public void setType(String type)
  {
    typeLabel.setText(type);
  }
  
  public void setTypeToolTipText(String toolTip)
  {
    setNameToolTipText(toolTip);
  }

  public void setNameToolTipText(String toolTip)
  {
    if (toolTip.length() > 0)
    {
      nameLabel.setToolTip(toolTipLabel);
      toolTipLabel.setText(toolTip);
    }
    else
    {
      nameLabel.setToolTip(null);
    }
  }
  
  public void setNameAnnotationLabel(String text)
  {
    nameAnnotationLabel.setText(text);
  }

  public void setNameAnnotationLabelIcon(Image icon)
  {
    nameAnnotationLabel.setIcon(icon);
  }
  
  public Label getNameAnnotationLabel()
  {
    return nameAnnotationLabel;
  }
  
  public void setTypeAnnotationLabel(String text)
  {
    typeAnnotationLabel.setText(text);
  }

  public void setTypeAnnotationLabelIcon(Image icon)
  {
    typeAnnotationLabel.setIcon(icon);
  }

  public Label getTypeAnnotationLabel()
  {
    return typeAnnotationLabel;
  }
  
  public void recomputeLayout()
  {
    RowLayout layout = (RowLayout)rowFigure.getLayoutManager();
    if (layout != null && layout.getColumnData() != null)
    {
      layout.getColumnData().clearColumnWidths();
    }    
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
    boolean highContrast = false;
    try
    {
      highContrast = Display.getDefault().getHighContrast();
    }
    catch (Exception e)
    {
    }
    if (highContrast)
    {
      rowFigure.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      rowFigure.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
    }
    else
    {
      rowFigure.setBackgroundColor(cellColor);
    }
  }
  
  public void removeSelectionFeedback()
  {
    boolean highContrast = false;
    try
    {
      highContrast = Display.getDefault().getHighContrast();
    }
    catch (Exception e)
    {
    }
    if (highContrast)
    {
      rowFigure.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
      rowFigure.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }
    else
    {
      rowFigure.setBackgroundColor(getBackgroundColor());
    }
  }
  
  public void refreshVisuals(Object model)
  {
  }
  
  public void paint(Graphics graphics)
  {
    super.paint(graphics);
    if (hasFocus)
    {
      try
      {
        graphics.pushState();
        Rectangle r = getBounds();
        graphics.setXORMode(true);
        graphics.drawFocus(r.x, r.y + 1, r.width - 1, r.height - 2);
      }
      finally
      {
        graphics.popState();
      }
    }
  }
}
