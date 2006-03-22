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

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.wst.xsd.adt.design.IAnnotationProvider;
import org.eclipse.wst.xsd.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.adt.facade.IField;
import org.eclipse.wst.xsd.adt.outline.ITreeElement;

public class XSDBaseFieldEditPart extends BaseFieldEditPart
{

  public XSDBaseFieldEditPart()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
   */
  protected void refreshVisuals()
  {
    IFieldFigure figure = getFieldFigure();
    IField field = (IField) getModel();
    
    figure.getNameLabel().setText(field.getName());
    figure.getTypeLabel().setText(field.getTypeName());
    figure.refreshVisuals(getModel());

    String occurrenceDescription = "";
    if (field instanceof IAnnotationProvider)
    {
      occurrenceDescription = ((IAnnotationProvider)field).getNameAnnotationString();
    }
    
    figure.getNameAnnotationLabel().setText(occurrenceDescription);
    
    figure.recomputeLayout();

    // our model implements ITreeElement
    if (getModel() instanceof ITreeElement)
    {
      figure.getNameLabel().setIcon(((ITreeElement)getModel()).getImage());
    }

    if (getRoot() != null)
      ((GraphicalEditPart)getRoot()).getFigure().invalidateTree();
  }

  public void addNotify()
  {
    super.addNotify();
    getFieldFigure().editPartAttached(this);
  }

}
