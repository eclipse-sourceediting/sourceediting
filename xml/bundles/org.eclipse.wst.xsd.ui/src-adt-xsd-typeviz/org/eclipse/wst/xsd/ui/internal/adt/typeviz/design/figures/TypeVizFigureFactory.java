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
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures;

import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.ICompartmentFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.design.figures.IExtendedFigureFactory;
import org.eclipse.wst.xsd.ui.internal.design.figures.IModelGroupFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.ModelGroupFigure;

public class TypeVizFigureFactory implements IExtendedFigureFactory
{  
  public IStructureFigure createStructureFigure(Object model)
  {
    StructureFigure figure = new StructureFigure();
    figure.setBorder(new LineBorder(1));    
    ToolbarLayout toolbarLayout = new ToolbarLayout();
    toolbarLayout.setStretchMinorAxis(true);
    figure.setLayoutManager(toolbarLayout);

    if (model instanceof ITreeElement)
    {
      figure.getNameLabel().setIcon(((ITreeElement)model).getImage());
    }    
    //figure.getHeadingFigure().setIsReadOnly(getComplexType().isReadOnly());
    // we should organize ITreeElement and integrate it with the facade    
    return figure;
  }

  public IFieldFigure createFieldFigure(Object model)
  {
    // TODO Auto-generated method stub
    return new FieldFigure();
  }
  
  public ICompartmentFigure createCompartmentFigure(Object model)
  {
    CompartmentFigure figure = new CompartmentFigure();
    figure.setBorder(new MarginBorder(1));
    ToolbarLayout toolbarLayout = new ToolbarLayout(false);
    toolbarLayout.setStretchMinorAxis(true);
    figure.setLayoutManager(toolbarLayout);
    return figure;
  }  
  
  public IModelGroupFigure createModelGroupFigure(Object model)
  {
    // TODO Auto-generated method stub
    return new ModelGroupFigure();
  }
}
