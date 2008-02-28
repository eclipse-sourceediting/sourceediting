/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Moves the cursor to the end tag if it is in a start tag, and vice versa.
 * 
 * @author nitin
 * 
 */
class GoToMatchingTagAction extends TextEditorAction {

	/**
	 * @param bundle
	 * @param prefix
	 * @param editor
	 * @param style
	 */
	GoToMatchingTagAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
		if (getTextEditor() == null)
			return;

		ISelection selection = getTextEditor().getSelectionProvider().getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection && selection instanceof ITextSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if (o instanceof IDOMNode) {
				int offset = ((ITextSelection) selection).getOffset();
				IStructuredDocumentRegion matchRegion = null;
				if (((Node) o).getNodeType() == Node.ATTRIBUTE_NODE) {
					o = ((Attr) o).getOwnerElement();
				}

				if (o instanceof IDOMNode) {
					IDOMNode node = (IDOMNode) o;
					if (node.getStartStructuredDocumentRegion().containsOffset(offset)) {
						matchRegion = ((IDOMNode) o).getEndStructuredDocumentRegion();
					}
					else if (node.getEndStructuredDocumentRegion().containsOffset(offset)) {
						matchRegion = ((IDOMNode) o).getStartStructuredDocumentRegion();
					}
				}

				if (matchRegion != null) {
					getTextEditor().getSelectionProvider().setSelection(new TextSelection(matchRegion.getStartOffset(), matchRegion.getTextLength()));
				}
			}
		}
	}

	public void update() {
		setEnabled(true);
	}

}
