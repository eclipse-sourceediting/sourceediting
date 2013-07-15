/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.taginfo;

import java.io.Reader;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.ui.JSdocContentAccess;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabels;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTHoverProcessor extends AbstractHoverProcessor {
	private static final String[] PARTITION_TYPES= new String[] {"org.eclipse.wst.html.SCRIPT","org.eclipse.wst.html.SCRIPT.EVENTHANDLER"}; //$NON-NLS-1$ //$NON-NLS-2$
	/*
	 * Bulk of the work was copied from
	 * org.eclipse.wst.jsdt.internal.ui.text.java.hover.JavadocHover
	 */
	private final long LABEL_FLAGS = JavaScriptElementLabels.ALL_FULLY_QUALIFIED | JavaScriptElementLabels.M_PRE_RETURNTYPE | JavaScriptElementLabels.M_PARAMETER_TYPES | JavaScriptElementLabels.M_PARAMETER_NAMES | JavaScriptElementLabels.M_EXCEPTIONS | JavaScriptElementLabels.F_PRE_TYPE_SIGNATURE | JavaScriptElementLabels.M_PRE_TYPE_PARAMETERS | JavaScriptElementLabels.T_TYPE_PARAMETERS | JavaScriptElementLabels.USE_RESOLVED;
	private final long LOCAL_VARIABLE_FLAGS = LABEL_FLAGS & ~JavaScriptElementLabels.F_FULLY_QUALIFIED | JavaScriptElementLabels.F_POST_QUALIFIED;
	
	private String getHoverInfo(IJavaScriptElement[] result) {
		StringBuffer buffer = new StringBuffer();
		int nResults = result.length;
		if (nResults == 0) {
			return null;
		}
		if (nResults > 1) {
			for (int i = 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaScriptElement curr = result[i];
				if (curr instanceof IMember || curr.getElementType() == IJavaScriptElement.LOCAL_VARIABLE) {
					HTMLPrinter.addBullet(buffer, getInfoText(curr));
				}
				HTMLPrinter.endBulletList(buffer);
			}
		} else {
			IJavaScriptElement curr = result[0];
			if (curr == null) {
				return null;
			}
			if (curr instanceof IMember) {
				IMember member = (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader = JSdocContentAccess.getHTMLContentReader(member, true, true);
				} catch (JavaScriptModelException ex) {
					return null;
				}
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
			} else if (curr.getElementType() == IJavaScriptElement.LOCAL_VARIABLE) {
				HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
			}
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
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(textViewer.getDocument());
			if (xmlModel != null) {
				IDOMDocument xmlDoc = xmlModel.getDocument();
				JsTranslationAdapter adapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
				if (adapter != null) {
					try {
						boolean proceed = false;
						ITypedRegion[] partitions = xmlDoc.getStructuredDocument().computePartitioning(hoverRegion.getOffset(), hoverRegion.getLength());
						for (int i = 0; i < partitions.length; i++) {
							for (int j = 0; j < PARTITION_TYPES.length; j++) {
								if (PARTITION_TYPES[j].equals(partitions[i].getType())) {
									proceed = true;
									break;
								}
							}
						}
						if (proceed) {
							IJsTranslation translation = adapter.getJsTranslation(true);
							IJavaScriptElement[] result = translation.getElementsFromJsRange(translation.getJavaScriptOffset(hoverRegion.getOffset()), translation.getJavaScriptOffset(hoverRegion.getOffset() + hoverRegion.getLength()));
		
							return translation.fixupMangledName(getHoverInfo(result));
						}
					}
					catch (BadLocationException e) {
						// do nothing
					}
				}
			}
		}
		finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
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
		return JsWordFinder.findWord(textViewer.getDocument(), offset);
	}
	
	private String getInfoText(IJavaScriptElement member) {
		long flags = member.getElementType() == IJavaScriptElement.LOCAL_VARIABLE ? LOCAL_VARIABLE_FLAGS : LABEL_FLAGS;
		String label = JavaScriptElementLabels.getElementLabel(member, flags);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < label.length(); i++) {
			char ch = label.charAt(i);
			if (ch == '<') {
				buf.append("&lt;"); //$NON-NLS-1$
			} else if (ch == '>') {
				buf.append("&gt;"); //$NON-NLS-1$
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
}
