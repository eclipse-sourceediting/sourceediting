/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.design.editpolicies;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.INamedEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TopLevelComponentEditPart;
import org.eclipse.xsd.XSDNamedComponent;

public class TopLevelNameDirectEditManager extends DirectEditManager
{
  protected XSDNamedComponent component;
  private IActionBars actionBars;
  private CellEditorActionHandler actionHandler;
  private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
  private Font scaledFont;

  public TopLevelNameDirectEditManager(GraphicalEditPart source, CellEditorLocator locator, XSDNamedComponent component)
  {
    super(source, null, locator);
    this.component = component;
  }

  /**
   * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
   */
  protected void bringDown()
  {
    if (actionHandler != null)
    {
      actionHandler.dispose();
      actionHandler = null;
    }
    if (actionBars != null)
    {
      restoreSavedActions(actionBars);
      actionBars.updateActionBars();
      actionBars = null;
    }

    Font disposeFont = scaledFont;
    scaledFont = null;
    super.bringDown();
    if (disposeFont != null)
      disposeFont.dispose();

    if (getEditPart() instanceof TopLevelComponentEditPart)
    {
      Runnable runnable = new Runnable()
      {
        public void run()
        {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
          Object adapter = editor.getAdapter(ISelectionProvider.class);
          if (adapter instanceof ISelectionProvider)
          {
            ISelectionProvider sel = (ISelectionProvider) adapter;
            sel.setSelection(new StructuredSelection(getEditPart().getModel()));
          }
        }
      };
      Display.getCurrent().asyncExec(runnable);
    }
  }

  public void showFeedback()
  {
    super.showFeedback();
  }

  protected CellEditor createCellEditorOn(Composite composite)
  {
    return new TextCellEditor(composite, SWT.SINGLE | SWT.WRAP);
  }

  protected void initCellEditor()
  {
    Text text = (Text) getCellEditor().getControl();
    Label label = ((INamedEditPart) getEditPart()).getNameLabelFigure();

    if (label != null)
    {
      scaledFont = label.getFont();

      Color color = label.getBackgroundColor();
      text.setBackground(color);

      String initialLabelText = component.getName();
      getCellEditor().setValue(initialLabelText);
    }
    else
    {
      scaledFont = label.getParent().getFont();
      text.setBackground(label.getParent().getBackgroundColor());
    }

    FontData data = scaledFont.getFontData()[0];
    Dimension fontSize = new Dimension(0, data.getHeight());
    label.getParent().translateToAbsolute(fontSize);
    data.setHeight(fontSize.height);
    scaledFont = new Font(null, data);

    text.setFont(scaledFont);
    // text.selectAll();

    // Hook the cell editor's copy/paste actions to the actionBars so that they
    // can
    // be invoked via keyboard shortcuts.
    actionBars = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite().getActionBars();
    saveCurrentActions(actionBars);
    actionHandler = new CellEditorActionHandler(actionBars);
    actionHandler.addCellEditor(getCellEditor());
    actionBars.updateActionBars();
  }

  private void restoreSavedActions(IActionBars actionBars)
  {
    actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
    actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=252509
    // Delete action (key) doesn't always work.  The saved action could be the 
    // CellEditorActionHandler's DeleteActionHandler...due to timing issues.
    // We'll only restore the delete action if it is indeed the one for the Design view.
    // We should update the other actions too, but currently, none are applicable.
    if (delete instanceof BaseSelectionAction)
    {
      actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
    }
    actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
    actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
    actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
  }

  private void saveCurrentActions(IActionBars actionBars)
  {
    copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
    paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
    IAction currentDeleteAction = actionBars.getGlobalActionHandler(ActionFactory.DELETE.getId());
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=252509
    // Delete action (key) doesn't always work.  The saved action could be the 
    // CellEditorActionHandler's DeleteActionHandler...due to timing issues.
    // We'll only restore the delete action if it is indeed the one for the Design view.
    // We should update the other actions too, but currently, none are applicable.
    if (currentDeleteAction instanceof BaseSelectionAction)
    {
      delete = currentDeleteAction;
    }
    selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
    cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
    find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
    undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
    redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
  }

}