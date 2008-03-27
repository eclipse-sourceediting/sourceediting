/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation, bug 212330
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.handlers;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

public class AddBlockCommentHandler extends CommentHandler implements IHandler {

	public AddBlockCommentHandler() {
		super();
	}

	void processAction(ITextEditor textEditor, IDocument document, ITextSelection textSelection) {
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForEdit(document);
		if (model != null) {
			try {
				IndexedRegion selectionStartIndexedRegion = model.getIndexedRegion(textSelection.getOffset());
				IndexedRegion selectionEndIndexedRegion = model.getIndexedRegion(textSelection.getOffset() + textSelection.getLength());

				if (selectionStartIndexedRegion == null) {
					return;
				}
				if ((selectionEndIndexedRegion == null) && (textSelection.getLength() > 0)) {
					selectionEndIndexedRegion = model.getIndexedRegion(textSelection.getOffset() + textSelection.getLength() - 1);
				}
				if (selectionEndIndexedRegion == null) {
					return;
				}

				int openCommentOffset = selectionStartIndexedRegion.getStartOffset();
				int closeCommentOffset = selectionEndIndexedRegion.getEndOffset() + OPEN_COMMENT.length();

				if ((textSelection.getLength() == 0) && (selectionStartIndexedRegion instanceof CommentImpl)) {
					return;
				}

				model.beginRecording(this, XMLUIMessages.AddBlockComment_tooltip);
				model.aboutToChangeModel();

				try {
					document.replace(openCommentOffset, 0, OPEN_COMMENT);
					document.replace(closeCommentOffset, 0, CLOSE_COMMENT);
					super.removeOpenCloseComments(document, openCommentOffset + OPEN_COMMENT.length(), closeCommentOffset - openCommentOffset - CLOSE_COMMENT.length());
				}
				catch (BadLocationException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
				finally {
					model.changedModel();
					model.endRecording(this);
				}
			}
			finally {
				model.releaseFromEdit();
			}
		}
	}
}
