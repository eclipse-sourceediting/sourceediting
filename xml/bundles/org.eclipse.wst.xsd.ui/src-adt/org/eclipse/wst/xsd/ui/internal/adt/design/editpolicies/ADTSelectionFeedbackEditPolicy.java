/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IFeedbackHandler;

public class ADTSelectionFeedbackEditPolicy extends SelectionEditPolicy
{

  public ADTSelectionFeedbackEditPolicy()
  {
    super();
  }

  protected void hideSelection()
  {
    if (getHost() instanceof IFeedbackHandler)
    {
      ((IFeedbackHandler) getHost()).removeFeedback();
    }
  }

  protected void showSelection()
  {
    if (getHost() instanceof IFeedbackHandler)
    {
      ((IFeedbackHandler) getHost()).addFeedback();
    }
  }
  
  protected void showFocus()
  {
    super.showFocus();
    getHost().setFocus(true);
  }
  
  protected void hideFocus()
  {
    super.hideFocus();
    getHost().setFocus(false);
  }

}
