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
package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.wst.xsd.ui.internal.actions.IXSDToolbarAction;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class XSDMultiPageEditorContributor extends MultiPageEditorActionBarContributor
{
  private IEditorPart activeEditorPart;

  /**
   * Creates a multi-page contributor.
   */
  public XSDMultiPageEditorContributor()
  {
    super();
  }

  /**
   * Returns the action registed with the given text editor.
   * 
   * @return IAction or null if editor is null.
   */
  protected IAction getAction(ITextEditor editor, String actionID)
  {
    return (editor == null ? null : editor.getAction(actionID));
  }

  /*
   * (non-JavaDoc) Method declared in
   * AbstractMultiPageEditorActionBarContributor.
   */

  public void setActivePage(IEditorPart part)
  {
    if (activeEditorPart == part)
      return;

    activeEditorPart = part;

    IActionBars actionBars = getActionBars();

    if (part != null)
    {
      Object adapter = part.getAdapter(ActionRegistry.class);
      if (adapter instanceof ActionRegistry)
      {
        ActionRegistry registry = (ActionRegistry) adapter;
        actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), registry.getAction(ActionFactory.UNDO.getId()));
        actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), registry.getAction(ActionFactory.REDO.getId()));
        actionBars.updateActionBars();
      }
    }

    if (actionBars != null)
    {

      ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

      actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor, ITextEditorActionConstants.DELETE));
      actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor, ITextEditorActionConstants.UNDO));
      actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor, ITextEditorActionConstants.REDO));
      actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor, ITextEditorActionConstants.CUT));
      actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor, ITextEditorActionConstants.COPY));
      actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor, ITextEditorActionConstants.PASTE));
      actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor, ITextEditorActionConstants.SELECT_ALL));
      actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor, ITextEditorActionConstants.FIND));
      actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor, IDEActionFactory.BOOKMARK.getId()));

      actionBars.updateActionBars();
    }
  }

  public void setActiveEditor(IEditorPart part)
  {
    IEditorPart activeNestedEditor = null;
    if (part instanceof MultiPageEditorPart)
    {
      activeNestedEditor = part;
    }
    setActivePage(activeNestedEditor);
    List list = XSDEditorPlugin.getPlugin().getXSDEditorConfiguration().getToolbarActions();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      ((IXSDToolbarAction)i.next()).setEditorPart(activeNestedEditor);
    }
  }

  public void contributeToMenu(IMenuManager manager)
  {
    IMenuManager menu = new MenuManager(Messages._UI_MENU_XSD_EDITOR);
    manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);

    // Add extension menu actions
    List list = XSDEditorPlugin.getPlugin().getXSDEditorConfiguration().getToolbarActions();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      menu.add((IXSDToolbarAction)i.next());
    }

    menu.add((new ZoomInRetargetAction()));
    menu.add((new ZoomOutRetargetAction()));

    menu.updateAll(true);
  }

  public void contributeToToolBar(IToolBarManager manager)
  {
    manager.add(new Separator());
    // Add extension toolbar actions
    List list = XSDEditorPlugin.getPlugin().getXSDEditorConfiguration().getToolbarActions();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      manager.add((IXSDToolbarAction)i.next());
    }

    manager.add(new Separator());
    String[] zoomStrings = new String[] { ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH };
    manager.add(new ZoomComboContributionItem(getPage(), zoomStrings));
  }
}
