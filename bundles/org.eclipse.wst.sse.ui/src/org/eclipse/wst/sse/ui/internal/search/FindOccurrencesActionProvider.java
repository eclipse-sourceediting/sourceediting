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
package org.eclipse.wst.sse.ui.internal.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.util.PlatformStatusLineUtil;



/**
 * Performs the appropriate FindOccurrences action call based on selection.
 * Clients can add actions for different partitions via <code>addAction(BasicFindOccurrencesAction action)</code> 
 * 
 * @author pavery
 */
public class FindOccurrencesActionProvider extends TextEditorAction {

	private List actions = null;

	public FindOccurrencesActionProvider(ResourceBundle bundle, String prefix, ITextEditor editor) {

		super(bundle, prefix, editor);
	}

	public void addAction(BasicFindOccurrencesAction action) {

		getActions().add(action);
	}


	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		
		BasicFindOccurrencesAction action = getActionForCurrentSelection();
		String errorMessage = ResourceHandler.getString("FindOccurrencesActionProvider.0"); //$NON-NLS-1$
		if (action != null) {
			action.update();
			if(action.isEnabled()) {
				// first of all activate the view
				NewSearchUI.activateSearchResultView();
				
				// run the action
				action.run();
				
				// clear status message
				PlatformStatusLineUtil.clearStatusLine();
			}
			else {
				PlatformStatusLineUtil.displayErrorMessage(errorMessage);
				PlatformStatusLineUtil.addOneTimeClearListener();
			}
		}
		else {
			PlatformStatusLineUtil.displayErrorMessage(errorMessage);
			PlatformStatusLineUtil.addOneTimeClearListener();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update() {

		super.update();	
		// clear status message
		PlatformStatusLineUtil.clearStatusLine();
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	public boolean isEnabled() {
		// always enabled
		return true;
	}
	
	private BasicFindOccurrencesAction getActionForCurrentSelection() {
		
		// check if we have an action that's enabled on the current partition
		ITypedRegion tr = getPartition();
		String partition = tr != null ? tr.getType() : ""; //$NON-NLS-1$

		Iterator it = getActions().iterator();
		BasicFindOccurrencesAction action = null;
		while (it.hasNext()) {
			action = (BasicFindOccurrencesAction) it.next();
			// we just choose the first action that can handle the partition
			if (action.enabledForParitition(partition)) 
				return action;
		}
		return null;
	}
	
	private ITypedRegion getPartition() {
		ITextSelection sel = getTextSelection();
		if (sel != null) {
			ITypedRegion region = getDocument().getDocumentPartitioner().getPartition(sel.getOffset());
			return region;
		}
		return null;
	}

	private IDocument getDocument() {
		return getTextEditor().getDocumentProvider().getDocument(getTextEditor().getEditorInput());
	}

	private ITextSelection getTextSelection() {
		ISelection selection = getTextEditor().getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection && !selection.isEmpty()) {
			ITextSelection textSel = (ITextSelection) selection;
			return textSel;
		}
		return null;
	}

	private List getActions() {
		if (this.actions == null)
			this.actions = new ArrayList();
		return this.actions;
	}
}
