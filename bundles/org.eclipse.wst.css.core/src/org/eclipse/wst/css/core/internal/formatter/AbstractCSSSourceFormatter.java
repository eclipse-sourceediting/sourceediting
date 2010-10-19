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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategyImpl;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.internal.util.CSSLinkConverter;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;

/**
 * 
 */
public abstract class AbstractCSSSourceFormatter implements CSSSourceGenerator {

	protected final static short GENERATE = 0;
	protected final static short FORMAT = 1;
	protected final static short CLEANUP = 2;
	protected static short strategy;

	/**
	 * 
	 */
	AbstractCSSSourceFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void appendDelimBefore(ICSSNode node, CompoundRegion toAppend, StringBuffer source) {
		if (node == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		String delim = getLineDelimiter(node);

		boolean needIndent = !(node instanceof ICSSStyleSheet);
		if (toAppend == null) {
			source.append(delim);
			source.append(getIndent(node));
			if (needIndent)
				source.append(getIndentString());
		}
		else {
			String type = toAppend.getType();
			if (type == CSSRegionContexts.CSS_COMMENT) {
				RegionIterator it = new RegionIterator(toAppend.getDocumentRegion(), toAppend.getTextRegion());
				it.prev();
				ITextRegion prev = it.prev();
				int[] result = null;
				if (prev == null || (prev.getType() == CSSRegionContexts.CSS_S && (result = TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, it.getStructuredDocumentRegion().getText(prev), 0))[0] >= 0)) {
					// Collapse to one empty line if there's more than one.
					int offset = result[0] + DefaultLineTracker.DELIMITERS[result[1]].length();
					if (offset < it.getStructuredDocumentRegion().getText(prev).length() ) {
						if (TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, it.getStructuredDocumentRegion().getText(prev), offset)[0] >= 0) {
							source.append(delim);
						}
					}
					source.append(delim);
					source.append(getIndent(node));
					if (needIndent)
						source.append(getIndentString());
				}
				else if (prev.getType() == CSSRegionContexts.CSS_COMMENT) {
					String fullText = toAppend.getDocumentRegion().getFullText(prev);
					String trimmedText = toAppend.getDocumentRegion().getText(prev);
					String whiteSpaces = "";//$NON-NLS-1$
					if (fullText != null && trimmedText != null)
					    whiteSpaces = fullText.substring(trimmedText.length());
					int[] delimiterFound = TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, whiteSpaces, 0);
					if (delimiterFound[0] != -1) {
						source.append(delim);	
					}
					else {
						appendSpaceBefore(node, toAppend.getText(), source);
						
						/*If two comments can't be adjusted in one line(combined length exceeds line width),
						 * a tab is also appended along with next line delimiter , we need to remove that. 
						 */
						if (source.toString().endsWith(getIndentString())) {
							source.delete((source.length() - getIndentString().length()), source.length());
						}
					}
				}
				else {
					appendSpaceBefore(node, toAppend.getText(), source);
				}
			}
			else if (type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
				RegionIterator it = new RegionIterator(toAppend.getDocumentRegion(), toAppend.getTextRegion());
				it.prev();
				ITextRegion prev = it.prev();

				Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

				if (prev.getType() == CSSRegionContexts.CSS_S && TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, it.getStructuredDocumentRegion().getText(prev), 0)[0] >= 0) {
					source.append(delim);
					source.append(getIndent(node));
					if (needIndent)
						source.append(getIndentString());
				}
				else if (preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH) > 0 && (!preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node.getOwnerDocument().getNodeType() != ICSSNode.STYLEDECLARATION_NODE)) {
					int length = getLastLineLength(node, source);
					int append = 1;
					if (length + append > preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH)) {
						source.append(getLineDelimiter(node));
						source.append(getIndent(node));
						if (needIndent)
							source.append(getIndentString());
					}
				}
			}
			else if (type == CSSRegionContexts.CSS_RBRACE || type == CSSRegionContexts.CSS_LBRACE) {
				source.append(delim);
				source.append(getIndent(node));
			}
			else {
				source.append(delim);
				source.append(getIndent(node));
				if (needIndent)
					source.append(getIndentString());
			}
		}
	}

	/**
	 * 
	 */
	protected void appendSpaceBefore(ICSSNode node, CompoundRegion toAppend, StringBuffer source) {
		if (node == null || toAppend == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		String type = toAppend.getType();

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

		boolean needIndent = !(node instanceof ICSSStyleSheet);
		if (type == CSSRegionContexts.CSS_COMMENT) {
			// check whether previous region is 'S' and has CR-LF
			String delim = getLineDelimiter(node);
			RegionIterator it = new RegionIterator(toAppend.getDocumentRegion(), toAppend.getTextRegion());
			it.prev();
			ITextRegion prev = it.prev();
			if (prev.getType() == CSSRegionContexts.CSS_S && TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, it.getStructuredDocumentRegion().getText(prev), 0)[0] >= 0) {
				source.append(delim);
				source.append(getIndent(node));
				if (needIndent)
					source.append(getIndentString());
			}
			else {
				appendSpaceBefore(node, toAppend.getText(), source);
			}
		}
		else if (type == CSSRegionContexts.CSS_LBRACE && preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE)) {
			String delim = getLineDelimiter(node);
			source.append(delim);
			source.append(getIndent(node));
			// } else if (type == CSSRegionContexts.CSS_CURLY_BRACE_CLOSE) {
			// } else if (type == CSSRegionContexts.CSS_INCLUDES || type ==
			// CSSRegionContexts.CSS_DASHMATCH) {
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_SEPARATOR && node instanceof ICSSStyleDeclItem) {
			int n = preferences.getInt(CSSCorePreferenceNames.FORMAT_PROP_PRE_DELIM);
			// no delimiter case
			while (n-- > 0)
				source.append(" ");//$NON-NLS-1$
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR || type == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE) {
			if (preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH) > 0 && (!preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node.getOwnerDocument().getNodeType() != ICSSNode.STYLEDECLARATION_NODE)) {
				int length = getLastLineLength(node, source);
				int append = 1;
				if (length + append > preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH)) {
					source.append(getLineDelimiter(node));
					source.append(getIndent(node));
					if (needIndent)
						source.append(getIndentString());
				}
			}
		}
		else if (CSSRegionContexts.CSS_FOREIGN_ELEMENT == type || CSSRegionContexts.CSS_DECLARATION_DELIMITER == type) {
			return;
		}
		else
			appendSpaceBefore(node, toAppend.getText(), source);
	}

	/**
	 * 
	 */
	protected void appendSpaceBefore(ICSSNode node, String toAppend, StringBuffer source) {
		if (node == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		if (toAppend != null && toAppend.startsWith("{") && preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE)) {//$NON-NLS-1$
			source.append(getLineDelimiter(node));
			source.append(getIndent(node));
			return;
		}
		else if (/* ! mgr.isOnePropertyPerLine() && */preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH) > 0 && (!preferences.getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node.getOwnerDocument().getNodeType() != ICSSNode.STYLEDECLARATION_NODE)) {
			int n = getLastLineLength(node, source);
			int append = (toAppend != null) ? TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, toAppend, 0)[0] : 0;
			if (toAppend != null)
				append = (append < 0) ? toAppend.length() : append;
			if (n + append + 1 > preferences.getInt(CSSCorePreferenceNames.LINE_WIDTH)) {
				source.append(getLineDelimiter(node));
				source.append(getIndent(node));
				source.append(getIndentString());
				return;
			}
		}
		source.append(" ");//$NON-NLS-1$
	}

	/**
	 * 
	 */
	public final StringBuffer cleanup(ICSSNode node) {
		short oldStrategy = strategy;
		strategy = CLEANUP;
		StringBuffer source = formatProc(node);
		strategy = oldStrategy;

		return source;
	}

	/**
	 * 
	 */
	public final StringBuffer cleanup(ICSSNode node, IRegion region) {
		short oldStrategy = strategy;
		strategy = CLEANUP;
		StringBuffer source = formatProc(node, region);
		strategy = oldStrategy;

		return source;
	}

	/**
	 * 
	 */
	protected String decoratedIdentRegion(CompoundRegion region, CSSCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		
		if (region.getType() == CSSRegionContexts.CSS_STRING || region.getType() == CSSRegionContexts.CSS_URI)
			return decoratedRegion(region, 0, stgy);

		if (isCleanup()) {
			if (stgy.getIdentCase() == CSSCleanupStrategy.ASIS || region.getType() == CSSRegionContexts.CSS_COMMENT)
				return text;
			else if (stgy.getIdentCase() == CSSCleanupStrategy.UPPER)
				return text.toUpperCase();
			else
				return text.toLowerCase();
		}

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		if (region.getType() == CSSRegionContexts.CSS_COMMENT)
			return text;
		else if (preferences.getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
			return text.toUpperCase();
		else
			return text.toLowerCase();
	}

	/**
	 * 
	 */
	protected String decoratedPropNameRegion(CompoundRegion region, CSSCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		
		if (region.getType() == CSSRegionContexts.CSS_STRING || region.getType() == CSSRegionContexts.CSS_URI)
			return decoratedRegion(region, 1, stgy);
		if (isCleanup()) {
			if (stgy.getPropNameCase() == CSSCleanupStrategy.ASIS || region.getType() != CSSRegionContexts.CSS_DECLARATION_PROPERTY)
				return text;
			else if (stgy.getPropNameCase() == CSSCleanupStrategy.UPPER)
				return text.toUpperCase();
			else
				return text.toLowerCase();
		}
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

		if (region.getType() != CSSRegionContexts.CSS_DECLARATION_PROPERTY)
			return text;
		else if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_NAME) == CSSCorePreferenceNames.UPPER)
			return text.toUpperCase();
		else
			return text.toLowerCase();
	}

	/**
	 * 
	 */
	protected String decoratedPropValueRegion(CompoundRegion region, CSSCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		
		String type = region.getType();
		if (type == CSSRegionContexts.CSS_STRING || type == CSSRegionContexts.CSS_URI || type == CSSRegionContexts.CSS_DECLARATION_VALUE_URI)
			return decoratedRegion(region, 2, stgy);
		if (isCleanup()) {
			if (stgy.getPropValueCase() != CSSCleanupStrategy.ASIS) {
				if (type == CSSRegionContexts.CSS_COMMENT) {
				}
				else {
					if (stgy.getPropValueCase() == CSSCleanupStrategy.UPPER)
						text = text.toUpperCase();
					else
						text = text.toLowerCase();
				}
			}
		}
		return text;
	}

	/**
	 * 
	 */
	protected String decoratedRegion(CompoundRegion region, int type, CSSCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		
		String regionType = region.getType();
		if (regionType == CSSRegionContexts.CSS_URI || regionType == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
			String uri = CSSLinkConverter.stripFunc(text);

			boolean prefIsUpper = preferences.getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER;
			boolean upper = (type == 0) ? prefIsUpper : ((type == 1) ? preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_NAME) == CSSCorePreferenceNames.UPPER : preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER);
			String func = text.substring(0, 4);
			if (isCleanup()) {
				upper = ((type == 0) ? stgy.getIdentCase() : ((type == 1) ? stgy.getPropNameCase() : stgy.getPropValueCase())) == CSSCleanupStrategy.UPPER;
				func = ((type == 0) ? stgy.getIdentCase() : ((type == 1) ? stgy.getPropNameCase() : stgy.getPropValueCase())) == CSSCleanupStrategy.ASIS ? text.substring(0, 4) : (upper ? "URL(" : "url(");//$NON-NLS-2$//$NON-NLS-1$
			}
			if ((!isCleanup() && preferences.getBoolean(CSSCorePreferenceNames.FORMAT_QUOTE_IN_URI)) || (isCleanup() && stgy.isQuoteValues())) {
				String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
				quote = CSSUtil.detectQuote(uri, quote);
				text = func + quote + uri + quote + ")";//$NON-NLS-1$
			}
			else if (isCleanup() && !stgy.isQuoteValues()) {
				text = func + CSSLinkConverter.removeFunc(text) + ")";//$NON-NLS-1$
			}
			else {
				text = func + uri + ")";//$NON-NLS-1$
			}
		}
		else if (region.getType() == CSSRegionContexts.CSS_STRING && (!isCleanup() || stgy.isQuoteValues())) {
			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
			// begginning
			if (!text.startsWith(quote)) {
				if (text.startsWith("\"") || text.startsWith("\'")) //$NON-NLS-1$ //$NON-NLS-2$
					text = quote + text.substring(1);
				else
					text = quote + text;
			}
			// ending
			if (!text.endsWith(quote)) {
				if (text.endsWith("\"") || text.endsWith("\'")) //$NON-NLS-1$ //$NON-NLS-2$
					text = text.substring(0, text.length() - 1) + quote;
				else
					text = text + quote;
			}
		}
		return text;
	}

	/**
	 * 
	 */
	public final StringBuffer format(ICSSNode node) {
		short oldStrategy = strategy;
		strategy = FORMAT;
		StringBuffer source = formatProc(node);
		strategy = oldStrategy;

		return source;
	}

	/**
	 * 
	 */
	public final StringBuffer format(ICSSNode node, IRegion region) {
		short oldStrategy = strategy;
		strategy = FORMAT;
		StringBuffer source = formatProc(node, region);
		strategy = oldStrategy;

		return source;
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		return new StringBuffer(insert && (attr != null) ? attr.getValue() : "");//$NON-NLS-1$
	}

	/**
	 * Generate or format source between children('child' and its previous
	 * sibling) and append to string buffer
	 */
	abstract protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor);

	/**
	 * Generate or format source between children('child' and its previous
	 * sibling) and append to string buffer
	 */
	public final StringBuffer formatBefore(ICSSNode node, ICSSNode child, IRegion exceptFor) {
		Assert.isTrue(child == null || child.getParentNode() == node);
		StringBuffer buf = new StringBuffer();
		formatBefore(node, child, /* (child != null) ? (child.getCssText()) : */"", buf, exceptFor);//$NON-NLS-1$
		return buf;
	}

	/**
	 * Generate or format source between children('child' and its previous
	 * sibling) and append to string buffer
	 */
	protected abstract void formatBefore(ICSSNode node, ICSSNode child, IRegion region, String toAppend, StringBuffer source);

	/**
	 * 
	 */
	protected final void formatChildren(ICSSNode node, StringBuffer source) {
		ICSSNode child = node.getFirstChild();
		boolean first = true;
		while (child != null) {
			// append child
			CSSSourceFormatter formatter = (CSSSourceFormatter) ((INodeNotifier) child).getAdapterFor(CSSSourceFormatter.class);
			if (formatter == null) {
				formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) child);
			}
			StringBuffer childSource = ((AbstractCSSSourceFormatter) formatter).formatProc(child);
			if (!first) {
				formatBefore(node, child, new String(childSource), source, null);
			}
			source.append(childSource);
			// append between children
			child = child.getNextSibling();
			first = false;
		}
	}

	/**
	 * 
	 */
	protected final void formatChildren(ICSSNode node, IRegion region, StringBuffer source) {
		ICSSNode child = node.getFirstChild();
		int start = region.getOffset();
		int end = region.getOffset() + region.getLength();
		boolean first = true;
		while (child != null) {
			int curEnd = ((IndexedRegion) child).getEndOffset();
			StringBuffer childSource = null;
			boolean toFinish = false;
			if (start < curEnd) {
				int curStart = ((IndexedRegion) child).getStartOffset();
				if (curStart < end) {
					// append child
					CSSSourceFormatter formatter = (CSSSourceFormatter) ((INodeNotifier) child).getAdapterFor(CSSSourceFormatter.class);
					if (formatter == null) {
						formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) child);
					}
					if (includes(region, curStart, curEnd))
						childSource = ((AbstractCSSSourceFormatter) formatter).formatProc(child);
					else
						childSource = ((AbstractCSSSourceFormatter) formatter).formatProc(child, overlappedRegion(region, curStart, curEnd));
				}
				else
					toFinish = true;
			}
			// append between children
			if (!first) {
				curEnd = ((IndexedRegion) child).getStartOffset(); // change
				// only
				// start
				if (start < curEnd) {
					int curStart = ((IndexedRegion) child.getPreviousSibling()).getEndOffset();
					if (curStart < end) {
						String toAppend = (childSource != null) ? new String(childSource) : "";//$NON-NLS-1$
						if (includes(region, curStart, curEnd))
							formatBefore(node, child, toAppend, source, null);
						else
							formatBefore(node, child, overlappedRegion(region, curStart, curEnd), toAppend, source);
					}
				}
			}
			if (childSource != null) {
				source.append(childSource);
			}
			first = false;
			if (toFinish)
				break;
			child = child.getNextSibling();
		}
	}

	/**
	 * Generate or format source after the last child and append to string
	 * buffer
	 */
	protected abstract void formatPost(ICSSNode node, StringBuffer source);

	/**
	 * Generate or format source after the last child and append to string
	 * buffer
	 */
	protected abstract void formatPost(ICSSNode node, IRegion region, StringBuffer source);

	/**
	 * Generate or format source before the first child and append to string
	 * buffer
	 */
	protected abstract void formatPre(ICSSNode node, StringBuffer source);

	/**
	 * Generate or format source before the first child and append to string
	 * buffer
	 */
	abstract protected void formatPre(ICSSNode node, IRegion region, StringBuffer source);

	/**
	 * 
	 * @return java.lang.StringBuffer
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	protected final StringBuffer formatProc(ICSSNode node) {
		StringBuffer source = new StringBuffer();
		formatPre(node, source);
		formatChildren(node, source);
		formatPost(node, source);
		return source;
	}

	/**
	 * 
	 * @return java.lang.StringBuffer
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param region
	 *            org.eclipse.jface.text.IRegion
	 */
	protected final StringBuffer formatProc(ICSSNode node, IRegion region) {
		StringBuffer source = new StringBuffer();
		int curStart = ((IndexedRegion) node).getStartOffset();
		int curEnd = ((IndexedRegion) node).getEndOffset();
		if (node.getChildNodes().getLength() > 0) {
			curEnd = ((IndexedRegion) node.getFirstChild()).getStartOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPre(node, source);
				else
					formatPre(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = ((IndexedRegion) node.getLastChild()).getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatChildren(node, source);
				else
					formatChildren(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = ((IndexedRegion) node).getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPost(node, source);
				else
					formatPost(node, overlappedRegion(region, curStart, curEnd), source);
			}
		}
		else {
			curEnd = getChildInsertPos(node);
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPre(node, source);
				else
					formatPre(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = ((IndexedRegion) node).getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPost(node, source);
				else
					formatPost(node, overlappedRegion(region, curStart, curEnd), source);
			}
		}
		return source;
	}

	/**
	 * 
	 */
	public int getAttrInsertPos(ICSSNode node, String attrName) {
		return -1;
	}

	/**
	 * Insert the method's description here.
	 * 
	 * @return org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	protected CSSCleanupStrategy getCleanupStrategy(ICSSNode node) {
		CSSCleanupStrategy currentStrategy = CSSCleanupStrategyImpl.getInstance();
		ICSSDocument doc = node.getOwnerDocument();
		if (doc == null)
			return currentStrategy;
		ICSSModel model = doc.getModel();
		if (model == null)
			return currentStrategy;
		if (model.getStyleSheetType() != ICSSModel.EXTERNAL) {
			// TODO - TRANSITION Nakamori-san, or Kit, how can we move to
			// "HTML" plugin?
			// can we subclass?
			// currentStrategy = CSSInHTMLCleanupStrategyImpl.getInstance();
		}
		return currentStrategy;
	}

	/**
	 * 
	 */
	protected String getIndent(ICSSNode node) {
		if (node == null)
			return "";//$NON-NLS-1$
		ICSSNode parent = node.getParentNode();
		if (node instanceof ICSSAttr)
			parent = ((ICSSAttr) node).getOwnerCSSNode();
		if (parent == null)
			return "";//$NON-NLS-1$
		if (node instanceof org.w3c.dom.css.CSSStyleDeclaration)
			parent = parent.getParentNode();
		if (parent == null)
			return "";//$NON-NLS-1$

		String parentIndent = getIndent(parent);
		if (parent instanceof org.w3c.dom.css.CSSRule)
			return parentIndent + getIndentString();
		if (node.getParentNode() instanceof ICSSStyleDeclaration)
			return parentIndent + getIndentString();
		return parentIndent;
	}

	/**
	 * 
	 */
	protected int getLastLineLength(ICSSNode node, StringBuffer source) {
		if (node == null || source == null)
			return 0;
		String delim = getLineDelimiter(node);
		String str = new String(source);
		int n = str.lastIndexOf(delim);
		if (n < 0)
			return str.length();

		return str.length() - n - delim.length();
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
		if (insertPos < 0 || !nnode.contains(insertPos))
			return 0;

		IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(insertPos);
		if (flatNode == null)
			return 0;
		ITextRegion region = flatNode.getRegionAtCharacterOffset(insertPos);
		if (region == null)
			return 0;
		RegionIterator it = new RegionIterator(flatNode, region);
		boolean found = false;
		while (it.hasNext()) {
			region = it.next();
			// if (region.getType() != CSSRegionContexts.CSS_S &&
			// region.getType() != CSSRegionContexts.CSS_DELIMITER &&
			// region.getType() !=
			// CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
			if (region.getType() != CSSRegionContexts.CSS_S) {
				found = true;
				break;
			}
		}
		int pos = (found ? it.getStructuredDocumentRegion().getStartOffset(region) : it.getStructuredDocumentRegion().getTextEndOffset(region)) - insertPos;
		return (pos >= 0) ? pos : 0;
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

		IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(insertPos - 1);
		if (flatNode == null)
			return 0;
		ITextRegion region = flatNode.getRegionAtCharacterOffset(insertPos - 1);
		if (region == null)
			return 0;
		RegionIterator it = new RegionIterator(flatNode, region);
		boolean found = false;
		while (it.hasPrev()) {
			region = it.prev();
			// if (region.getType() != CSSRegionContexts.CSS_S &&
			// region.getType() != CSSRegionContexts.CSS_DELIMITER &&
			// region.getType() !=
			// CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
			if (region.getType() != CSSRegionContexts.CSS_S) {
				found = true;
				break;
			}
		}
		int pos = insertPos - (found ? it.getStructuredDocumentRegion().getTextEndOffset(region) : it.getStructuredDocumentRegion().getStartOffset(region));
		// flatNode = it.getStructuredDocumentRegion();
		// if (found) {
		// if (region.getLength() != region.getTextLength()) {
		// pos = insertPos - flatNode.getTextEndOffset(region);
		// } else {
		// pos = insertPos - flatNode.getEndOffset(region);
		// }
		// } else {
		// pos = insertPos - flatNode.getStartOffset(region);
		// }
		return (pos >= 0) ? pos : 0;
	}

	/**
	 * 
	 */
	String getLineDelimiter(ICSSNode node) {
		ICSSModel model = (node != null) ? node.getOwnerDocument().getModel() : null;
		return (model != null) ? model.getStructuredDocument().getLineDelimiter() : "\n"; //$NON-NLS-1$

		// TODO : check whether to use model.getLineDelimiter() or
		// model.getStructuredDocument().getLineDelimiter()
	}

	/**
	 * 
	 */
	protected CompoundRegion[] getOutsideRegions(IStructuredDocument model, IRegion reg) {
		CompoundRegion[] ret = new CompoundRegion[2];
		RegionIterator it = new RegionIterator(model, reg.getOffset());
		it.prev();
		if (it.hasPrev()) {
			ITextRegion textRegion = it.prev();
			IStructuredDocumentRegion documentRegion = it.getStructuredDocumentRegion();
			ret[0] = new CompoundRegion(documentRegion, textRegion);
		}
		else {
			ret[0] = null;
		}
		it.reset(model, reg.getOffset() + reg.getLength());
		if (it.hasNext()) {
			ITextRegion textRegion = it.next();
			IStructuredDocumentRegion documentRegion = it.getStructuredDocumentRegion();
			ret[1] = new CompoundRegion(documentRegion, textRegion);
		}
		else {
			ret[1] = null;
		}
		return ret;
	}

	/**
	 */
	protected CSSSourceGenerator getParentFormatter(ICSSNode node) {
		ICSSNode parent = node.getParentNode();
		if (parent != null) {
			CSSSourceGenerator formatter = (CSSSourceGenerator) ((INodeNotifier) parent).getAdapterFor(CSSSourceFormatter.class);
			if (formatter == null) {
				formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) parent);
			}
			return formatter;
		}
		return null;
	}

	/**
	 * 
	 */
	protected CompoundRegion[] getRegions(IStructuredDocument model, IRegion reg, IRegion exceptFor, String pickupType) {
		int start = reg.getOffset();
		int end = reg.getOffset() + reg.getLength();
		int startE = (exceptFor != null) ? exceptFor.getOffset() : -1;
		int endE = (exceptFor != null) ? exceptFor.getOffset() + exceptFor.getLength() : 0;

		ArrayList list = new ArrayList();
		IStructuredDocumentRegion flatNode = model.getRegionAtCharacterOffset(start);
		boolean pickuped = false;
		while (flatNode != null && flatNode.getStartOffset() < end) {
			ITextRegionList regionList = flatNode.getRegions();
			Iterator it = regionList.iterator();
			while (it.hasNext()) {
				ITextRegion region = (ITextRegion) it.next();
				if (flatNode.getStartOffset(region) < start)
					continue;
				if (end <= flatNode.getStartOffset(region))
					break;
				if (startE >= 0 && startE <= flatNode.getStartOffset(region) && flatNode.getEndOffset(region) <= endE)
					continue;
				if (region.getType() == CSSRegionContexts.CSS_COMMENT || region.getType() == CSSRegionContexts.CSS_CDC || region.getType() == CSSRegionContexts.CSS_CDO)
					list.add(new CompoundRegion(flatNode, region));
				else if (!pickuped && region.getType() == pickupType) {
					list.add(new CompoundRegion(flatNode, region));
					pickuped = true;
				}
			}
			flatNode = flatNode.getNext();
		}
		if (list.size() > 0) {
			CompoundRegion[] regions = new CompoundRegion[list.size()];
			list.toArray(regions);
			return regions;
		}
		return new CompoundRegion[0];
	}

	/**
	 * 
	 */
	protected CompoundRegion[] getRegionsWithoutWhiteSpaces(IStructuredDocument model, IRegion reg, CSSCleanupStrategy stgy) {
		int start = reg.getOffset();
		int end = reg.getOffset() + reg.getLength() - 1;
		ArrayList list = new ArrayList();
		IStructuredDocumentRegion flatNode = model.getRegionAtCharacterOffset(start);
		while (flatNode != null && flatNode.getStartOffset() <= end) {
			ITextRegionList regionList = flatNode.getRegions();
			Iterator it = regionList.iterator();
			while (it.hasNext()) {
				ITextRegion region = (ITextRegion) it.next();
				if (flatNode.getStartOffset(region) < start)
					continue;
				if (end < flatNode.getStartOffset(region))
					break;
				if (region.getType() != CSSRegionContexts.CSS_S || (isCleanup() && !stgy.isFormatSource())) // for
					// not
					// formatting
					// case
					// on
					// cleanup
					// action
					list.add(new CompoundRegion(flatNode, region));
			}
			flatNode = flatNode.getNext();
		}
		if (list.size() > 0) {
			CompoundRegion[] regions = new CompoundRegion[list.size()];
			list.toArray(regions);
			return regions;
		}
		return new CompoundRegion[0];
	}

	/**
	 * 
	 */
	public static boolean includes(IRegion region, int start, int end) {
		if (region == null)
			return false;

		return (region.getOffset() <= start) && (end <= region.getOffset() + region.getLength());
	}

	/**
	 * 
	 * @return boolean
	 */
	protected static boolean isCleanup() {
		return strategy == CLEANUP;
	}

	/**
	 * 
	 * @return boolean
	 */
	protected static boolean isFormat() {
		return strategy == FORMAT;
	}

	/**
	 * 
	 */
	protected boolean isIncludesPreEnd(ICSSNode node, IRegion region) {
		return (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getStartOffset() == (region.getOffset() + region.getLength()));
	}

	/**
	 * 
	 */
	static protected boolean needS(CompoundRegion region) {
		return (region != null && region.getType() != CSSRegionContexts.CSS_S);
	}

	/**
	 * 
	 */
	public static IRegion overlappedRegion(IRegion region, int start, int end) {
		if (overlaps(region, start, end)) {
			int offset = (region.getOffset() <= start) ? start : region.getOffset();
			int length = ((end <= region.getOffset() + region.getLength()) ? end : region.getOffset() + region.getLength()) - offset;
			return new FormatRegion(offset, length);
		}
		return null;
	}

	/**
	 * 
	 */
	public static boolean overlaps(IRegion region, int start, int end) {
		if (region == null)
			return false;

		return (start < region.getOffset() + region.getLength()) && (region.getOffset() < end);
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