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
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.StructuredDocumentCommand;
import org.eclipse.wst.sse.ui.edit.util.BasicAutoEditStrategy;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.w3c.dom.Node;


public class StructuredAutoEditStrategyXML extends BasicAutoEditStrategy {
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		StructuredDocumentCommand structuredDocumentCommand = (StructuredDocumentCommand) command;
		Object textEditor = getActiveTextEditor();
		if (!(textEditor instanceof ITextEditorExtension3 && ((ITextEditorExtension3) textEditor).getInsertMode() == ITextEditorExtension3.SMART_INSERT))
			return;

		IStructuredModel model = null;
		try {
			model = getModelManager().getExistingModelForRead(document);
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

	private IModelManager getModelManager() {
		return StructuredModelManager.getInstance().getModelManager();
	}

	private boolean isCommentNode(XMLNode node) {
		return (node != null && node instanceof XMLElement && ((XMLElement) node).isCommentTag());
	}

	private boolean isDocumentNode(XMLNode node) {
		return (node != null && node.getNodeType() == Node.DOCUMENT_NODE);
	}

	protected boolean isEndTagRequired(XMLNode node) {

		if (node == null)
			return false;
		return node.isContainer();
	}

	protected boolean prefixedWith(IDocument document, int offset, String string) {

		try {
			return document.getLength() >= string.length() && document.get(offset - string.length(), string.length()).equals(string);
		} catch (BadLocationException e) {
			Logger.logException(e);
			return false;
		}
	}

	protected void smartInsertForComment(StructuredDocumentCommand structuredDocumentCommand, IDocument document, IStructuredModel model) {
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

	protected void smartInsertForEndTag(StructuredDocumentCommand structuredDocumentCommand, IDocument document, IStructuredModel model) {
		try {
			if (structuredDocumentCommand.text.equals("/") && document.getLength() >= 1 && document.get(structuredDocumentCommand.offset - 1, 1).equals("<")) { //$NON-NLS-1$ //$NON-NLS-2$
				XMLNode parentNode = (XMLNode) ((XMLNode) model.getIndexedRegion(structuredDocumentCommand.offset - 1)).getParentNode();
				if (isCommentNode(parentNode)) {
					// loop and find non comment node parent
					while (parentNode != null && isCommentNode(parentNode)) {
						parentNode = (XMLNode) parentNode.getParentNode();
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
}
