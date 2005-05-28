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
package org.eclipse.jst.jsp.ui.internal.java.search;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.provisional.text.IJSPPartitionTypes;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.search.BasicFindOccurrencesAction;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * <p>
 * Finds occurrences of Java elements in a JSP file using JSPSearchSupport.
 * </p>
 * 
 * @author pavery
 */
public class JSPFindOccurrencesAction extends BasicFindOccurrencesAction implements IJavaSearchConstants {

	private IFile fJSPFile = null;

	public JSPFindOccurrencesAction(ResourceBundle bundle, String prefix, ITextEditor editor) {

		super(bundle, prefix, editor);
	}


	public ISearchQuery getSearchQuery() {
		return new JSPSearchQuery(getJspFile(), getJavaElement());
	}

	public void update() {

		super.update();

		if (isEnabled()) {
			// do java element check here...
			// should already be in a jsp/java partition
			IJavaElement[] elements = getJavaElementsForCurrentSelection();
			// we can only find occurrences of one element
			setEnabled(elements.length == 1);
		}
	}

	/**
	 * uses JSPTranslation to get currently selected Java elements.
	 * 
	 * @return currently selected IJavaElements
	 */
	public IJavaElement[] getJavaElementsForCurrentSelection() {

		IJavaElement[] elements = new IJavaElement[0];
		StructuredTextEditor editor = (StructuredTextEditor) getTextEditor();
		// get JSP translation object for this viewer's document
		IStructuredModel model = editor.getModel();
		if (model != null && model instanceof IDOMModel) {
			IDOMDocument xmlDoc = ((IDOMModel) model).getDocument();
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (adapter != null) {
				JSPTranslation translation = adapter.getJSPTranslation();
				Point selected = editor.getSelectionRange();
				elements = translation.getElementsFromJspRange(selected.x, selected.x + selected.y);
			}
		}
		return elements;
	}

	public String[] getPartitionTypes() {
		return new String[]{IJSPPartitionTypes.JSP_DEFAULT, IJSPPartitionTypes.JSP_CONTENT_JAVA};
	}

	public String[] getRegionTypes() {
		return new String[]{DOMRegionContext.BLOCK_TEXT, DOMJSPRegionContexts.JSP_CONTENT};
	}

	private IFile getJspFile() {
		if (this.fJSPFile == null)
			this.fJSPFile = ((StructuredTextEditor) getTextEditor()).getFileInEditor();
		return this.fJSPFile;
	}

	private IJavaElement getJavaElement() {
		IJavaElement[] elements = getJavaElementsForCurrentSelection();
		return elements.length > 0 ? elements[0] : null;
	}
}