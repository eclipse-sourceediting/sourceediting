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



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * 
 */
public class StyleDeclItemFormatter extends DefaultCSSSourceFormatter {

	private static StyleDeclItemFormatter instance;

	/**
	 * 
	 */
	StyleDeclItemFormatter() {
		super();
	}

	/**
	 * 
	 */
	private void appendAfterColonSpace(ICSSNode node, StringBuffer source) {
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		int n = preferences.getInt(CSSCorePreferenceNames.FORMAT_PROP_POST_DELIM);

		if (preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH) > 0 && (!preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node.getOwnerDocument().getNodeType() != ICSSNode.STYLEDECLARATION_NODE)) {
			int length = getLastLineLength(node, source);
			int append = getFirstChildRegionLength(node);
			if (length + n + append > preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH)) {
				source.append(getLineDelimiter(node));
				source.append(getIndent(node));
				source.append(getIndentString());
				n = 0; // no space is necessary
			}
		}
		// no delimiter case
		while (n-- > 0)
			source.append(" ");//$NON-NLS-1$
	}

	/**
	 * 
	 */
	private int getFirstChildRegionLength(ICSSNode node) {
		ICSSNode firstChild = node.getFirstChild();
		if (firstChild == null)
			return 1;
		int start = ((IndexedRegion) firstChild).getStartOffset();
		RegionIterator itr = new RegionIterator(node.getOwnerDocument().getModel().getStructuredDocument(), start);
		while (itr.hasNext()) {
			ITextRegion reg = itr.next();
			if (reg != null && reg.getType() != CSSRegionContexts.CSS_S)
				return reg.getTextLength();
		}
		return 1;
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		StringBuffer buf = new StringBuffer();
		if (node == null || attr == null)
			return buf;

		if (!ICSSStyleDeclItem.IMPORTANT.equalsIgnoreCase(attr.getName()))
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
		}
		return buf;
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
		ICSSNode prev = (child != null) ? child.getPreviousSibling() : node.getLastChild();
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
				regions = getRegions(structuredDocument, new FormatRegion(start, end - start), exceptFor, null);
			}
			// generate source
			for (int i = 0; i < regions.length; i++) {
				appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 2, stgy)); // must
				// be
				// comments
			}
		}
		if(child != null) {
			boolean append = true;
			if (child instanceof ICSSPrimitiveValue) {
				if (((ICSSPrimitiveValue) child).getPrimitiveType() == ICSSPrimitiveValue.CSS_COMMA)
					toAppend = ",";//$NON-NLS-1$
				else if (((ICSSPrimitiveValue) child).getPrimitiveType() == ICSSPrimitiveValue.CSS_SLASH)
					toAppend = "/";//$NON-NLS-1$
				ICSSNode prevSibling = child.getPreviousSibling();
				if (prevSibling instanceof ICSSPrimitiveValue && ((ICSSPrimitiveValue)prevSibling).getPrimitiveType() == ICSSPrimitiveValue.CSS_SLASH)
					append = false;
			}
			if (toAppend != null && !toAppend.equals(",") && !toAppend.equals("/") && append) {//$NON-NLS-1$ //$NON-NLS-2$
				appendSpaceBefore(node, toAppend, source);
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
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 2, stgy)); // must be
			// comments
		}
		if (needS(outside[1])) {
			if (((IndexedRegion) child).getStartOffset() == region.getOffset() + region.getLength()) {
				if (toAppend != "," && toAppend != "/") //$NON-NLS-1$ //$NON-NLS-2$
					appendSpaceBefore(node, toAppend, source);
			}
		}
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
				appendSpaceBefore(node, regions[i], source);
				source.append(decoratedIdentRegion(regions[i], stgy));
			}
		} else { // generate source
			// append "!important"
			String priority = ((ICSSStyleDeclItem) node).getPriority();
			if (priority != null && priority.length() > 0) {
				appendSpaceBefore(node, priority, source);
				source.append(priority);
			}
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
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedIdentRegion(regions[i], stgy));
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		if (end > 0) { // format source
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedPropNameRegion(regions[i], stgy));
			}
		} else { // generatoe source
			ICSSStyleDeclItem item = (ICSSStyleDeclItem) node;
			if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_NAME) == CSSCorePreferenceNames.UPPER)
				source.append(item.getPropertyName().toUpperCase());
			else
				source.append(item.getPropertyName());

			int k = preferences.getInt(CSSCorePreferenceNames.FORMAT_PROP_PRE_DELIM);
			if (preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH) > 0 && (!preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node.getOwnerDocument().getNodeType() != ICSSNode.STYLEDECLARATION_NODE)) {
				int length = getLastLineLength(node, source);
				int append = 1;
				if (length + k + append > preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH)) {
					source.append(getLineDelimiter(node));
					source.append(getIndent(node));
					source.append(getIndentString());
					k = 0; // no space is necessary
				}
			}
			// no delimiter case
			while (k-- > 0)
				source.append(" ");//$NON-NLS-1$
			source.append(":");//$NON-NLS-1$
		}
		if (!isCleanup() || getCleanupStrategy(node).isFormatSource()) {
			appendAfterColonSpace(node, source);
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
			source.append(decoratedPropNameRegion(regions[i], stgy));
		}
		if (needS(outside[1])) {
			if (isIncludesPreEnd(node, region) && (!isCleanup() || getCleanupStrategy(node).isFormatSource())) {
				appendAfterColonSpace(node, source);
			} else
				appendSpaceBefore(node, outside[1], source);
		}
	}

	/**
	 * 
	 */
	public StringBuffer formatValue(ICSSNode node) {
		StringBuffer source = new StringBuffer();
		formatChildren(node, source);

		return source;
	}

	/**
	 * 
	 */
	public int getAttrInsertPos(ICSSNode node, String attrName) {
		if (node == null || attrName == null || attrName.length() == 0)
			return -1;

		if (!ICSSStyleDeclItem.IMPORTANT.equalsIgnoreCase(attrName))
			return -1;

		ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICSSStyleDeclItem.IMPORTANT);
		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
			return ((IndexedRegion) attr).getStartOffset();
		IndexedRegion iNode = (IndexedRegion) node;
		if (iNode.getEndOffset() <= 0)
			return -1;

		CompoundRegion regions[] = getRegionsWithoutWhiteSpaces(node.getOwnerDocument().getModel().getStructuredDocument(), new FormatRegion(iNode.getStartOffset(), iNode.getEndOffset() - iNode.getStartOffset()), getCleanupStrategy(node));
		for (int i = regions.length - 1; i >= 0; i--) {
			if (regions[i].getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_IMPORTANT)
				return regions[i].getStartOffset();
		}
		return iNode.getEndOffset();
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n < 0) {
			return -1;
		}
		IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
		if (flatNode != null && flatNode.getType() == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
			n -= flatNode.getLength();
		}
		if (n > 0) {
			String important = ((ICSSStyleDeclItem) node).getPriority();
			if (important != null && important.length() > 0) {
				// find before "!important" position
				flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
				RegionIterator it = new RegionIterator(flatNode, flatNode.getRegionAtCharacterOffset(n - 1));
				while (it.hasPrev()) {
					ITextRegion region = it.prev();
					if (it.getStructuredDocumentRegion() != flatNode)
						break;
					if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_IMPORTANT)
						return it.getStructuredDocumentRegion().getStartOffset(region);
				}
			}
			// skip last space
			flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
			ITextRegion region = flatNode.getRegionAtCharacterOffset(n - 1);
			if (region != null) {
				n -= region.getLength() - region.getTextLength();
			}

			return n;
		}
		return -1;
	}

	/**
	 * 
	 */
	public synchronized static StyleDeclItemFormatter getInstance() {
		if (instance == null)
			instance = new StyleDeclItemFormatter();
		return instance;
	}

	private String getIndentString() {
		StringBuffer indent = new StringBuffer();

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		if (preferences != null) {
			char indentChar = ' ';
			String indentCharPref = preferences.getString(CSSCorePreferenceNames.INDENTATION_CHAR);
			if (CSSCorePreferenceNames.TAB.equals(indentCharPref)) {
				indentChar = '\t';
			}
			int indentationWidth = preferences.getInt(CSSCorePreferenceNames.INDENTATION_SIZE);

			for (int i = 0; i < indentationWidth; i++) {
				indent.append(indentChar);
			}
		}
		return indent.toString();
	}
}
