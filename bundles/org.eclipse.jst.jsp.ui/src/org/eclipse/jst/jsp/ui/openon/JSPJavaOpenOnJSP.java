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
package org.eclipse.jst.jsp.ui.openon;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.util.ResourceUtil;
import org.eclipse.wst.sse.ui.openon.AbstractOpenOn;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

/**
 * This action opens classes referenced in JSP Java content of a JSP page.
 */
public class JSPJavaOpenOnJSP extends AbstractOpenOn {

	//private final String SSE_MODEL_ID = IModelManagerPlugin.ID; //$NON-NLS-1$
	private final String SELECT_JAVA_TITLE = JSPUIPlugin.getResourceString("%JSPJavaOpenOnJSP.0"); //$NON-NLS-1$
	private final String SELECT_JAVA_MESSAGE = JSPUIPlugin.getResourceString("%JSPJavaOpenOnJSP.1"); //$NON-NLS-1$

	/**
	 * Get JSP translation object
	 * 
	 * @return JSPTranslation if one exists, null otherwise
	 */
	private JSPTranslation getJSPTranslation() {
		// get JSP translation object for this action's editor's document
		XMLModel xmlModel = (XMLModel) getModelManager().getExistingModelForRead(getDocument());
		if (xmlModel != null) {
			XMLDocument xmlDoc = xmlModel.getDocument();
			xmlModel.releaseFromRead();
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (adapter != null) {
				return adapter.getJSPTranslation();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.AbstractOpenOn#doOpenOn(org.eclipse.jface.text.IRegion)
	 */
	protected void doOpenOn(IRegion region) {
		JSPTranslation jspTranslation = getJSPTranslation();
		if (jspTranslation != null) {
			IJavaElement element = getJavaElement(region, jspTranslation);

			// open local variable in the JSP file...
			if (element instanceof ILocalVariable) {
				// source reference has java text range info
				if (element instanceof ISourceReference) {
					try {
						// get Java range, translate coordinate to JSP
						ISourceRange range = ((ISourceReference) element).getSourceRange();
						int jspStart = jspTranslation.getJspOffset(range.getOffset());

						// open the JSP editor
						IEditorPart jspEditor = openDocumentEditor();
						// set the cursor to the declaration of the variable
						if (jspEditor instanceof ITextEditor) {
							((ITextEditor) jspEditor).setHighlightRange(jspStart, 0, true);
						}
					}
					catch (JavaModelException jme) {
						Logger.logException("error getting source range from java element (local variable)", jme); //$NON-NLS-1$
					}
				}
			}
			else {
				try {
					IEditorPart part = JavaUI.openInEditor(element);
					if (part != null) {
						if (element != null)
							JavaUI.revealInEditor(part, element);
					}
					else {
						// could not open editor
						openFileFailed();
					}
				}
				catch (Exception e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Determine the one Java element to open file on
	 * 
	 * @param region
	 *            IRegion
	 * @param translation
	 *            jsp java translation
	 * @return the one Java element to open file on
	 */
	private IJavaElement getJavaElement(IRegion region, JSPTranslation translation) {
		if (region == null) {
			return null;
		}

		IJavaElement[] elements = translation.getElementsFromJspRange(region.getOffset(), region.getOffset() + region.getLength());
		if (elements == null || elements.length == 0)
			return null;
		IJavaElement candidate = elements[0];
		// more than one Java element was selected so ask user which element
		// should open file open
		if (elements.length > 1) {
			candidate = selectJavaElement(elements);
		}
		return candidate;
	}

	/**
	 * Shows a dialog for resolving an ambigous java element. This method was
	 * copied from org.eclipse.jdt.internal.ui.actions.OpenActionUtil except I
	 * set the shell, title, message in this method instead of passing it and
	 * this method is private
	 */
	private IJavaElement selectJavaElement(IJavaElement[] elements) {

		int nResults = elements.length;
		if (nResults == 0)
			return null;
		if (nResults == 1)
			return elements[0];
		int flags = JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT;
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(null, new JavaElementLabelProvider(flags));
		dialog.setTitle(SELECT_JAVA_TITLE);
		dialog.setMessage(SELECT_JAVA_MESSAGE);
		dialog.setElements(elements);
		if (dialog.open() == Window.OK) {
			Object[] selection = dialog.getResult();
			if (selection != null && selection.length > 0) {
				nResults = selection.length;
				for (int i = 0; i < nResults; i++) {
					Object current = selection[i];
					if (current instanceof IJavaElement)
						return (IJavaElement) current;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.AbstractOpenOn#doGetOpenOnRegion(int)
	 */
	protected IRegion doGetOpenOnRegion(int offset) {
		IRegion region;

		// check and make sure this is a valid Java type
		JSPTranslation jspTranslation = getJSPTranslation();
		IJavaElement[] elements = jspTranslation.getElementsFromJspRange(offset, offset);
		// if no Java element found, this is not a valid openable region
		if (elements == null || elements.length == 0)
			region = null;
		else {
			// return the type region
			region = selectWord(getDocument(), offset);
		}
		return region;
	}

	/**
	 * Java always selects word when defining region
	 * 
	 * @param document
	 * @param anchor
	 * @return IRegion
	 */
	private IRegion selectWord(IDocument document, int anchor) {

		try {
			int offset = anchor;
			char c;

			while (offset >= 0) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--offset;
			}

			int start = offset;

			offset = anchor;
			int length = document.getLength();

			while (offset < length) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++offset;
			}

			int end = offset;

			if (start == end)
				return new Region(start, 0);
			else
				return new Region(start + 1, end - start - 1);

		}
		catch (BadLocationException x) {
			return null;
		}
	}

	/**
	 * Open the editor associated with the current document
	 * 
	 * @return the editor opened or null if an external editor was opened or
	 *         no editor was opened
	 */
	private IEditorPart openDocumentEditor() {
		IEditorPart theEditor = null;

		IStructuredModel model = null;
		IFile file = null;
		try {
			model = getModelManager().getExistingModelForRead(getDocument());
			IFile[] files = ResourceUtil.getFilesFor(model);
			int i = 0;
			while (i < files.length && file == null) {
				if (files[i].exists()) {
					file = files[i];
				}
				else {
					++i;
				}
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		if (file != null) {
			theEditor = openFileInEditor(file);
		}
		return theEditor;
	}
}