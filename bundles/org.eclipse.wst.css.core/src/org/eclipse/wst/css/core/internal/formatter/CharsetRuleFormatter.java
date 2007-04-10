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
import org.eclipse.wst.css.core.internal.provisional.document.ICSSCharsetRule;
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
public class CharsetRuleFormatter extends DefaultCSSSourceFormatter {

	public final static java.lang.String CHARSET = "@charset";//$NON-NLS-1$
	private static CharsetRuleFormatter instance;

	/**
	 * 
	 */
	CharsetRuleFormatter() {
		super();
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		StringBuffer buf = new StringBuffer();
		if (node == null || attr == null)
			return buf;

		if (!ICSSCharsetRule.ENCODING.equalsIgnoreCase(attr.getName()))
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
				} else
					context.start = it.getStructuredDocumentRegion().getStartOffset(region);
				context.end = it.getStructuredDocumentRegion().getEndOffset(region);
			} else {
				IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
				ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
				if (region.getType() == CSSRegionContexts.CSS_S) {
					context.start = flatNode.getStartOffset(region);
					context.end = flatNode.getStartOffset(region);
				} else {
					context.start = flatNode.getEndOffset() + 1;
					context.end = flatNode.getEndOffset();
				}
			}
		}
		// generate text
		if (insert) {
			appendSpaceBefore(node, attr.getValue(), buf);
			buf.append(attr.getValue());
		}
		return buf;
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int end = ((IndexedRegion) node).getEndOffset();
		if (end > 0) { // format source
			int start = ((IndexedRegion) node).getStartOffset();
			formatPre(node, new FormatRegion(start, end - start), source);
		} else { // generate source
			Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
			String str = CHARSET;
			if (preferences.getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
				str = CHARSET.toUpperCase();
			source.append(str);
			String enc = ((ICSSCharsetRule) node).getEncoding();
			quote = CSSUtil.detectQuote(enc, quote);
			str = quote + ((enc != null) ? enc : "") + quote + ";";//$NON-NLS-1$ //$NON-NLS-2$
			appendSpaceBefore(node, str, source);
			source.append(str);
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if ((i != 0 || needS(outside[0])) && !regions[i].getType().equals(CSSRegionContexts.CSS_DELIMITER))
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedIdentRegion(regions[i], stgy));
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
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		if (!ICSSCharsetRule.ENCODING.equalsIgnoreCase(attrName))
			return -1;

		ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICSSCharsetRule.ENCODING);
		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
			return ((IndexedRegion) attr).getStartOffset();
		IndexedRegion iNode = (IndexedRegion) node;
		if (iNode.getEndOffset() <= 0)
			return -1;

		CompoundRegion regions[] = getRegionsWithoutWhiteSpaces(node.getOwnerDocument().getModel().getStructuredDocument(), new FormatRegion(iNode.getStartOffset(), iNode.getEndOffset() - iNode.getStartOffset()), stgy);
		for (int i = regions.length - 1; i >= 0; i--) {
			if (regions[i].getType() != CSSRegionContexts.CSS_COMMENT)
				return regions[i].getStartOffset();
		}
		return iNode.getEndOffset();
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
	public synchronized static CharsetRuleFormatter getInstance() {
		if (instance == null)
			instance = new CharsetRuleFormatter();
		return instance;
	}
}
