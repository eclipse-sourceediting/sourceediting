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
package org.eclipse.wst.xsd.ui.internal.adt.typeviz;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.wst.xsd.ui.internal.adt.editor.EditorMode;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlineProvider;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.TypeVizFigureFactory;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDEditPartFactory;

public class TypeVizEditorMode extends EditorMode
{
  private EditPartFactory editPartFactory;
  public final static String ID = "org.eclipse.wst.xsd.ui.typeviz";
  
  public Object getAdapter(Class adapter)
  {
    return null;
  }

  public String getDisplayName()
  {
    return "Advanced";
  }

  public EditPartFactory getEditPartFactory()
  {
    if (editPartFactory == null)
    {
      editPartFactory = new XSDEditPartFactory(new TypeVizFigureFactory());
    }  
    return editPartFactory;
  }

  public String getId()
  {
    return ID;
  }

  public IContentProvider getOutlineProvider()
  {
    return new ADTContentOutlineProvider();
  }
}
