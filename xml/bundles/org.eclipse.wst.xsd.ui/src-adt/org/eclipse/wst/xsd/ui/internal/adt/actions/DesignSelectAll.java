/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.wst.xsd.ui.internal.adt.editor.CommonMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.design.editparts.CategoryEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDSchemaEditPart;

public class DesignSelectAll extends BaseSelectionAction
{
  private GraphicalViewer graphicalViewer;
  private List selected;

  public DesignSelectAll(IWorkbenchPart part)
  {
    super(part);
    setId(ActionFactory.SELECT_ALL.getId());
    setText(Messages._UI_ACTION_SELECT_ALL);
  }

  public void run()
  {
    super.run();

    IWorkbenchPart part = getWorkbenchPart();
    selected = new ArrayList();
    if (part instanceof CommonMultiPageEditor)
    {
      graphicalViewer = (GraphicalViewer) ((CommonMultiPageEditor) part).getAdapter(GraphicalViewer.class);
      if (graphicalViewer != null)
      {
        EditPart editPart = graphicalViewer.getContents();
        doSelectChildren(editPart);

        graphicalViewer.setSelection(new StructuredSelection(selected));
      }
    }
  }

  private void doSelectChildren(EditPart editPart)
  {
    List list = editPart.getChildren();
    for (Iterator i = list.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if (o instanceof GraphicalEditPart)
      {
        if (!(o instanceof XSDSchemaEditPart) && !(o instanceof CategoryEditPart))
        {
          selected.add(o);
        }
        doSelectChildren((GraphicalEditPart) o);
      }
    }
  }
}
