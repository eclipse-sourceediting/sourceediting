/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;

public class StructuredSelectHistoryHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		SelectionHistory history = editor.getAdapter(SelectionHistory.class);
		if (history != null) {
			IRegion old = history.getLast();
			if (old != null) {
				try {
					history.ignoreSelectionChanges();

					ITextEditor textEditor = null;
					if (editor instanceof ITextEditor)
						textEditor = (ITextEditor) editor;
					else {
						Object o = editor.getAdapter(ITextEditor.class);
						if (o != null)
							textEditor = (ITextEditor) o;
					}
					if (textEditor != null)
						textEditor.selectAndReveal(old.getOffset(), old.getLength());
				}
				finally {
					history.listenToSelectionChanges();
				}
			}
		}
		return null;
	}
}
