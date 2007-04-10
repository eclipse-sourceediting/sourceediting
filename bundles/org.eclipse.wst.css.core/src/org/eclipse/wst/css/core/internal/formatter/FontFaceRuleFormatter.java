/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;


/**
 * 
 */
public class FontFaceRuleFormatter extends DeclContainerFormatter {

	public final static java.lang.String FONT_FACE = "@font-face";//$NON-NLS-1$
	private static FontFaceRuleFormatter instance;

	/**
	 * 
	 */
	FontFaceRuleFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatPre(org.eclipse.wst.css.core.internal.provisional.document.ICSSNode node, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);
		if (end > 0) {
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedIdentRegion(regions[i], stgy));
			}
		}
		else {
			String str = FONT_FACE;
			if (CSSCorePlugin.getDefault().getPluginPreferences().getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
				str = FONT_FACE.toUpperCase();
			source.append(str);
			appendSpaceBefore(node, "{", source);//$NON-NLS-1$
			source.append("{");//$NON-NLS-1$
		}
		appendDelimBefore(node, null, source);
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedIdentRegion(regions[i], stgy));
		}
		if (needS(outside[1])) {
			if (isIncludesPreEnd(node, region))
				appendDelimBefore(node, null, source);
			else
				appendSpaceBefore(node, outside[1], source);
		}
	}

	/**
	 * 
	 */
	public synchronized static FontFaceRuleFormatter getInstance() {
		if (instance == null)
			instance = new FontFaceRuleFormatter();
		return instance;
	}
}
