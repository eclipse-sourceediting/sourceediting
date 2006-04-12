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
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.layouts;

import java.util.HashMap;
import java.util.List;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class RowLayout extends AbstractLayout
{
  // layout is associated with a parent context
  // any layout manager under the parent context is connected
  // column rows are maintained accross container boundaries  
  protected ColumnData columnData;
  protected HashMap figureToContstraintMap = new HashMap();
  
  public RowLayout()
  {
    super();
  }
  

  // this method computes the minimum size required to display the figures
  //
  private Dimension calculateChildrenSize(IFigure container, List children, int wHint, int hHint, boolean preferred)
  {
    Dimension childSize;
    IFigure child;
    int height = 0;
    int width = 0;
    
    //IRowFigure figure = (IRowFigure)container;
    
    // for each cell in the row
    //
    for (int i = 0; i < children.size(); i++)
    {
      child = (IFigure) children.get(i);
      String columnIdenifier = (String)getConstraint(child);
             
      // first we compute the child size without regard for columnData
      //
      childSize = child.getPreferredSize(wHint, hHint);// : child.getMinimumSize(wHint, hHint);
        
      // now that the columnData has been populated we can consider if the row needs to be larger
      //
      int effectiveWidth = childSize.width;
      if (columnIdenifier != null)
      {  
        columnData.stretchColumnWidthIfNeeded(columnIdenifier, childSize.width);
        effectiveWidth = columnData.getColumnWidth(columnIdenifier);
      }                       
      height = Math.max(childSize.height, height);
      width += effectiveWidth;
    }  
    return new Dimension(width, height);
  }
  
  
  
  protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint)
  {    
    List children = container.getChildren();
    Dimension prefSize = calculateChildrenSize(container, children, wHint, hHint, true);
    return prefSize;
  }

  public void layout(IFigure parent)
  {
    // layout a table with the columns aligned      
    //IRowFigure rowFigure = (IRowFigure)parent;    
    Rectangle clientArea = parent.getClientArea();   
    List children = parent.getChildren();
    Rectangle r = new Rectangle();
    r.x = clientArea.x;
    r.y = clientArea.y;
    r.height = clientArea.height;
    
    int childrenSize = children.size();
    Rectangle[] bounds = new Rectangle[childrenSize];
    
    // for each cell in the row
    //
    int requiredWidth = 0;
    int totalColumnWeight = 0;
    for (int i = 0; i < childrenSize; i++)
    {
      IFigure child = (IFigure) children.get(i);
      //String columnIdenifier = figure.getColumnIdentifier(child);             
      // first we compute the child size without regard for columnData
      //
      Dimension childSize = child.getPreferredSize(-1, -1);
      
      int columnWidth = -1;
      //String columnIdentifier = rowFigure.getColumnIdentifier(child);
      String columnIdentifier = (String)getConstraint(child);
      if (columnIdentifier != null)
      {
        //columnData.stretchColumnWidthIfNeeded(columnIdentifier, childSize.width);        
        columnWidth = columnData.getColumnWidth(columnIdentifier);
        totalColumnWeight += columnData.getColumnWeight(columnIdentifier);
      }  
      r.width = Math.max(childSize.width, columnWidth);
      requiredWidth += r.width;
      bounds[i] = new Rectangle(r);      
      r.x += r.width;
    }          
    if (totalColumnWeight < 1)
    {
      totalColumnWeight = 1;
    }
    int extraWidth = Math.max(clientArea.width - requiredWidth, 0);    
    int extraWidthAllocated = 0;
    for (int i = 0; i < childrenSize; i++)
    {
      IFigure child = (IFigure) children.get(i);      
      Rectangle b = bounds[i];    
      if (extraWidth > 0)
      {  
        String columnIdentifier = (String)getConstraint(child);
        if (columnIdentifier != null)
        {        
          int weight = columnData.getColumnWeight(columnIdentifier);
          float fraction = (float)weight / (float)totalColumnWeight;
          int extraWidthForChild = (int)(extraWidth * fraction);
       
          b.width += extraWidthForChild;        
          b.x += extraWidthAllocated;
          extraWidthAllocated += extraWidthForChild;
        }  
        else
        {
          b.x += extraWidthAllocated;
        }
      }
      child.setBounds(new Rectangle(b));  
    }  
  }

  public ColumnData getColumnData()
  {
    return columnData;
  }

  public void setColumnData(ColumnData columnData)
  {
    this.columnData = columnData;
  }    
  
  public Object getConstraint(IFigure child)
  {
    return figureToContstraintMap.get(child);
  }
  
  public void setConstraint(IFigure child, Object constraint)
  {
    figureToContstraintMap.put(child, constraint);
  }
  
  public void invalidate()
  {
    //figureToContstraintMap.clear();
    //this.columnData.clearColumnWidths();
    super.invalidate();
   
  }
}

