/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.sse.ui.ui.ActionContributor;
import org.eclipse.wst.sse.ui.ui.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.ui.StructuredTextEditorActionConstants;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


/**
 * XMLEditorActionContributor
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
public class ActionContributorXML extends ActionContributor {
	private static final String[] EDITOR_IDS = {"org.eclipse.wst.xml.ui.StructuredTextEditorXML", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$

	protected RetargetTextEditorAction fShowTooltipAction = null; // show tooltip action
	protected RetargetTextEditorAction fContentAssist = null;
	protected RetargetTextEditorAction fQuickFix = null;
	protected RetargetTextEditorAction fComment = null;
	protected RetargetTextEditorAction fUncomment = null;
	protected RetargetTextEditorAction fCleanupDocument = null;
	protected MenuManager fFormatMenu = null;
	protected RetargetTextEditorAction fFormatDocument = null;
	protected RetargetTextEditorAction fFormatActiveElements = null;
	protected RetargetTextEditorAction fOpenFileAction = null; // open file action
	protected RetargetTextEditorAction fFindOccurrences = null;

	public ActionContributorXML() {
		super();

		ResourceBundle resourceBundle = ResourceHandler.getResourceBundle();

		// edit commands
		fShowTooltipAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_INFORMATION + StructuredTextEditorActionConstants.DOT);
		fShowTooltipAction.setActionDefinitionId(ActionDefinitionIds.INFORMATION);

		fContentAssist = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS + StructuredTextEditorActionConstants.DOT);
		fContentAssist.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

		fQuickFix = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_QUICK_FIX + StructuredTextEditorActionConstants.DOT);
		fQuickFix.setActionDefinitionId(ActionDefinitionIds.QUICK_FIX);

		// source commands
		fCleanupDocument = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT + StructuredTextEditorActionConstants.DOT);
		fCleanupDocument.setActionDefinitionId(ActionDefinitionIds.CLEANUP_DOCUMENT);

		fFormatDocument = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT + StructuredTextEditorActionConstants.DOT);
		fFormatDocument.setActionDefinitionId(ActionDefinitionIds.FORMAT_DOCUMENT);

		fFormatActiveElements = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS + StructuredTextEditorActionConstants.DOT);
		fFormatActiveElements.setActionDefinitionId(ActionDefinitionIds.FORMAT_ACTIVE_ELEMENTS);

		fFormatMenu = new MenuManager(ResourceHandler.getString("FormatMenu.label")); //$NON-NLS-1$
		fFormatMenu.add(fFormatDocument);
		fFormatMenu.add(fFormatActiveElements);

		// navigate commands
		fOpenFileAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE + StructuredTextEditorActionConstants.DOT);
		fOpenFileAction.setActionDefinitionId(ActionDefinitionIds.OPEN_FILE);

		fFindOccurrences = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES + StructuredTextEditorActionConstants.DOT);
		fFindOccurrences.setActionDefinitionId(ActionDefinitionIds.FIND_OCCURRENCES);
	}

	/* (non-Javadoc)
	 */
	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
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
			editMenu.add(fShowTooltipAction);
			editMenu.add(fContentAssist);
			editMenu.add(fQuickFix);
			editMenu.add(fMenuAdditionsGroupMarker);
		}

		// source commands
		String sourceMenuLabel = ResourceHandler.getString("SourceMenu.label"); //$NON-NLS-1$
		String sourceMenuId = "sourceMenuId"; // This is just a menu id. No need to translate. //$NON-NLS-1$
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
			sourceMenu.add(fFormatMenu);
			sourceMenu.add(fCommandsSeparator);
			sourceMenu.add(fConvertDelimitersMenu);
			sourceMenu.add(fFindOccurrences);
		}

		// navigate commands
		IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fCommandsSeparator);
			navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fOpenFileAction);
		}
	}

	/**
	 * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	public void setActiveEditor(IEditorPart activeEditor) {
		if (getActiveEditorPart() == activeEditor)
			return;
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

		fShowTooltipAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_INFORMATION));
		fContentAssist.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS));
		fQuickFix.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_QUICK_FIX));

		fCleanupDocument.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT));
		fFormatDocument.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT));
		fFormatActiveElements.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS));

		fOpenFileAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE));

		fFindOccurrences.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_FIND_OCCURRENCES));
	}

	/* (non-Javadoc)
	 */
	public void setViewerSpecificContributionsEnabled(boolean enabled) {
		super.setViewerSpecificContributionsEnabled(enabled);

		fShowTooltipAction.setEnabled(enabled);
		fContentAssist.setEnabled(enabled);
		fQuickFix.setEnabled(enabled);
		// cleanup and format document actions do not rely on source viewer being enabled
		//		fCleanupDocument.setEnabled(enabled);
		//		fFormatDocument.setEnabled(enabled);

		fFormatActiveElements.setEnabled(enabled);
		fOpenFileAction.setEnabled(enabled);
		fFindOccurrences.setEnabled(enabled);
	}
}
