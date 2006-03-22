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
package org.eclipse.wst.xsd.editor.internal.design.figures;
            
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class CenteredIconFigure extends RectangleFigure
{                                                
  public Image image;
  protected Label toolTipLabel;
                               
  public CenteredIconFigure()
  {
    super();
    setFill(true);   
    toolTipLabel = new Label();
  }
  
  
  protected void outlineShape(Graphics graphics)
  {
  }

  protected void fillShape(Graphics g)
  {    
    super.fillShape(g);    
    if (image != null)
    {                         
      Rectangle r = getBounds();
      Dimension imageSize = new Dimension(15, 15);
      g.drawImage(image, r.x + (r.width - imageSize.width)/2, r.y + (r.height - imageSize.height)/2);
    }
  }
  
  public Label getToolTipLabel()
  {
    return toolTipLabel;
  }
  
  public void setToolTipText(String text)
  {
    if (text.length() > 0)
    {
      setToolTip(toolTipLabel);
      toolTipLabel.setText(text);
    }
    else
    {
      setToolTip(null);
    }
  }  
}
