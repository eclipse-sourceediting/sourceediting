/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.wst.xsd.ui.internal.graph.GraphicsConstants;


              

//  ------------------------------
//  | RepeatableGraphNodeFigure  |
//  |                            |
//  | -------------------------  |
//  | | vertical group        |  |
//  | | --------------------- |  |
//  | | | outlined area     | |  |
//  | | | ----------------- | |  |
//  | | | | icon area     | | |  |
//  | | | ----------------- | |  |
//  | | | ----------------- | |  |
//  | | | | inner content | | |  |
//  | | | ----------------- | |  |
//  | | --------------------- |  |
//  | |                       |  |
//  | | ------------------    |  |
//  | | | occurence area |    |  |
//  | | ------------------    |  |
//  | -------------------------  |
//  ------------------------------

public class RepeatableGraphNodeFigure extends GraphNodeFigure
{                            
  protected ContainerFigure occurenceArea;  
  protected LabelFigure occurenceLabel; 

  public RepeatableGraphNodeFigure()
  {    
    super();          
  }     

  protected void createFigure()
  {                  
    createPreceedingSpace(this);
    createVerticalGroup(this);
    createOutlinedArea(verticalGroup); 
    createOccurenceArea(verticalGroup);
  }

  protected void createOccurenceArea(IFigure parent)
  {
    occurenceArea = new ContainerFigure();   
    occurenceLabel = new LabelFigure();
    occurenceLabel.setForegroundColor(ColorConstants.black);
    occurenceLabel.setShowEmptyLabel(false);
    occurenceLabel.setFont(GraphicsConstants.medium); 
    occurenceArea.add(occurenceLabel);               
    parent.add(occurenceArea);
  }
                                 
  protected void createPreceedingSpace(IFigure parent)
  {
    // create a small space
    RectangleFigure space = new RectangleFigure();
    space.setVisible(false);
    space.setPreferredSize(new Dimension(10, 10));
    parent.add(space);  
  } 

  public LabelFigure getOccurenceLabel()
  {
    return occurenceLabel;
  }
}
