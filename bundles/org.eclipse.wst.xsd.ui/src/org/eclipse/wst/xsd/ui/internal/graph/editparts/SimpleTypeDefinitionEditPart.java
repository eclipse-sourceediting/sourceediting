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
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;

public class SimpleTypeDefinitionEditPart extends BaseEditPart
{
  protected Label label;
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;

  ImageFigure figure;
  Image image;
  
  protected IFigure createFigure()
  {
    String iconName = "icons/XSDSimpleTypeForEditPart.gif";
    image = XSDEditorPlugin.getXSDImage(iconName);
  
    figure = new ImageFigure(image);
    return figure;
  }

  protected void refreshVisuals()
  {
    if (isSelected)
    {
      image = XSDEditorPlugin.getXSDImage("icons/XSDSimpleTypeForEditPart.gif");
    }
    else
    {
      image = XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }
    figure.setImage(image);
    figure.repaint();
  }
  
  protected boolean isConnectedEditPart()
  {
    return false;
  }
  
  public void deactivate()
  {
    super.deactivate();
    image = null;  // where do we dispose the image?
  }
  

  protected void createEditPolicies()
  { 
    selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);   
  }  
}
