/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.ui.StructuredTextEditorActionConstants;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


public class RemoveBlockCommentActionXML extends AddBlockCommentActionXML {
	public RemoveBlockCommentActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected void init() {
		super.init();

		fCloseCommentOffset = fSelectionEndIndexedRegion.getEndOffset() - OPEN_COMMENT.length() - CLOSE_COMMENT.length();
	}

	protected void processAction() {
		fModel.beginRecording(this, ResourceHandler.getString(StructuredTextEditorActionConstants.ACTION_NAME_REMOVE_BLOCK_COMMENT + ".tooltip")); //$NON-NLS-1$
		fModel.aboutToChangeModel();

		if (fSelection.getLength() == 0) {
			if (fSelectionStartIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fOpenCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
					fDocument.replace(fCloseCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				}
				catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}
		}
		else {
			if (fSelectionStartIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fOpenCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
				}
				catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}

			if (fSelectionEndIndexedRegion instanceof CommentImpl) {
				try {
					fDocument.replace(fCloseCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				}
				catch (BadLocationException e) {
					throw new SourceEditingRuntimeException();
				}
			}
		}
		removeOpenCloseComments(fOpenCommentOffset + OPEN_COMMENT.length(), fCloseCommentOffset - fOpenCommentOffset - CLOSE_COMMENT.length());

		fModel.changedModel();
		fModel.endRecording(this);
	}
}
