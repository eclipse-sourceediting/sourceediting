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
                                        
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ExpandableGraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;

                  

public abstract class ExpandableGraphNodeEditPart extends RepeatableGraphNodeEditPart implements MouseListener, ActionListener
{
  protected boolean needToPerformDefaultExpansion = true;
   
  protected GraphNodeFigure createGraphNodeFigure()
  {
    return new ExpandableGraphNodeFigure();     
  }

  protected void addGraphNodeFigureListeners()
  {                                                                    
    getExpandableGraphNodeFigure().getInteractor().addMouseListener(this); 
  }  
            
  protected ExpandableGraphNodeFigure getExpandableGraphNodeFigure()
  {
    return (ExpandableGraphNodeFigure)graphNodeFigure;
  } 

  public IFigure getContentPane()  
  {
    return getExpandableGraphNodeFigure().getOuterContentArea();
  }
    
  protected boolean isDefaultExpanded()
  {
    return false;
  }

  protected boolean hasChildren()
  {
    return getModelChildrenHelper().size() > 0;
  }         

  protected abstract List getModelChildrenHelper();
      

  protected List getModelChildren() 
  {                                                                             
    return getExpandableGraphNodeFigure().isExpanded() ? getModelChildrenHelper() : Collections.EMPTY_LIST;
  }  

  protected void refreshChildren()
  {    
    if (needToPerformDefaultExpansion && isDefaultExpanded())
    {
      needToPerformDefaultExpansion = false;   
      performExpandOrCollapseHelper();

      super.refreshChildren();

      EditPart root = getRoot();
      if (root instanceof AbstractGraphicalEditPart)
      {         
        getContentPane().setVisible(true); 
                    
        IFigure rootFigure = ((AbstractGraphicalEditPart)root).getFigure();
        invalidateAll(rootFigure);
        rootFigure.validate();
        rootFigure.repaint();
      }     
      getExpandableGraphNodeFigure().getInteractor().repaint();
    } 
    else
    {
      super.refreshChildren(); 
    }   
    getExpandableGraphNodeFigure().getInteractor().setVisible(hasChildren());
  }


  protected void performExpandOrCollapseHelper()
  {
    boolean isButtonExpanded = !getExpandableGraphNodeFigure().isExpanded();
    getExpandableGraphNodeFigure().setExpanded(isButtonExpanded);
  }

  public void doPerformExpandOrCollapse()
  {
    performExpandOrCollapse();
  }  
  
  public boolean isExpanded()
  {
    return getExpandableGraphNodeFigure().isExpanded();
  }

  protected void performExpandOrCollapse()
  {
    performExpandOrCollapseHelper();                              

    boolean isButtonExpanded = getExpandableGraphNodeFigure().isExpanded();

    refreshChildren();

    EditPart root = getRoot();
    if (root instanceof AbstractGraphicalEditPart)
    {         
      getContentPane().setVisible(isButtonExpanded); 
                       
      IFigure rootFigure = ((AbstractGraphicalEditPart)root).getFigure();
      invalidateAll(rootFigure);
      rootFigure.validate();
      rootFigure.repaint();
    }      
    getExpandableGraphNodeFigure().getInteractor().repaint();
  }

  
  protected void refreshOccurenceLabel(int min, int max)
  {
    super.refreshOccurenceLabel(min, max);

    // TODO: revisit the 'hack' to understand why we need to do this 
    // in order to get the view to layout propetly
    //
    IFigure thisFigure = getFigure();
    invalidateAll(thisFigure);
    thisFigure.validate();
    thisFigure.repaint();
  }

  protected void invalidateAll(IFigure figure)
  {
    figure.invalidate();   
    LayoutManager manager = figure.getLayoutManager();
    if (manager != null)
    {
      manager.invalidate();
    }
    for (Iterator i = figure.getChildren().iterator(); i.hasNext(); )
    {
      IFigure child = (IFigure)i.next();
      invalidateAll(child);
    }
  } 

  
  // implements MouseListener
  // 
  public void mouseDoubleClicked(MouseEvent me) 
  {
  }

  public void mousePressed(MouseEvent me) 
  {                             
    me.consume();
    needToPerformDefaultExpansion = false;
    performExpandOrCollapse();  
  }

  public void mouseReleased(MouseEvent me) 
  {
  } 

  public void actionPerformed(ActionEvent event) 
  {                      
    performExpandOrCollapse();
  }   
}
