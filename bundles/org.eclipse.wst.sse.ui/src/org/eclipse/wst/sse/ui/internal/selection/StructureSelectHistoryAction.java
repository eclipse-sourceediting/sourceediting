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

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Node;


public class StructureSelectHistoryAction extends StructureSelectAction implements IUpdate {
	public StructureSelectHistoryAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(ResourceHandler.getString("StructureSelectHistory.label")); //$NON-NLS-1$
		setToolTipText(ResourceHandler.getString("StructureSelectHistory.tooltip")); //$NON-NLS-1$
		setDescription(ResourceHandler.getString("StructureSelectHistory.description")); //$NON-NLS-1$

		update();
	}

	public void update() {
		setEnabled(!fHistory.isEmpty());
	}

	public void run() {
		IRegion old = fHistory.getLast();
		if (old != null) {
			try {
				fHistory.ignoreSelectionChanges();
				fEditor.selectAndReveal(old.getOffset(), old.getLength());
			}
			finally {
				fHistory.listenToSelectionChanges();
			}
		}
	}

	protected IndexedRegion getCursorIndexedRegion() {
		return null;
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		return null;
	}
}
