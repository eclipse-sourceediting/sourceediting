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
            
import java.util.List;

import org.eclipse.draw2d.IFigure;

public interface IConnectedEditPartFigure extends IConnectedFigure
{                       
  public static final int UP_CONNECTION = 1; 
  public static final int DOWN_CONNECTION = 2;
  public static final int LEFT_CONNECTION = 3;
  public static final int RIGHT_CONNECTION = 4;

  public IFigure getSelectionFigure();
  public IFigure getConnectionFigure();
  public List getConnectedFigures(int type);
  public int getConnectionType();      
  public void addConnectedFigure(IConnectedEditPartFigure figure);
  public void removeConnectedFigure(IConnectedEditPartFigure figure);
}