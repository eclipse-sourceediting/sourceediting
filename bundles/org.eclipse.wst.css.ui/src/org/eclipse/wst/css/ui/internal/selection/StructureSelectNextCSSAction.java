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
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.w3c.dom.Node;

public class StructureSelectNextCSSAction extends StructureSelectCSSAction {
	public StructureSelectNextCSSAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(SSEUIPlugin.getResourceString("%StructureSelectNext.label")); //$NON-NLS-1$
		setToolTipText(SSEUIPlugin.getResourceString("%StructureSelectNext.tooltip")); //$NON-NLS-1$
		setDescription(SSEUIPlugin.getResourceString("%StructureSelectNext.description")); //$NON-NLS-1$
	}

	protected IndexedRegion getCursorIndexedRegion() {
		int offset = fViewer.getSelectedRange().x + fViewer.getSelectedRange().y - 1;

		if (offset < 0)
			offset = 0;

		return getIndexedRegion(offset);
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		return null;
	}

	protected Region getNewSelectionRegion(ICSSNode node, Region region) {
		Region newRegion = null;

		ICSSNode newNode = node.getNextSibling();
		if (newNode == null) {
			newNode = node.getParentNode();

			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(newIndexedRegion.getStartOffset(), newIndexedRegion.getEndOffset() - newIndexedRegion.getStartOffset());
			}
		} else {
			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(region.getOffset(), newIndexedRegion.getEndOffset() - region.getOffset());
			}
		}

		return newRegion;
	}
}