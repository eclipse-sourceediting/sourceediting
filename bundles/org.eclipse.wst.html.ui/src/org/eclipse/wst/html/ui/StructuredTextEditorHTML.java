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
package org.eclipse.wst.html.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.html.ui.edit.ui.CleanupActionHTML;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.html.ui.internal.search.HTMLFindOccurrencesAction;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.search.FindOccurrencesActionProvider;
import org.eclipse.wst.xml.ui.actions.AddBlockCommentActionXML;
import org.eclipse.wst.xml.ui.actions.RemoveBlockCommentActionXML;
import org.eclipse.wst.xml.ui.actions.ToggleCommentActionXML;

public class StructuredTextEditorHTML extends StructuredTextEditor {
	protected void createActions() {
		super.createActions();

		ResourceBundle resourceBundle = SSEUIPlugin.getDefault().getResourceBundle();

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
		foAction.setActionDefinitionId(ActionDefinitionIds.FIND_OCCURRENCES);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, foAction);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, true);
	}
	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(IHelpContextIds.HTML_SOURCEVIEW_HELPID);
	}
}