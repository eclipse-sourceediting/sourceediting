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
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.StructuredDocumentCommand;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;

public class StructuredAutoEditStrategyJSP extends StructuredAutoEditStrategyXML {
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

					}
					else
						super.customizeDocumentCommand(document, structuredDocumentCommand);
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
}