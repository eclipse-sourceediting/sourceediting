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
            
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
              

public class LabelFigure extends Figure
{                                                
  protected String text = "";  
  protected boolean isShowEmptyLabel = true;
  protected int left = 4; 
  protected int right = 4; 
  protected int textHeight;
  protected int textWidth;
                       
  public LabelFigure()
  {
    setPreferredSize(new Dimension());
  }  

  protected void setLeft(int left)
  {
    this.left = left;
  }

  protected void setRight(int right)
  {
    this.right = right;
  }

  public void setShowEmptyLabel(boolean isShowEmptyLabel)
  {
    this.isShowEmptyLabel = isShowEmptyLabel;
  }     
       

  public void setText(String s)
  { 
	  if (s == null)
		  s = "";

    if (!text.equals(s))
    {
	    text = s;
      if (text.length() > 0 || isShowEmptyLabel)
      {
        textHeight = FigureUtilities.getFontMetrics(getFont()).getHeight();
        textWidth = FigureUtilities.getTextWidth(text, getFont());
        textWidth = Math.max(textWidth, FigureUtilities.getTextWidth("abcdefg", getFont()));
        setPreferredSize(new Dimension(textWidth + left + right, textHeight));
      } 
      else
      {
        setPreferredSize(new Dimension());
      } 
    }   
    //revalidate(); 
    //repaint();
  }


  
  protected void paintFigure(Graphics graphics)
  //protected void fillShape(Graphics graphics)
  {  
    super.paintFigure(graphics);
    //super.fillShape(graphics);
    if (text.length() > 0)
    {
      Rectangle r = getBounds();
		  graphics.setForegroundColor(getForegroundColor());
      graphics.drawString(text, left + r.x, r.y);// + (r.width - textWidth)/2, r.y + (r.height - textHeight)/2);           
    }
  }
}
