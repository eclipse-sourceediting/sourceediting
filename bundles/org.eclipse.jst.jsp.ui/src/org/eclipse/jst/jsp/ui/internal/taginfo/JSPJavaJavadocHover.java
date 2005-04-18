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
package org.eclipse.jst.jsp.ui.internal.taginfo;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Provides javadoc hover help documentation for java code inside JSPs
 * @deprecated use JSPJavaJavadocHoverProcessor instead
 */
public class JSPJavaJavadocHover extends JavadocHover {
	
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		// get JSP translation object for this viewer's document
		IDOMModel xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(textViewer.getDocument());
		try {
			if(xmlModel != null) {
				IDOMDocument xmlDoc = xmlModel.getDocument();
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				if (adapter != null) {
					JSPTranslation translation = adapter.getJSPTranslation();
					IJavaElement[] result = translation.getElementsFromJspRange(hoverRegion.getOffset(), hoverRegion.getOffset() + hoverRegion.getLength());
					return getHoverInfo(result);
				}
			}
		}
		finally {
			if(xmlModel != null) 
				xmlModel.releaseFromRead();
		}
		return null;
	}
}
