/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class TypeReferenceConnection extends PolylineConnection
{
  protected boolean highlight = false;
  protected static final Color activeConnection = ColorConstants.black;
  protected static final Color inactiveConnection = new Color(null, 198, 195, 198);

  /**
   * Default constructor
   */
  public TypeReferenceConnection()
  {
    super();
    setTargetDecoration(new PolygonDecoration());
  }
  
  public TypeReferenceConnection(boolean fill)
  {
    super();
    PolygonDecoration dec = new PolygonDecoration()
    {
      protected void fillShape(Graphics g)
      {
        try
        {
          g.pushState();
          g.setBackgroundColor(ColorConstants.white);
          super.fillShape(g);
        }
        finally
        {
          g.popState();
        }
      }
      
    };
    dec.setFill(fill);
    dec.setTemplate(PolygonDecoration.TRIANGLE_TIP);
    dec.setScale(14,6);
    setTargetDecoration(dec);
  }
  

  public void setConnectionRouter(ConnectionRouter cr)
  {
    if (cr != null && getConnectionRouter() != null && !(getConnectionRouter() instanceof ManhattanConnectionRouter))
      super.setConnectionRouter(cr);
  }

  /**
   * @return Returns the current highlight status.
   */
  public boolean isHighlighted()
  {
    return highlight;
  }

  /**
   * @param highlight
   *          The highlight to set.
   */
  public void setHighlight(boolean highlight)
  {
    this.highlight = highlight;
    // Update our connection to use the correct colouring
    boolean highContrast = false;
    try
    {
      highContrast = Display.getDefault().getHighContrast();
    }
    catch (Exception e)
    {
    }
    if (highContrast)
    {
      setForegroundColor(highlight ? Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND) : ColorConstants.lightGray);
    }
    else
    {
      setForegroundColor(highlight ? activeConnection : inactiveConnection);
    }
    setOpaque(highlight);
  }
}
