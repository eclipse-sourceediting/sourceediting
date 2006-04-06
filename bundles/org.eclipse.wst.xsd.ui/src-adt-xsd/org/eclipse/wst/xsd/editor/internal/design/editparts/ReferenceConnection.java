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
package org.eclipse.wst.xsd.editor.internal.design.editparts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Color;

public class ReferenceConnection extends PolylineConnection
{
  protected boolean highlight = false;

  protected static final Color activeConnection = ColorConstants.black;
  public static final Color inactiveConnection = new Color(null, 198, 195, 198);

  public ReferenceConnection()
  {
    super();
    setConnectionRouter(new XSDModelGroupRouter());
  }

  public void setConnectionRouter(ConnectionRouter cr)
  {
    if (cr != null && getConnectionRouter() != null && !(getConnectionRouter() instanceof XSDModelGroupRouter))
      super.setConnectionRouter(cr);
  }

  public boolean isHighlighted()
  {
    return highlight;
  }

  public void setHighlight(boolean highlight)
  {
    this.highlight = highlight;
    setForegroundColor(highlight ? activeConnection : inactiveConnection);
    setOpaque(highlight);
  }

}
