/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.autoedit;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.wst.html.ui.internal.autoedit.StructuredAutoEditStrategyHTML;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.StructuredDocumentCommand;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class StructuredAutoEditStrategyJSP extends StructuredAutoEditStrategyHTML {
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		StructuredDocumentCommand structuredDocumentCommand = (StructuredDocumentCommand) command;
		Object textEditor = getActiveTextEditor();
		if (!(textEditor instanceof ITextEditorExtension3 && ((ITextEditorExtension3) textEditor).getInsertMode() == ITextEditorExtension3.SMART_INSERT))
			return;
		
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);

			if (model != null) {
				if (structuredDocumentCommand.text != null) {
					if (structuredDocumentCommand.text.equals("%")) { //$NON-NLS-1$
						// scriptlet - add end %>
						IDOMNode node = (IDOMNode) model.getIndexedRegion(structuredDocumentCommand.offset);
						try {
							if (prefixedWith(document, structuredDocumentCommand.offset, "<") && !node.getSource().endsWith("%>")) { //$NON-NLS-1$ //$NON-NLS-2$
								structuredDocumentCommand.doit = false;
								structuredDocumentCommand.addCommand(structuredDocumentCommand.offset, 0, " %>", null); //$NON-NLS-1$
							}
						}
						catch (BadLocationException e) {
							Logger.logException(e);
						}

					} if (structuredDocumentCommand.text.equals("{")) { //$NON-NLS-1$
						IDOMNode node = (IDOMNode) model.getIndexedRegion(structuredDocumentCommand.offset);
						try {
							if ((prefixedWith(document, structuredDocumentCommand.offset, "$") || prefixedWith(document, structuredDocumentCommand.offset, "#")) && 
									!node.getSource().endsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$
								structuredDocumentCommand.doit = false;
								structuredDocumentCommand.addCommand(structuredDocumentCommand.offset, 0, " }", null); //$NON-NLS-1$
							}
						}
						catch (BadLocationException e) {
							Logger.logException(e);
						}
					}
					else {
						super.customizeDocumentCommand(document, structuredDocumentCommand);
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
	
	/**
	 * Return the active text editor if possible, otherwise the active editor
	 * part.
	 * 
	 * @return
	 */
	private Object getActiveTextEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					if (editor instanceof ITextEditor)
						return editor;
					ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
					if (textEditor != null)
						return textEditor;
					return editor;
				}
			}
		}
		return null;
	}
	
	private boolean prefixedWith(IDocument document, int offset, String string) {

		try {
			return document.getLength() >= string.length() && document.get(offset - string.length(), string.length()).equals(string);
		} catch (BadLocationException e) {
			Logger.logException(e);
			return false;
		}
	}
}