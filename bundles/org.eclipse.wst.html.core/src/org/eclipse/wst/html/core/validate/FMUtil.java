/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.validate;



import java.util.Iterator;

import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

final class FMUtil {

	public final static int SEG_NONE = 0;
	public final static int SEG_WHOLE_TAG = 1;
	public final static int SEG_START_TAG = 2;
	public final static int SEG_END_TAG = 3;

	/**
	 */
	private FMUtil() {
		super();
	}

	/**
	 */
	public final static Segment getSegment(XMLNode target, int segType) {
		if (target == null)
			return new Segment(0, 0);
		Segment seg = null;
		IStructuredDocumentRegion startTag = null;
		IStructuredDocumentRegion endTag = null;
		switch (segType) {
			case SEG_WHOLE_TAG :
				startTag = target.getFirstStructuredDocumentRegion();
				if (startTag != null) {
					endTag = target.getLastStructuredDocumentRegion();
					seg = new Segment(startTag, endTag);
				}
				else {
					int startOffset = target.getStartOffset();
					int endOffset = target.getEndOffset();
					seg = new Segment(startOffset, endOffset - startOffset);
				}
				break;
			case SEG_START_TAG :
				startTag = target.getStartStructuredDocumentRegion();
				if (startTag != null) {
					seg = new Segment(startTag);
				}
				else {
					seg = new Segment(target.getStartOffset(), 0);
				}
				break;
			case SEG_END_TAG :
				endTag = target.getEndStructuredDocumentRegion();
				if (endTag != null) {
					seg = new Segment(endTag);
				}
				else {
					seg = new Segment(target.getEndOffset(), 0);
				}
				break;
			case SEG_NONE :
			default :
				return new Segment(0, 0);
		}
		return seg;
	}

	/**
	 */
	public final static boolean hasJSPRegion(ITextRegion container) {
		if (!(container instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) container).getRegions();
		if (regions == null)
			return false;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region == null)
				continue;
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_TAG_OPEN || regionType == XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN || regionType == XMLJSPRegionContexts.JSP_EXPRESSION_OPEN || regionType == XMLJSPRegionContexts.JSP_DECLARATION_OPEN || regionType == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN)
				return true;
		}
		return false;
	}
}