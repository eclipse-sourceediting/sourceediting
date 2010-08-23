/*******************************************************************************
 * Copyright (c) 2008, 2010 Standards for Technology in Automotive Retail and others.
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.wst.xml.ui.internal.Logger;

public class CommentHandler extends AbstractHandler implements IHandler {
	static final String CLOSE_COMMENT = "-->"; //$NON-NLS-1$
	static final String OPEN_COMMENT = "<!--"; //$NON-NLS-1$

	public CommentHandler() {
		super();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		ITextEditor textEditor = null;
		if (editor instanceof ITextEditor)
			textEditor = (ITextEditor) editor;
		else {
			Object o = editor.getAdapter(ITextEditor.class);
			if (o != null)
				textEditor = (ITextEditor) o;
		}
		if (textEditor != null && validateEditorInput(textEditor)) {
			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
			if (document != null) {
				// get current text selection
				ITextSelection textSelection = getCurrentSelection(textEditor);
				if (textSelection.isEmpty()) {
					return null;
				}

				processAction(textEditor, document, textSelection);
			}
		}

		return null;
	}

	protected boolean validateEditorInput(ITextEditor textEditor) {
		if (textEditor instanceof ITextEditorExtension2)
			return ((ITextEditorExtension2)textEditor).validateEditorInputState();
		else if (textEditor instanceof ITextEditorExtension)
			return ((ITextEditorExtension)textEditor).isEditorInputReadOnly();
		else if (textEditor != null)
			return textEditor.isEditable();
		return true;
		
	}

	protected ITextSelection getCurrentSelection(ITextEditor textEditor) {
		ISelectionProvider provider = textEditor.getSelectionProvider();
		if (provider != null) {
			ISelection selection = provider.getSelection();
			if (selection instanceof ITextSelection) {
				return (ITextSelection) selection;
			}
		}
		return TextSelection.emptySelection();
	}

	void processAction(ITextEditor textEditor, IDocument document, ITextSelection textSelection) {
		// Implementations to over ride.
	}

	protected void removeOpenCloseComments(IDocument document, int offset, int length) {
		try {
			int adjusted_length = length;

			// remove open comments
			String string = document.get(offset, length);
			int index = string.lastIndexOf(OPEN_COMMENT);
			while (index != -1) {
				document.replace(offset + index, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
				index = string.lastIndexOf(OPEN_COMMENT, index - 1);
				adjusted_length -= OPEN_COMMENT.length();
			}

			// remove close comments
			string = document.get(offset, adjusted_length);
			index = string.lastIndexOf(CLOSE_COMMENT);
			while (index != -1) {
				document.replace(offset + index, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
				index = string.lastIndexOf(CLOSE_COMMENT, index - 1);
			}
		}
		catch (BadLocationException e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
	}
}
