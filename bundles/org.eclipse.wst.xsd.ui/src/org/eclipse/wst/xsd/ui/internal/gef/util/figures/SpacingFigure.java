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
package org.eclipse.wst.xsd.ui.internal.gef.util.figures;
            
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
              
public class SpacingFigure extends RectangleFigure
{  
  public SpacingFigure()
  {
    setFill(false);
    setPreferredSize(new Dimension(0, 0));
  }
  
  //protected void outlineShape(Graphics graphics)
  //{ 
  //}                                              
}