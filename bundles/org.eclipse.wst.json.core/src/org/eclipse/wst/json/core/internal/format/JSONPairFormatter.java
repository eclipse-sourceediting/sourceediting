/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
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

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.cleanup.IJSONCleanupStrategy;
import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class JSONPairFormatter extends JSONStructureFormatter {

	private static AbstractJSONSourceFormatter instance;

	JSONPairFormatter() {
		super();
	}

	@Override
	protected void formatChildren(IJSONNode node, StringBuilder source) {
		if (node instanceof IJSONPair) {
			IJSONPair pair = (IJSONPair) node;
			IJSONValue value = pair.getValue();
			if (value instanceof IJSONObject || value instanceof IJSONArray) {
				formatObject(node, source, value);
			} else {
				formatValue(node, source, value);
			}
		}
	}

	@Override
	protected void formatChildren(IJSONNode node, IRegion region, StringBuilder source) {
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
						formatter = JSONSourceFormatterFactory.getInstance().getSourceFormatter(child);
					}
					if (includes(region, curStart, curEnd))
						childSource = ((AbstractJSONSourceFormatter) formatter).formatProc(child);
					else
						childSource = ((AbstractJSONSourceFormatter) formatter).formatProc(child,
								overlappedRegion(region, curStart, curEnd));
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

	@Override
	protected void formatPre(IJSONNode node, StringBuilder source) {
		formatPre(node, new FormatRegion(node.getStartOffset(), node.getEndOffset() - node.getStartOffset()), source);
	}

	@Override
	protected void formatPre(IJSONNode node, IRegion region, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		int length = (regions.length >= 2) ? 2 : regions.length;
		for (int i = 0; i < length; i++) {
			source.append(decoratedRegion(regions[i], 0, stgy));
			if (regions[i].getType() == JSONRegionContexts.JSON_COLON) {
				source.append(' ');
			}
		}
	}

	@Override
	protected void formatPost(IJSONNode node, IRegion region, StringBuilder source) {
		if (node.getNextSibling() != null) {
			int start = node.getEndOffset();
			int end = node.getNextSibling().getStartOffset();
			IJSONCleanupStrategy stgy = getCleanupStrategy(node);
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument,
					new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
			String delim = getLineDelimiter(node);
			source.append(delim);
			if (node.getNextSibling() != null) {
				source.append(getIndent(node));
			} else {
				source.append(getIndent(node.getParentNode()));
			}
		}
	}

	@Override
	protected void formatPost(IJSONNode node, StringBuilder source) {
		formatPost(node, new FormatRegion(node.getStartOffset(), node.getEndOffset() - node.getStartOffset()), source);
	}

	public synchronized static AbstractJSONSourceFormatter getInstance() {
		if (instance == null)
			instance = new JSONPairFormatter();
		return instance;
	}

}