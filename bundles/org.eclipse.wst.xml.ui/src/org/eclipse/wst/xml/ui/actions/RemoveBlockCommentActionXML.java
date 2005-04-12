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
package org.eclipse.wst.xml.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

public class RemoveBlockCommentActionXML extends AddBlockCommentActionXML {
	public RemoveBlockCommentActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected void init() {
		super.init();

		fCloseCommentOffset = fSelectionEndIndexedRegion.getEndOffset() - OPEN_COMMENT.length() - CLOSE_COMMENT.length();
	}

	protected void processAction() {
		fModel.beginRecording(this, XMLUIMessages.RemoveBlockComment_tooltip);
		fModel.aboutToChangeModel();

		if (fSelection.getLength() == 0) {
			if (fSelectionStartIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fOpenCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
					fDocument.replace(fCloseCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				} catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}
		} else {
			if (fSelectionStartIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fOpenCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
				} catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}

			if (fSelectionEndIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fCloseCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				} catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}
		}
		removeOpenCloseComments(fOpenCommentOffset + OPEN_COMMENT.length(), fCloseCommentOffset - fOpenCommentOffset - CLOSE_COMMENT.length());

		fModel.changedModel();
		fModel.endRecording(this);
	}
}
