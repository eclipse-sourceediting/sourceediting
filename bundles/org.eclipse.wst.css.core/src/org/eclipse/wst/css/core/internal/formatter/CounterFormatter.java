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



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICounter;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * 
 */
public class CounterFormatter extends DefaultCSSSourceFormatter {

	private static CounterFormatter instance;

	/**
	 * 
	 */
	CounterFormatter() {
		super();
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		
		StringBuffer source = new StringBuffer();
		if (node == null || attr == null)
			return source;

		if (!ICounter.IDENTIFIER.equalsIgnoreCase(attr.getName()) && !ICounter.LISTSTYLE.equalsIgnoreCase(attr.getName()) && !ICounter.SEPARATOR.equalsIgnoreCase(attr.getName()))
			return source;

		// get region to replace
		IndexedRegion iNode = (IndexedRegion) node;
		context.start = iNode.getStartOffset();
		context.end = iNode.getEndOffset();

		ICounter counter = (ICounter) node;
		String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
		String sep = counter.getSeparator();
		String ident = counter.getIdentifier();
		String style = counter.getListStyle();

		if (ICounter.IDENTIFIER.equalsIgnoreCase(attr.getName())) {
			if (insert)
				ident = attr.getValue();
			else
				ident = "";//$NON-NLS-1$
		}
		else if (ICounter.LISTSTYLE.equalsIgnoreCase(attr.getName())) {
			if (insert)
				style = attr.getValue();
			else
				style = null;
		}
		else if (ICounter.SEPARATOR.equalsIgnoreCase(attr.getName())) {
			if (insert)
				sep = attr.getValue();
			else
				sep = null;
		}

		quote = CSSUtil.detectQuote(sep, quote);
		sep = (sep == null || sep.length() == 0) ? null : (quote + sep + quote);

		String func = (sep == null || sep.length() == 0) ? "counter(" : "counters(";//$NON-NLS-2$//$NON-NLS-1$
		if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER) {
			ident = ident.toUpperCase();
			style = style.toUpperCase();
			func = func.toUpperCase();
		}
		else {
			ident = ident.toLowerCase();
			style = style.toLowerCase();
		}
		if (sep == null || sep.length() == 0) {
			source.append(func);
			appendSpaceBefore(node, ident, source);
			source.append(ident);
		}
		else {
			source.append(func);
			appendSpaceBefore(node, ident, source);
			source.append(ident);
			source.append(",");//$NON-NLS-1$
			appendSpaceBefore(node, sep, source);
			source.append(sep);
		}

		if (style != null && style.length() != 0) {
			source.append(",");//$NON-NLS-1$
			appendSpaceBefore(node, style, source);
		}
		source.append(")");//$NON-NLS-1$

