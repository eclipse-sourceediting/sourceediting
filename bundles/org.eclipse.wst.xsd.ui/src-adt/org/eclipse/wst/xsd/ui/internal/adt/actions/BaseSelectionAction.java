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
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;

public abstract class BaseSelectionAction extends SelectionAction
{
  public static final String SEPARATOR_ID = "org.eclipse.jface.action.Separator"; //$NON-NLS-1$
  public static final String SUBMENU_START_ID = "SUBMENU_START_ID: "; //$NON-NLS-1$
  public static final String SUBMENU_END_ID = "SUBMENU_END_ID: "; //$NON-NLS-1$

  protected ISelectionProvider provider;
  
  public BaseSelectionAction(IWorkbenchPart part)
  {
    super(part);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.SelectionAction#getSelection()
   */
  protected ISelection getSelection()
  {
    // always get selection from selection provider first
    if (provider!=null)
      return provider.getSelection();
    
    return super.getSelection();
  }
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.SelectionAction#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
   */
  public void setSelectionProvider(ISelectionProvider provider)
  {
    super.setSelectionProvider(provider);
    this.provider = provider;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction#calculateEnabled()
   */
  protected boolean calculateEnabled()
  {
    if (getSelectedObjects().size() > 0)
    {
      Object o = getSelectedObjects().get(0);
      if (o instanceof IComplexType)
      {
        return !((IComplexType)o).isReadOnly();
      }
      else if (o instanceof IField)
      {
        return !((IField)o).isReadOnly();
      }
    }
    return true;
  }

}
