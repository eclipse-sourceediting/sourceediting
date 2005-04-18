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
import org.eclipse.wst.css.ui.internal.CSSUIMessages;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.w3c.dom.Node;

public class StructureSelectEnclosingCSSAction extends StructureSelectCSSAction {
	public StructureSelectEnclosingCSSAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(CSSUIMessages.StructureSelectEnclosing_label);
		setToolTipText(CSSUIMessages.StructureSelectEnclosing_tooltip);
		setDescription(CSSUIMessages.StructureSelectEnclosing_description);
	}

	protected IndexedRegion getCursorIndexedRegion() {
		return getIndexedRegion(fViewer.getSelectedRange().x);
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		return null;
	}

	protected Region getNewSelectionRegion(ICSSNode node, Region region) {
		Region newRegion = null;

		ICSSNode newNode = node.getParentNode();

		if (newNode instanceof IndexedRegion) {
			IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
			newRegion = new Region(newIndexedRegion.getStartOffset(), newIndexedRegion.getEndOffset() - newIndexedRegion.getStartOffset());
		}

		return newRegion;
	}
}