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
package org.eclipse.jst.jsp.ui;

import java.util.ResourceBundle;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.ui.internal.IActionConstantsJSP;
import org.eclipse.jst.jsp.ui.internal.editor.IHelpContextIds;
import org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPMoveElementAction;
import org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPRenameElementAction;
import org.eclipse.jst.jsp.ui.internal.java.search.JSPFindOccurrencesAction;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.wst.html.ui.edit.ui.CleanupActionHTML;
import org.eclipse.wst.html.ui.internal.search.HTMLFindOccurrencesAction;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.edit.util.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.edit.util.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.search.FindOccurrencesActionProvider;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.ui.actions.AddBlockCommentActionXML;
import org.eclipse.wst.xml.ui.actions.RemoveBlockCommentActionXML;
import org.eclipse.wst.xml.ui.actions.ToggleCommentActionXML;

public class StructuredTextEditorJSP extends StructuredTextEditor {

	protected void createActions() {
		super.createActions();

		ResourceBundle resourceBundle = ResourceHandler.getResourceBundle();

		Action action = new CleanupActionHTML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.CLEANUP_DOCUMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT, action);

		action = new ToggleCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.TOGGLE_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT, action);

		action = new AddBlockCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.ADD_BLOCK_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT, action);

		action = new RemoveBlockCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.REMOVE_BLOCK_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT, action);

		FindOccurrencesActionProvider foAction = new FindOccurrencesActionProvider(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES + DOT, this);
		foAction.addAction(new HTMLFindOccurrencesAction(resourceBundle, "", this)); //$NON-NLS-1$
		foAction.addAction(new JSPFindOccurrencesAction(resourceBundle, "", this)); //$NON-NLS-1$
		foAction.setActionDefinitionId(ActionDefinitionIds.FIND_OCCURRENCES);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, foAction);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, true);

		JSPRenameElementAction renameAction = new JSPRenameElementAction(org.eclipse.jst.jsp.ui.internal.nls.ResourceHandler.getResourceBundle(), IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT + DOT, this);
		setAction(IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT, renameAction);
		markAsSelectionDependentAction(IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT, true);

		JSPMoveElementAction moveAction = new JSPMoveElementAction(org.eclipse.jst.jsp.ui.internal.nls.ResourceHandler.getResourceBundle(), IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT + DOT, this);
		setAction(IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT, moveAction);
		markAsSelectionDependentAction(IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT, true);
	}

	/**
	 * Uses JSPTranslation to get currently selected Java elements.
	 * 
	 * @return currently selected IJavaElements
	 */
	public IJavaElement[] getJavaElementsForCurrentSelection() {

		IJavaElement[] elements = new IJavaElement[0];
		// get JSP translation object for this viewer's document
		XMLModel xmlModel = (XMLModel) StructuredModelManager.getInstance().getModelManager().getExistingModelForRead(getDocument());
		try {
			if(xmlModel != null) {
				XMLDocument xmlDoc = xmlModel.getDocument();
	
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				if (adapter != null) {
					JSPTranslation translation = adapter.getJSPTranslation();
					Point selected = getSelectionRange();
					elements = translation.getElementsFromJspRange(selected.x, selected.x + selected.y);
				}
			}
		}
		finally {
			if (xmlModel != null)
				xmlModel.releaseFromRead();
		}
		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.StructuredTextEditor#addContextMenuActions(org.eclipse.jface.action.IMenuManager)
	 */
	protected void addContextMenuActions(IMenuManager menu) {

		super.addContextMenuActions(menu);

		if (getSourceViewer().isEditable()) {
			String label = org.eclipse.jst.jsp.ui.internal.nls.ResourceHandler.getString("Refactor.label"); //$NON-NLS-1$ = "Format"
			MenuManager subMenu = new MenuManager(label, "Refactor"); // menu id //$NON-NLS-1$
			addAction(subMenu, IActionConstantsJSP.ACTION_NAME_RENAME_ELEMENT);
			addAction(subMenu, IActionConstantsJSP.ACTION_NAME_MOVE_ELEMENT);
			subMenu.add(new GroupMarker(GROUP_NAME_ADDITIONS));
			menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, subMenu);
		}
	}
	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(IHelpContextIds.JSP_SOURCEVIEW_HELPID);
	}
}