		/*
		 * IStructuredDocument structuredDocument =
		 * node.getOwnerDocument().getModel().getStructuredDocument();
		 * ITextRegion[] regions =
		 * getRegionsWithoutWhiteSpaces(structuredDocument, new
		 * FormatRegion(context.start, context.end - context.start + 1)); int
		 * commas[2]; int numComma = 0; for(int j = 0; j <regions.length; j++) {
		 * if (regions[j].getType() == CSSRegionContexts.COMMA) {
		 * commas[numComma++] = j; if (numComma > 1) break; } }
		 * 
		 * if (ICounter.IDENTIFIER.equalsIgnoreCase(attr.getName())) { } else
		 * if (ICounter.LISTSTYLE.equalsIgnoreCase(attr.getName())) { } else
		 * if (ICounter.SEPARATOR.equalsIgnoreCase(attr.getName())) { boolean
		 * skipSpace = false; String func = insert ? "counters(" : "counter(";
		 * if (mgr.isPropValueUpperCase()) func = func.toUpperCase();
		 * 
		 * for(int i=0; i <regions.length; i++) { if (regions[i].getType() ==
		 * CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
		 * source.append(func); else { if (numComma == 2 && commas[1] < i) {
		 *  } else if (numComma == 1 && commas[0] < i && insert) {
		 *  } else if (numComma == 1 && commas[0] < i && ! insert) { } else if
		 * (numComma == 0 && insert && regions[i].getType ==
		 * CSSRegionContexts.PARENTHESIS_CLOSE) { source.append(",");
		 * appendSpaceBefore(node,attr.getValue(),source);
		 * source.append(attr.getValue()); }
		 * 
		 * if (i != 0 && !skipSpace)
		 * appendSpaceBefore(node,regions[i],source);
		 * source.append(decoratedPropValueRegion(regions[i])); } skipSpace =
		 * false;
		 * 
		 * if (regions[i].getType() ==
		 * CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) skipSpace = true; } }
		 */
		return source;
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = ((IndexedRegion) node).getEndOffset();

		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			boolean skipSpace = false;
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 && !skipSpace)
					appendSpaceBefore(node, regions[i], source);
				skipSpace = false;
				source.append(decoratedPropValueRegion(regions[i], stgy));
				if (regions[i].getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
					skipSpace = true;
			}
		}
		else { // generate source
			ICounter counter = (ICounter) node;
			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
			String separator = counter.getSeparator();
			quote = CSSUtil.detectQuote(separator, quote);
			String sep = (separator == null || separator.length() == 0) ? null : (quote + separator + quote);
			String ident = counter.getIdentifier();
			String style = counter.getListStyle();
			String func = (sep == null || sep.length() == 0) ? "counter(" : "counters(";//$NON-NLS-2$//$NON-NLS-1$
			// normalize
			if (ident == null)
				ident = "";//$NON-NLS-1$
			if (style == null)
				style = "";//$NON-NLS-1$

			if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER) {
				ident = ident.toUpperCase();
				style = style.toUpperCase();
				func = func.toUpperCase();
			}
			else {
				ident = ident.toLowerCase();
				style = style.toLowerCase();
			}
			if (sep == null || sep.length() == 0) {
				source.append(func);
				appendSpaceBefore(node, ident, source);
				source.append(ident);
			}
			else {
				source.append(func);
				appendSpaceBefore(node, ident, source);
				source.append(ident);
				source.append(",");//$NON-NLS-1$
				appendSpaceBefore(node, sep, source);
				source.append(sep);
			}

			if (style != null && style.length() != 0) {
				source.append(",");//$NON-NLS-1$
				appendSpaceBefore(node, style, source);
			}
			source.append(")");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

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
	public int getAttrInsertPos(ICSSNode node, String attrName) {
		if (node == null || attrName == null || attrName.length() == 0)
			return -1;

		IndexedRegion iNode = (IndexedRegion) node;
		if (ICounter.IDENTIFIER.equalsIgnoreCase(attrName)) {
			ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICounter.IDENTIFIER);
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
				return ((IndexedRegion) attr).getStartOffset();
			if (iNode.getEndOffset() <= 0)
				return -1;

			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(iNode.getEndOffset() - 1);
			RegionIterator it = new RegionIterator(flatNode, flatNode.getRegionAtCharacterOffset(iNode.getEndOffset() - 1));
			while (it.hasPrev()) {
				ITextRegion region = it.prev();
				if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
					return it.getStructuredDocumentRegion().getEndOffset(region);
			}
			return ((IndexedRegion) node).getEndOffset();
		}
		else if (ICounter.LISTSTYLE.equalsIgnoreCase(attrName)) {
			ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICounter.LISTSTYLE);
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
				return ((IndexedRegion) attr).getStartOffset();

			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(iNode.getEndOffset() - 1);
			RegionIterator it = new RegionIterator(flatNode, flatNode.getRegionAtCharacterOffset(iNode.getEndOffset() - 1));
			while (it.hasPrev()) {
				ITextRegion region = it.prev();
				if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR)
					return it.getStructuredDocumentRegion().getEndOffset(region);
				else if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
					return it.getStructuredDocumentRegion().getEndOffset(region);
			}
			return ((IndexedRegion) node).getEndOffset();
		}
		else if (ICounter.SEPARATOR.equalsIgnoreCase(attrName)) {
			ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(ICounter.SEPARATOR);
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
				return ((IndexedRegion) attr).getStartOffset();

			IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(iNode.getEndOffset() - 1);
			RegionIterator it = new RegionIterator(flatNode, flatNode.getRegionAtCharacterOffset(iNode.getEndOffset() - 1));
			boolean hasComma = false;
			while (it.hasPrev()) {
				ITextRegion region = it.prev();
				if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR) {
					if (!hasComma)
						hasComma = true;
					else
						return it.getStructuredDocumentRegion().getEndOffset(region);
				}
				else if (region.getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
					return it.getStructuredDocumentRegion().getEndOffset(region);
			}
			return ((IndexedRegion) node).getEndOffset();
		}
		else
			return -1;
	}

	/**
	 * 
	 */
	public synchronized static CounterFormatter getInstance() {
		if (instance == null)
			instance = new CounterFormatter();
		return instance;
	}
}
