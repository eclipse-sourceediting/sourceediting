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
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.w3c.dom.Node;

public class StructureSelectPreviousAction extends StructureSelectAction {
	public StructureSelectPreviousAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(SSEUIMessages.StructureSelectPrevious_label); //$NON-NLS-1$
		setToolTipText(SSEUIMessages.StructureSelectPrevious_tooltip); //$NON-NLS-1$
		setDescription(SSEUIMessages.StructureSelectPrevious_description); //$NON-NLS-1$
	}

	protected IndexedRegion getCursorIndexedRegion() {
		return getIndexedRegion(fViewer.getSelectedRange().x);
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		Region newRegion = null;

		Node newNode = node.getPreviousSibling();
		if (newNode == null) {
			newNode = node.getParentNode();

			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(newIndexedRegion.getStartOffset(), newIndexedRegion.getEndOffset() - newIndexedRegion.getStartOffset());
			}
		} else {
			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(newIndexedRegion.getStartOffset(), region.getOffset() + region.getLength() - newIndexedRegion.getStartOffset());

				if (newNode.getNodeType() == Node.TEXT_NODE)
					newRegion = getNewSelectionRegion(newNode, newRegion);
			}
		}

		return newRegion;
	}
}
