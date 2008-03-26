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
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

public class CommentHandler extends AbstractHandler implements IHandler {
	

	static final String CLOSE_COMMENT = "-->"; //$NON-NLS-1$
	static final String OPEN_COMMENT = "<!--"; //$NON-NLS-1$

	IEditorPart fEditor;
	
	public CommentHandler() {
		super();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		fEditor = HandlerUtil.getActiveEditor(event);
		
		if (fEditor instanceof XMLMultiPageEditorPart) {
			final ITextEditor textEditor = (ITextEditor) fEditor.getAdapter(ITextEditor.class);
			fEditor = textEditor;
			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
			if (document != null) {
				// get current text selection
				ITextSelection textSelection = getCurrentSelection();
				if (textSelection.isEmpty()) {
					return null;
				}

				processAction(document, textSelection);
			}
		}
	
		return null;
	}

	protected ITextSelection getCurrentSelection() {
		if (fEditor instanceof ITextEditor) {
			ISelectionProvider provider = ((ITextEditor) fEditor).getSelectionProvider();
			if (provider != null) {
				ISelection selection = provider.getSelection();
				if (selection instanceof ITextSelection) {
					return (ITextSelection) selection;
				}
			}
		}
		return TextSelection.emptySelection();
	}
	
	void processAction(IDocument document, ITextSelection textSelection) {
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
