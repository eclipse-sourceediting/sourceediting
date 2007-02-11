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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.IAnnotationProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.DragAndDropEditPolicy;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.xsd.XSDConcreteComponent;

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
    if (field.isReadOnly())
      figure.setForegroundColor(ColorConstants.darkGray);
    else
      figure.setForegroundColor(ColorConstants.black);

    String occurrenceDescription = ""; //$NON-NLS-1$
    if (field instanceof IAnnotationProvider)
    {
      occurrenceDescription = ((IAnnotationProvider)field).getNameAnnotationString();
    }
    refreshIcon();
    figure.getNameAnnotationLabel().setText(occurrenceDescription);
    
    figure.recomputeLayout();


    if (getRoot() != null)
      ((GraphicalEditPart)getRoot()).getFigure().invalidateTree();
  }
  
  protected void refreshIcon()  
  {
    IFieldFigure figure = getFieldFigure();   
    // our model implements ITreeElement
    if (getModel() instanceof XSDBaseAdapter)
    {
      Image image = ((XSDBaseAdapter)getModel()).getImage();
      boolean isReadOnly = ((XSDBaseAdapter)getModel()).isReadOnly();
      figure.getNameLabel().setIcon(image);
      
      if (image != null)
      {
        XSDConcreteComponent comp = (XSDConcreteComponent) ((XSDBaseAdapter)getModel()).getTarget();
        figure.getNameLabel().setIcon(XSDCommonUIUtils.getUpdatedImage(comp, image, isReadOnly));
      }
    }    
  }

  public void addNotify()
  {
    super.addNotify();
    getFieldFigure().editPartAttached(this);
  }

  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DragAndDropEditPolicy(getViewer(), selectionHandlesEditPolicy));
  }
}
