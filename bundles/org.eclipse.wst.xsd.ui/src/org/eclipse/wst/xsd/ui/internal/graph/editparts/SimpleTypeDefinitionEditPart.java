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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;

public class SimpleTypeDefinitionEditPart extends BaseEditPart //GraphNodeEditPart
{
  protected Label label;
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;

  ImageFigure figure;
  Color color;
  
  protected IFigure createFigure()
  {
    String iconName = "icons/XSDSimpleType.gif";
    Image image = XSDEditorPlugin.getXSDImage(iconName);
  
    figure = new ImageFigure(image);
    RoundedLineBorder lb = new RoundedLineBorder(1, 6);
    figure.setOpaque(true);
    figure.setBorder(lb);
    figure.setBackgroundColor(ColorConstants.white);
    figure.setForegroundColor(elementBorderColor);
    
    return figure;
  }

  protected void refreshVisuals()
  {
    figure.setBorder(new RoundedLineBorder(isSelected ? ColorConstants.black : elementBorderColor, 1, 6));
    figure.repaint();
  }
  
  protected boolean isConnectedEditPart()
  {
    return false;
  }

  protected void createEditPolicies()
  { 
    selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);   
  }  

  public void deactivate() 
  {
    super.deactivate();
    if (color != null)
    {
      color.dispose();
    }
  }   
}
