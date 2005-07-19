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
package org.eclipse.wst.xml.ui.internal.autoedit;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.StructuredDocumentCommand;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.w3c.dom.Node;


public class StructuredAutoEditStrategyXML implements IAutoEditStrategy {
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
					smartInsertForComment(structuredDocumentCommand, document, model);
					smartInsertForEndTag(structuredDocumentCommand, document, model);
				}
			}
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private boolean isCommentNode(IDOMNode node) {
		return (node != null && node instanceof IDOMElement && ((IDOMElement) node).isCommentTag());
	}

	private boolean isDocumentNode(IDOMNode node) {
		return (node != null && node.getNodeType() == Node.DOCUMENT_NODE);
	}

	private void smartInsertForComment(StructuredDocumentCommand structuredDocumentCommand, IDocument document, IStructuredModel model) {
		try {
			if (structuredDocumentCommand.text.equals("-") && document.getLength() >= 3 && document.get(structuredDocumentCommand.offset - 3, 3).equals("<!-")) { //$NON-NLS-1$ //$NON-NLS-2$
				structuredDocumentCommand.text += " "; //$NON-NLS-1$
				structuredDocumentCommand.doit = false;
				structuredDocumentCommand.addCommand(structuredDocumentCommand.offset, 0, " -->", null); //$NON-NLS-1$
			}
		} catch (BadLocationException e) {
			Logger.logException(e);
		}

	}

	private void smartInsertForEndTag(StructuredDocumentCommand structuredDocumentCommand, IDocument document, IStructuredModel model) {
		try {
			if (structuredDocumentCommand.text.equals("/") && document.getLength() >= 1 && document.get(structuredDocumentCommand.offset - 1, 1).equals("<")) { //$NON-NLS-1$ //$NON-NLS-2$
				IDOMNode parentNode = (IDOMNode) ((IDOMNode) model.getIndexedRegion(structuredDocumentCommand.offset - 1)).getParentNode();
				if (isCommentNode(parentNode)) {
					// loop and find non comment node parent
					while (parentNode != null && isCommentNode(parentNode)) {
						parentNode = (IDOMNode) parentNode.getParentNode();
					}
				}

				if (!isDocumentNode(parentNode)) {
					IStructuredDocumentRegion endTagStructuredDocumentRegion = parentNode.getEndStructuredDocumentRegion();
					if (endTagStructuredDocumentRegion == null) {
						structuredDocumentCommand.text += parentNode.getNodeName();
						structuredDocumentCommand.text += ">"; //$NON-NLS-1$
					}
				}
			}
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
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
}
