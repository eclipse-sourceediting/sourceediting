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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.IModelProvider;
import org.eclipse.wst.sse.ui.edit.util.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class CommentActionXML extends TextEditorAction {
	protected static final String CLOSE_COMMENT = "-->"; //$NON-NLS-1$
	protected static final String OPEN_COMMENT = "<!--"; //$NON-NLS-1$
	protected IDocument fDocument;
	protected IStructuredModel fModel;
	protected ITextSelection fSelection;
	protected int fSelectionEndLine;
	protected int fSelectionEndLineOffset;
	protected int fSelectionEndOffset;
	protected Position fSelectionPosition;
	protected int fSelectionStartLine;
	protected int fSelectionStartLineOffset;
	protected int fSelectionStartOffset;
	protected boolean fUpdateSelection;

	public CommentActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected void comment(int openCommentOffset, int closeCommentOffset) {
		try {
			fDocument.replace(openCommentOffset, 0, OPEN_COMMENT);
			fDocument.replace(closeCommentOffset, 0, CLOSE_COMMENT);
			removeOpenCloseComments(openCommentOffset + OPEN_COMMENT.length(), closeCommentOffset - openCommentOffset - CLOSE_COMMENT.length());
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}
	}

	protected ITextSelection getCurrentSelection() {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			ISelectionProvider provider = editor.getSelectionProvider();
			if (provider != null) {
				ISelection selection = provider.getSelection();
				if (selection instanceof ITextSelection)
					return (ITextSelection) selection;
			}
		}
		return null;
	}

	protected void init() {
		ITextEditor editor = getTextEditor();
		if (editor == null)
			return;

		IDocumentProvider docProvider = editor.getDocumentProvider();
		if (docProvider == null || !(docProvider instanceof IModelProvider))
			return;

		IModelProvider modelProvider = (IModelProvider) docProvider;

		IEditorInput input = editor.getEditorInput();
		if (input == null)
			return;

		fDocument = modelProvider.getDocument(input);
		fModel = modelProvider.getModel(input);
		if (fDocument == null || fModel == null)
			return;

		fSelection = getCurrentSelection();
		fSelectionStartOffset = fSelection.getOffset();
		fSelectionEndOffset = fSelectionStartOffset + fSelection.getLength();

		// add selection position to document
		fSelectionPosition = new Position(fSelection.getOffset(), fSelection.getLength());
		try {
			fDocument.addPosition(fSelectionPosition);
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}

		try {
			fSelectionStartLine = fDocument.getLineOfOffset(fSelectionStartOffset);
			fSelectionEndLine = fDocument.getLineOfOffset(fSelectionEndOffset);
			fSelectionStartLineOffset = fDocument.getLineOffset(fSelectionStartLine);
			fSelectionEndLineOffset = fDocument.getLineOffset(fSelectionEndLine);
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}

		// adjust selection end line
		if (fSelectionEndLine > fSelectionStartLine && fSelectionEndLineOffset == fSelectionEndOffset)
			fSelectionEndLine--;
	}

	protected boolean isCommentLine(int line) {
		try {
			IRegion region = fDocument.getLineInformation(line);
			String string = fDocument.get(region.getOffset(), region.getLength()).trim();
			return string.length() >= OPEN_COMMENT.length() + CLOSE_COMMENT.length() && string.startsWith(OPEN_COMMENT) && string.endsWith(CLOSE_COMMENT);
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}
	}

	protected void prepareSelection() {
		fUpdateSelection = fSelection.getLength() > 0 && fSelectionStartLineOffset == fSelectionStartOffset && !isCommentLine(fSelectionStartLine);
	}

	protected void processAction() {
		fModel.beginRecording(this, ResourceHandler.getString(StructuredTextEditorActionConstants.ACTION_NAME_COMMENT + ".tooltip")); //$NON-NLS-1$
		fModel.aboutToChangeModel();

		for (int i = fSelectionStartLine; i <= fSelectionEndLine; i++) {
			try {
				if (fDocument.getLineLength(i) > 0 && !isCommentLine(i)) {
					int openCommentOffset = fDocument.getLineOffset(i);
					int lineDelimiterLength = fDocument.getLineDelimiter(i) == null ? 0 : fDocument.getLineDelimiter(i).length();
					int closeCommentOffset = openCommentOffset + fDocument.getLineLength(i) - lineDelimiterLength + OPEN_COMMENT.length();
					comment(openCommentOffset, closeCommentOffset);
				}
			} catch (BadLocationException e) {
				throw new SourceEditingRuntimeException();
			}
		}

		fModel.changedModel();
		fModel.endRecording(this);
	}

	protected void removeOpenCloseComments(int offset, int length) {
		try {
			int adjusted_length = length;

			// remove open comments
			String string = fDocument.get(offset, length);
			int index = string.lastIndexOf(OPEN_COMMENT);
			while (index != -1) {
				fDocument.replace(offset + index, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
				index = string.lastIndexOf(OPEN_COMMENT, index - 1);
				adjusted_length -= OPEN_COMMENT.length();
			}

			// remove close comments
			string = fDocument.get(offset, adjusted_length);
			index = string.lastIndexOf(CLOSE_COMMENT);
			while (index != -1) {
				fDocument.replace(offset + index, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				index = string.lastIndexOf(CLOSE_COMMENT, index - 1);
			}
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}
	}

	public void run() {
		init();
		prepareSelection();
		processAction();
		updateSelection();
	}

	protected void setCurrentSelection(ITextSelection selection) {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			ISelectionProvider provider = editor.getSelectionProvider();
			if (provider != null) {
				provider.setSelection(selection);
			}
		}
	}

	protected void updateSelection() {
		if (fUpdateSelection) {
			ITextSelection selection = new TextSelection(fDocument, fSelectionPosition.getOffset() - OPEN_COMMENT.length(), fSelectionPosition.getLength() + OPEN_COMMENT.length());
			setCurrentSelection(selection);
		}
	}
}
