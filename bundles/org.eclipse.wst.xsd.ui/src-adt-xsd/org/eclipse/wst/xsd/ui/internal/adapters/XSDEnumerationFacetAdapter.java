/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class XSDEnumerationFacetAdapter extends XSDBaseAdapter implements IActionProvider
{
  public XSDEnumerationFacetAdapter()
  {
    super();
  }

  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    list.add(ShowPropertiesViewAction.ID);
    return (String [])list.toArray(new String[0]);
  }
  
  public Image getImage()
  {
    return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif"); //$NON-NLS-1$
  }
}
