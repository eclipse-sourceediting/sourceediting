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
import org.eclipse.draw2d.geometry.Rectangle;
              

//  ------------------------------------------------------------
//  | ExpandableGraphNodeFigure                                |
//  |                                                          |
//  |   ----------------------------------   ---------------   |
//  |   | verticalGroup                  |   |             |   |
//  |   |                                |   |             |   |
//  |   | -----------------------------  |   |             |   |
//  |   | | horizontalGroup           |  |   |             |   |
//  |   | |                           |  |   |             |   |
//  |   | | ---------------------     |  |   |             |   |
//  |   | | | outlinedArea      |     |  |   |             |   |
//  |   | | | ----------------- |     |  |   |  outer      |   |
//  |   | | | | iconArea      | |     |  |   |  Content    |   |
//  |   | | | ----------------- | [+] |  |   |  Area       |   |
//  |   | | | ----------------- |     |  |   |             |   |
//  |   | | | | innerContent  | |     |  |   |             |   |
//  |   | | | ----------------- |     |  |   |             |   |
//  |   | | ---------------------     |  |   |             |   |
//  |   | -----------------------------  |   |             |   |
//  |   |                                |   |             |   |
//  |   |   ------------------           |   |             |   |
//  |   |   | occurenceArea  |           |   |             |   |
//  |   |   ------------------           |   |             |   |
//  |   ----------------------------------   ---------------   |
//  ------------------------------------------------------------

public class ExpandableGraphNodeFigure extends RepeatableGraphNodeFigure
{                        
  protected ContainerFigure horizontalGroup;
  protected Interactor interactor; 
  protected ContainerFigure outerContentArea;

  public boolean isExpanded()
  {
    return interactor.isExpanded();
  }   

  public void setExpanded(boolean isExpanded)
  {
    interactor.setExpanded(isExpanded);
  }
  
  public ExpandableGraphNodeFigure()
  {
    super();    
    isConnected = true;
  }      

  protected void createFigure()
  {   
    createPreceedingSpace(this);           
    createVerticalGroup(this);
    createHorizontalGroup(verticalGroup);
    createOutlinedArea(horizontalGroup);   
    createInteractor(horizontalGroup);
    createOccurenceArea(verticalGroup);   
    createOuterContentArea(this);
  }
  
  protected void createVerticalGroup(IFigure parent)
  {
    verticalGroup = new FloatableFigure(this);
    verticalGroup.getContainerLayout().setHorizontal(false);
    parent.add(verticalGroup);
  }

  protected void createHorizontalGroup(IFigure parent)
  {
    horizontalGroup = new ContainerFigure();
    parent.add(horizontalGroup);
  }                 

  protected void createInteractor(IFigure parent)
  {
    interactor = new Interactor();
    interactor.setForegroundColor(ColorConstants.black);
    interactor.setBackgroundColor(ColorConstants.white); 
    parent.add(interactor);
  } 

  protected void createOuterContentArea(IFigure parent)
  {
    // create a small space between the interactor and the contentArea
    //
    RectangleFigure space = new RectangleFigure();
    space.setVisible(false);
    space.setPreferredSize(new Dimension(5, 10));
    parent.add(space);   
      
    outerContentArea = new GraphNodeContainerFigure(this);
    outerContentArea.getContainerLayout().setHorizontal(false);
    parent.add(outerContentArea);
  }

  public Interactor getInteractor()
  {
    return interactor;
  } 

  public ContainerFigure getOuterContentArea()
  {
    return outerContentArea;
  }
     
  public IFigure getConnectionFigure()
  {
    return horizontalGroup;
  }

  public Rectangle getConnectionRectangle()
  {
    return horizontalGroup.getBounds();
  }
}
