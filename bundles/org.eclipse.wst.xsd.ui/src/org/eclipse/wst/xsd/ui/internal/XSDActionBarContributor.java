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
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.wst.xsd.ui.internal.actions.ISchemaEditorActionConstants;
import org.eclipse.wst.xsd.ui.internal.actions.ReloadDependenciesAction;

public class XSDActionBarContributor extends MultiPageEditorActionBarContributor
{
  protected XSDEditor xsdEditor;
  protected XSDTextEditor textEditor;

  protected ReloadDependenciesAction reloadDependenciesAction;
  
  protected List fPartListeners= new ArrayList();

  protected RetargetAction retargetReloadDependenciesAction;
 
  /**
   * Constructor for XSDActionBarContributor.
   */
  public XSDActionBarContributor()
  {
    super();

    // Reload Dependencies
    reloadDependenciesAction = new ReloadDependenciesAction(XSDEditorPlugin.getXSDString("_UI_MENU_RELOAD_DEPENDENCIES"));
    retargetReloadDependenciesAction = new RetargetAction(ISchemaEditorActionConstants.RETARGET_RELOAD_DEPENDENCIES_ACTION_ID, XSDEditorPlugin.getXSDString("_UI_MENU_RELOAD_DEPENDENCIES"));
    retargetReloadDependenciesAction.setToolTipText(XSDEditorPlugin.getXSDString("_UI_MENU_RELOAD_DEPENDENCIES_TOOLTIP"));
    retargetReloadDependenciesAction.setImageDescriptor(
        ImageDescriptor.createFromFile(XSDEditorPlugin.getPlugin().getClass(), "icons/reloadgrammar.gif"));
    fPartListeners.add(retargetReloadDependenciesAction);
  }

  protected void updateActions()
  {
    if (xsdEditor != null && xsdEditor.getCurrentPageType().equals(XSDEditorPlugin.GRAPH_PAGE))
    {
      IAction deleteAction = xsdEditor.getGraphViewer().getComponentViewer().getMenuListener().getDeleteAction();
      getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);

      IAction printGraphAction = xsdEditor.getGraphViewer().getPrintGraphAction();
      getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printGraphAction);
    }
    else
    {
      getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), null);      
      // always enable print regardless of whether we are on source or design
      updateAction(ActionFactory.PRINT.getId(), ITextEditorActionConstants.PRINT, true);
    }
  }
      
  public void setActivePage(IEditorPart activeEditor)
  {
    updateActions();
    getActionBars().updateActionBars();
  }

  protected void updateAction(String globalActionId, String textEditorActionId, boolean enable)
  {
    getActionBars().setGlobalActionHandler(globalActionId,
                                      enable ? getAction(textEditor, textEditorActionId) :
                                               null);
  }

  /**
   * Returns the action registed with the given text editor.
   * @return IAction or null if editor is null.
   */
  protected IAction getAction(ITextEditor editor, String actionID)
  {
    try
    {
      return (editor == null ? null : editor.getAction(actionID));
    }
    catch (Exception e)
    {
      return null;
    }
  }

  private IMenuManager editMenu;

  public void addToMenu(IMenuManager menuManager)
  {
    editMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);

    MenuManager treeMenu = new MenuManager(XSDEditorPlugin.getXSDString("_UI_MENU_XSD_EDITOR"));
    menuManager.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, treeMenu);

    treeMenu.add(new Separator("group1"));
//  Add retarget actions

    treeMenu.add(retargetReloadDependenciesAction);

    treeMenu.add(new Separator("group2"));
  }


public void addToToolBar(IToolBarManager toolBarManager)
  {
    toolBarManager.add(new Separator("XMLSchema.2"));
//  Add retarget actions
    toolBarManager.add(retargetReloadDependenciesAction);

    toolBarManager.add(new Separator("XMLSchema.1"));

    toolBarManager.add(new Separator());
  }
  
  public void contributeToToolBar(IToolBarManager toolBarManager)
  {
    addToToolBar(toolBarManager);
  }
  
  public void contributeToMenu(IMenuManager menuManager)
  {
    addToMenu(menuManager);
  }
  
  /**
   * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(IEditorPart)
   */
  public void setActiveEditor(IEditorPart targetEditor)
  {
    super.setActiveEditor(targetEditor);

    if (targetEditor instanceof XSDEditor)
    {
      xsdEditor = (XSDEditor) targetEditor;
      reloadDependenciesAction.setEditor((XSDEditor)targetEditor);

      textEditor = ((XSDEditor)targetEditor).getXSDTextEditor();
      if (textEditor != null)
      {      
        updateActions();  
        getActionBars().updateActionBars();
      }
    }
  }

  public void init(IActionBars bars, IWorkbenchPage page)
  {
    Iterator e = fPartListeners.iterator();
    while (e.hasNext())
    {
      page.addPartListener((RetargetAction) e.next());
    }

    // register actions that have a dynamic editor. 

    bars.setGlobalActionHandler(ISchemaEditorActionConstants.RETARGET_RELOAD_DEPENDENCIES_ACTION_ID, reloadDependenciesAction);

    super.init(bars, page);
  }
}
