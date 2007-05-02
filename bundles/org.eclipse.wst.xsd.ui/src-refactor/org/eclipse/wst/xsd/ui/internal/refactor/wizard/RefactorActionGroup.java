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
package org.eclipse.wst.xsd.ui.internal.refactor.wizard;

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
import org.eclipse.wst.xsd.ui.internal.refactor.actions.RenameAction;
import org.eclipse.wst.xsd.ui.internal.refactor.actions.SelectionDispatchAction;

/**
 * Action group that adds refactor actions (for example 'Rename', 'Move') to a
 * context menu and the global menu bar.
 * 
 */
public abstract class RefactorActionGroup extends ActionGroup {
	
	private static class NoActionAvailable extends Action {
		public NoActionAvailable() {
			setEnabled(true);
			setText(RefactoringWizardMessages.RefactorActionGroup_no_refactoring_available); 
		}
	}

	/**
	 * Pop-up menu: name of group for reorganize actions (value
	 * <code>"group.reorganize"</code>).
	 */
	public static final String GROUP_REORGANIZE = IWorkbenchActionConstants.GROUP_REORGANIZE;

	public static final String MENU_ID = "org.eclipse.wst.xsd.ui.refactoring.menu"; //$NON-NLS-1$

	public static final String RENAME = "org.eclipse.wst.xsd.ui.refactoring.actions.Rename"; //$NON-NLS-1$


	protected static void initAction(SelectionDispatchAction action,
			ISelection selection) {

		Assert.isNotNull(selection);
		Assert.isNotNull(action);
		action.update(selection);
		//provider.addSelectionChangedListener(action);
	}

	protected List fEditorActions;

	private String fGroupName = GROUP_REORGANIZE;
	
	private Action fNoActionAvailable = new NoActionAvailable();

	protected RenameAction fRenameAction;

	protected SelectionDispatchAction fRenameTargetNamespace;

	protected ISelection selection;
	
	public RefactorActionGroup(ISelection selection) {
			this.selection = selection;
			
	}

	public int addAction(IAction action) {
		if (action != null && action.isEnabled()) {
			fEditorActions.add(action);
			return 1;
		}
		return 0;
	}

	private void addRefactorSubmenu(IMenuManager menu) {

		IMenuManager refactorSubmenu = new MenuManager(RefactoringWizardMessages.RefactorMenu_label, MENU_ID);
		refactorSubmenu.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					refactorMenuShown(manager);
				}
			});
		refactorSubmenu.add(fNoActionAvailable);
			if (menu.find(refactorSubmenu.getId()) == null) {
				if (menu.find(fGroupName) == null) {
					menu.add(refactorSubmenu);
				} else {
					menu.appendToGroup(fGroupName, refactorSubmenu);
				}
			}
	}

	protected void disposeAction(ISelectionChangedListener action,
			ISelectionProvider provider) {
		if (action != null)
			provider.removeSelectionChangedListener(action);
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(RENAME, fRenameAction);
		retargetFileMenuActions(actionBars);
	}

	public void fillActions(List enabledActions) {
		
		if(selection != null && fEditorActions != null){
			for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
				Action action = (Action) iter.next();
				if (action instanceof SelectionDispatchAction) {
					SelectionDispatchAction selectionAction = (SelectionDispatchAction) action;
					selectionAction.update(selection);
				}

			}
			for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
				Action action = (Action) iter.next();
				if (action != null) {
					enabledActions.add(action);
				}
			}
		}
		
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		addRefactorSubmenu(menu);
	}

	private int fillRefactorMenu(IMenuManager refactorSubmenu) {
		int added = 0;
		refactorSubmenu.add(new Separator(GROUP_REORGANIZE));
		for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
			Action action = (Action) iter.next();
			if (action != null && action.isEnabled()) {
				fEditorActions.add(action);
				return 1;
			}
		}
		return added;
	}

	private void refactorMenuHidden(IMenuManager manager) {

		for (Iterator iter = fEditorActions.iterator(); iter.hasNext();) {
			Action action = (Action) iter.next();
			if (action instanceof SelectionDispatchAction) {
				SelectionDispatchAction selectionAction = (SelectionDispatchAction) action;
				selectionAction.update(selection);
			}

		}
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


}
