/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.editor;

import java.util.ResourceBundle;

import org.eclipse.jdt.ui.actions.RefactorActionGroup;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jst.jsp.ui.internal.IActionConstantsJSP;
import org.eclipse.jst.jsp.ui.internal.IActionDefinitionIdsJSP;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.html.ui.internal.edit.ui.ActionContributorHTML;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;

/**
 * ActionContributorJSP
 * 
 * This class should not be used inside multi page editor's
 * ActionBarContributor, since cascaded init() call from the
 * ActionBarContributor will causes exception and it leads to lose whole
 * toolbars.
 * 
 * Instead, use SourcePageActionContributor for source page contributor of
 * multi page editor.
 * 
 * Note that this class is still valid for single page editor.
 */
public class ActionContributorJSP extends ActionContributorHTML {

	private RetargetTextEditorAction renameElementAction = null;
	private RetargetTextEditorAction moveElementAction = null;
	private IMenuManager refactorMenu = null;

	private static final String[] EDITOR_IDS = {"org.eclipse.jst.jsp.core.jspsource.source", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$

	public ActionContributorJSP() {
		super();

		ResourceBundle bundle = JSPUIMessages.getResourceBundle();
		this.renameElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT + StructuredTextEditorActionConstants.UNDERSCORE);
		this.renameElementAction.setActionDefinitionId(IActionDefinitionIdsJSP.RENAME_ELEMENT);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.renameElementAction, IHelpContextIds.JSP_REFACTORRENAME_HELPID);

		this.moveElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT + StructuredTextEditorActionConstants.UNDERSCORE);
		this.moveElementAction.setActionDefinitionId(IActionDefinitionIdsJSP.MOVE_ELEMENT);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.moveElementAction, IHelpContextIds.JSP_REFACTORMOVE_HELPID);

		// the refactor menu, add the menu itself to add all refactor actions
		this.refactorMenu = new MenuManager(JSPUIMessages.ActionContributorJSP_0, RefactorActionGroup.MENU_ID); //$NON-NLS-1$
		refactorMenu.add(this.renameElementAction);
		refactorMenu.add(this.moveElementAction);
	}


	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
	}


	protected void addToMenu(IMenuManager menu) {
		super.addToMenu(menu);

		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, this.refactorMenu);
	}


	public void setActiveEditor(IEditorPart activeEditor) {

		super.setActiveEditor(activeEditor);
		this.renameElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT));
		this.moveElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT));
	}


	public void setViewerSpecificContributionsEnabled(boolean enabled) {

		super.setViewerSpecificContributionsEnabled(enabled);
		this.renameElementAction.setEnabled(enabled);
		this.moveElementAction.setEnabled(enabled);
	}
}
