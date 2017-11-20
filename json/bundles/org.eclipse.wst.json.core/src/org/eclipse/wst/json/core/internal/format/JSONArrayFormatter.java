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
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class JSONArrayFormatter extends JSONStructureFormatter {

	private static JSONArrayFormatter instance;

	JSONArrayFormatter() {
		super();
	}

	@Override
	protected void formatChildren(IJSONNode node, StringBuilder source) {
		if (node instanceof IJSONArray) {
			IJSONArray array = (IJSONArray) node;
			IJSONNode child = array.getFirstChild();
			while (child != null) {
				if (child instanceof IJSONObject || child instanceof IJSONArray) {
					formatObject(node, source, child);
					if (child.getNextSibling() != null) {
						int start = child.getEndOffset();
						int end = child.getNextSibling().getStartOffset();
						if (end > start) {
							IJSONCleanupStrategy stgy = getCleanupStrategy(node);
							IStructuredDocument structuredDocument = node.getOwnerDocument().getModel()
									.getStructuredDocument();
							CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument,
									new FormatRegion(start, end - start), stgy);
							for (int i = 0; i < regions.length; i++) {
								source.append(decoratedRegion(regions[i], 0, stgy));
							}
						}
					}
					String delim = getLineDelimiter(node);
					source.append(delim);
					source.append(getIndent(node));
				} else {
					formatValue(node, source, child);
				}
				child = child.getNextSibling();
				if (child != null) {
					source.append(getIndentString());
				}
			}
		}
	}

	@Override
	protected void formatValue(IJSONNode node, StringBuilder source, IJSONNode value) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);
		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		int start = value.getStartOffset();
		int end = value.getEndOffset();
		if (value.getNextSibling() != null) {
			int s = value.getEndOffset();
			int e = value.getNextSibling().getStartOffset();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(s, e - s),
					stgy);
			if (regions.length > 0) {
				end++;
			}
		}
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument,
				new FormatRegion(start, end - start), stgy);
		for (int i = 0; i < regions.length; i++) {
			source.append(decoratedRegion(regions[i], 0, stgy));
		}
		source.append(getLineDelimiter(node));
		source.append(getIndent(node));
	}

	@Override
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

		if (region.getOffset() >= 0 && region.getLength() >= 0) {
			IStructuredDocument document = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(document, region, stgy);
			if (regions.length > 0 && regions[0] != null
					&& regions[0].getType() == JSONRegionContexts.JSON_ARRAY_OPEN) {
				source.append(decoratedRegion(regions[0], 0, stgy));
			}
		}
		if (node instanceof IJSONArray && node.hasChildNodes()) {
			source.append(getLineDelimiter(node));
			source.append(getIndent(node));
			source.append(getIndentString());
		}
	}

	@Override
	protected void formatPost(IJSONNode node, StringBuilder source) {
		formatPost(node, new FormatRegion(node.getStartOffset(), node.getEndOffset() - node.getStartOffset()), source);
	}

	@Override
	protected void formatPost(IJSONNode node, IRegion region, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		if (region.getOffset() >= 0 && region.getLength() >= 0) {
			IStructuredDocument document = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(document, region, stgy);
			if (regions.length > 0 && regions[regions.length - 1] != null) {
				CompoundRegion r = regions[regions.length - 1];
				if (r != null && r.getType() == JSONRegionContexts.JSON_ARRAY_CLOSE) {
					source.append(decoratedRegion(r, 0, stgy));
				}
			}
		}
	}
	public synchronized static JSONArrayFormatter getInstance() {
		if (instance == null)
			instance = new JSONArrayFormatter();
		return instance;
	}

}