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

import java.io.Reader;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.javadoc.JavaDocAccess;
import org.eclipse.jdt.internal.ui.text.javadoc.JavaDoc2HTMLTextReader;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLabels;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

/**
 * Provides javadoc hover help documentation for java code inside JSPs
 */
public class JSPJavaJavadocHoverProcessor implements ITextHover {
	// NOTE: https://bugs.eclipse.org/bugs/show_bug.cgi?id=77888  
	// was opened to request referenced jdt internal classes be made public
	
	private final long LABEL_FLAGS=  JavaElementLabels.ALL_FULLY_QUALIFIED
	| JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS 
	| JavaElementLabels.F_PRE_TYPE_SIGNATURE;
	
	private String getInfoText(IJavaElement member) {
		String label= JavaElementLabels.getElementLabel(member, LABEL_FLAGS);
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < label.length(); i++) {
			char ch= label.charAt(i);
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
	
	private String getHoverInfo(IJavaElement[] result) {
		StringBuffer buffer= new StringBuffer();
		int nResults= result.length;
		if (nResults == 0)
			return null;
		
		if (nResults > 1) {
			
			for (int i= 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr= result[i];
				if (curr instanceof IMember || curr.getElementType() == IJavaElement.LOCAL_VARIABLE)
					HTMLPrinter.addBullet(buffer, getInfoText(curr));
				HTMLPrinter.endBulletList(buffer);
			}
			
		} else {
			
			IJavaElement curr= result[0];
			if (curr instanceof IMember) {
				IMember member= (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader= JavaDocAccess.getJavaDoc(member, true);
				} catch (JavaModelException ex) {
					return null;
				}
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, new JavaDoc2HTMLTextReader(reader));
				}
			} else if (curr.getElementType() == IJavaElement.LOCAL_VARIABLE)
				HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
		}
		
		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		// get JSP translation object for this viewer's document
		XMLModel xmlModel = (XMLModel) ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager().getExistingModelForRead(textViewer.getDocument());
		try {
			if(xmlModel != null) {
				XMLDocument xmlDoc = xmlModel.getDocument();
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return JavaWordFinder.findWord(textViewer.getDocument(), offset);
	}

}
