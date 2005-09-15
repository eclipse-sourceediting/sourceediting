/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.provisional;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.search.FindOccurrencesActionProvider;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.actions.AddBlockCommentActionXML;
import org.eclipse.wst.xml.ui.internal.actions.CleanupActionXML;
import org.eclipse.wst.xml.ui.internal.actions.RemoveBlockCommentActionXML;
import org.eclipse.wst.xml.ui.internal.actions.ToggleCommentActionXML;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.xml.ui.internal.search.XMLFindOccurrencesAction;

/**
 * @deprecated XML editor is created from StructuredTextEditor with
 *             XML configurations
 */
public class StructuredTextEditorXML extends StructuredTextEditor {
	private final static String UNDERSCORE = "_"; //$NON-NLS-1$
	
	protected void createActions() {
		super.createActions();

		ResourceBundle resourceBundle = XMLUIMessages.getResourceBundle();

		Action action = new CleanupActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT + UNDERSCORE, this);
		action.setActionDefinitionId(ActionDefinitionIds.CLEANUP_DOCUMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT, action);

		/*
		 * action = new CommentActionXML(resourceBundle,
		 * StructuredTextEditorActionConstants.ACTION_NAME_COMMENT + DOT,
		 * this); action.setActionDefinitionId(ActionDefinitionIds.COMMENT);
		 * setAction(StructuredTextEditorActionConstants.ACTION_NAME_COMMENT,
		 * action);
		 * 
		 * action = new UncommentActionXML(resourceBundle,
		 * StructuredTextEditorActionConstants.ACTION_NAME_UNCOMMENT + DOT,
		 * this); action.setActionDefinitionId(ActionDefinitionIds.UNCOMMENT);
		 * setAction(StructuredTextEditorActionConstants.ACTION_NAME_UNCOMMENT,
		 * action);
		 */

		action = new ToggleCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT + UNDERSCORE, this);
		action.setActionDefinitionId(ActionDefinitionIds.TOGGLE_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT, action);

		action = new AddBlockCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT + UNDERSCORE, this);
		action.setActionDefinitionId(ActionDefinitionIds.ADD_BLOCK_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT, action);

		action = new RemoveBlockCommentActionXML(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT + UNDERSCORE, this);
		action.setActionDefinitionId(ActionDefinitionIds.REMOVE_BLOCK_COMMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT, action);

		FindOccurrencesActionProvider foAction = new FindOccurrencesActionProvider(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES + UNDERSCORE, this);
		foAction.addAction(new XMLFindOccurrencesAction(resourceBundle, "", this)); //$NON-NLS-1$
		foAction.setActionDefinitionId(ActionDefinitionIds.FIND_OCCURRENCES);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, foAction);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES, true);
	}

	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(IHelpContextIds.XML_SOURCEVIEW_HELPID);
	}
}
