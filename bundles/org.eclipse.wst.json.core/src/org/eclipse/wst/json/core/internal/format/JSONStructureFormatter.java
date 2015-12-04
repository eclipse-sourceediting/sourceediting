/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.DeclContainerFormatter
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.cleanup.IJSONCleanupStrategy;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * 
 */
public class JSONStructureFormatter extends DefaultJSONSourceFormatter {

	/**
	 * 
	 */
	JSONStructureFormatter() {
		super();
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	protected String decoratedSelectorRegion(CompoundRegion region,
			CompoundRegion prevRegion, IJSONCleanupStrategy stgy) {
		if (isFormat())
			return region.getText();

		String text = null;
		if (!stgy.isFormatSource())
			text = region.getFullText();
		else
			text = region.getText();

		String type = region.getType();
		short selCase = -1;
		// if (type == JSONRegionContexts.JSON_SELECTOR_ATTRIBUTE_NAME
		// || type == JSONRegionContexts.JSON_SELECTOR_ATTRIBUTE_VALUE
		// || type == JSONRegionContexts.JSON_SELECTOR_ELEMENT_NAME
		// || type == JSONRegionContexts.JSON_SELECTOR_PSEUDO) {
		// selCase = stgy.getSelectorTagCase();
		// } else if (type == JSONRegionContexts.JSON_SELECTOR_CLASS) {
		// selCase = stgy.getClassSelectorCase();
		// } else if (type == JSONRegionContexts.JSON_SELECTOR_ID) {
		// selCase = stgy.getIdSelectorCase();
		// }
		//
		// if (selCase ==
		// org.eclipse.wst.css.core.internal.cleanup.JSONCleanupStrategy.UPPER)
		// {
		// return text.toUpperCase();
		// } else if (selCase ==
		// org.eclipse.wst.css.core.internal.cleanup.JSONCleanupStrategy.LOWER)
		// {
		// return text.toLowerCase();
		// } else if (selCase ==
		// org.eclipse.wst.css.core.internal.cleanup.JSONCleanupStrategy.ASIS) {
		// return text;
		// }

		return decoratedRegion(region, 0, stgy);
	}

	/**
	 * 
	 */
	protected void formatPost(IJSONNode node, StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node
				.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node
				.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument()
					.getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
					structuredDocument, new FormatRegion(start, end - start),
					stgy);
			for (int i = 0; i < regions.length; i++) {
				appendDelimBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 0, stgy));
			}
		} else { // generate source
			String delim = getLineDelimiter(node);
			source.append(delim);
			source.append(getIndent(node));
			source.append("}");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	protected void formatPost(IJSONNode node, IRegion region,
			StringBuilder source) {
		IJSONCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument()
				.getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(
				structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendDelimBefore(node, regions[i], source);
			source.append(decoratedRegion(regions[i], 0, stgy));
		}
	}

	/**
	 * 
	 */
	public int getChildInsertPos(IJSONNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument()
					.getModel().getStructuredDocument()
					.getRegionAtCharacterOffset(n - 1);
			if (flatNode.getRegionAtCharacterOffset(n - 1).getType() == JSONRegionContexts.JSON_OBJECT_OPEN
					|| flatNode.getRegionAtCharacterOffset(n - 1).getType() == JSONRegionContexts.JSON_ARRAY_OPEN)
				return n - 1;
			else
				return n;
		}
		return -1;
	}
}
