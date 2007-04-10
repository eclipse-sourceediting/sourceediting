/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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

		List tokens;
		int offset;

		ParserCache cache = getCachedParsingResult(wholeRegion);
		if (cache == null) {
			offset = wholeRegion.getStartOffset();
			String content;
			content = wholeRegion.getText();

			CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_STYLESHEET, content);
			tokens = parser.getTokenList();
			cacheParsingResult(wholeRegion, new ParserCache(offset, tokens));
		} else {
			tokens = cache.tokens;
			offset = cache.offset;
		}

		boolean result = false;

		if (0 < tokens.size()) {
			int start = offset;
			Iterator i = tokens.iterator();
			while (i.hasNext()) {
				CSSTextToken token = (CSSTextToken) i.next();
				if (regionStart <= start && start < regionEnd) {
					TextAttribute attribute = getAttributeFor(token.kind);
					if (attribute != null) {
						holdResults.add(new StyleRange(start, token.length, attribute.getForeground(), attribute.getBackground(), attribute.getStyle()));
					}
					else {
						holdResults.add(new StyleRange(start, token.length, null, null));
					}
				}
				start += token.length;
			}
			result = true;
		}

		return result;
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		return null;
	}

	private void cleanupCache() {
		fCacheKey = -1;
		fCacheResult = null;
	}

	private ParserCache getCachedParsingResult(IStructuredDocumentRegion region) {
		if (fCacheKey == region.getText().hashCode()) {
			return fCacheResult;
		}
		return null;
	}

	private void cacheParsingResult(IStructuredDocumentRegion region, ParserCache result) {
		fCacheKey = region.getText().hashCode();
		fCacheResult = result;
	}

	public void release() {
		super.release();
		cleanupCache();
	}

	private class ParserCache {
		ParserCache(int newOffset, List newTokens) {
			offset = newOffset;
			tokens = newTokens;
		}

		int offset;
		List tokens;
	}

	int fCacheKey = -1;
	ParserCache fCacheResult = null;
}
