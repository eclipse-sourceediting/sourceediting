/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.html.ui.edit.ui.ActionContributorHTML;
import org.eclipse.wst.sse.ui.edit.util.StructuredTextEditorActionConstants;

/**
 * ActionContributorJSP
 * 
 * This class should not be used inside multi page editor's ActionBarContributor,
 * since cascaded init() call from the ActionBarContributor
 * will causes exception and it leads to lose whole toolbars. 
 *
 * Instead, use SourcePageActionContributor for source page contributor
 * of multi page editor.
 * 
 * Note that this class is still valid for single page editor.
 */
public class ActionContributorJSP extends ActionContributorHTML {
	
	private RetargetTextEditorAction renameElementAction = null;
	private RetargetTextEditorAction moveElementAction = null;
	private IMenuManager refactorMenu = null;
	
	private static final String[] EDITOR_IDS = {"org.eclipse.jst.jsp.ui.StructuredTextEditorJSP", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$

	public ActionContributorJSP() {
		super();

		ResourceBundle bundle = JSPUIPlugin.getDefault().getResourceBundle();
		this.renameElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT + StructuredTextEditorActionConstants.DOT);
		this.renameElementAction.setActionDefinitionId(IActionDefinitionIdsJSP.RENAME_ELEMENT);
		
		this.moveElementAction = new RetargetTextEditorAction(bundle, IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT + StructuredTextEditorActionConstants.DOT);
		this.moveElementAction.setActionDefinitionId(IActionDefinitionIdsJSP.MOVE_ELEMENT);
		
		// the refactor menu, add the menu itself to add all refactor actions
		this.refactorMenu = new MenuManager(JSPUIPlugin.getResourceString("%ActionContributorJSP.0"), RefactorActionGroup.MENU_ID); //$NON-NLS-1$
		refactorMenu.add(this.renameElementAction);
		refactorMenu.add(this.moveElementAction);
	}
	
	/**
	 * @see com.ibm.sse.editor.ui.ActionContributor#getExtensionIDs()
	 */
	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
	}
	
	/**
	 * @see com.ibm.sse.editor.xml.ui.actions.ActionContributorXML#addToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void addToMenu(IMenuManager menu) {
		super.addToMenu(menu);

		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, this.refactorMenu);
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.ui.actions.ActionContributorXML#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IEditorPart activeEditor) {
		
		super.setActiveEditor(activeEditor);
		this.renameElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT));
		this.moveElementAction.setAction(getAction(getTextEditor(getActiveEditorPart()), IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT));
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.ui.actions.ActionContributorXML#setViewerSpecificContributionsEnabled(boolean)
	 */
	public void setViewerSpecificContributionsEnabled(boolean enabled) {
		
		super.setViewerSpecificContributionsEnabled(enabled);
		this.renameElementAction.setEnabled(enabled);
		this.moveElementAction.setEnabled(enabled);
	}
}
