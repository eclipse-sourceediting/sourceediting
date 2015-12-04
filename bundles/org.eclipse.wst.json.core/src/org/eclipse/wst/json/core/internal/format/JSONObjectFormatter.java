/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.StyleRuleFormatter
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.cleanup.IJSONCleanupStrategy;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * 
 */
public class JSONObjectFormatter extends JSONStructureFormatter {

	private static JSONObjectFormatter instance;

	/**
	 * 
	 */
	JSONObjectFormatter() {
		super();
	}

	/**
	 * 
	 */
//	public StringBuilder formatAttrChanged(IJSONNode node, IJSONAttr attr,
//			boolean insert, AttrChangeContext context) {
//		StringBuilder buf = new StringBuilder();
//		if (node == null || attr == null)
//			return buf;
//
//		if (!IJSONStyleRule.SELECTOR.equalsIgnoreCase(attr.getName()))
//			return buf;
//
//		// get region to replace
//		if (context != null && ((IndexedRegion) node).getEndOffset() > 0) {
//			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0) {
//				IStructuredDocumentRegion flatNode = node
//						.getOwnerDocument()
//						.getModel()
//						.getStructuredDocument()
//						.getRegionAtCharacterOffset(
//								((IndexedRegion) attr).getEndOffset() - 1);
//				ITextRegion region = flatNode
//						.getRegionAtCharacterOffset(((IndexedRegion) attr)
//								.getEndOffset() - 1);
//				RegionIterator it = new RegionIterator(flatNode, region);
//				it.next();
//				if (it.hasNext()) {
//					ITextRegion next = it.next();
//					if (next.getType() == JSONRegionContexts.JSON_S)
//						context.end = it.getStructuredDocumentRegion()
//								.getEndOffset(next);
//					else
//						context.end = it.getStructuredDocumentRegion()
//								.getEndOffset(region);
//				} else
//					context.end = it.getStructuredDocumentRegion()
//							.getEndOffset(region);
//				context.start = it.getStructuredDocumentRegion()
//						.getStartOffset(region);
//			} else {
//				IStructuredDocumentRegion flatNode = node
//						.getOwnerDocument()
//						.getModel()
//						.getStructuredDocument()
//						.getRegionAtCharacterOffset(
//								((IndexedRegion) node).getStartOffset());
//				ITextRegion region = flatNode
//						.getRegionAtCharacterOffset(((IndexedRegion) node)
//								.getStartOffset());
//				if (region.getType() == JSONRegionContexts.JSON_S) {
//					context.start = flatNode.getStartOffset(region);
//					context.end = flatNode.getEndOffset(region);
//				} else {
//					context.start = flatNode.getEndOffset();
//					context.end = flatNode.getEndOffset();
//				}
//			}
//		}
//		// generate text
//		if (insert) {
//			buf.append(attr.getValue());
//			appendSpaceBefore(node, "", buf);//$NON-NLS-1$
//		}
//		return buf;
//	}

	/**
	 * 
	 */
	protected void formatPre(IJSONNode node, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node
				.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node
				.getFirstChild()).getStartOffset() : getChildInsertPos(node);
		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument()
					.getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
					structuredDocument, new FormatRegion(start, end - start),
					stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBetween(node, regions[i - 1], regions[i], source);
				source.append(decoratedSelectorRegion(regions[i],
						(i != 0) ? regions[i - 1] : null, stgy));
			}
		} else { // generate source
			String str = "";//((IJSONStyleRule) node).getSelectorText();
			// appendSpaceBefore(node,str,source);
			source.append(str);
			appendSpaceBefore(node, "{", source);//$NON-NLS-1$
			source.append("{");//$NON-NLS-1$
		}
		appendDelimBefore(node, null, source);
	}

	/**
	 * 
	 */
	protected void formatPre(IJSONNode node, IRegion region, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument()
				.getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
				structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendSpaceBetween(node,
						(i == 0) ? outside[0] : regions[i - 1], regions[i],
						source);
			source.append(decoratedSelectorRegion(regions[i],
					(i != 0) ? regions[i - 1] : null, stgy));
		}
		if (needS(outside[1])) {
			if (isIncludesPreEnd(node, region))
				appendDelimBefore(node, null, source);
			else
				appendSpaceBetween(node, regions[regions.length - 1],
						outside[1], source);
		}
	}

	/**
	 * 
	 */
//	public int getAttrInsertPos(IJSONNode node, String attrName) {
//		if (node == null || attrName == null || attrName.length() == 0)
//			return -1;
//
//		if (!IJSONStyleRule.SELECTOR.equalsIgnoreCase(attrName))
//			return -1;
//
//		IJSONAttr attr = (IJSONAttr) node.getAttributes().getNamedItem(
//				IJSONStyleRule.SELECTOR);
//		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
//			return ((IndexedRegion) attr).getStartOffset();
//		else
//			return ((IndexedRegion) node).getStartOffset();
//	}

	/**
	 * 
	 */
	public synchronized static JSONObjectFormatter getInstance() {
		if (instance == null)
			instance = new JSONObjectFormatter();
		return instance;
	}
}
