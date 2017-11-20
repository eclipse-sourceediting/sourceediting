/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
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
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class JSONObjectFormatter extends JSONStructureFormatter {

	private static JSONObjectFormatter instance;

	JSONObjectFormatter() {
		super();
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
				if (r != null && r.getType() == JSONRegionContexts.JSON_OBJECT_CLOSE) {
					source.append(getLineDelimiter(node));
					source.append(getIndent(node));
					source.append(decoratedRegion(r, 0, stgy));
				}
			}
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
					&& regions[0].getType() == JSONRegionContexts.JSON_OBJECT_OPEN) {
				source.append(decoratedRegion(regions[0], 0, stgy));
			}
		}
		source.append(getLineDelimiter(node));
		source.append(getIndent(node));
		source.append(getIndentString());
	}

	public synchronized static JSONObjectFormatter getInstance() {
		if (instance == null)
			instance = new JSONObjectFormatter();
		return instance;
	}
}
