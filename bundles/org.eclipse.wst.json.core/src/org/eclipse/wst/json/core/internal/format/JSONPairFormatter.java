/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.StyleDeclarationFormatter
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.cleanup.IJSONCleanupStrategy;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.internal.util.RegionIterator;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * 
 */
public class JSONPairFormatter extends DefaultJSONSourceFormatter {

	private static JSONPairFormatter instance;

	/**
	 * 
	 */
	JSONPairFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatBefore(IJSONNode node, IJSONNode child,
			String toAppend, StringBuilder source, IRegion exceptFor) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IJSONNode prev = (child != null) ? child.getPreviousSibling() : node
				.getLastChild();
		int start = (prev != null) ? ((IndexedRegion) prev).getEndOffset() : 0;
		int end = (child != null) ? ((IndexedRegion) child).getStartOffset()
				: 0;

		// check no child
		if (child == null && prev == null)
			return;

		if (start > 0 && start < end) { // format source
			IJSONModel cssModel = node.getOwnerDocument().getModel();
			// BUG202615 - it is possible to have a style declaration with no
			// model associated with it
			if (cssModel != null) {
				IStructuredDocument structuredDocument = cssModel
						.getStructuredDocument();
				if (structuredDocument != null) {
					// get meaning regions
					CompoundRegion[] regions = null;
					if (exceptFor == null)
						regions = getRegionsWithoutWhiteSpaces(
								structuredDocument, new FormatRegion(start, end
										- start), stgy);
					else {
						String pickupType = JSONRegionContexts.JSON_COMMA;
						if (prev == null || child == null)
							pickupType = null;
						regions = getRegions(structuredDocument,
								new FormatRegion(start, end - start),
								exceptFor, pickupType);
					}
					// extract source
					for (int i = 0; i < regions.length; i++) {
						appendSpaceBefore(node, regions[i], source);
						source.append(decoratedRegion(regions[i], 0, stgy)); // must
						// be comments
					}
				}
			}
		} else if (prev != null && child != null) { // generate source :
			// between two declarations
			// BUG93037-properties view adds extra ; when add new property
			boolean semicolonFound = false;

			IJSONModel cssModel = node.getOwnerDocument().getModel();
			// BUG202615 - it is possible to have a style declaration with no
			// model associated with it
			if (cssModel != null) {
				IStructuredDocument structuredDocument = cssModel
						.getStructuredDocument();
				if (structuredDocument != null) {
					int prevStart = (prev != null) ? ((IndexedRegion) prev)
							.getStartOffset() : 0;
					int prevEnd = (prev != null) ? ((IndexedRegion) prev)
							.getEndOffset() : 0;
					CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
							structuredDocument, new FormatRegion(prevStart,
									prevEnd - prevStart), stgy);
					int i = regions.length - 1;
					while (i >= 0 && !semicolonFound) {
						if (regions[i].getType() == JSONRegionContexts.JSON_COMMA)
							semicolonFound = true;
						--i;
					}
				}
			}
			if (!semicolonFound)
				source.append(";");//$NON-NLS-1$
		} else if (prev == null) { // generate source : before the first
			// declaration
			RegionIterator it = null;
			if (end > 0) {
				IJSONModel cssModel = node.getOwnerDocument().getModel();
				// BUG202615 - it is possible to have a style declaration with
				// no model associated with it
				if (cssModel != null) {
					IStructuredDocument structuredDocument = cssModel
							.getStructuredDocument();
					if (structuredDocument != null) {
						it = new RegionIterator(structuredDocument, end - 1);
					}
				}
			} else {
				int pos = getChildInsertPos(node);
				if (pos >= 0) {
					IJSONModel cssModel = node.getOwnerDocument().getModel();
					// BUG202615 - it is possible to have a style declaration
					// with no model associated with it
					if (cssModel != null) {
						IStructuredDocument structuredDocument = cssModel
								.getStructuredDocument();
						if (structuredDocument != null) {
							it = new RegionIterator(structuredDocument, pos - 1);
						}
					}
				}
			}
			if (it != null) {
				int limit = ((IndexedRegion) ((node.getParentNode() != null) ? node
						.getParentNode() : node)).getStartOffset();
				while (it.hasPrev()) {
					ITextRegion curReg = it.prev();
					if (curReg.getType() == JSONRegionContexts.JSON_OBJECT_OPEN
							|| curReg.getType() == JSONRegionContexts.JSON_ARRAY_OPEN
							|| curReg.getType() == JSONRegionContexts.JSON_COMMA)
						break;
					//if (curReg.getType() != JSONRegionContexts.JSON_S
					//		&& curReg.getType() != JSONRegionContexts.JSON_COMMENT) {
						source.append(",");//$NON-NLS-1$
						//break;
					//}
					if (it.getStructuredDocumentRegion().getStartOffset(curReg) <= limit)
						break;
				}
			}
		} else if (child == null) { // generate source : after the last
			// declaration
			RegionIterator it = null;
			if (start > 0) {
				IJSONModel cssModel = node.getOwnerDocument().getModel();
				// BUG202615 - it is possible to have a style declaration with
				// no model associated with it
				if (cssModel != null) {
					IStructuredDocument structuredDocument = cssModel
							.getStructuredDocument();
					if (structuredDocument != null) {
						it = new RegionIterator(structuredDocument, start);
					}
				}
			} else {
				int pos = getChildInsertPos(node);
				if (pos >= 0) {
					IJSONModel cssModel = node.getOwnerDocument().getModel();
					// BUG202615 - it is possible to have a style declaration
					// with no model associated with it
					if (cssModel != null) {
						IStructuredDocument structuredDocument = cssModel
								.getStructuredDocument();
						if (structuredDocument != null) {
							it = new RegionIterator(structuredDocument, pos);
						}
					}
				}
			}
			if (it != null) {
				int limit = ((IndexedRegion) ((node.getParentNode() != null) ? node
						.getParentNode() : node)).getEndOffset();
				while (it.hasNext()) {
					ITextRegion curReg = it.next();
					if (curReg.getType() == JSONRegionContexts.JSON_OBJECT_OPEN || curReg.getType() == JSONRegionContexts.JSON_ARRAY_OPEN
							|| curReg.getType() == JSONRegionContexts.JSON_COMMA)
						break;
					//if (curReg.getType() != JSONRegionContexts.JSON_S
					//		&& curReg.getType() != JSONRegionContexts.JSON_COMMENT) {
						// Bug 219004 - Before appending a ;, make sure that
						// there
						// isn't one already
						boolean semicolonFound = false;
						while (it.hasNext() && !semicolonFound) {
							if (it.next().getType() == JSONRegionContexts.JSON_COMMA)
								semicolonFound = true;
						}

						if (!semicolonFound)
							source.append(";");//$NON-NLS-1$
						break;
					//}
//					if (limit <= it.getStructuredDocumentRegion().getEndOffset(
//							curReg))
//						break;
				}
			}
		}
		if (child == null) {
			if (((IndexedRegion) node).getEndOffset() <= 0) {
				// get next region
				int pos = getChildInsertPos(node);
				CompoundRegion toAppendRegion = null;
				if (pos >= 0) {
					IJSONModel cssModel = node.getOwnerDocument().getModel();
					// BUG202615 - it is possible to have a style declaration
					// with no model associated with it
					if (cssModel != null) {
						IStructuredDocument structuredDocument = cssModel
								.getStructuredDocument();
						if (structuredDocument != null) {
							IStructuredDocumentRegion flatNode = structuredDocument
									.getRegionAtCharacterOffset(pos);
							toAppendRegion = new CompoundRegion(flatNode,
									flatNode.getRegionAtCharacterOffset(pos));
						}
					}
				}
				appendDelimBefore(node.getParentNode(), toAppendRegion, source);
			}
		} else if ((prev != null || ((IndexedRegion) node).getEndOffset() <= 0)) {
			Preferences preferences = JSONCorePlugin.getDefault()
					.getPluginPreferences();

			if (preferences
					.getBoolean(JSONCorePreferenceNames.WRAPPING_ONE_PER_LINE)
					&& (node.getOwnerDocument() != node || !preferences
							.getBoolean(JSONCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR)))
				appendDelimBefore(node, null, source);
			else if (prev != null || node.getOwnerDocument() != node)
				appendSpaceBefore(node, toAppend, source);
		}
	}

	/**
	 * 
	 */
	protected void formatBefore(IJSONNode node, IJSONNode child,
			IRegion region, String toAppend, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IJSONModel cssModel = node.getOwnerDocument().getModel();
		// BUG202615 - it is possible to have a style declaration
		// with no model associated with it
		if (cssModel != null) {
			IStructuredDocument structuredDocument = cssModel
					.getStructuredDocument();
			if (structuredDocument != null) {
				CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
						structuredDocument, region, stgy);
				CompoundRegion[] outside = getOutsideRegions(
						structuredDocument, region);

				for (int i = 0; i < regions.length; i++) {
					if (i != 0 || needS(outside[0]))
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy)); // must
																			// be
																			// comments
				}
				Preferences preferences = JSONCorePlugin.getDefault()
						.getPluginPreferences();
				if (needS(outside[1])) {
					if (((IndexedRegion) child).getStartOffset() == region
							.getOffset() + region.getLength()
							&& preferences
									.getBoolean(JSONCorePreferenceNames.WRAPPING_ONE_PER_LINE)
							&& (node.getOwnerDocument() != node || !preferences
									.getBoolean(JSONCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR))) {
						appendDelimBefore(node, null, source);
					} else
						appendSpaceBefore(node, toAppend, source);
				}
			}
		}
	}

	/**
	 * Insert the method's description here.
	 */
	public int getChildInsertPos(IJSONNode node) {
		if (node == null)
			return -1;
		int pos = super.getChildInsertPos(node);
		if (pos < 0) {
			IJSONSourceGenerator formatter = getParentFormatter(node);
			return (formatter != null) ? formatter.getChildInsertPos(node
					.getParentNode()) : -1;
		}
		return pos;
	}

	/**
	 * 
	 */
	public synchronized static JSONPairFormatter getInstance() {
		if (instance == null)
			instance = new JSONPairFormatter();
		return instance;
	}

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 * @param insertPos
	 *            int
	 */
	public int getLengthToReformatAfter(IJSONNode node, int insertPos) {
		if (node == null)
			return 0;
		IndexedRegion nnode = (IndexedRegion) node;
		if (insertPos < 0 || !nnode.contains(insertPos)) {
			if (node.getParentNode() != null && nnode.getEndOffset() <= 0) {
				IJSONSourceGenerator pntFormatter = getParentFormatter(node);
				if (pntFormatter != null)
					return pntFormatter.getLengthToReformatAfter(
							node.getParentNode(), insertPos);
			}
			return 0;
		}
		return super.getLengthToReformatAfter(node, insertPos);
	}

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 * @param insertPos
	 *            int
	 */
	public int getLengthToReformatBefore(IJSONNode node, int insertPos) {
		if (node == null)
			return 0;
		IndexedRegion nnode = (IndexedRegion) node;
		if (insertPos <= 0 || !nnode.contains(insertPos - 1)) {
			if (node.getParentNode() != null && nnode.getEndOffset() <= 0) {
				IJSONSourceGenerator pntFormatter = getParentFormatter(node);
				if (pntFormatter != null)
					return pntFormatter.getLengthToReformatBefore(
							node.getParentNode(), insertPos);
			}
			return 0;
		}
		return super.getLengthToReformatBefore(node, insertPos);
	}
}