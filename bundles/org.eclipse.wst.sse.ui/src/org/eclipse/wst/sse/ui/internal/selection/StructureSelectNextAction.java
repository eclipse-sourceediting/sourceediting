/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.selection;

import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Node;


public class StructureSelectNextAction extends StructureSelectAction {
	public StructureSelectNextAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(ResourceHandler.getString("StructureSelectNext.label")); //$NON-NLS-1$
		setToolTipText(ResourceHandler.getString("StructureSelectNext.tooltip")); //$NON-NLS-1$
		setDescription(ResourceHandler.getString("StructureSelectNext.description")); //$NON-NLS-1$
	}

	protected IndexedRegion getCursorIndexedRegion() {
		int offset = fViewer.getSelectedRange().x + fViewer.getSelectedRange().y - 1;

		if (offset < 0)
			offset = 0;

		return getIndexedRegion(offset);
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		Region newRegion = null;

		Node newNode = node.getNextSibling();
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

				if (newNode.getNodeType() == Node.TEXT_NODE)
					newRegion = getNewSelectionRegion(newNode, newRegion);
			}
		}

		return newRegion;
	}
}
