/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.html.ui.internal.autoedit;

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
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.w3c.dom.Node;

/**
 * Automatically inserts closing comment tag or end tag when appropriate.
 */
public class StructuredAutoEditStrategyHTML implements IAutoEditStrategy {
	/*
	 * NOTE: copies of this class exists in
	 * org.eclipse.wst.xml.ui.internal.autoedit
	 * org.eclipse.wst.html.ui.internal.autoedit
	 */
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		Object textEditor = getActiveTextEditor();
		if (!(textEditor instanceof ITextEditorExtension3 && ((ITextEditorExtension3) textEditor).getInsertMode() == ITextEditorExtension3.SMART_INSERT))
			return;

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				if (command.text != null) {
					smartInsertForComment(command, document, model);
					smartInsertForEndTag(command, document, model);
					smartRemoveEndTag(command, document, model);
				}
			}
		}
		finally {
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
	
	/**
	 * Attempts to clean up an end-tag if a start-tag is converted into an empty-element
	 * tag (e.g., <node />) and the original element was empty.
	 * 
	 * @param command the document command describing the change
	 * @param document the document that will be changed
	 * @param model the model based on the document
	 */
	private void smartRemoveEndTag(DocumentCommand command, IDocument document, IStructuredModel model) {
		try {
			// An opening tag is now a self-terminated end-tag
			if ("/".equals(command.text) && ">".equals(document.get(command.offset, 1))) { //$NON-NLS-1$ //$NON-NLS-2$
				IDOMNode node = (IDOMNode) model.getIndexedRegion(command.offset);
				if (node != null && !node.hasChildNodes()) {
					IStructuredDocumentRegion region = node.getEndStructuredDocumentRegion();

					if (region != null && region.isEnded())
						document.replace(region.getStartOffset(), region.getLength(), ""); //$NON-NLS-1$
				}
			}
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

	private void smartInsertForComment(DocumentCommand command, IDocument document, IStructuredModel model) {
		try {
			if (command.text.equals("-") && document.getLength() >= 3 && document.get(command.offset - 3, 3).equals("<!-")) { //$NON-NLS-1$ //$NON-NLS-2$
				command.text += "  -->"; //$NON-NLS-1$
				command.shiftsCaret = false;
				command.caretOffset = command.offset + 2;
				command.doit = false;
			}
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}

	}

	private void smartInsertForEndTag(DocumentCommand command, IDocument document, IStructuredModel model) {
		try {
			if (command.text.equals("/") && document.getLength() >= 1 && document.get(command.offset - 1, 1).equals("<")) { //$NON-NLS-1$ //$NON-NLS-2$
				IDOMNode parentNode = (IDOMNode) ((IDOMNode) model.getIndexedRegion(command.offset - 1)).getParentNode();
				if (isCommentNode(parentNode)) {
					// loop and find non comment node parent
					while (parentNode != null && isCommentNode(parentNode)) {
						parentNode = (IDOMNode) parentNode.getParentNode();
					}
				}

				if (!isDocumentNode(parentNode)) {
					// only add end tag if one does not already exist or if
					// add '/' does not create one already
					IStructuredDocumentRegion endTagStructuredDocumentRegion = parentNode.getEndStructuredDocumentRegion();
					if (endTagStructuredDocumentRegion == null) {
						StringBuffer toAdd = new StringBuffer(parentNode.getNodeName());
						if (toAdd.length() > 0) {
							toAdd.append(">"); //$NON-NLS-1$
							String suffix = toAdd.toString();
							if ((document.getLength() < command.offset + suffix.length()) || (!suffix.equals(document.get(command.offset, suffix.length())))) {
								command.text += suffix;
							}
						}
					}
				}
			}
		}
		catch (BadLocationException e) {
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
