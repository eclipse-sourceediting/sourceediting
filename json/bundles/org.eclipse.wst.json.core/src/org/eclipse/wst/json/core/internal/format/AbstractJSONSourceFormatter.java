/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.AbstractCSSSourceFormatter
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.cleanup.IJSONCleanupStrategy;
import org.eclipse.wst.json.core.cleanup.JSONCleanupStrategyImpl;
import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.internal.util.RegionIterator;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

public abstract class AbstractJSONSourceFormatter implements
		IJSONSourceFormatter {

	protected final static short GENERATE = 0;
	protected final static short FORMAT = 1;
	protected final static short CLEANUP = 2;
	protected static short strategy;

	AbstractJSONSourceFormatter() {
		super();
	}

	protected void appendDelimBefore(IJSONNode node, CompoundRegion toAppend,
			StringBuilder source) {
		if (node == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		String delim = getLineDelimiter(node);

		boolean needIndent = !(node instanceof IJSONDocument);
		if (toAppend == null) {
			source.append(delim);
			source.append(getIndent(node));
			if (needIndent)
				source.append(getIndentString());
		} else {
			String type = toAppend.getType();
			if (type == JSONRegionContexts.JSON_COMMENT) {
				RegionIterator it = new RegionIterator(
						toAppend.getDocumentRegion(), toAppend.getTextRegion());
				it.prev();
				ITextRegion prev = it.prev();
				int[] result = null;
				if (prev == null
						|| (prev.getType() == JSONRegionContexts.WHITE_SPACE && (result = TextUtilities
								.indexOf(DefaultLineTracker.DELIMITERS,
										it.getStructuredDocumentRegion()
												.getText(prev), 0))[0] >= 0)) {
					// Collapse to one empty line if there's more than one.
					if (result != null) {
						int offset = result[0]
								+ DefaultLineTracker.DELIMITERS[result[1]]
										.length();
						if (offset < it.getStructuredDocumentRegion()
								.getText(prev).length()) {
							if (TextUtilities.indexOf(
									DefaultLineTracker.DELIMITERS, it
											.getStructuredDocumentRegion()
											.getText(prev), offset)[0] >= 0) {
								source.append(delim);
							}
						}
						source.append(delim);
						source.append(getIndent(node));
						if (needIndent)
							source.append(getIndentString());
					}
				} else if (prev.getType() == JSONRegionContexts.JSON_COMMENT) {
					String fullText = toAppend.getDocumentRegion().getFullText(
							prev);
					String trimmedText = toAppend.getDocumentRegion().getText(
							prev);
					String whiteSpaces = "";//$NON-NLS-1$
					if (fullText != null && trimmedText != null)
						whiteSpaces = fullText.substring(trimmedText.length());
					int[] delimiterFound = TextUtilities.indexOf(
							DefaultLineTracker.DELIMITERS, whiteSpaces, 0);
					if (delimiterFound[0] != -1) {
						source.append(delim);
					} else {
						appendSpaceBefore(node, toAppend.getText(), source);

						/*
						 * If two comments can't be adjusted in one
						 * line(combined length exceeds line width), a tab is
						 * also appended along with next line delimiter , we
						 * need to remove that.
						 */
						if (source.toString().endsWith(getIndentString())) {
							source.delete((source.length() - getIndentString()
									.length()), source.length());
						}
					}
				} else {
					appendSpaceBefore(node, toAppend.getText(), source);
				}
			} else if (type == JSONRegionContexts.JSON_COMMA) {
				RegionIterator it = new RegionIterator(
						toAppend.getDocumentRegion(), toAppend.getTextRegion());
				it.prev();
				ITextRegion prev = it.prev();

				Preferences preferences = JSONCorePlugin.getDefault()
						.getPluginPreferences();

				if (prev.getType() == JSONRegionContexts.WHITE_SPACE
						&& TextUtilities.indexOf(DefaultLineTracker.DELIMITERS,
								it.getStructuredDocumentRegion().getText(prev),
								0)[0] >= 0) {
					source.append(delim);
					source.append(getIndent(node));
					if (needIndent)
						source.append(getIndentString());
				} else if (preferences
						.getInt(JSONCorePreferenceNames.LINE_WIDTH) > 0
						&& (!preferences
								.getBoolean(JSONCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node
										.getOwnerDocument().getNodeType() != IJSONNode.PAIR_NODE)) {
					int length = getLastLineLength(node, source);
					int append = 1;
					if (length + append > preferences
							.getInt(JSONCorePreferenceNames.LINE_WIDTH)) {
						source.append(getLineDelimiter(node));
						source.append(getIndent(node));
						if (needIndent)
							source.append(getIndentString());
					}
				}
			} else
				if (type == JSONRegionContexts.JSON_OBJECT_OPEN
						|| type == JSONRegionContexts.JSON_OBJECT_CLOSE
					|| type == JSONRegionContexts.JSON_ARRAY_OPEN
					|| type == JSONRegionContexts.JSON_ARRAY_CLOSE) {
				source.append(delim);
				source.append(getIndent(node));
			} else {
				source.append(delim);
				source.append(getIndent(node));
				if (needIndent)
					source.append(getIndentString());
			}
		}
	}

	protected void appendSpaceBefore(IJSONNode node, CompoundRegion toAppend,
			StringBuilder source) {
		if (node == null || toAppend == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		String type = toAppend.getType();

		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();

		boolean needIndent = !(node instanceof IJSONDocument);
		/*if (type == JSONRegionContexts.JSON_COMMENT) {
			// check whether previous region is 'S' and has CR-LF
			String delim = getLineDelimiter(node);
			RegionIterator it = new RegionIterator(
					toAppend.getDocumentRegion(), toAppend.getTextRegion());
			it.prev();
			ITextRegion prev = it.prev();
			// bug390904
			if (prev.getType() == JSONRegionContexts.JSON_LBRACE
					&& TextUtilities
							.indexOf(DefaultLineTracker.DELIMITERS,
									it.getStructuredDocumentRegion()
											.getFullText(prev), 0)[0] > 0) {
				source.append(delim);
				source.append(getIndent(node));
				source.append(getIndentString());
			} else if (prev.getType() == JSONRegionContexts.WHITE_SPACE
					&& TextUtilities.indexOf(DefaultLineTracker.DELIMITERS, it
							.getStructuredDocumentRegion().getText(prev), 0)[0] >= 0) {
				source.append(delim);
				source.append(getIndent(node));
				if (needIndent)
					source.append(getIndentString());
			} else {
				appendSpaceBefore(node, toAppend.getText(), source);
			}
		}*/
		if ((type == JSONRegionContexts.JSON_OBJECT_OPEN || type == JSONRegionContexts.JSON_ARRAY_OPEN)
				&& preferences
						.getBoolean(JSONCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE)) {
			String delim = getLineDelimiter(node);
			source.append(delim);
			source.append(getIndent(node));
			// } else if (type == JSONRegionContexts.JSON_CURLY_BRACE_CLOSE) {
			// } else if (type == JSONRegionContexts.JSON_INCLUDES || type ==
			// JSONRegionContexts.JSON_DASHMATCH) {
		/*} else if (type == JSONRegionContexts.JSON_DECLARATION_SEPARATOR
				&& node instanceof IJSONStyleDeclItem) {
			int n = preferences
					.getInt(JSONCorePreferenceNames.FORMAT_PROP_PRE_DELIM);
			// no delimiter case
			while (n-- > 0)
				source.append(" ");//$NON-NLS-1$
		} else if (type == JSONRegionContexts.JSON_DECLARATION_VALUE_OPERATOR
				|| type == JSONRegionContexts.JSON_DECLARATION_VALUE_PARENTHESIS_CLOSE) {
			if (preferences.getInt(JSONCorePreferenceNames.LINE_WIDTH) > 0
					&& (!preferences
							.getBoolean(JSONCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR) || node
							.getOwnerDocument().getNodeType() != IJSONNode.STYLEDECLARATION_NODE)) {
				int length = getLastLineLength(node, source);
				int append = 1;
				if (length + append > preferences
						.getInt(JSONCorePreferenceNames.LINE_WIDTH)) {
					source.append(getLineDelimiter(node));
					source.append(getIndent(node));
					if (needIndent)
						source.append(getIndentString());
				}
			}
		} else if (JSONRegionContexts.JSON_FOREIGN_ELEMENT == type
				|| JSONRegionContexts.JSON_DECLARATION_DELIMITER == type) {
			return;
		*/
		} else
			appendSpaceBefore(node, toAppend.getText(), source);
	}

	protected void appendSpaceBefore(IJSONNode node, String toAppend,
			StringBuilder source) {
		if (node == null || source == null)
			return;
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action

		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();
		if (toAppend != null
				&& toAppend.startsWith("{") && preferences.getBoolean(JSONCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE)) {//$NON-NLS-1$
			source.append(getLineDelimiter(node));
			source.append(getIndent(node));
			return;
		} else if (/* ! mgr.isOnePropertyPerLine() && */preferences
				.getInt(JSONCorePreferenceNames.LINE_WIDTH) > 0
				&& (!preferences
						.getBoolean(JSONCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR)
						/*|| node
						.getOwnerDocument().getNodeType() != IJSONNode.STYLEDECLARATION_NODE*/)) {
			int n = getLastLineLength(node, source);
			int append = (toAppend != null) ? TextUtilities.indexOf(
					DefaultLineTracker.DELIMITERS, toAppend, 0)[0] : 0;
			if (toAppend != null)
				append = (append < 0) ? toAppend.length() : append;
			if (n + append + 1 > preferences
					.getInt(JSONCorePreferenceNames.LINE_WIDTH)) {
				source.append(getLineDelimiter(node));
				source.append(getIndent(node));
				source.append(getIndentString());
				return;
			}
		}
		// bug412395
		// just verify if the source and the toAppend strings do not end with a
		// whitespace to avoid the whitespace duplication.
		if (!(source.length() > 0 && source.toString().charAt(
				source.length() - 1) == ' ')
				&& !(toAppend.length() > 0 && toAppend
						.charAt(toAppend.length() - 1) == ' ')) {
			source.append(" ");//$NON-NLS-1$
		}
	}

	@Override
	public final StringBuilder cleanup(IJSONNode node) {
		short oldStrategy = strategy;
		strategy = CLEANUP;
		StringBuilder source = formatProc(node);
		strategy = oldStrategy;
		return source;
	}

	@Override
	public final StringBuilder cleanup(IJSONNode node, IRegion region) {
		short oldStrategy = strategy;
		strategy = CLEANUP;
		StringBuilder source = formatProc(node, region);
		strategy = oldStrategy;

		return source;
	}

	protected String decoratedIdentRegion(CompoundRegion region,
			IJSONCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();

//		if (region.getType() == JSONRegionContexts.WHITE_SPACETRING
//				|| region.getType() == JSONRegionContexts.JSON_URI)
//			return decoratedRegion(region, 0, stgy);

		if (isCleanup()) {
			if (stgy.getIdentCase() == IJSONCleanupStrategy.ASIS
					/*|| region.getType() == JSONRegionContexts.JSON_COMMENT*/)
				return text;
			else if (stgy.getIdentCase() == IJSONCleanupStrategy.UPPER)
				return text.toUpperCase();
			else
				return text.toLowerCase();
		}

		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();
//		if (region.getType() == JSONRegionContexts.JSON_COMMENT)
//			return text;
//		else if (preferences.getInt(JSONCorePreferenceNames.CASE_IDENTIFIER) == JSONCorePreferenceNames.UPPER)
//			return text.toUpperCase();
//		else
			return text.toLowerCase();
	}

	protected String decoratedPropNameRegion(CompoundRegion region,
			IJSONCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();

//		if (region.getType() == JSONRegionContexts.WHITE_SPACETRING
//				|| region.getType() == JSONRegionContexts.JSON_URI)
//			return decoratedRegion(region, 1, stgy);
		if (isCleanup()) {
			/*if (stgy.getPropNameCase() == JSONCleanupStrategy.ASIS
					|| region.getType() != JSONRegionContexts.JSON_DECLARATION_PROPERTY)
				return text;
			else*/
				if (stgy.getPropNameCase() == IJSONCleanupStrategy.UPPER)
				return text.toUpperCase();
			else
				return text.toLowerCase();
		}
		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();

//		if (region.getType() != JSONRegionContexts.JSON_DECLARATION_PROPERTY)
//			return text;
		//else
			if (preferences.getInt(JSONCorePreferenceNames.CASE_PROPERTY_NAME) == JSONCorePreferenceNames.UPPER)
			return text.toUpperCase();
		else
			return text.toLowerCase();
	}

	protected String decoratedPropValueRegion(CompoundRegion region,
			IJSONCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();

		String type = region.getType();
//		if (type == JSONRegionContexts.WHITE_SPACETRING
//				|| type == JSONRegionContexts.JSON_URI
//				|| type == JSONRegionContexts.JSON_DECLARATION_VALUE_URI)
//			return decoratedRegion(region, 2, stgy);
		if (isCleanup()) {
			if (stgy.getPropValueCase() != IJSONCleanupStrategy.ASIS) {
				//if (type == JSONRegionContexts.JSON_COMMENT) {
				//} else {
					if (stgy.getPropValueCase() == IJSONCleanupStrategy.UPPER)
						text = text.toUpperCase();
					else
						text = text.toLowerCase();
				//}
			}
		}
		return text;
	}

	protected String decoratedRegion(CompoundRegion region, int type,
			IJSONCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();
		return text;
	}

	@Override
	public final StringBuilder format(IJSONNode node) {
		short oldStrategy = strategy;
		strategy = FORMAT;
		StringBuilder source = formatProc(node);
		strategy = oldStrategy;

		return source;
	}

	@Override
	public final StringBuilder format(IJSONNode node, IRegion region) {
		short oldStrategy = strategy;
		strategy = FORMAT;
		StringBuilder source = formatProc(node, region);
		strategy = oldStrategy;
		return source;
	}

	protected void formatChildren(IJSONNode node, StringBuilder source) {
		IJSONNode child = node.getFirstChild();
		IJSONNode last = null;
		while (child != null) {
			// append child
			IJSONSourceFormatter formatter = (IJSONSourceFormatter) ((INodeNotifier) child)
					.getAdapterFor(IJSONSourceFormatter.class);
			if (formatter == null) {
				formatter = JSONSourceFormatterFactory.getInstance()
						.getSourceFormatter(child);
			}
			StringBuilder childSource = ((AbstractJSONSourceFormatter) formatter)
					.formatProc(child);
			source.append(childSource);
			last = child;
			child = child.getNextSibling();
		}
	}

	protected void formatChildren(IJSONNode node, IRegion region,
			StringBuilder source) {
		IJSONNode child = node.getFirstChild();
		int start = region.getOffset();
		int end = region.getOffset() + region.getLength();
		while (child != null) {
			int curEnd = child.getEndOffset();
			StringBuilder childSource = null;
			boolean toFinish = false;
			if (start < curEnd) {
				int curStart = child.getStartOffset();
				if (curStart < end) {
					// append child
					IJSONSourceFormatter formatter = (IJSONSourceFormatter) ((INodeNotifier) child)
							.getAdapterFor(IJSONSourceFormatter.class);
					if (formatter == null) {
						formatter = JSONSourceFormatterFactory.getInstance()
								.getSourceFormatter(child);
					}
					if (includes(region, curStart, curEnd))
						childSource = ((AbstractJSONSourceFormatter) formatter)
								.formatProc(child);
					else
						childSource = ((AbstractJSONSourceFormatter) formatter)
								.formatProc(
										child,
										overlappedRegion(region, curStart,
												curEnd));
				} else
					toFinish = true;
			}
			if (childSource != null) {
				source.append(childSource);
			}
			if (toFinish)
				break;
			child = child.getNextSibling();
		}
	}

	/**
	 * Generate or format source after the last child and append to string
	 * buffer
	 */
	protected abstract void formatPost(IJSONNode node, StringBuilder source);

	/**
	 * Generate or format source after the last child and append to string
	 * buffer
	 */
	protected abstract void formatPost(IJSONNode node, IRegion region,
			StringBuilder source);

	/**
	 * Generate or format source before the first child and append to string
	 * buffer
	 */
	protected abstract void formatPre(IJSONNode node, StringBuilder source);

	/**
	 * Generate or format source before the first child and append to string
	 * buffer
	 */
	abstract protected void formatPre(IJSONNode node, IRegion region,
			StringBuilder source);

	/**
	 *
	 * @return java.lang.StringBuilder
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 */
	protected final StringBuilder formatProc(IJSONNode node) {
		StringBuilder source = new StringBuilder();
		formatPre(node, source);
		formatChildren(node, source);
		formatPost(node, source);
		return source;
	}

	/**
	 *
	 * @return java.lang.StringBuilder
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 * @param region
	 *            org.eclipse.jface.text.IRegion
	 */
	protected StringBuilder formatProc(IJSONNode node, IRegion region) {
		StringBuilder source = new StringBuilder();
		int curStart = node.getStartOffset();
		int curEnd = node.getEndOffset();
		if (node.hasChildNodes()) {
			curEnd = node.getFirstChild().getStartOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPre(node, source);
				else
					formatPre(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = node.getLastChild().getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatChildren(node, source);
				else
					formatChildren(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = node.getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPost(node, source);
				else
					formatPost(node, overlappedRegion(region, curStart, curEnd), source);
			}
		} else if (node instanceof IJSONArray || node instanceof IJSONObject) {
			curStart = node.getStartOffset();
			curEnd = node.getEndOffset();
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd)) {
					formatPre(node, source);
					formatPost(node, source);
				} else {
					formatPre(node, overlappedRegion(region, curStart, curEnd), source);
					formatPost(node, overlappedRegion(region, curStart, curEnd), source);
				}
			}
		} else {
			// curEnd = getChildInsertPos(node);
			curEnd = node.getEndOffset() > 0 ? node.getEndOffset() : -1;
			if (overlaps(region, curStart, curEnd)) {
				if (includes(region, curStart, curEnd))
					formatPre(node, source);
				else
					formatPre(node, overlappedRegion(region, curStart, curEnd), source);
			}
			curStart = curEnd;
			curEnd = node.getEndOffset();
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
	 * Insert the method's description here.
	 *
	 * @return org.eclipse.wst.css.core.internal.cleanup.JSONCleanupStrategy
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 */
	protected IJSONCleanupStrategy getCleanupStrategy(IJSONNode node) {
		IJSONCleanupStrategy currentStrategy = JSONCleanupStrategyImpl
				.getInstance();
		IJSONDocument doc = node.getOwnerDocument();
		if (doc == null)
			return currentStrategy;
		IJSONModel model = doc.getModel();
		if (model == null)
			return currentStrategy;
		return currentStrategy;
	}

	protected String getIndent(IJSONNode node) {
		if (node == null)
			return "";//$NON-NLS-1$
		IJSONNode parent = node.getParentNode();
		if (parent == null || parent instanceof IJSONDocument)
			return "";//$NON-NLS-1$
		String parentIndent = getIndent(parent);
		return parentIndent + getIndentString();
	}

	protected int getLastLineLength(IJSONNode node, StringBuilder source) {
		if (node == null || source == null)
			return 0;
		String delim = getLineDelimiter(node);
		String str = new String(source);
		int n = str.lastIndexOf(delim);
		if (n < 0)
			return str.length();

		return str.length() - n - delim.length();
	}

	String getLineDelimiter(IJSONNode node) {
		IJSONModel model = node != null ? 
				node.getOwnerDocument().getModel() :
					null;
		IStructuredDocument structuredDocument = model != null ? 
				model.getStructuredDocument() :
					null;
		return structuredDocument != null ?
				structuredDocument.getLineDelimiter() :
					"\n"; //$NON-NLS-1$
	}

	protected CompoundRegion[] getOutsideRegions(IStructuredDocument model,
			IRegion reg) {
		CompoundRegion[] ret = new CompoundRegion[2];
		RegionIterator it = new RegionIterator(model, reg.getOffset());
		it.prev();
		if (it.hasPrev()) {
			ITextRegion textRegion = it.prev();
			IStructuredDocumentRegion documentRegion = it
					.getStructuredDocumentRegion();
			ret[0] = new CompoundRegion(documentRegion, textRegion);
		} else {
			ret[0] = null;
		}
		it.reset(model, reg.getOffset() + reg.getLength());
		if (it.hasNext()) {
			ITextRegion textRegion = it.next();
			IStructuredDocumentRegion documentRegion = it
					.getStructuredDocumentRegion();
			ret[1] = new CompoundRegion(documentRegion, textRegion);
		} else {
			ret[1] = null;
		}
		return ret;
	}

	protected IJSONSourceFormatter getParentFormatter(IJSONNode node) {
		IJSONNode parent = node.getParentNode();
		if (parent != null) {
			IJSONSourceFormatter formatter = (IJSONSourceFormatter) ((INodeNotifier) parent)
					.getAdapterFor(IJSONSourceFormatter.class);
			if (formatter == null) {
				formatter = JSONSourceFormatterFactory.getInstance()
						.getSourceFormatter(parent);
			}
			return formatter;
		}
		return null;
	}

	protected CompoundRegion[] getRegions(IStructuredDocument model,
			IRegion reg, IRegion exceptFor, String pickupType) {
		int start = reg.getOffset();
		int end = reg.getOffset() + reg.getLength();
		int startE = (exceptFor != null) ? exceptFor.getOffset() : -1;
		int endE = (exceptFor != null) ? exceptFor.getOffset()
				+ exceptFor.getLength() : 0;

		ArrayList list = new ArrayList();
		IStructuredDocumentRegion flatNode = model
				.getRegionAtCharacterOffset(start);
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
				if (startE >= 0 && startE <= flatNode.getStartOffset(region)
						&& flatNode.getEndOffset(region) <= endE)
					continue;
//				if (region.getType() == JSONRegionContexts.JSON_COMMENT
//						|| region.getType() == JSONRegionContexts.JSON_CDC
//						|| region.getType() == JSONRegionContexts.JSON_CDO)
//					list.add(new CompoundRegion(flatNode, region));
//				else
				if (!pickuped && region.getType() == pickupType) {
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

	protected CompoundRegion[] getRegionsWithoutWhiteSpaces(
			IStructuredDocument model, IRegion reg, IJSONCleanupStrategy stgy) {
		int start = reg.getOffset();
		int end = reg.getOffset() + reg.getLength() - 1;
		ArrayList list = new ArrayList();
		IStructuredDocumentRegion flatNode = model
				.getRegionAtCharacterOffset(start);
		while (flatNode != null && flatNode.getStartOffset() <= end) {
			ITextRegionList regionList = flatNode.getRegions();
			Iterator it = regionList.iterator();
			while (it.hasNext()) {
				ITextRegion region = (ITextRegion) it.next();
				if (flatNode.getStartOffset(region) < start)
					continue;
				if (end < flatNode.getStartOffset(region))
					break;
				if (region.getType() != JSONRegionContexts.WHITE_SPACE
						|| (isCleanup() && !stgy.isFormatSource())) // for
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

	public static boolean includes(IRegion region, int start, int end) {
		if (region == null)
			return false;

		return (region.getOffset() <= start)
				&& (end <= region.getOffset() + region.getLength());
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

	protected boolean isIncludesPreEnd(IJSONNode node, IRegion region) {
		return (node.getFirstChild() != null && ((IndexedRegion) node
				.getFirstChild()).getStartOffset() == (region.getOffset() + region
				.getLength()));
	}

	static protected boolean needS(CompoundRegion region) {
		return (region != null && region.getType() != JSONRegionContexts.WHITE_SPACE);
	}

	public static IRegion overlappedRegion(IRegion region, int start, int end) {
		if (overlaps(region, start, end)) {
			int offset = (region.getOffset() <= start) ? start : region
					.getOffset();
			int length = ((end <= region.getOffset() + region.getLength()) ? end
					: region.getOffset() + region.getLength())
					- offset;
			return new FormatRegion(offset, length);
		}
		return null;
	}

	public static boolean overlaps(IRegion region, int start, int end) {
		if (region == null)
			return false;

		return (start < region.getOffset() + region.getLength())
				&& (region.getOffset() < end);
	}

	protected String getIndentString() {
		StringBuilder indent = new StringBuilder();

		Preferences preferences = JSONCorePlugin.getDefault()
				.getPluginPreferences();
		if (preferences != null) {
			char indentChar = ' ';
			String indentCharPref = preferences
					.getString(JSONCorePreferenceNames.INDENTATION_CHAR);
			if (JSONCorePreferenceNames.TAB.equals(indentCharPref)) {
				indentChar = '\t';
			}
			int indentationWidth = preferences
					.getInt(JSONCorePreferenceNames.INDENTATION_SIZE);

			for (int i = 0; i < indentationWidth; i++) {
				indent.append(indentChar);
			}
		}
		return indent.toString();
	}

	protected void formatValue(IJSONNode node, StringBuilder source, IJSONNode value) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);
		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		int start = node.getStartOffset();
		int end = node.getEndOffset();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument,
				new FormatRegion(start, end - start), stgy);
		if (regions.length > 2) {
			for (int i = 2; i < regions.length; i++) {
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
		}
	}

	protected void formatObject(IJSONNode node, StringBuilder source, IJSONNode jsonObject) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);
		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		IStructuredDocumentRegion[] structuredRegions = structuredDocument
				.getStructuredDocumentRegions(node.getStartOffset(), node.getEndOffset());
		if (structuredRegions.length >= 2) {
			int start = structuredRegions[1].getStartOffset();
			int end = node.getEndOffset();
			IJSONSourceFormatter formatter = (IJSONSourceFormatter) ((INodeNotifier) jsonObject)
					.getAdapterFor(IJSONSourceFormatter.class);
			if (formatter == null) {
				formatter = JSONSourceFormatterFactory.getInstance().getSourceFormatter(jsonObject);
			}
			StringBuilder objectSource = formatter.format(jsonObject, new FormatRegion(start, end - start));
			if (objectSource != null) {
				source.append(objectSource);
			}
		}
	}

}