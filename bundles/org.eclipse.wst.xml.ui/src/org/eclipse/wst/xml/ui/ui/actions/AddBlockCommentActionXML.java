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
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.ui.StructuredTextEditorActionConstants;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


public class AddBlockCommentActionXML extends CommentActionXML {
	protected IndexedRegion fSelectionStartIndexedRegion;
	protected IndexedRegion fSelectionEndIndexedRegion;
	protected int fOpenCommentOffset;
	protected int fCloseCommentOffset;

	public AddBlockCommentActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected void init() {
		super.init();

		fSelectionStartIndexedRegion = fModel.getIndexedRegion(fSelectionStartOffset);
		fSelectionEndIndexedRegion = fModel.getIndexedRegion(fSelectionEndOffset);

		if (fSelectionStartIndexedRegion == null || fSelectionEndIndexedRegion == null)
			return;

		fOpenCommentOffset = fSelectionStartIndexedRegion.getStartOffset();
		fCloseCommentOffset = fSelectionEndIndexedRegion.getEndOffset() + OPEN_COMMENT.length();
	}

	protected void processAction() {
		if (fSelection.getLength() == 0 && fSelectionStartIndexedRegion instanceof CommentImpl)
			return;

		fModel.beginRecording(this, ResourceHandler.getString(StructuredTextEditorActionConstants.ACTION_NAME_ADD_BLOCK_COMMENT + ".tooltip")); //$NON-NLS-1$
		fModel.aboutToChangeModel();

		try {
			fDocument.replace(fOpenCommentOffset, 0, OPEN_COMMENT);
			fDocument.replace(fCloseCommentOffset, 0, CLOSE_COMMENT);
			removeOpenCloseComments(fOpenCommentOffset + OPEN_COMMENT.length(), fCloseCommentOffset - fOpenCommentOffset - CLOSE_COMMENT.length());
		}
		catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}

		fModel.changedModel();
		fModel.endRecording(this);
	}
}
