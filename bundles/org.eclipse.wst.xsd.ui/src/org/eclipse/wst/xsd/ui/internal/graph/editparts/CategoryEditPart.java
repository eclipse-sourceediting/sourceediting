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
package org.eclipse.wst.xsd.ui.internal.graph.editparts;
                           
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.ViewportLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.GraphicsConstants;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.graph.model.Category;


public class CategoryEditPart extends BaseEditPart
{
  protected ScrollPane scrollpane;
  protected Label label;


  public int getType()
  {
    return ((Category)getModel()).getGroupType();
  }

  protected IFigure createFigure()
  {           
    ContainerFigure outerPane = new ContainerFigure();    
    outerPane.setBorder(new RoundedLineBorder(1, 6));
    outerPane.setForegroundColor(categoryBorderColor);

    ContainerFigure r = new ContainerFigure();  
    r.setOutline(false);
    r.setMinimumSize(new Dimension(0, 0));
    r.setFill(true);
    r.setBackgroundColor(GraphicsConstants.elementBackgroundColor);
    outerPane.add(r);
    
    int minHeight = 250;
    switch (getType())
    {
    	case Category.DIRECTIVES :
		case Category.NOTATIONS :
		{
			minHeight = 50;
			break;	
		}
		case Category.ATTRIBUTES :
		case Category.GROUPS :		  
		{
			minHeight = 100;
			break;
		}
    }
    
	final int theMinHeight = minHeight;
    FillLayout outerLayout = new FillLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.union(new Dimension(100, theMinHeight));
        return d;
      }
    };
    //outerLayout.setHorizontal(false);
    outerPane.setLayoutManager(outerLayout);
    
   
    label = new Label();
    label.setForegroundColor(ColorConstants.black);
    label.setBorder(new MarginBorder(2, 4, 2, 4));
    r.add(label); //Holder);

    RectangleFigure line = new RectangleFigure();
    line.setPreferredSize(20, 1);
    outerPane.add(line);


    scrollpane = new ScrollPane();
    scrollpane.setForegroundColor(ColorConstants.black);
    scrollpane.setVerticalScrollBarVisibility(ScrollPane.AUTOMATIC); //ScrollPane.ALWAYS);
    outerPane.add(scrollpane);

    ContainerFigure pane = new ContainerFigure();
    pane.setBorder(new MarginBorder(5, 8, 5, 8));
    ContainerLayout layout = new ContainerLayout();
    layout.setHorizontal(false);
    layout.setSpacing(0);
    pane.setLayoutManager(layout);

    Viewport viewport = new Viewport();
    viewport.setContentsTracksHeight(true);
    ViewportLayout viewportLayout = new ViewportLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.height = Math.min(d.height, theMinHeight - 25); //getViewer().getControl().getBounds().height);
        return d;
      }
    };
    viewport.setLayoutManager(viewportLayout);

    scrollpane.setViewport(viewport);
    scrollpane.setContents(pane);

    return outerPane;
  }  

  protected List getModelChildren() 
  {            
    return ((Category)getModel()).getChildren();
  }  
        
  public void refreshVisuals()
  {
    Category category = (Category)getModel();
    // temp hack --- added empty space to make the min width of groups bigger  
    label.setText("  " + category.getName() + "                                                    ");
  } 
  
  public IFigure getContentPane()
  {
    return scrollpane.getContents();
  }  

  public void scrollTo(AbstractGraphicalEditPart topLevel)
  {
    Rectangle topLevelBounds = topLevel.getFigure().getBounds();
    Rectangle categoryBounds = getFigure().getBounds();
    int scrollValue = scrollpane.getVerticalScrollBar().getValue();
    int location = topLevelBounds.y + scrollValue - categoryBounds.y;
    scrollpane.scrollVerticalTo(location - categoryBounds.height/2);
  }

  protected EditPart createChild(Object model)
  {
    EditPart editPart = new TopLevelComponentEditPart();
    editPart.setModel(model);
    editPart.setParent(this);
    return editPart;
  }
}
