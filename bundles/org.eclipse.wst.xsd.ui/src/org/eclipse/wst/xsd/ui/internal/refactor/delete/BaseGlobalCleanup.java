/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.delete;

import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;

public class BaseGlobalCleanup extends BaseCleanup
{
  public BaseGlobalCleanup(XSDConcreteComponent deletedItem)
  {
    this.deletedItem = deletedItem;
  }
  
  protected XSDConcreteComponent deletedItem;
  
  protected String getDeletedQName()
  {
    return ((XSDNamedComponent)deletedItem).getQName();
  }
}
