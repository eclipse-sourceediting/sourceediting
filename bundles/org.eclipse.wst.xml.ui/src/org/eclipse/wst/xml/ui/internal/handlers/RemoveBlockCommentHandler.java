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

/**
 * @author dcarver
 * 
 */
public class RemoveBlockCommentHandler extends CommentHandler implements IHandler {

	public RemoveBlockCommentHandler() {
		super();
	}

	void processAction(ITextEditor textEditor, IDocument document, ITextSelection textSelection) {
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForEdit(document);
		if (model != null) {
			try {
				IndexedRegion selectionStartIndexedRegion = model.getIndexedRegion(textSelection.getOffset());
				IndexedRegion selectionEndIndexedRegion = model.getIndexedRegion(textSelection.getOffset() + textSelection.getLength());

				if ((selectionStartIndexedRegion == null) || (selectionEndIndexedRegion == null)) {
					return;
				}

				int openCommentOffset = selectionStartIndexedRegion.getStartOffset();
				int closeCommentOffset = selectionEndIndexedRegion.getEndOffset() - OPEN_COMMENT.length() - CLOSE_COMMENT.length();

				model.beginRecording(this, XMLUIMessages.RemoveBlockComment_tooltip);
				model.aboutToChangeModel();

				try {
					if (textSelection.getLength() == 0) {
						if (selectionStartIndexedRegion instanceof CommentImpl) {
							document.replace(openCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
							document.replace(closeCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
						}
					}
					else {
						if (selectionStartIndexedRegion instanceof CommentImpl) {
							document.replace(openCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
						}

						if (selectionEndIndexedRegion instanceof CommentImpl) {
							document.replace(closeCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
						}
					}
					removeOpenCloseComments(document, openCommentOffset + OPEN_COMMENT.length(), closeCommentOffset - openCommentOffset - CLOSE_COMMENT.length());
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
