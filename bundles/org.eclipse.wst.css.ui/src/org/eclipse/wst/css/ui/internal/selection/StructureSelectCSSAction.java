/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.internal.selection;

import org.eclipse.jface.text.Region;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectAction;

public abstract class StructureSelectCSSAction extends StructureSelectAction {
	public StructureSelectCSSAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
	}

	public void run() {
		Region currentRegion = new Region(fViewer.getSelectedRange().x, fViewer.getSelectedRange().y);
		if (currentRegion.getLength() == fViewer.getDocument().getLength())
			return;

		IndexedRegion cursorIndexedRegion = getCursorIndexedRegion();
		if (cursorIndexedRegion instanceof ICSSNode) {
			ICSSNode cursorNode = (ICSSNode) cursorIndexedRegion;

			Region cursorNodeRegion = new Region(cursorIndexedRegion.getStartOffset(), cursorIndexedRegion.getEndOffset() - cursorIndexedRegion.getStartOffset());

			Region newRegion = null;
			if (cursorNodeRegion.getOffset() >= currentRegion.getOffset() && cursorNodeRegion.getOffset() <= currentRegion.getOffset() + currentRegion.getLength() && cursorNodeRegion.getOffset() + cursorNodeRegion.getLength() >= currentRegion.getOffset() && cursorNodeRegion.getOffset() + cursorNodeRegion.getLength() <= currentRegion.getOffset() + currentRegion.getLength())
				newRegion = getNewSelectionRegion(cursorNode, currentRegion);
			else
				newRegion = cursorNodeRegion;

			if (newRegion != null) {
				fHistory.remember(currentRegion);
				try {
					fHistory.ignoreSelectionChanges();
					fEditor.selectAndReveal(newRegion.getOffset(), newRegion.getLength());
				} finally {
					fHistory.listenToSelectionChanges();
				}
			}
		}
	}

	abstract protected Region getNewSelectionRegion(ICSSNode node, Region region);
}