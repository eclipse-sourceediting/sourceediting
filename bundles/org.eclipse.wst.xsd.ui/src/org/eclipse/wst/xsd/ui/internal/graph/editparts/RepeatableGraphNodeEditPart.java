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
                                        
import org.eclipse.gef.EditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.DragAndDropEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RepeatableGraphNodeFigure;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

                  

public class RepeatableGraphNodeEditPart extends GraphNodeEditPart
{
  protected RepeatableGraphNodeFigure getRepeatableGraphNodeFigure()
  {
    return (RepeatableGraphNodeFigure)graphNodeFigure;
  }
  
  protected GraphNodeFigure createGraphNodeFigure()
  {
    return new RepeatableGraphNodeFigure();                                                        
  }   
      
  protected void refreshOccurenceLabel(XSDParticle particle)
  {  
    if (particle != null)
    { 
      refreshOccurenceLabel(particle.getMinOccurs(), particle.getMaxOccurs());
    }
  }

  protected void refreshOccurenceLabel(int min, int max)
  {                     
    if (min == 1 && max == 1)
    {
      getRepeatableGraphNodeFigure().getOccurenceLabel().setText("");
    }
    else
    {
      String maxString = max == -1 ? "*" : "" + max;
      getRepeatableGraphNodeFigure().getOccurenceLabel().setText(min + ".." + maxString);
    }
    getRepeatableGraphNodeFigure().getOccurenceLabel().repaint();
  }

  protected void createEditPolicies()
  { 
    super.createEditPolicies();
    
    if (getModel() instanceof XSDElementDeclaration) {
    	Object parent = ((XSDElementDeclaration) getModel()).eContainer();
    	
    	if (!(parent instanceof XSDSchema)) {
    		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DragAndDropEditPolicy(getViewer(), selectionHandlesEditPolicy));      
    	}
    }
    else {
    	installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DragAndDropEditPolicy(getViewer(), selectionHandlesEditPolicy));
    }
  }  
}
