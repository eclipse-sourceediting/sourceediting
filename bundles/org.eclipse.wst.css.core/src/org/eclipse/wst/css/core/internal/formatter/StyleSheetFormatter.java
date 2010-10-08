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
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * 
 */
public class StyleSheetFormatter extends AbstractCSSSourceFormatter {

	private static StyleSheetFormatter instance;

	/**
	 * 
	 */
	StyleSheetFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
		String delim = getLineDelimiter(node);
		ICSSNode prev = (child != null) ? child.getPreviousSibling() : node.getLastChild();
		if (prev == null && child == null)
			return;
		int start = (prev != null) ? ((IndexedRegion) prev).getEndOffset() : 0;
		int end = (child != null) ? ((IndexedRegion) child).getStartOffset() : 0;
		if (start > 0 && start < end) {
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			// get meaning regions
			CompoundRegion[] regions = null;
			if (exceptFor == null)
				regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			else {
				String pickupType = CSSRegionContexts.CSS_DELIMITER;
				if (prev == null)
					pickupType = null;
				regions = getRegions(structuredDocument, new FormatRegion(start, end - start), exceptFor, pickupType);
			}
			// generate source
			if (prev != null) {
				if (regions.length > 0 && !regions[0].getType().equals(CSSRegionContexts.CSS_COMMENT))
					appendDelimBefore(node, regions[0], source);
				else if (regions.length == 0) {
					appendDelimBefore(node, null, source);
				}
			}
			for (int i = 0; i < regions.length; i++) {
				appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy)); // must
																		// be
																		// comments
																		// or
																		// semi_colon
				if (regions[i].getType() == CSSRegionContexts.CSS_DELIMITER) {
					// append delim after
					appendDelimBefore(node, null, source);
				}
			}
			if (prev != null)
				appendDelimBefore(node, null, source);
		}
		else if (prev != null && child != null) { // source generation :
													// between two rules
			source.append(delim);
			source.append(delim);
		}
		else if (((IndexedRegion) node).getEndOffset() > 0) {
			if (prev == null) { // source formatting : before the first rule
				ICSSNode next = child.getNextSibling();
				int pos = getChildInsertPos(node);
				if (next != null && ((IndexedRegion) next).getEndOffset() > 0)
					pos = ((IndexedRegion) next).getStartOffset();
				if (pos != 0) {
					IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
					CompoundRegion[] prevRegions = getRegions(structuredDocument, new FormatRegion(0, pos), null, null);
					if (prevRegions != null && prevRegions.length > 0)
						appendDelimBefore(node, null, source);
				}
			}
			else if (child == null) { // source formatting : after the last
										// rule
				if (start > 0 && start <= ((IndexedRegion) node).getEndOffset()) {
					appendDelimBefore(node, null, source);
				}
				else if (start <= 0) {
					int pos = getChildInsertPos(node);
					if (pos < ((IndexedRegion) node).getEndOffset()) {
						appendDelimBefore(node, null, source);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, IRegion region, String toAppend, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendDelimBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 0, stgy)); // must be
																	// comments
		}
		if (needS(outside[1])) {
			if (((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength())
				appendDelimBefore(node, null, source);
		}
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, StringBuffer source) {
		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) {
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
			if ((regions != null && regions.length > 0 && regions[regions.length - 1].getType() == CSSRegionContexts.CSS_CDC) || node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED)
				appendDelimBefore(node, null, source);
		}
		else { // source generation
			if (node.getOwnerDocument().getModel() != null && node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED)
				appendDelimBefore(node, null, source);
			return; // nothing
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
	protected void formatPre(org.eclipse.wst.css.core.internal.provisional.document.ICSSNode node, java.lang.StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);
		if (end > 0) {
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 || regions[i].getType() == CSSRegionContexts.CSS_CDO || node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED)
					appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
			if (node.getLastChild() != null && (source.length() > 0 || node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED)) {
				appendDelimBefore(node, null, source);
			}
		}
		else if (node.getOwnerDocument().getModel() != null && node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED) {
			appendDelimBefore(node, null, source);
			// source.append("<!--");
			// appendDelimBefore(node,null, source);
		}
		else
			return; // nothing
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, java.lang.StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]) || regions[i].getType() == CSSRegionContexts.CSS_CDO || node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED)
				appendDelimBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 0, stgy));
		}
		if (needS(outside[1])) {
			if (isIncludesPreEnd(node, region)) {
				if (source.length() > 0 || node.getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED) {
					String delim = getLineDelimiter(node);
					source.append(delim);
				}
			}
		}
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			IStructuredDocumentRegion flatNode = structuredDocument.getRegionAtCharacterOffset(n - 1);
			ITextRegion region = flatNode.getRegionAtCharacterOffset(n - 1);
			RegionIterator it = new RegionIterator(flatNode, region);
			while (it.hasPrev()) {
				ITextRegion reg = it.prev();
				if (reg.getType() == CSSRegionContexts.CSS_CDC)
					return it.getStructuredDocumentRegion().getStartOffset(reg);
			}
			return n;
		}
		return -1;
	}

	/**
	 * 
	 */
	public synchronized static StyleSheetFormatter getInstance() {
		if (instance == null)
			instance = new StyleSheetFormatter();
		return instance;
	}
}
