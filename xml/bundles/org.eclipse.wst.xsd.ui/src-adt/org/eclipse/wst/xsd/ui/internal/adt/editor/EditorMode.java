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
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.IContentProvider;

public abstract class EditorMode implements IAdaptable
{
  public abstract String getId();
  
  public abstract String getDisplayName();
  
  public abstract EditPartFactory getEditPartFactory();
  
  // TODO (cs) this should return ITreeContentProvider
  public abstract IContentProvider getOutlineProvider();
  
  public ContextMenuParticipant getContextMenuParticipant()
  {
    return null;
  }
  
  public Object getAdapter(Class adapter)
  {
    return null;
  }
}
