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
package org.eclipse.wst.xsd.ui.internal.design.figures;

import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class ModelGroupFigure extends GenericGroupFigure implements IModelGroupFigure
{
  public static final Image SEQUENCE_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/sequence_obj.gif"); //$NON-NLS-1$
  public static final Image SEQUENCE_ICON_DISABLED_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/sequencedis_obj.gif"); //$NON-NLS-1$
  public static final Image CHOICE_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/choice_obj.gif"); //$NON-NLS-1$
  public static final Image CHOICE_ICON_DISABLED_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/choicedis_obj.gif"); //$NON-NLS-1$
  public static final Image ALL_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/all_obj.gif"); //$NON-NLS-1$
  public static final Image ALL_ICON_DISABLED_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/alldis_obj.gif"); //$NON-NLS-1$
  
  public ModelGroupFigure()
  {
    super();
  }

  public void setIconFigure(Image image)
  {
    centeredIconFigure.image = image;
  }

  public void addSelectionFeedback()
  {
    // TODO Auto-generated method stub
    
  }

  public void editPartAttached(EditPart owner)
  {
    // TODO Auto-generated method stub
    
  }

  public void refreshVisuals(Object model)
  {
    // TODO Auto-generated method stub
    
  }

  public void removeSelectionFeedback()
  {
    // TODO Auto-generated method stub   
  }
}