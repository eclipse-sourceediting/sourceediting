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



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * 
 */
public class ImportRuleFormatter extends AbstractCSSSourceFormatter {

	public final static java.lang.String IMPORT = "@import";//$NON-NLS-1$
	private static ImportRuleFormatter instance;

	/**
	 * 
	 */
	ImportRuleFormatter() {
		super();
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		StringBuffer buf = new StringBuffer();
		if (node == null || attr == null)
			return buf;

		if (!ICSSImportRule.HREF.equalsIgnoreCase(attr.getName()))
			return buf;

		// get region to replace
		if (context != null && ((IndexedRegion) node).getEndOffset() > 0) {
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0) {
				IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) attr).getStartOffset());
				ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) attr).getStartOffset());
				RegionIterator it = new RegionIterator(flatNode, region);
				it.prev();
				if (it.hasPrev()) {
					ITextRegion prev = it.prev();
					if (prev.getType() == CSSRegionContexts.CSS_S)
						context.start = it.getStructuredDocumentRegion().getStartOffset(prev);
					else
						context.start = it.getStructuredDocumentRegion().getStartOffset(region);
					it.next();
				} else
					context.start = it.getStructuredDocumentRegion().getStartOffset(region);
				it.next();
				it.next();
				if (it.hasNext()) {
					ITextRegion next = it.next();
					if (next.getType() == CSSRegionContexts.CSS_S)
						context.end = it.getStructuredDocumentRegion().getEndOffset(next);
					else
						context.end = it.getStructuredDocumentRegion().getEndOffset(region);
				} else
					context.end = it.getStructuredDocumentRegion().getEndOffset(region);
			} else {
				ICSSNode child = node.getFirstChild();
				IStructuredDocumentRegion flatNode = null;
				ITextRegion region = null;
				if (child == null) {
					flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
					region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
				} else {
					flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) child).getStartOffset() - 1);
					region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) child).getStartOffset() - 1);
				}
				if (region.getType() == CSSRegionContexts.CSS_S) {
					context.start = flatNode.getStartOffset(region);
					context.end = flatNode.getEndOffset(region);
				} else {
					context.start = flatNode.getEndOffset();
					context.end = flatNode.getEndOffset();
				}
			}
		}
		// generate text
		if (insert && attr.getValue() != null && attr.getValue().length() > 0) {
			appendSpaceBefore(node, attr.getValue(), buf);
			buf.append(attr.getValue());
			appendSpaceBefore(node, "", buf);//$NON-NLS-1$
		}
		return buf;
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
		// for media-type
		ICSSNode prev = (child != null) ? child.getPreviousSibling() : node.getLastChild();
		int start = (prev != null) ? ((IndexedRegion) prev).getEndOffset() : 0;
		int end = (child != null) ? ((IndexedRegion) child).getStartOffset() : 0;
		if (start > 0 && start < end) { // format source
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			// get meaning regions
			CompoundRegion[] regions = null;
			if (exceptFor == null)
				regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			else {
				String pickupType = CSSRegionContexts.CSS_MEDIA_SEPARATOR;
				if (prev == null || child == null)
					pickupType = null;
				regions = getRegions(structuredDocument, new FormatRegion(start, end - start), exceptFor, pickupType);
			}
			// extract source
			for (int i = 0; i < regions.length; i++) {
				appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy)); // must
				// be
				// comments
			}
			appendSpaceBefore(node, toAppend, source);
		} else if (prev != null && child != null) { // generate source : ????
			source.append(",");//$NON-NLS-1$
			appendSpaceBefore(node, toAppend, source);
		} else if (child != null) { // generate source : between 'url()' and
			// media types
			appendSpaceBefore(node, toAppend, source);
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
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 0, stgy)); // must be
			// comments
		}
		if (needS(outside[1])) {
			if (((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength())
				appendSpaceBefore(node, toAppend, source);
			else
				appendSpaceBefore(node, outside[1], source);
		}
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) {
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
		} else {
			source.append(";"); //$NON-NLS-1$
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
			if ((i != 0 || needS(outside[0])) && !regions[i].getType().equals(CSSRegionContexts.CSS_DELIMITER))
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

		if (end > 0) { // format source
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				String str = regions[i].getText();
				if (regions[i].getType() == CSSRegionContexts.CSS_IMPORT)
					str = decoratedIdentRegion(regions[i], stgy);
				else
					str = decoratedPropValueRegion(regions[i], stgy);
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(str);
			}
		} else { // generate source
			Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

			String str = IMPORT;
			if (preferences.getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
				str = IMPORT.toUpperCase();
			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
			source.append(str);
			str = "url(";//$NON-NLS-1$
			if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER)
				str = str.toUpperCase();
			String href = ((ICSSImportRule) node).getHref();
			quote = CSSUtil.detectQuote(href, quote);
			str = str + quote + href + quote + ")";//$NON-NLS-1$
			appendSpaceBefore(node, str, source);
			source.append(str);
		}
		ICSSNode child = node.getFirstChild();
		if (child != null && (child instanceof org.w3c.dom.stylesheets.MediaList) && ((org.w3c.dom.stylesheets.MediaList) child).getLength() > 0) {
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
			String str = regions[i].getText();
			if (regions[i].getType() == CSSRegionContexts.CSS_IMPORT)
				str = decoratedIdentRegion(regions[i], stgy);
			else
				str = decoratedPropValueRegion(regions[i], stgy);
			if (i != 0 || needS(outside[0]))
				appendSpaceBefore(node, regions[i], source);
			source.append(str);
		}
		if (needS(outside[1]) && !isIncludesPreEnd(node, region))
			appendSpaceBefore(node, outside[1], source);
	}

	/**
	 * 
	 */
	public int getAttrInsertPos(ICSSNode node, String attrName) {
		if (node == null || attrName == null || attrName.length() == 0)
			return -1;

		if (!ICSSImportRule.HREF.equalsIgnoreCase(attrName))
			return -1;

		ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICSSImportRule.HREF);
		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
			return ((IndexedRegion) attr).getStartOffset();
		IndexedRegion iNode = (IndexedRegion) node;
		if (iNode.getEndOffset() <= 0)
			return -1;

		FormatRegion formatRegion = null;
		ICSSNode child = node.getFirstChild();
		if (child != null && ((IndexedRegion) child).getEndOffset() > 0)
			formatRegion = new FormatRegion(iNode.getStartOffset(), ((IndexedRegion) child).getStartOffset() - iNode.getStartOffset());
		else
			formatRegion = new FormatRegion(iNode.getStartOffset(), iNode.getEndOffset() - iNode.getStartOffset());
		CompoundRegion regions[] = getRegionsWithoutWhiteSpaces(node.getOwnerDocument().getModel().getStructuredDocument(), formatRegion, getCleanupStrategy(node));

		boolean atrule = false;
		for (int i = 0; i < regions.length; i++) {
			if (regions[i].getType() == CSSRegionContexts.CSS_IMPORT) {
				atrule = true;
				continue;
			} else if (!atrule)
				continue;
			if (regions[i].getType() != CSSRegionContexts.CSS_COMMENT)
				return regions[i].getStartOffset();
		}
		return (child != null && ((IndexedRegion) child).getEndOffset() > 0) ? ((IndexedRegion) child).getStartOffset() : iNode.getEndOffset();
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
			if (flatNode.getRegionAtCharacterOffset(n - 1).getType() == CSSRegionContexts.CSS_DELIMITER)
				return n - 1;
			return n;
		}
		return -1;
	}

	/**
	 * 
	 */
	public synchronized static ImportRuleFormatter getInstance() {
		if (instance == null)
			instance = new ImportRuleFormatter();
		return instance;
	}
}
