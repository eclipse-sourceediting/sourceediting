/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
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
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
class JSPJavaSelectionProvider {
	static IJavaScriptElement[] getSelection(ITextEditor textEditor) {
		IJavaScriptElement[] elements = null;
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
						IJsTranslation translation = adapter.getJsTranslation(true);
						elements = translation.getElementsFromJsRange(translation.getJavaScriptOffset(textSelection.getOffset()), translation.getJavaScriptOffset(textSelection.getOffset() + textSelection.getLength()));
					}
				}
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
		}
		if (elements == null) {
			elements = new IJavaScriptElement[0];
		}
		return elements;
	}
}
