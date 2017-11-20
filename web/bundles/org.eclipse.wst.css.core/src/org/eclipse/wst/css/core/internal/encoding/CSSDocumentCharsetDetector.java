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
package org.eclipse.wst.css.core.internal.encoding;

import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.css.core.internal.contenttype.CSSResourceEncodingDetector;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;


public class CSSDocumentCharsetDetector extends CSSResourceEncodingDetector implements IDocumentCharsetDetector {

	public String getEncodingName(IStructuredDocument structuredDocument) {
		String result = null;
		// if the document is empty, then there will be no nodes,
		// so no need to continue.
		IStructuredDocumentRegionList nodes = structuredDocument.getRegionList();
		if (nodes.getLength() > 0) {
			IStructuredDocumentRegion node = null;
			// skip any initial whitespace
			// Note that @charset "encodingname";
			// must appear at very beginning of document,
			// to be valid.
			// May have to test with "damaged" files (e.g.
			// beginning EOLs, etc., to verify this works
			// as expected.
			for (int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				if (getType(node) != CSSRegionContexts.CSS_S) {
					break;
				}
			}
			Iterator regions = node.getRegions().iterator();
			ITextRegion region = getNextRegionOfType(CSSRegionContexts.CSS_CHARSET, regions);
			if (region != null) {
				ITextRegion valueRegion = getNextRegionOfType(CSSRegionContexts.CSS_STRING, regions);
				if (valueRegion == null) {
					// if didn't find the region, its probably due to ill
					// formed input, such as
					// @charset "ISO-8859-6;
					// so we'll try again for "unknown" region.
					// If that fails, we'll give up?
					regions = node.getRegions().iterator();
					region = getNextRegionOfType(CSSRegionContexts.CSS_CHARSET, regions);
					if (region != null) {
						valueRegion = getNextRegionOfType(CSSRegionContexts.CSS_UNKNOWN, regions);
						if (valueRegion != null) {
							result = node.getText(valueRegion);
						}
					}
				}
				else {
					result = node.getText(valueRegion);
				}
				result = StringUtils.stripNonLetterDigits(result);
			}
		}
		return result;
	}

	public String getEncodingName(IDocument document) {
		String enc = null;
		if (document instanceof IStructuredDocument) {
			enc = getEncodingName((IStructuredDocument) document);
		}
		else {
			// TODO Important: need to implement some "raw" parser for
			// IDocument level
		}
		return enc;
	}

	private String getType(IStructuredDocumentRegion node) {
		if (node == null)
			return null;
		ITextRegionList regions = node.getRegions();
		if (regions == null || regions.size() == 0)
			return null;
		ITextRegion region = regions.get(0);
		String result = region.getType();
		return result;
	}

	private ITextRegion getNextRegionOfType(String type, Iterator regions) {
		if (type == null)
			return null;
		if (regions == null)
			return null;
		ITextRegion result = null;
		while (regions.hasNext()) {
			ITextRegion region = (ITextRegion) regions.next();
			if (region.getType() == type) {
				result = region;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 */

	public void set(IDocument document) {
		set(new DocumentReader(document, 0));

	}

}
