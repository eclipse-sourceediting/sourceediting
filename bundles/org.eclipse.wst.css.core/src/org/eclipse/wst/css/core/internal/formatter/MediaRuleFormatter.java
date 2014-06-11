/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
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
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
public class MediaRuleFormatter extends AbstractCSSSourceFormatter {

	public final static java.lang.String MEDIA = "@media";//$NON-NLS-1$
	private static MediaRuleFormatter instance;

	/**
	 * 
	 */
	MediaRuleFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
		ICSSNode prev = (child != null) ? child.getPreviousSibling() : node.getLastChild();
		int start = (prev != null) ? ((IndexedRegion) prev).getEndOffset() : 0;
		int end = (child != null) ? ((IndexedRegion) child).getStartOffset() : 0;
		if (start > 0 && start < end) { // source formatting
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			// get meaning regions
			CompoundRegion[] regions = null;
			if (exceptFor == null)
				regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			else {
				String pickupType = null;
				if ((prev != null && prev.getNodeType() == ICSSNode.MEDIALIST_NODE) || (prev == null && (child == null || child.getNodeType() != ICSSNode.MEDIALIST_NODE))) {
					pickupType = CSSRegionContexts.CSS_LBRACE;
				}
				regions = getRegions(structuredDocument, new FormatRegion(start, end - start), exceptFor, pickupType);
			}
			// extract source
			if (child != null && child.getNodeType() == ICSSNode.MEDIALIST_NODE && ((MediaList) child).getLength() > 0) { // between
				// "@media" and mediatype
				for (int i = 0; i < regions.length; i++) {
					appendSpaceBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy)); // must
					// be comments
				}
				appendSpaceBefore(node, toAppend, source);
			} else if (child != null && (child.getPreviousSibling() == null || child.getPreviousSibling().getNodeType() == ICSSNode.MEDIALIST_NODE)) { // between
				// mediatype and the first style rule
				for (int i = 0; i < regions.length; i++) {
					appendSpaceBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy)); // must
					// be comments
				}
				appendDelimBefore(node, null, source);
			} else { // between styles
				for (int i = 0; i < regions.length; i++) {
					appendDelimBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy)); // must
					// be comments
				}
				appendDelimBefore(node, null, source);
			}
		} else { // source generation
			if (child == null && prev != null && prev.getNodeType() != ICSSNode.MEDIALIST_NODE) { // after
				// the last style rule
				appendDelimBefore(node.getParentNode(), null, source);
			} else if (child != null && child.getNodeType() == ICSSNode.MEDIALIST_NODE && ((MediaList) child).getLength() > 0) { // between
				// "@media" and mediatype
				appendSpaceBefore(node, toAppend, source);
			} else if (prev != null && prev.getNodeType() == ICSSNode.MEDIALIST_NODE && ((MediaList) prev).getLength() > 0) { // between
				// mediatype and the first style rule
				appendSpaceBefore(node, "{", source);//$NON-NLS-1$
				source.append("{");//$NON-NLS-1$
				if (child != null)
					appendDelimBefore(node, null, source);
				else
					appendDelimBefore(node.getParentNode(), null, source);
			} else { // normal case
				appendDelimBefore(node, null, source);
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
		if (child != null && child.getNodeType() == ICSSNode.MEDIALIST_NODE) { // between
			// "@media" and mediatype
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 || needS(outside[0]))
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy)); // must
				// be comments
			}
			if (needS(outside[1]) && ((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength()) {
				appendSpaceBefore(node, toAppend, source);
			}
		} else if (child != null && (child.getPreviousSibling() == null || child.getPreviousSibling().getNodeType() == ICSSNode.MEDIALIST_NODE)) { // between
			// mediatype and the first style rule
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 || needS(outside[0]))
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy)); // must
				// be comments
			}
			if (needS(outside[1]) && ((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength()) {
				appendDelimBefore(node, null, source);
			}
		} else { // between styles
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 || needS(outside[0]))
					appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy)); // must
				// be comments
			}
			if (needS(outside[1]) && ((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength()) {
				appendDelimBefore(node, null, source);
			}
		}
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, StringBuffer source) {
		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) { // source formatting
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			if (node.getLastChild() == null || node.getLastChild().getNodeType() != ICSSNode.MEDIALIST_NODE) {
				for (int i = 0; i < regions.length; i++) {
					appendDelimBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy));
				}
			} else {
				boolean bInCurlyBrace = false;
				for (int i = 0; i < regions.length; i++) {
					if (!bInCurlyBrace)
						appendSpaceBefore(node, regions[i], source);
					else
						appendDelimBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy));
					if (regions[i].getType() == CSSRegionContexts.CSS_LBRACE)
						bInCurlyBrace = true;
				}
			}
		} else { // source generation
			String delim = getLineDelimiter(node);
			if (node.getLastChild() != null && node.getLastChild().getNodeType() == ICSSNode.MEDIALIST_NODE) {
				appendSpaceBefore(node, "{", source);//$NON-NLS-1$
				source.append("{");//$NON-NLS-1$
			}

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
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);
		ICSSNode child = node.getFirstChild();
		if (child != null && (child instanceof MediaList) && ((MediaList) child).getLength() == 0) {
			if (child.getNextSibling() != null)
				end = ((IndexedRegion) child.getNextSibling()).getStartOffset();
			else
				end = -1;
		}
		
		if (end > 0) { // source formatting
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedIdentRegion(regions[i], stgy));
			}
		} else { // source generation
			String str = MEDIA;
			if (CSSCorePlugin.getDefault().getPluginPreferences().getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
				str = MEDIA.toUpperCase();
			source.append(str);
		}

		if (child != null && (child instanceof MediaList) && ((MediaList) child).getLength() > 0) {
			appendSpaceBefore(node, "", source);//$NON-NLS-1$
		}
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
		if (needS(outside[1]) && !isIncludesPreEnd(node, region))
			appendSpaceBefore(node, outside[1], source);
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
			if (flatNode.getRegionAtCharacterOffset(n - 1).getType() == CSSRegionContexts.CSS_LBRACE)
				return n - 1;
			return n;
		}
		return -1;
	}

	/**
	 * 
	 */
	public synchronized static MediaRuleFormatter getInstance() {
		if (instance == null)
			instance = new MediaRuleFormatter();
		return instance;
	}

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param insertPos
	 *            int
	 */
	public int getLengthToReformatAfter(ICSSNode node, int insertPos) {
		if (node == null)
			return 0;
		IndexedRegion nnode = (IndexedRegion) node;
		if (insertPos <= 0 || !nnode.contains(insertPos - 1))
			return 0;

		if (node.getFirstChild().getNextSibling() == node.getLastChild()) { // inserted
			// first style rule
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(insertPos);
			if (flatNode == null)
				return 0;
			ITextRegion region = flatNode.getRegionAtCharacterOffset(insertPos);
			if (region == null)
				return 0;
			RegionIterator it = new RegionIterator(flatNode, region);
			while (it.hasNext()) {
				region = it.next();
				if (region.getType() == CSSRegionContexts.CSS_LBRACE)
					break;
				if (nnode.getEndOffset() <= it.getStructuredDocumentRegion().getEndOffset(region))
					break;
			}
			int pos = it.getStructuredDocumentRegion().getStartOffset(region) - insertPos;
			return (pos >= 0) ? pos : 0;

		}
		return super.getLengthToReformatAfter(node, insertPos);
	}

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param insertPos
	 *            int
	 */
	public int getLengthToReformatBefore(ICSSNode node, int insertPos) {
		if (node == null)
			return 0;
		IndexedRegion nnode = (IndexedRegion) node;
		if (insertPos <= 0 || !nnode.contains(insertPos - 1))
			return 0;

		if (node.getFirstChild().getNextSibling() == node.getLastChild()) { // inserted
			// first style rule
			int pos = ((IndexedRegion) node.getFirstChild()).getEndOffset();
			if (pos <= 0)
				pos = ((IndexedRegion) node).getStartOffset() + 6 /*
																	 * length
																	 * of
																	 * "@media"
																	 */;
			return insertPos - pos;
		}
		return super.getLengthToReformatBefore(node, insertPos);
	}
}
