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
package org.eclipse.wst.xsd.ui.internal.graph;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.wst.xsd.ui.internal.XSDMenuListener;
import org.eclipse.wst.xsd.ui.internal.XSDTextEditor;


public class GraphContextMenuProvider extends ContextMenuProvider
{

  XSDMenuListener xsdMenuListener;
  
  /**
   * Constructor for GraphContextMenuProvider.
   * @param selectionProvider
   * @param editor
   */
  public GraphContextMenuProvider(
    EditPartViewer viewer,
    ISelectionProvider selectionProvider,
    XSDTextEditor editor)
  {
    super(viewer);
    this.viewer = viewer;
    xsdMenuListener = new XSDMenuListener(selectionProvider);
  }

  public XSDMenuListener getMenuListener()
  {
      return xsdMenuListener;
  }

  
  /**
   * @see org.eclipse.gef.ui.parts.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.gef.EditPartViewer)
   */
  public void buildContextMenu(IMenuManager arg0)
  {
    xsdMenuListener.menuAboutToShow(arg0);
  }
  
  protected EditPartViewer viewer;

}
