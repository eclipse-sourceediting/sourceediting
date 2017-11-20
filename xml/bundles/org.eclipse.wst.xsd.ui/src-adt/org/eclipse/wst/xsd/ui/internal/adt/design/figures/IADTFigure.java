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
package org.eclipse.wst.xsd.ui.internal.adt.design.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

public interface IADTFigure extends IFigure
{
  void editPartAttached(EditPart owner); 
  void addSelectionFeedback();
  void removeSelectionFeedback();
  void refreshVisuals(Object model);
}
