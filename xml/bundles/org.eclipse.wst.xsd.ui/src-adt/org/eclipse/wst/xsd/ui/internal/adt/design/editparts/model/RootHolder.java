/*******************************************************************************
 * Copyright (c) 22007 IBM Corporation and others.
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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;

public class RootHolder
{
  IADTObject model;

  public RootHolder(IADTObject model)
  {
    this.model = model;
  }

  public IADTObject getModel()
  {
    return model;
  }
}
