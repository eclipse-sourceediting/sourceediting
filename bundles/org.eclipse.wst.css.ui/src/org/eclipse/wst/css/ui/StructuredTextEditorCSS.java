/*****************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.css.ui.edit.ui.CleanupActionCSS;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectEnclosingCSSAction;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectNextCSSAction;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectPreviousCSSAction;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.edit.util.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.edit.util.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectHistoryAction;

public class StructuredTextEditorCSS extends StructuredTextEditor {
	protected void createActions() {
		super.createActions();

		ResourceBundle resourceBundle = CSSUIPlugin.getDefault().getResourceBundle();

		Action action = new CleanupActionCSS(SSEUIPlugin.getDefault().getResourceBundle(), StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.CLEANUP_DOCUMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT, action);

		SelectionHistory selectionHistory = new SelectionHistory(this);
		action = new StructureSelectEnclosingCSSAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_ENCLOSING);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_ENCLOSING, action);

		action = new StructureSelectNextCSSAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_NEXT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_NEXT, action);

		action = new StructureSelectPreviousCSSAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_PREVIOUS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_PREVIOUS, action);

		action = new StructureSelectHistoryAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_HISTORY);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_HISTORY, action);
		selectionHistory.setHistoryAction((StructureSelectHistoryAction) action);
	}

	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(IHelpContextIds.CSS_SOURCEVIEW_HELPID);
	}
}