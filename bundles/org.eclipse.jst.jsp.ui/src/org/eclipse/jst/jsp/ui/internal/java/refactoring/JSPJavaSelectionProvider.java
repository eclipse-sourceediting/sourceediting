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
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

class JSPJavaSelectionProvider {
	static IJavaElement[] getSelection(ITextEditor textEditor) {
		IJavaElement[] elements = null;

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		ISelection selection = textEditor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			// get the JSP translation object for this editor's document
			XMLModel xmlModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(document);
			try {
				if (xmlModel != null) {
					XMLDocument xmlDoc = xmlModel.getDocument();

					JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
					if (adapter != null) {
						JSPTranslation translation = adapter.getJSPTranslation();
						elements = translation.getElementsFromJspRange(textSelection.getOffset(), textSelection.getLength());
					}
				}
			}
			finally {
				if (xmlModel != null)
					xmlModel.releaseFromRead();
			}
		}
		if (elements == null) {
			elements = new IJavaElement[0];
		}
		return elements;
	}

}
