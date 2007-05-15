/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.text.IJSPPartitions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.search.FindOccurrencesProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Configures a FindOccurrencesProcessor with JSP partitions and regions
 */
public class JSPFindOccurrencesProcessor extends FindOccurrencesProcessor {

	@Override
	protected String[] getPartitionTypes() {
		return new String[] { IJSPPartitions.JSP_DEFAULT,
				IJSPPartitions.JSP_CONTENT_JAVA };
	}

	@Override
	protected String[] getRegionTypes() {
		return new String[] { DOMRegionContext.BLOCK_TEXT };
	}

	@Override
	protected ISearchQuery getSearchQuery(IFile file,
			IStructuredDocument document, String regionText, String regionType,
			ITextSelection textSelection) {
		return new JSPSearchQuery(file, getJavaElement(document, textSelection));
	}

	private IJavaElement getJavaElement(IDocument document,
			ITextSelection textSelection) {
		IJavaElement[] elements = getJavaElementsForCurrentSelection(document,
				textSelection);
		return elements.length > 0 ? elements[0] : null;
	}

	/**
	 * uses JSPTranslation to get currently selected Java elements.
	 * 
	 * @return currently selected IJavaElements
	 */
	private IJavaElement[] getJavaElementsForCurrentSelection(
			IDocument document, ITextSelection selection) {
		IJavaElement[] elements = new IJavaElement[0];
		// get JSP translation object for this viewer's document
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
			.getExistingModelForRead(document);
			if (model != null && model instanceof IDOMModel) {
				IDOMDocument xmlDoc = ((IDOMModel) model).getDocument();
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc
						.getAdapterFor(IJSPTranslation.class);
				if (adapter != null) {
					JSPTranslation translation = adapter.getJSPTranslation();

					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=102211
					elements = translation.getElementsFromJspRange(selection
							.getOffset(), selection.getOffset()
							+ selection.getLength());
				}
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return elements;
	}
}
