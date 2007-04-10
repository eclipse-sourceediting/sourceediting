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
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
public class MediaListFormatter extends DefaultCSSSourceFormatter {

	private static MediaListFormatter instance;

	/**
	 * 
	 */
	MediaListFormatter() {
		super();
	}

	/**
	 * 
	 */
	public StringBuffer formatAttrChanged(ICSSNode node, ICSSAttr attr, boolean insert, AttrChangeContext context) {
		StringBuffer buf = new StringBuffer();
		if (node == null || attr == null)
			return buf;

		boolean first = false, last = false;
		// get region to replace
		if (context != null && ((IndexedRegion) node).getEndOffset() > 0) {
			if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0) {
				IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) attr).getStartOffset());
				ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) attr).getStartOffset());
				RegionIterator it = new RegionIterator(flatNode, region);
				it.prev();
				context.start = it.getStructuredDocumentRegion().getStartOffset(region);
				while (it.hasPrev()) {
					ITextRegion prev = it.prev();
					if (prev.getType() == CSSRegionContexts.CSS_S || prev.getType() == CSSRegionContexts.CSS_MEDIA_SEPARATOR || prev.getType() == CSSRegionContexts.CSS_COMMENT)
						context.start = it.getStructuredDocumentRegion().getStartOffset(prev);
					else
						break;
				}
				if (context.start < ((IndexedRegion) node).getStartOffset()) {
					context.start = ((IndexedRegion) node).getStartOffset();
					first = true;

				}
				it.reset(flatNode, region);
				context.end = it.getStructuredDocumentRegion().getEndOffset(region);
				while (it.hasNext()) {
					ITextRegion next = it.next();
					if (next.getType() == CSSRegionContexts.CSS_S || next.getType() == CSSRegionContexts.CSS_MEDIA_SEPARATOR || next.getType() == CSSRegionContexts.CSS_COMMENT)
						context.end = it.getStructuredDocumentRegion().getEndOffset(next);
					else
						break;
				}
				if (((IndexedRegion) node).getEndOffset() < context.end) {
					context.end = ((IndexedRegion) node).getEndOffset();
					last = true;
				}
			} else {
				last = true;
				IStructuredDocumentRegion flatNode = node.getOwnerDocument().getModel().getStructuredDocument().getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
				ITextRegion region = flatNode.getRegionAtCharacterOffset(((IndexedRegion) node).getEndOffset() - 1);
				RegionIterator it = new RegionIterator(flatNode, region);
				context.start = it.getStructuredDocumentRegion().getEndOffset(region);
				context.end = it.getStructuredDocumentRegion().getEndOffset(region);
				while (it.hasPrev()) {
					ITextRegion prev = it.prev();
					if (prev.getType() == CSSRegionContexts.CSS_S || prev.getType() == CSSRegionContexts.CSS_MEDIA_SEPARATOR || prev.getType() == CSSRegionContexts.CSS_COMMENT)
						context.start = it.getStructuredDocumentRegion().getStartOffset(prev);
					else
						break;
				}
				if (context.start < ((IndexedRegion) node).getStartOffset()) {
					context.start = ((IndexedRegion) node).getStartOffset();
					first = true;
				}
			}
		}
		// generate text
		if (insert && attr.getValue() != null && attr.getValue().length() > 0) {
			if (!first)
				buf.append(","); //$NON-NLS-1$
			appendSpaceBefore(node, attr.getValue(), buf);
			buf.append(attr.getValue());
			if (!last) {
				buf.append(","); //$NON-NLS-1$
				appendSpaceBefore(node, "", buf); //$NON-NLS-1$
			}
		} else if (!first && !last) {
			buf.append(","); //$NON-NLS-1$
			appendSpaceBefore(node, "", buf); //$NON-NLS-1$
		}
		return buf;
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int start = ((IndexedRegion) node).getStartOffset();
		int end = ((IndexedRegion) node).getEndOffset();

		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			for (int i = 0; i < regions.length; i++) {
				if (i != 0)
					appendSpaceBefore(node, regions[i], source);
				source.append(decoratedIdentRegion(regions[i], stgy));
			}
		} else { // generate source
			MediaList list = (MediaList) node;
			int n = list.getLength();
			for (int i = 0; i < n; i++) {
				String medium = list.item(i);
				if (CSSCorePlugin.getDefault().getPluginPreferences().getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
					medium = medium.toUpperCase();
				else
					medium = medium.toLowerCase();

				if (i != 0) {
					source.append(","); //$NON-NLS-1$
					appendSpaceBefore(node, medium, source);
				}
				source.append(medium);
			}
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
			source.append(decoratedIdentRegion(regions[i], stgy));
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

		ICSSAttr attr = (ICSSAttr) node.getAttributes().getNamedItem(attrName);

		if (attr != null && ((IndexedRegion) attr).getEndOffset() > 0)
			return ((IndexedRegion) attr).getStartOffset();
		IndexedRegion iNode = (IndexedRegion) node;
		if (iNode.getEndOffset() <= 0)
			return -1;

		/*
		 * ITextRegion regions[] =
		 * getRegionsWithoutWhiteSpaces(node.getOwnerDocument().getModel().getStructuredDocument(),
		 * new FormatRegion(iNode.getStartOffset(), iNode.getEndOffset() -
		 * iNode.getStartOffset() + 1)); for(int i=regions.length - 1; i >= 0;
		 * i--) { if (regions[i].getType() == CSSRegionContexts.IMPORTANT_SYM)
		 * return regions[i].getStartOffset(); }
		 */
		return iNode.getEndOffset();
	}

	/**
	 * 
	 */
	public synchronized static MediaListFormatter getInstance() {
		if (instance == null)
			instance = new MediaListFormatter();
		return instance;
	}
}
