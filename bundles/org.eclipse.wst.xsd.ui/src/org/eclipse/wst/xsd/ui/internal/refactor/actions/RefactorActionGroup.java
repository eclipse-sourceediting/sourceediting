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
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDSchema;

/**
 * Action group that adds refactor actions (for example 'Rename', 'Move') to a
 * context menu and the global menu bar.
 * 
 */
public class RefactorActionGroup extends ActionGroup {

	public static final String MENU_ID = "org.eclipse.wst.xsd.ui.refactoring.menu"; //$NON-NLS-1$

	public static final String GROUP_REORG = "reorgGroup"; //$NON-NLS-1$

	public static final String RENAME_ELEMENT = "org.eclipse.wst.xsd.ui.refactor.rename.element"; //$NON-NLS-1$

	public static final String MAKE_ELEMENT_GLOBAL = "org.eclipse.wst.xsd.ui.refactor.makeElementGlobal"; //$NON-NLS-1$
	
	public static final String MAKE_TYPE_GLOBAL = "org.eclipse.wst.xsd.ui.refactor.makeTypeGlobal"; //$NON-NLS-1$

	public static final String RENAME = "org.eclipse.wst.xsd.ui.refactoring.actions.Rename"; //$NON-NLS-1$

	/**
	 * Pop-up menu: name of group for reorganize actions (value
	 * <code>"group.reorganize"</code>).
	 */
	public static final String GROUP_REORGANIZE = IWorkbenchActionConstants.GROUP_REORGANIZE;

	/**
	 * 
	 * @deprecated
	 * 
	 */
	private ISelectionProvider fSelectionProvider;

	private String fGroupName = GROUP_REORGANIZE;

	private SelectionDispatchAction fRenameAction;

	private SelectionDispatchAction fMakeLocalElementGlobal;
	
	private SelectionDispatchAction fMakeLocalTypeGlobal;

	private List fEditorActions;

	private IMenuManager fRefactorSubmenu;

	private static class NoActionAvailable extends Action {
		public NoActionAvailable() {
			setEnabled(true);
			setText(RefactoringMessages
					.getString("RefactorActionGroup.no_refactoring_available")); //$NON-NLS-1$
		}
	}

	private Action fNoActionAvailable = new NoActionAvailable();
	
	public RefactorActionGroup(ISelectionProvider selectionProvider,
			XSDSchema schema, String groupName) {
		this(selectionProvider, schema);
		fGroupName = groupName;
	}
	
	public RefactorActionGroup(ISelectionProvider selectionProvider,
			XSDSchema schema) {
		fSelectionProvider = selectionProvider;
		fEditorActions = new ArrayList();
		fRenameAction = new RenameAction(selectionProvider, schema);
		fRenameAction.setActionDefinitionId(RENAME_ELEMENT);
		fEditorActions.add(fRenameAction);

		fMakeLocalElementGlobal = new MakeLocalElementGlobalAction(
				selectionProvider, schema);
		fMakeLocalElementGlobal.setActionDefinitionId(MAKE_ELEMENT_GLOBAL);
		fEditorActions.add(fMakeLocalElementGlobal);
		
		fMakeLocalTypeGlobal = new MakeAnonymousTypeGlobalAction(
				selectionProvider, schema);
		fMakeLocalTypeGlobal.setActionDefinitionId(MAKE_TYPE_GLOBAL);
		fEditorActions.add(fMakeLocalTypeGlobal);

		initAction(fRenameAction, fSelectionProvider);
		initAction(fMakeLocalElementGlobal, fSelectionProvider);
		initAction(fMakeLocalTypeGlobal, fSelectionProvider);
	}

	private static void initAction(SelectionDispatchAction action,
			ISelectionProvider provider) {

		Assert.isNotNull(provider);
		Assert.isNotNull(action);
		action.update(provider.getSelection());
		provider.addSelectionChangedListener(action);
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(RENAME, fRenameAction);
		retargetFileMenuActions(actionBars);
	}

	/**
	 * Retargets the File actions with the corresponding refactoring actions.
	 * 
	 * @param actionBars
	 *            the action bar to register the move and rename action with
	 */
	public void retargetFileMenuActions(IActionBars actionBars) {
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
				fRenameAction);
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		addRefactorSubmenu(menu);
	}

	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		disposeAction(fRenameAction, fSelectionProvider);
		disposeAction(fMakeLocalElementGlobal, fSelectionProvider);
		super.dispose();
	}

	private void disposeAction(ISelectionChangedListener action,
			ISelectionProvider provider) {
		if (action != null)
			provider.removeSelectionChangedListener(action);
	}

	private void addRefactorSubmenu(IMenuManager menu) {

		String menuText = RefactoringMessages.getString("RefactorMenu.label"); //$NON-NLS-1$

		fRefactorSubmenu = new MenuManager(menuText, MENU_ID);
		//if (fEditor != null) {
			fRefactorSubmenu.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					refactorMenuShown(manager);
				}
			});
			fRefactorSubmenu.add(fNoActionAvailable);
			if (menu.find(fRefactorSubmenu.getId()) == null) {
				if (menu.find(fGroupName) == null) {
					menu.add(fRefactorSubmenu);
				} else {
					menu.appendToGroup(fGroupName, fRefactorSubmenu);
				}
			}
//		} else {
//			if (fillRefactorMenu(fRefactorSubmenu) > 0)
//				menu.appendToGroup(fGroupName, fRefactorSubmenu);
//		}
	}

	private int fillRefactorMenu(IMenuManager refactorSubmenu) {
		int added = 0;
		refactorSubmenu.add(new Separator(GROUP_REORG));
		for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
			Action action = (Action) iter.next();
			added += addAction(refactorSubmenu, action);
		}
		return added;
	}

	private int addAction(IMenuManager menu, IAction action) {
		if (action != null && action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}

	public int addAction(IAction action) {
		if (action != null && action.isEnabled()) {
			fEditorActions.add(action);
			return 1;
		}
		return 0;
	}

	private void refactorMenuShown(final IMenuManager refactorSubmenu) {
		// we know that we have an MenuManager since we created it in
		// addRefactorSubmenu.
		Menu menu = ((MenuManager) refactorSubmenu).getMenu();
		menu.addMenuListener(new MenuAdapter() {
			public void menuHidden(MenuEvent e) {
				refactorMenuHidden(refactorSubmenu);
			}
		});
//		ISelection selection = (ISelection) fEditor.getSelectionManager()
//				.getSelection();
		ISelection selection = fSelectionProvider.getSelection();
		for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
			Action action = (Action) iter.next();
			if (action instanceof SelectionDispatchAction) {
				SelectionDispatchAction selectionAction = (SelectionDispatchAction) action;
				selectionAction.update(selection);
			}
		}
		refactorSubmenu.removeAll();
		if (fillRefactorMenu(refactorSubmenu) == 0)
			refactorSubmenu.add(fNoActionAvailable);
	}

	private void refactorMenuHidden(IMenuManager manager) {
//		ISelection selection = (ISelection) fEditor.getSelectionManager()
//				.getSelection();
		ISelection selection = fSelectionProvider.getSelection();
		for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
			Action action = (Action) iter.next();
			if (action instanceof SelectionDispatchAction) {
				SelectionDispatchAction selectionAction = (SelectionDispatchAction) action;
				selectionAction.update(selection);
			}

		}
	}

}
