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
            
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
              

public class CenteredIconFigure extends ContainerFigure
{                                                
  public Image image;  
                               
  public CenteredIconFigure()
  {
    super();
    setFill(true);   
  }

  protected void fillShape(Graphics g)
  {    
    super.fillShape(g);    
    if (image != null)
    {                         
      Rectangle r = getBounds();                                                         
      Dimension imageSize = new Dimension(16, 16);
      g.drawImage(image, r.x + (r.width - imageSize.width)/2, r.y + (r.height - imageSize.height)/2);
    }
  }          
}
