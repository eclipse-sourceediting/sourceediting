/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.style;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class LineStyleProviderForEmbeddedCSS extends LineStyleProviderForCSS {

	public boolean prepareRegions(ITypedRegion typedRegion, int lineRequestStart, int lineRequestLength, Collection holdResults) {
		int regionStart = typedRegion.getOffset();
		int regionEnd = regionStart + typedRegion.getLength();
		IStructuredDocumentRegion wholeRegion = getDocument().getRegionAtCharacterOffset(regionStart);

		if (wholeRegion == null)
			return false;

		List tokens;
		// [264597] - Using a cached offset caused shifted highlighting in <style> elements.
		int offset = wholeRegion.getStartOffset();

		List cache = getCachedParsingResult(wholeRegion);
		if (cache == null) {
			String content;
			content = wholeRegion.getText();

			CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_STYLESHEET, content);
			tokens = parser.getTokenList();
			cacheParsingResult(wholeRegion, tokens);
		}
		else
			tokens = cache;

		boolean result = false;

		if (0 < tokens.size()) {
			int start = offset;
			int end = start;
			Iterator i = tokens.iterator();
			while (i.hasNext()) {
				CSSTextToken token = (CSSTextToken) i.next();
				end = start + token.length;
				int styleLength = token.length;
				/* The token starts in the region */
				if (regionStart <= start && start < regionEnd) {
					/* [239415] The region may not span the total length of the token -
					 * Adjust the length so that it doesn't overlap with other style ranges */
					if (regionEnd < end)
						styleLength = regionEnd - start;
					addStyleRange(holdResults, getAttributeFor(token.kind), start, styleLength);
				}
				/* The region starts in the token */
				else if (start <= regionStart && regionStart < end ) {
					/* The token may not span the total length of the region */
					if (end < regionEnd)
						styleLength = end - regionStart;
					else /* Bugzilla 282218 */ 
						styleLength = regionEnd - regionStart;
					addStyleRange(holdResults, getAttributeFor(token.kind), regionStart, styleLength);
				}
				start += token.length;
			}
			result = true;
		}

		return result;
	}
	
	private void addStyleRange(Collection holdResults, TextAttribute attribute, int start, int end) {
		if (attribute != null)
			holdResults.add(new StyleRange(start, end, attribute.getForeground(), attribute.getBackground(), attribute.getStyle()));
		else
			holdResults.add(new StyleRange(start, end, null, null));
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		return null;
	}

	private void cleanupCache() {
		fCacheKey = -1;
		fCacheResult = null;
	}

	private List getCachedParsingResult(IStructuredDocumentRegion region) {
		if (fCacheKey == region.getText().hashCode()) {
			return fCacheResult;
		}
		return null;
	}

	private void cacheParsingResult(IStructuredDocumentRegion region, List result) {
		fCacheKey = region.getText().hashCode();
		fCacheResult = result;
	}

	public void release() {
		super.release();
		cleanupCache();
	}

	int fCacheKey = -1;
	List fCacheResult = null;
}
