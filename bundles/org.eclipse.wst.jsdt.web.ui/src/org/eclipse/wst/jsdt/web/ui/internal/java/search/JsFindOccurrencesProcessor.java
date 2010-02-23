/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.text.IJsPartitions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.search.FindOccurrencesProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsFindOccurrencesProcessor extends FindOccurrencesProcessor {
	private IJavaScriptElement getJavaElement(IDocument document, ITextSelection textSelection) {
		IJavaScriptElement[] elements = getJavaElementsForCurrentSelection(document, textSelection);
		return elements.length > 0 ? elements[0] : null;
	}
	
	/**
	 * uses JSPTranslation to get currently selected Java elements.
	 * 
	 * @return currently selected IJavaElements
	 */
	private IJavaScriptElement[] getJavaElementsForCurrentSelection(IDocument document, ITextSelection selection) {
		IJavaScriptElement[] elements = new IJavaScriptElement[0];
		// get JSP translation object for this viewer's document
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null && model instanceof IDOMModel) {
				IDOMDocument xmlDoc = ((IDOMModel) model).getDocument();
				JsTranslationAdapter adapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
				if (adapter != null) {
					IJsTranslation translation = adapter.getJsTranslation(false);
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=102211
					elements = translation.getElementsFromJsRange(selection.getOffset(), selection.getOffset() + selection.getLength());
				}
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return elements;
	}
	
	
	protected String[] getPartitionTypes() {
		return new String[] { IJsPartitions.HtmlJsPartition };
	}
	
	
	protected String[] getRegionTypes() {
		return new String[] { DOMRegionContext.BLOCK_TEXT };
	}
	
	
	protected ISearchQuery getSearchQuery(IFile file, IStructuredDocument document, String regionText, String regionType, ITextSelection textSelection) {
		IJavaScriptElement javaScriptElement = getJavaElement(document, textSelection);
		if (javaScriptElement != null) {
			return new JsSearchQuery(file, javaScriptElement);
		}
		return null;
	}
}
