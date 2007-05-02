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
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.jface.action.IMenuManager;

public class ContextMenuParticipant
{  
  public boolean isApplicable(Object object, String actionId)
  {
    return true;
  }
  
  public void contributeActions(Object object, IMenuManager menu)
  {    
  }
}
