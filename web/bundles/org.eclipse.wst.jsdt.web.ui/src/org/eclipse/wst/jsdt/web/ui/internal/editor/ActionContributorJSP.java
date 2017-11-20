/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.editor;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.html.ui.internal.edit.ui.ActionContributorHTML;
import org.eclipse.wst.jsdt.ui.actions.RefactorActionGroup;
import org.eclipse.wst.jsdt.web.ui.internal.IActionConstantsJs;
import org.eclipse.wst.jsdt.web.ui.internal.IActionDefinitionIdsJs;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class ActionContributorJSP extends ActionContributorHTML {
	private static final String[] EDITOR_IDS = { "org.eclipse.wst.html.core.htmlsource.source", "org.eclipse.wst.sse.ui.StructuredTextEditor" }; //$NON-NLS-1$ //$NON-NLS-2$
	private RetargetTextEditorAction moveElementAction = null;
	private IMenuManager refactorMenu = null;
	private RetargetTextEditorAction renameElementAction = null;
	
	public ActionContributorJSP() {
		super();
		ResourceBundle bundle = JsUIMessages.getResourceBundle();
		this.renameElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJs.ACTION_NAME_RENAME_ELEMENT + StructuredTextEditorActionConstants.UNDERSCORE);
		this.renameElementAction.setActionDefinitionId(IActionDefinitionIdsJs.RENAME_ELEMENT);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.renameElementAction, IHelpContextIds.JSP_REFACTORRENAME_HELPID);
		this.moveElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJs.ACTION_NAME_MOVE_ELEMENT + StructuredTextEditorActionConstants.UNDERSCORE);
		this.moveElementAction.setActionDefinitionId(IActionDefinitionIdsJs.MOVE_ELEMENT);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.moveElementAction, IHelpContextIds.JSP_REFACTORMOVE_HELPID);
		// the refactor menu, add the menu itself to add all refactor actions
		this.refactorMenu = new MenuManager(JsUIMessages.ActionContributorJSP_0, RefactorActionGroup.MENU_ID);
		refactorMenu.add(this.renameElementAction);
		refactorMenu.add(this.moveElementAction);
	}
	
	
	protected void addToMenu(IMenuManager menu) {
		super.addToMenu(menu);
		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, this.refactorMenu);
	}
	
	
	protected String[] getExtensionIDs() {
		return ActionContributorJSP.EDITOR_IDS;
	}
	
	
	public void setActiveEditor(IEditorPart activeEditor) {
		super.setActiveEditor(activeEditor);
		this.renameElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJs.ACTION_NAME_RENAME_ELEMENT));
		this.moveElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJs.ACTION_NAME_MOVE_ELEMENT));
	}
	
	
	public void setViewerSpecificContributionsEnabled(boolean enabled) {
		super.setViewerSpecificContributionsEnabled(enabled);
		this.renameElementAction.setEnabled(enabled);
		this.moveElementAction.setEnabled(enabled);
	}
}
