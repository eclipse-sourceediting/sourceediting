/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.text;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3;

/**
 * Abstract class providing functionality to verify if a document supports
 * the smart insert mode.
 *
 */
public abstract class StructuredAutoEditStrategy implements IAutoEditStrategy {

	/**
	 * Evaluates if the document should support smart insert mode
	 * @param document the document being edited
	 * @return false if the document belongs to an active editor whose smart insert mode is disabled; otherwise, true.
	 */
	protected boolean supportsSmartInsert(IDocument document) {
		Object editor = getActiveTextEditor();
		if (editor instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editor;
			final IDocumentProvider provider = textEditor.getDocumentProvider();
			if (provider != null) {
				if (document == provider.getDocument(textEditor.getEditorInput())) {
					// Document belongs to the active editor
					if (!((textEditor instanceof ITextEditorExtension3) && (((ITextEditorExtension3) textEditor).getInsertMode() == ITextEditorExtension3.SMART_INSERT))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Return the active text editor if possible, otherwise the active editor
	 * part.
	 * 
	 * @return Object
	 */
	private Object getActiveTextEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					if (editor instanceof ITextEditor) {
						return editor;
					}
					ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
					if (textEditor != null) {
						return textEditor;
					}
					return editor;
				}
			}
		}
		return null;
	}
}
