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
package org.eclipse.wst.xml.ui.internal.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

public class AddBlockCommentActionXML extends CommentActionXML {
	protected int fCloseCommentOffset;
	protected int fOpenCommentOffset;
	protected IndexedRegion fSelectionEndIndexedRegion;
	protected IndexedRegion fSelectionStartIndexedRegion;

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

		fModel.beginRecording(this, XMLUIMessages.AddBlockComment_tooltip);
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
