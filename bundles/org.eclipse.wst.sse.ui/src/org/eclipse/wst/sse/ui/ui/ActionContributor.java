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
package org.eclipse.wst.sse.ui.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.sse.ui.GotoAnnotationAction;
import org.eclipse.wst.sse.ui.ISourceViewerActionBarContributor;
import org.eclipse.wst.sse.ui.extension.ExtendedEditorActionBuilder;
import org.eclipse.wst.sse.ui.extension.IExtendedContributor;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


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
public class ActionContributor extends BasicTextEditorActionContributor implements ISourceViewerActionBarContributor, IExtendedContributor {
	private static final String[] EDITOR_IDS = {"org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$

	protected Separator fCommandsSeparator = null;
	protected MenuManager fExpandSelectionToMenu = null;
	protected RetargetTextEditorAction fStructureSelectEnclosingAction = null;
	protected RetargetTextEditorAction fStructureSelectNextAction = null;
	protected RetargetTextEditorAction fStructureSelectPreviousAction = null;
	protected RetargetTextEditorAction fStructureSelectHistoryAction = null;
	protected RetargetTextEditorAction fShiftRight = null;
	protected RetargetTextEditorAction fShiftLeft = null;
	protected MenuManager fConvertDelimitersMenu = null; // convert line delimiters submenu
	protected RetargetTextEditorAction fConvertToWindows; // convert to windows action
	protected RetargetTextEditorAction fConvertToUNIX; // convert to unix action
	protected RetargetTextEditorAction fConvertToMac; // convert to mac action
	
	protected RetargetTextEditorAction fToggleComment = null;
	protected RetargetTextEditorAction fAddBlockComment = null;
	protected RetargetTextEditorAction fRemoveBlockComment = null;

	protected GotoAnnotationAction fPreviousAnnotation = null;
	protected GotoAnnotationAction fNextAnnotation = null;
	protected Separator fToolbarSeparator = null;
	protected GroupMarker fToolbarAdditionsGroupMarker = null;
	protected GroupMarker fMenuAdditionsGroupMarker = null;
	protected IExtendedContributor extendedContributor;
	protected RetargetTextEditorAction fToggleInsertModeAction;

	public ActionContributor() {
		super();

		ResourceBundle resourceBundle = ResourceHandler.getResourceBundle();

		fCommandsSeparator = new Separator();

		// edit commands
		fStructureSelectEnclosingAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_ENCLOSING + StructuredTextEditorActionConstants.DOT);
		fStructureSelectEnclosingAction.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_ENCLOSING);
		
		fStructureSelectNextAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_NEXT + StructuredTextEditorActionConstants.DOT);
		fStructureSelectNextAction.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_NEXT);
		
		fStructureSelectPreviousAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_PREVIOUS + StructuredTextEditorActionConstants.DOT);
		fStructureSelectPreviousAction.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_PREVIOUS);
		
		fStructureSelectHistoryAction = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_HISTORY + StructuredTextEditorActionConstants.DOT);
		fStructureSelectHistoryAction.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_HISTORY);
		
		fExpandSelectionToMenu = new MenuManager(ResourceHandler.getString("ExpandSelectionToMenu.label")); //$NON-NLS-1$
		fExpandSelectionToMenu.add(fStructureSelectEnclosingAction);
		fExpandSelectionToMenu.add(fStructureSelectNextAction);
		fExpandSelectionToMenu.add(fStructureSelectPreviousAction);
		fExpandSelectionToMenu.add(fStructureSelectHistoryAction);

		// source commands
		fShiftRight = new RetargetTextEditorAction(resourceBundle, ITextEditorActionConstants.SHIFT_RIGHT + StructuredTextEditorActionConstants.DOT);
		fShiftRight.setActionDefinitionId(ITextEditorActionDefinitionIds.SHIFT_RIGHT);

		fShiftLeft = new RetargetTextEditorAction(resourceBundle, ITextEditorActionConstants.SHIFT_LEFT + StructuredTextEditorActionConstants.DOT);
		fShiftLeft.setActionDefinitionId(ITextEditorActionDefinitionIds.SHIFT_LEFT);

		fConvertToWindows = new RetargetTextEditorAction(resourceBundle, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_WINDOWS + StructuredTextEditorActionConstants.DOT);
		fConvertToUNIX = new RetargetTextEditorAction(resourceBundle, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_UNIX + StructuredTextEditorActionConstants.DOT);
		fConvertToMac = new RetargetTextEditorAction(resourceBundle, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_MAC + StructuredTextEditorActionConstants.DOT);

		fConvertDelimitersMenu = new MenuManager(ResourceHandler.getString("ConvertLineDelimitersMenu.label")); //$NON-NLS-1$
		fConvertDelimitersMenu.add(fConvertToWindows);
		fConvertDelimitersMenu.add(fConvertToUNIX);
		fConvertDelimitersMenu.add(fConvertToMac);

		fToggleComment = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT + StructuredTextEditorActionConstants.DOT);
		fToggleComment.setActionDefinitionId(ActionDefinitionIds.TOGGLE_COMMENT);

		fAddBlockComment = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT + StructuredTextEditorActionConstants.DOT);
		fAddBlockComment.setActionDefinitionId(ActionDefinitionIds.ADD_BLOCK_COMMENT);

		fRemoveBlockComment = new RetargetTextEditorAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT + StructuredTextEditorActionConstants.DOT);
		fRemoveBlockComment.setActionDefinitionId(ActionDefinitionIds.REMOVE_BLOCK_COMMENT);

		// goto prev/next error
		// CMVC 249017 for JavaEditor consistancy
		fPreviousAnnotation = new GotoAnnotationAction("Previous_error", false); //$NON-NLS-1$
		fPreviousAnnotation.setActionDefinitionId("org.eclipse.ui.navigate.previous"); //$NON-NLS-1$

		fNextAnnotation = new GotoAnnotationAction("Next_error", true); //$NON-NLS-1$
		fNextAnnotation.setActionDefinitionId("org.eclipse.ui.navigate.next"); //$NON-NLS-1$

		// Read action extensions.
		ExtendedEditorActionBuilder builder = new ExtendedEditorActionBuilder();
		extendedContributor = builder.readActionExtensions(getExtensionIDs());

		fMenuAdditionsGroupMarker = new GroupMarker(StructuredTextEditorActionConstants.GROUP_NAME_MENU_ADDITIONS);
		fToolbarSeparator = new Separator();
		fToolbarAdditionsGroupMarker = new GroupMarker(StructuredTextEditorActionConstants.GROUP_NAME_TOOLBAR_ADDITIONS);

		fToggleInsertModeAction= new RetargetTextEditorAction(resourceBundle, "Editor.ToggleInsertMode.", IAction.AS_CHECK_BOX); //$NON-NLS-1$
		fToggleInsertModeAction.setActionDefinitionId(ITextEditorActionDefinitionIds.TOGGLE_INSERT_MODE);
	}

	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionBarContributor#init(org.eclipse.ui.IActionBars, org.eclipse.ui.IWorkbenchPage)
	 */
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
	 */
	public void dispose() {
		super.dispose();

		if (extendedContributor != null)
			extendedContributor.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);

		addToMenu(menu);

		if (extendedContributor != null) {
			extendedContributor.contributeToMenu(menu);
		}
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
			sourceMenu.add(fCommandsSeparator);
			sourceMenu.add(fConvertDelimitersMenu);
		}
	}

	/* (non-Javadoc)
	 */
	public void contributeToPopupMenu(IMenuManager menu) {

		addToPopupMenu(menu);

		if (extendedContributor != null) {
			extendedContributor.contributeToPopupMenu(menu);
		}
	}

	protected void addToPopupMenu(IMenuManager menu) {
		// add nothing
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);

		addToToolBar(toolBarManager);

		if (extendedContributor != null) {
			extendedContributor.contributeToToolBar(toolBarManager);
		}
	}

	protected void addToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(fToolbarSeparator);
		toolBarManager.add(fToolbarAdditionsGroupMarker);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	public void contributeToStatusLine(IStatusLineManager manager) {
		super.contributeToStatusLine(manager);

		addToStatusLine(manager);

		if (extendedContributor != null) {
			extendedContributor.contributeToStatusLine(manager);
		}
	}

	protected void addToStatusLine(IStatusLineManager manager) {
	}

	/* (non-Javadoc)
	 */
	public void updateToolbarActions() {
		if (extendedContributor != null) {
			extendedContributor.updateToolbarActions();
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	public void setActiveEditor(IEditorPart activeEditor) {
		if (getActiveEditorPart() == activeEditor)
			return;
		super.setActiveEditor(activeEditor);

		ITextEditor textEditor = getTextEditor(activeEditor);

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			// register actions that have a dynamic editor. 
			actionBars.setGlobalActionHandler(ActionFactory.NEXT.getId(), fNextAnnotation); // is this the corrent mapping?
			actionBars.setGlobalActionHandler(ActionFactory.PREVIOUS.getId(), fPreviousAnnotation); // is this the corrent mapping?

			actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_NEXT_ANNOTATION, fNextAnnotation);
			actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_PREVIOUS_ANNOTATION, fPreviousAnnotation);

			IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
			if (statusLineManager != null) {
				statusLineManager.setMessage(null);
				statusLineManager.setErrorMessage(null);
			}
			if (textEditor != null) {
				actionBars.setGlobalActionHandler(IDEActionFactory.ADD_TASK.getId(), getAction(textEditor, IDEActionFactory.ADD_TASK.getId()));
				actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(textEditor, IDEActionFactory.BOOKMARK.getId()));
			}
		}

		fStructureSelectEnclosingAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_ENCLOSING));
		fStructureSelectNextAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_NEXT));
		fStructureSelectPreviousAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_PREVIOUS));
		fStructureSelectHistoryAction.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_HISTORY));

		fShiftRight.setAction(getAction(textEditor, ITextEditorActionConstants.SHIFT_RIGHT));
		fShiftLeft.setAction(getAction(textEditor, ITextEditorActionConstants.SHIFT_LEFT));

		// line delimiter conversion - tie to text editor's action
		fConvertToWindows.setAction(getAction(textEditor, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_WINDOWS));
		fConvertToUNIX.setAction(getAction(textEditor, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_UNIX));
		fConvertToMac.setAction(getAction(textEditor, ITextEditorActionConstants.CONVERT_LINE_DELIMITERS_TO_MAC));

		fToggleComment.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_TOGGLE_COMMENT));
		fAddBlockComment.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT));
		fRemoveBlockComment.setAction(getAction(textEditor, StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT));

		// go to prev/next error
		// CMVC 249017 for JavaEditor consistancy
		fPreviousAnnotation.setEditor(textEditor);
		fNextAnnotation.setEditor(textEditor);

		fToggleInsertModeAction.setAction(getAction(textEditor, ITextEditorActionConstants.TOGGLE_INSERT_MODE));

		if (extendedContributor != null) {
			extendedContributor.setActiveEditor(activeEditor);
		}
	}

	/**
	 * @param editor
	 * @return
	 */
	protected ITextEditor getTextEditor(IEditorPart editor) {
		ITextEditor textEditor = null;
		if (editor instanceof ITextEditor)
			textEditor = (ITextEditor) editor;
		if (textEditor == null && editor != null)
			textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
		return textEditor;
	}

	/*
	 *  (non-Javadoc)
	 */
	public void setViewerSpecificContributionsEnabled(boolean enabled) {
		fShiftRight.setEnabled(enabled);
		fShiftLeft.setEnabled(enabled);
		fNextAnnotation.setEnabled(enabled);
		fPreviousAnnotation.setEnabled(enabled);

		/*
		 fComment.setEnabled(enabled);
		 fUncomment.setEnabled(enabled);
		 */
		fToggleComment.setEnabled(enabled);
		fAddBlockComment.setEnabled(enabled);
		fRemoveBlockComment.setEnabled(enabled);
		// convert line delimiters are not source viewer-specific
	}
}
