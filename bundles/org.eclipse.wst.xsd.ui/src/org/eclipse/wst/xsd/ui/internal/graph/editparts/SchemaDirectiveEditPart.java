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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.xsd.XSDSchemaDirective;


public class SchemaDirectiveEditPart extends BaseEditPart
{
  protected CenteredIconFigure centeredIconFigure;
  protected Label label;
  /**
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  protected IFigure createFigure()
  {
 
    ContainerFigure figure = new ContainerFigure();

    figure.setLayoutManager(new FillLayout());
    figure.setBorder(new RoundedLineBorder(1, 8));

    ContainerFigure fig = new ContainerFigure();
    fig.setLayoutManager(new FillLayout());
    fig.setBorder(new MarginBorder(10, 0, 10, 0));
    figure.add(fig);


    label = new Label();    
    label.setBorder(new MarginBorder(4, 2, 2, 10));
    fig.add(label);        

    return figure;
  }  
 
  public void refreshVisuals()
  {
    XSDSchemaDirective directive = (XSDSchemaDirective)getModel();
    String schemaLocation = directive.getSchemaLocation();
    if (schemaLocation == null) schemaLocation = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED") + ")";
    if (schemaLocation.equals("")) schemaLocation = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED") + ")";
    label.setText("  " + directive.getElement().getLocalName() + " " + schemaLocation);
  } 
}
