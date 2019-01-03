/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;

public class SectionEditPart extends BaseEditPart
{
  protected IComplexType complexType;
  
  protected IFigure createFigure()
  {
    Figure fig = new Figure();
    fig.setLayoutManager(new ToolbarLayout());
    return fig;
  }

  protected void createEditPolicies()
  {

  }
  
  public boolean isSelectable()
  {
    return false;
  }
}
