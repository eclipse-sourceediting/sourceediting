/*****************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.css.ui.internal.edit.ui.CleanupActionCSS;
import org.eclipse.wst.css.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectEnclosingCSSAction;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectNextCSSAction;
import org.eclipse.wst.css.ui.internal.selection.StructureSelectPreviousCSSAction;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectHistoryAction;

public class StructuredTextEditorCSS extends StructuredTextEditor {
	private static final String UNDERSCORE = "_"; //$NON-NLS-1$
	
	protected void createActions() {
		super.createActions();

		Action action = new CleanupActionCSS(CSSUIMessages.getResourceBundle(), StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT + UNDERSCORE, this);
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