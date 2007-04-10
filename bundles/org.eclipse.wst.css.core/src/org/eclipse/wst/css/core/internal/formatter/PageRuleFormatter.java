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
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * 
 */
public class PageRuleFormatter extends DeclContainerFormatter {

	public final static java.lang.String PAGE = "@page";//$NON-NLS-1$
	private static PageRuleFormatter instance;

	/**
	 * 
	 */
	PageRuleFormatter() {
		super();
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		StringBuffer buf = new StringBuffer();
		if (node == null || attr == null)
			return buf;

		if (!ICSSPageRule.SELECTOR.equalsIgnoreCase(attr.getName()))
			return buf;

		// get region to replace
		if (context != null && ((IndexedRegion) node).getEndOffset() > 0) {
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0) {
				IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) attr).getEndOffset() - 1);
				ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) attr).getEndOffset() - 1);
				RegionIterator it = new RegionIterator(flatNode, region);
				it.next();
				if (it.hasNext()) {
					ITextRegion next = it.next();
					if (next.getType() == CSSRegionContexts.CSS_S)
						context.end = it.getStructuredDocumentRegion().getEndOffset(next);
					else
						context.end = it.getStructuredDocumentRegion().getEndOffset(region);
				}
				else
					context.end = it.getStructuredDocumentRegion().getEndOffset(region);
				context.start = it.getStructuredDocumentRegion().getStartOffset(region);
			}
			else {
				int pos = getAttrInsertPos(node, attr.getName());
				context.start = pos;
				context.end = pos;
				/*
				 * IStructuredDocumentRegion flatNode =
				 * node.getOwnerDocument().getModel().getStructuredDocument().getNodeAtCharacterOffset(((IndexedRegion)node).getStartOffset());
				 * ITextRegion region =
				 * flatNode.getRegionAtCharacterOffset(((IndexedRegion)node).getStartOffset());
				 * if (region.getType() == CSSRegionContexts.S) {
				 * context.start = region.getStartOffset(); context.end =
				 * region.getEndOffset(); } else { context.start =
				 * flatNode.getEndOffset() + 1; context.end =
				 * flatNode.getEndOffset(); }
				 */}
		}
		// generate text
		if (insert) {
			String val = attr.getValue();
			if (val != null && val.length() > 0) {
				buf.append(val);
				appendSpaceBefore(node, "", buf);//$NON-NLS-1$
			}
		}
		return buf;
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);

		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBetween(node, regions[i - 1], regions[i], source);
				source.append(decoratedIdentRegion(regions[i], stgy));
			}
		}
		else { // generate source
			String str = PAGE;
			if (CSSCorePlugin.getDefault().getPluginPreferences().getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
				str = PAGE.toUpperCase();
			source.append(str);
			str = ((ICSSPageRule) node).getSelectorText();
			if (str != null && str.length() > 0) {
				appendSpaceBefore(node, str, source);
				source.append(str);
			}
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
				appendSpaceBetween(node, (i == 0) ? outside[0] : regions[i - 1], regions[i], source);
			source.append(decoratedIdentRegion(regions[i], stgy));
		}
		if (needS(outside[1])) {
			if (isIncludesPreEnd(node, region))
				appendDelimBefore(node, null, source);
			else
				appendSpaceBetween(node, regions[regions.length - 1], outside[1], source);
		}
	}

	/**
	 * 
	 */
	public int getAttrInsertPos(ICSSNode node, String attrName) {
		if (node == null || attrName == null || attrName.length() == 0)
			return -1;

		if (!ICSSPageRule.SELECTOR.equalsIgnoreCase(attrName))
			return -1;

		ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICSSPageRule.SELECTOR);
		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
			return ((IndexedRegion) attr).getStartOffset();
		else if (((IndexedRegion) node).getEndOffset() > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) node).getStartOffset());
			ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) node).getStartOffset());
			RegionIterator it = new RegionIterator(flatNode, region);
			while (it.hasNext()) {
				region = it.next();
				if (region.getType() == CSSRegionContexts.CSS_LBRACE) {
					return it.getStructuredDocumentRegion().getStartOffset(region);
				}
				if (it.getStructuredDocumentRegion().getEndOffset(region) >= ((IndexedRegion) node).getEndOffset())
					break;
			}
		}
		return ((IndexedRegion) node).getStartOffset();
	}

	/**
	 * 
	 */
	public synchronized static PageRuleFormatter getInstance() {
		if (instance == null)
			instance = new PageRuleFormatter();
		return instance;
	}
}
