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
                                 
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.GraphicsConstants;
import org.eclipse.wst.xsd.ui.internal.graph.model.ModelAdapterListener;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;


public abstract class BaseEditPart extends AbstractGraphicalEditPart implements ModelAdapterListener, GraphicsConstants, IFeedbackHandler
{
	protected boolean isSelected = false;
	/**
	 * Activates the <code>EditPart</code> by setting the
	 * appropriate flags, and activating its children.
	 * activation signals to the EditPart that is should start observing
	 * it's model, and that is should support editing at this time.
	 * An EditPart will have a parent prior to activiation.
	 * @see #deactivate()
	 */
	public void activate() 
  {
		super.activate();         
    XSDModelAdapterFactory.addModelAdapterListener(getModel(), this);  
	}
	/** 
	 * Apart from the deactivation done in super, the source
	 * and target connections are deactivated, and the visual
	 * part of the this is removed.
	 *
	 * @see #activate() 
	 */
	public void deactivate() 
  {
    XSDModelAdapterFactory.removeModelAdapterListener(getModel(), this);  
		super.deactivate();
	}   
    
  protected void createEditPolicies() 
  {
  }  

  protected EditPart createChild(Object model) 
  {
    return XSDEditPartFactory.getInstance().createEditPart(this, model);
  } 

  public void propertyChanged(Object object, String property)
  {                                                                                                  
    refresh(); 
  }    

  //public BaseGraphicalViewer getBaseGraphicalViewer()
  //{
  //  return (BaseGraphicalViewer)getViewer();
  //}

  public IFigure getSelectionFigure()
  {
    return getFigure();
  }
  
  
  public void addFeedback()
  {
    isSelected = true;
    refreshVisuals();
  }

  public void removeFeedback()
  {
    isSelected = false;
    refreshVisuals();
  }  
}