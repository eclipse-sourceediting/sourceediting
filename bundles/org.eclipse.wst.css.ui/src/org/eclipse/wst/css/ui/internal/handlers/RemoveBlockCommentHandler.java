/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.handlers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.css.ui.internal.CSSUIMessages;

public class RemoveBlockCommentHandler extends AbstractCommentHandler {

	protected void processAction(ITextEditor textEditor, IDocument document, ITextSelection textSelection) {
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForEdit(document);
		if (model != null && document instanceof IStructuredDocument) {
			try {
				IStructuredDocument doc = (IStructuredDocument) document;
				IStructuredDocumentRegion startRegion = doc.getRegionAtCharacterOffset(textSelection.getOffset());
				IStructuredDocumentRegion endRegion = doc.getRegionAtCharacterOffset(textSelection.getOffset() + textSelection.getLength());

				if ((startRegion == null) || (endRegion == null)) {
					return;
				}

				int openCommentOffset = startRegion.getStartOffset();
				int closeCommentOffset = endRegion.getEndOffset() - OPEN_COMMENT.length() - CLOSE_COMMENT.length();

				model.beginRecording(this, CSSUIMessages.RemoveBlockComment_tooltip);
				model.aboutToChangeModel();

				try {
					if (textSelection.getLength() == 0) {
						if (startRegion.getType() == CSSRegionContexts.CSS_COMMENT) {
							document.replace(openCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
							document.replace(closeCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
						}
					}
					else {
						if (startRegion.getType() == CSSRegionContexts.CSS_COMMENT) {
							document.replace(openCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
						}

						if (endRegion.getType() == CSSRegionContexts.CSS_COMMENT) {
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
