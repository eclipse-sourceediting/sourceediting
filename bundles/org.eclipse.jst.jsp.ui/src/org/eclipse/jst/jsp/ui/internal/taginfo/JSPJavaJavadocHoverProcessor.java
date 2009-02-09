/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.taginfo;

import java.io.Reader;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavadocContentAccess;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Provides javadoc hover help documentation for java code inside JSPs
 */
public class JSPJavaJavadocHoverProcessor extends AbstractHoverProcessor {
	/*
	 * Bulk of the work was copied from
	 * org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover
	 */
	private final long LABEL_FLAGS = JavaElementLabels.ALL_FULLY_QUALIFIED | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS | JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.M_PRE_TYPE_PARAMETERS | JavaElementLabels.T_TYPE_PARAMETERS | JavaElementLabels.USE_RESOLVED;
	private final long LOCAL_VARIABLE_FLAGS = LABEL_FLAGS & ~JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.F_POST_QUALIFIED;

	protected String getHoverInfo(IJavaElement[] result) {
		StringBuffer buffer = new StringBuffer();
		int nResults = result.length;
		if (nResults == 0)
			return null;

		if (nResults > 1) {

			for (int i = 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr = result[i];
				if (curr instanceof IMember || curr.getElementType() == IJavaElement.LOCAL_VARIABLE)
					HTMLPrinter.addBullet(buffer, getInfoText(curr));
				HTMLPrinter.endBulletList(buffer);
			}

		}
		else {

			IJavaElement curr = result[0];
			if (curr instanceof IMember) {
				IMember member = (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader = JavadocContentAccess.getHTMLContentReader(member, true, true);
				}
				catch (JavaModelException ex) {
					return null;
				}
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
			}
			else if (curr.getElementType() == IJavaElement.LOCAL_VARIABLE || curr.getElementType() == IJavaElement.TYPE_PARAMETER)
				HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
		}

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		// get JSP translation object for this viewer's document
		IDOMModel xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(textViewer.getDocument());
		try {
			if (xmlModel != null) {
				IDOMDocument xmlDoc = xmlModel.getDocument();
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				if (adapter != null) {
					JSPTranslation translation = adapter.getJSPTranslation();
					
					IJavaElement[] result = translation.getElementsFromJspRange(hoverRegion.getOffset(), hoverRegion.getOffset() + hoverRegion.getLength());
					return translation.fixupMangledName(getHoverInfo(result));
				}
			}
		}
		finally {
			if (xmlModel != null)
				xmlModel.releaseFromRead();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return JavaWordFinder.findWord(textViewer.getDocument(), offset);
	}

	private String getInfoText(IJavaElement member) {
		long flags = member.getElementType() == IJavaElement.LOCAL_VARIABLE ? LOCAL_VARIABLE_FLAGS : LABEL_FLAGS;
		String label = JavaElementLabels.getElementLabel(member, flags);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < label.length(); i++) {
			char ch = label.charAt(i);
			if (ch == '<') {
				buf.append("&lt;"); //$NON-NLS-1$
			}
			else if (ch == '>') {
				buf.append("&gt;"); //$NON-NLS-1$
			}
			else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
}
