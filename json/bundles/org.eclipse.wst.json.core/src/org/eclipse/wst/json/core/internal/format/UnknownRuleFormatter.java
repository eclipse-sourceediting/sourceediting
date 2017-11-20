/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.UnknownRuleFormatter
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
public class UnknownRuleFormatter extends DefaultJSONSourceFormatter {

	private static UnknownRuleFormatter instance;

	UnknownRuleFormatter() {
		super();
	}

	@Override
	protected void formatPre(IJSONNode node, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		int start = ((IndexedRegion) node).getStartOffset();
		int end = ((IndexedRegion) node).getEndOffset();
		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument()
					.getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
					structuredDocument, new FormatRegion(start, end - start),
					stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedPropValueRegion(regions[i], stgy));
			}
		}
//		else { // generate source
//			JSONUnknownRule rule = (JSONUnknownRule) node;
//			source.append(rule.getCssText());
//		}
	}

	@Override
	protected void formatPre(IJSONNode node, IRegion region, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument()
				.getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
				structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedPropValueRegion(regions[i], stgy));
		}
		if (needS(outside[1]) && !isIncludesPreEnd(node, region))
			appendSpaceBefore(node, outside[1], source);
	}

	public synchronized static UnknownRuleFormatter getInstance() {
		if (instance == null)
			instance = new UnknownRuleFormatter();
		return instance;
	}
}
