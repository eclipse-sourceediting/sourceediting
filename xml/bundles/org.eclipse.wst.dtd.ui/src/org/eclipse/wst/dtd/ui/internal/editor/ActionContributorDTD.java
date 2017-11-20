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
package org.eclipse.wst.dtd.ui.internal.editor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.sse.ui.internal.actions.ActionContributor;

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
public class ActionContributorDTD extends ActionContributor {
	protected static final String[] EDITOR_IDS = {"org.eclipse.wst.dtd.core.dtdsource.source", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.edit.util.ActionContributor#getExtensionIDs()
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
			editMenu.add(fMenuAdditionsGroupMarker);
		}

		// source commands
		String sourceMenuLabel = DTDUIMessages.SourceMenu_label;
		String sourceMenuId = "sourceMenuId"; //$NON-NLS-1$
		IMenuManager sourceMenu = new MenuManager(sourceMenuLabel, sourceMenuId);
		menu.insertAfter(IWorkbenchActionConstants.M_EDIT, sourceMenu);
		if (sourceMenu != null) {
			sourceMenu.add(fCommandsSeparator);
			sourceMenu.add(fShiftRight);
			sourceMenu.add(fShiftLeft);
			sourceMenu.add(fCommandsSeparator);
		}
		IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			IMenuManager gotoGroup = navigateMenu.findMenuUsingPath(IWorkbenchActionConstants.GO_TO);
			if (gotoGroup != null) {
				gotoGroup.add(fGotoMatchingBracketAction);
			}
		}
	}
}
