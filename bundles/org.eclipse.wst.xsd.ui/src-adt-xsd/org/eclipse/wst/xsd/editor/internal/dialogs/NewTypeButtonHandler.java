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
package org.eclipse.wst.xsd.editor.internal.dialogs;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class NewTypeButtonHandler implements SelectionListener
{
  public NewTypeButtonHandler()
  {
  }

  public void widgetSelected(SelectionEvent e)
  {
    NewTypeDialog newTypeDialog = new NewTypeDialog();
    newTypeDialog.open();
  }
  
  public void widgetDefaultSelected(SelectionEvent e)
  {
    // TODO Auto-generated method stub
  }
}