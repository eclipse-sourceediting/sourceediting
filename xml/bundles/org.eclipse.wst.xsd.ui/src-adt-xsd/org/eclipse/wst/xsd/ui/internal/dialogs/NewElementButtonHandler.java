/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xsd.ui.internal.dialogs;

import org.eclipse.wst.common.ui.internal.search.dialogs.INewComponentHandler;

public class NewElementButtonHandler implements INewComponentHandler
{
  public NewElementButtonHandler()
  {
  }

  public void openNewComponentDialog()
  {
    NewElementDialog newElementDialog = new NewElementDialog();
    newElementDialog.createAndOpen();
  }
}
