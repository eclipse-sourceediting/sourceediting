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
package org.eclipse.wst.sse.ui.internal.selection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.texteditor.ITextEditor;

public class SelectionHistory {

	private List fHistory;
	private ITextEditor fEditor;
	private ISelectionChangedListener fSelectionListener;
	private int fSelectionChangeListenerCounter;
	private StructureSelectHistoryAction fHistoryAction;

	public SelectionHistory(ITextEditor editor) {
		Assert.isNotNull(editor);
		fEditor = editor;
		fHistory = new ArrayList(3);
		fSelectionListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (fSelectionChangeListenerCounter == 0)
					flush();
			}
		};
		fEditor.getSelectionProvider().addSelectionChangedListener(fSelectionListener);
	}

	public void setHistoryAction(StructureSelectHistoryAction action) {
		Assert.isNotNull(action);
		fHistoryAction = action;
	}

	public boolean isEmpty() {
		return fHistory.isEmpty();
	}

	public void remember(IRegion region) {
		fHistory.add(region);
		fHistoryAction.update();
	}

	public IRegion getLast() {
		if (isEmpty())
			return null;
		int size = fHistory.size();
		IRegion result = (IRegion) fHistory.remove(size - 1);
		fHistoryAction.update();
		return result;
	}

	public void flush() {
		if (fHistory.isEmpty())
			return;
		fHistory.clear();
		fHistoryAction.update();
	}

	public void ignoreSelectionChanges() {
		fSelectionChangeListenerCounter++;
	}

	public void listenToSelectionChanges() {
		fSelectionChangeListenerCounter--;
	}

	public void dispose() {
		fEditor.getSelectionProvider().removeSelectionChangedListener(fSelectionListener);
	}
}
