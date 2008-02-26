/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.internal.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.sse.ui.internal.actions.ActionContributor;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

/**
 * XMLEditorActionContributor
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
public class ActionContributorXML extends ActionContributor {
	private static final String[] EDITOR_IDS = {"org.eclipse.core.runtime.xml.source", "org.eclipse.core.runtime.xml.source2", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	protected RetargetTextEditorAction fCleanupDocument = null;
	protected RetargetTextEditorAction fComment = null;
	protected RetargetTextEditorAction fContentAssist = null;
	protected RetargetTextEditorAction fFindOccurrences = null;
	protected RetargetTextEditorAction fFormatActiveElements = null;
	protected RetargetTextEditorAction fFormatDocument = null;
	protected RetargetTextEditorAction fOpenFileAction = null; // open file

	protected RetargetTextEditorAction fUncomment = null;
	private SiblingNavigationAction fPreviousSibling;
	private SiblingNavigationAction fNextSibling;

	public ActionContributorXML() {
		super();

		ResourceBundle resourceBundle = XMLUIMessages.getResourceBundle();

		fContentAssist = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fContentAssist.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

		// source commands
		fCleanupDocument = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fCleanupDocument.setActionDefinitionId(ActionDefinitionIds.CLEANUP_DOCUMENT);

		fFormatDocument = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fFormatDocument.setActionDefinitionId(ActionDefinitionIds.FORMAT_DOCUMENT);

		fFormatActiveElements = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fFormatActiveElements.setActionDefinitionId(ActionDefinitionIds.FORMAT_ACTIVE_ELEMENTS);

		// navigate commands
		fOpenFileAction = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fOpenFileAction.setActionDefinitionId(ActionDefinitionIds.OPEN_FILE);

		fFindOccurrences = new RetargetTextEditorAction(resourceBundle, ""); //$NON-NLS-1$
		fFindOccurrences.setActionDefinitionId(ActionDefinitionIds.FIND_OCCURRENCES);

		fPreviousSibling = new SiblingNavigationAction(resourceBundle, "previousSibling_", null, false);
		fPreviousSibling.setActionDefinitionId("org.eclipse.wst.xml.ui.previousSibling");
		fNextSibling = new SiblingNavigationAction(resourceBundle, "nextSibling_", null, true);
		fNextSibling.setActionDefinitionId("org.eclipse.wst.xml.ui.nextSibling");
	}

	protected void addToMenu(IMenuManager menu) {
		// edit commands
		IMenuManager editMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(fCommandsSeparator);
			editMenu.add(fToggleInsertModeAction);
			editMenu.add(fCommandsSeparator);
			editMenu.add(fExpandSelectionToMenu);
			editMenu.add(fCommandsSeparator);
			editMenu.add(fContentAssist);
			editMenu.add(fMenuAdditionsGroupMarker);
		}

		// source commands
		String sourceMenuLabel = XMLUIMessages.SourceMenu_label;
		String sourceMenuId = "sourceMenuId"; //$NON-NLS-1$
		IMenuManager sourceMenu = new MenuManager(sourceMenuLabel, sourceMenuId);
		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, sourceMenu);
		if (sourceMenu != null) {
			sourceMenu.add(fCommandsSeparator);
			sourceMenu.add(fToggleComment);
			sourceMenu.add(fAddBlockComment);
			sourceMenu.add(fRemoveBlockComment);
			sourceMenu.add(fShiftRight);
			sourceMenu.add(fShiftLeft);
			sourceMenu.add(fCleanupDocument);
			sourceMenu.add(fFormatDocument);
			sourceMenu.add(fFormatActiveElements);
			sourceMenu.add(fCommandsSeparator);
			sourceMenu.add(fFindOccurrences);
		}

		// navigate commands
		IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fCommandsSeparator);
			navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fOpenFileAction);

			IMenuManager gotoGroup = navigateMenu.findMenuUsingPath(IWorkbenchActionConstants.GO_TO);
			if (gotoGroup != null) {
				gotoGroup.add(fGotoMatchingBracketAction);
				gotoGroup.add(new Separator());
				gotoGroup.add(fPreviousSibling);
				gotoGroup.add(fNextSibling);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.edit.util.ActionContributor#getExtensionIDs()
	 */
	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
	}

	/**
	 * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	public void setActiveEditor(IEditorPart activeEditor) {
		if (getActiveEditorPart() == activeEditor) {
			return;
		}
		super.setActiveEditor(activeEditor);

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
			if (statusLineManager != null) {
				statusLineManager.setMessage(null);
				statusLineManager.setErrorMessage(null);
			}
		}

		ITextEditor textEditor = getTextEditor(activeEditor);

		fContentAssist.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS));

		fCleanupDocument.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT));
		fFormatDocument.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT));
		fFormatActiveElements.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS));
		fCleanupDocument.setEnabled((textEditor != null) && textEditor.isEditable());
		fFormatDocument.setEnabled((textEditor != null) && textEditor.isEditable());
		fFormatActiveElements.setEnabled((textEditor != null) && textEditor.isEditable());

		fOpenFileAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE));

		fFindOccurrences.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES));

		fPreviousSibling.setEditor(textEditor);
		fNextSibling.setEditor(textEditor);
		
		if (actionBars != null) {
			actionBars.setGlobalActionHandler(fPreviousSibling.getActionDefinitionId(), fPreviousSibling);
			actionBars.setGlobalActionHandler(fNextSibling.getActionDefinitionId(), fNextSibling);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.ISourceViewerActionBarContributor#setViewerSpecificContributionsEnabled(boolean)
	 */
	public void setViewerSpecificContributionsEnabled(boolean enabled) {
		super.setViewerSpecificContributionsEnabled(enabled);

		fContentAssist.setEnabled(enabled);
		// cleanup and format document actions do not rely on source viewer
		// being enabled
		// fCleanupDocument.setEnabled(enabled);
		// fFormatDocument.setEnabled(enabled);

		fFormatActiveElements.setEnabled(enabled);
		fOpenFileAction.setEnabled(enabled);
		fFindOccurrences.setEnabled(enabled);
	}
}
