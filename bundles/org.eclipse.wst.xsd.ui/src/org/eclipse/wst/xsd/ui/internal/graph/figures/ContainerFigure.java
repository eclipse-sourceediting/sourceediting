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
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.RectangleFigure;
              
public class ContainerFigure extends RectangleFigure
{               
  protected boolean isOutlined = false;

  public ContainerFigure()
  {
    setLayoutManager(new ContainerLayout());
    setFill(false);
  }

  public void doLayout()
  {
	  layout();
	  setValid(true);
  }  
          
  public ContainerLayout getContainerLayout()
  {
    return (ContainerLayout)getLayoutManager();
  }           

  public void setOutlined(boolean isOutlined)
  {
    this.isOutlined = isOutlined;
  }     

  protected void outlineShape(Graphics graphics)
  { 
    if (isOutlined)
    {
      super.outlineShape(graphics);
    }
  }
  
  
  public void validate() 
  {
    if (isValid())
    {
      return;
    }
    super.validate();	
    postLayout();
  }


  protected void postLayout()
  {
    LayoutManager layoutManager = getLayoutManager();
    if (layoutManager instanceof PostLayoutManager)
    {
      ((PostLayoutManager)layoutManager).postLayout(this);    
    }  
  }  
}
