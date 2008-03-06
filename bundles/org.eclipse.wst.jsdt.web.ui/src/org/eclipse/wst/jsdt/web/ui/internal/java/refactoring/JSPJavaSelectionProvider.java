/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.core.IJavaElement;

import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

class JSPJavaSelectionProvider {
	static IJavaElement[] getSelection(ITextEditor textEditor) {
		IJavaElement[] elements = null;
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		ISelection selection = textEditor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			// get the JSP translation object for this editor's document
			IStructuredModel model = null;
			try {
				model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
				if (model instanceof IDOMModel) {
					IDOMModel xmlModel = (IDOMModel) model;
					IDOMDocument xmlDoc = xmlModel.getDocument();
					JsTranslationAdapter adapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
					if (adapter != null) {
						IJsTranslation translation = adapter.getJSPTranslation(true);
						elements = translation.getElementsFromJsRange(textSelection.getOffset(), textSelection.getOffset() + textSelection.getLength());
					}
				}
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
		}
		if (elements == null) {
			elements = new IJavaElement[0];
		}
		return elements;
	}
}
