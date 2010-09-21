/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * 
 */
public class DeclContainerFormatter extends DefaultCSSSourceFormatter {

	/**
	 * 
	 */
	DeclContainerFormatter() {
		super();
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	protected String decoratedSelectorRegion(CompoundRegion region, CompoundRegion prevRegion, CSSCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();
		
		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		
		String type = region.getType();
		short selCase = -1;
		if (type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_NAME || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_VALUE || type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_PSEUDO) {
			selCase = stgy.getSelectorTagCase();
		}
		else if (type == CSSRegionContexts.CSS_SELECTOR_CLASS) {
			selCase = stgy.getClassSelectorCase();
		}
		else if (type == CSSRegionContexts.CSS_SELECTOR_ID) {
			selCase = stgy.getIdSelectorCase();
		}

		if (selCase == org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy.UPPER) {
			return text.toUpperCase();
		}
		else if (selCase == org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy.LOWER) {
			return text.toLowerCase();
		}
		else if (selCase == org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy.ASIS) {
			return text;
		}

		return decoratedRegion(region, 0, stgy);
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
		}
		else { // generate source
			String delim = getLineDelimiter(node);
			source.append(delim);
			source.append(getIndent(node));
			source.append("}");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, IRegion region, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendDelimBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 0, stgy));
		}
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
			if (flatNode.getRegionAtCharacterOffset(n - 1).getType() == CSSRegionContexts.CSS_RBRACE)
				return n - 1;
			else
				return n;
		}
		return -1;
	}
}
