/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * 
 */
abstract public class FunctionFormatter extends AbstractCSSSourceFormatter {

	/**
	 * 
	 */
	FunctionFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
		ICSSNode prev = (child != null) ? child.getPreviousSibling() : node.getLastChild();
		int start = (prev != null) ? ((IndexedRegion) prev).getEndOffset() : 0;
		int end = (child != null) ? ((IndexedRegion) child).getStartOffset() : 0;
		if (start > 0 && start < end) { // format source
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			// get meaning regions
			CompoundRegion[] regions = null;
			if (exceptFor == null)
				regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			else {
				String pickupType = CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR;
				if (prev == null || child == null)
					pickupType = null;
				regions = getRegions(structuredDocument, new FormatRegion(start, end - start), exceptFor, pickupType);
			}
			// extract source
			for (int i = 0; i < regions.length; i++) {
				appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 2, stgy));
			}
		} else if (prev != null && child != null) { // generate source between
			// parameters
			source.append(",");//$NON-NLS-1$
		}
		appendSpaceBefore(node, toAppend, source);
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
			source.append(decoratedRegion(regions[i], 2, stgy));
		}
	}

	/**
	 * 
	 */
	protected void formatPost(org.eclipse.wst.css.core.internal.provisional.document.ICSSNode node, java.lang.StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int end = ((IndexedRegion) node).getEndOffset();
		int start = (node.getLastChild() != null && ((IndexedRegion) node.getLastChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getLastChild()).getEndOffset() : getChildInsertPos(node);
		if (end > 0 && start < end) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				appendSpaceBefore(node, regions[i], source);
				source.append(decoratedRegion(regions[i], 2, stgy));
			}
		} else { // generate source
			source.append(")");//$NON-NLS-1$
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
			source.append(decoratedRegion(regions[i], 2, stgy));
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = (node.getFirstChild() != null && ((IndexedRegion) node.getFirstChild()).getEndOffset() > 0) ? ((IndexedRegion) node.getFirstChild()).getStartOffset() : getChildInsertPos(node);

		if (end > 0) { // format source
			CSSCleanupStrategy stgy = getCleanupStrategy(node);

			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedPropValueRegion(regions[i], stgy));
			}
		} else { // generate source
			String func = getFunc();
			if (CSSCorePlugin.getDefault().getPluginPreferences().getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER)
				func = func.toUpperCase();
			source.append(func);
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
			source.append(decoratedPropValueRegion(regions[i], stgy));
		}
		if (needS(outside[1]) && !isIncludesPreEnd(node, region))
			appendSpaceBefore(node, outside[1], source);
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0) {
			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(n - 1);
			if (flatNode.getRegionAtCharacterOffset(n - 1).getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE)
				return n - 1;
			return n;
		}
		return -1;
	}

	/**
	 * 
	 */
	protected abstract String getFunc();
}
