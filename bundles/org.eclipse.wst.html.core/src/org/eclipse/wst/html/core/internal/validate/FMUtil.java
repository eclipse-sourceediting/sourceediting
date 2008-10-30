/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

final class FMUtil {

	public final static int SEG_NONE = 0;
	public final static int SEG_WHOLE_TAG = 1;
	public final static int SEG_START_TAG = 2;
	public final static int SEG_END_TAG = 3;
	public final static int SEG_START_TAG_NAME = 4;
	public final static int SEG_END_TAG_NAME = 5;

	/**
	 */
	private FMUtil() {
		super();
	}

	/**
	 */
	public final static Segment getSegment(IDOMNode target, int segType) {
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
					seg = new Segment(target.getStartOffset(), 1);
				}
				break;
			case SEG_END_TAG :
				endTag = target.getEndStructuredDocumentRegion();
				if (endTag != null) {
					seg = new Segment(endTag);
				}
				else {
					seg = new Segment(target.getEndOffset(), 1);
				}
				break;
			case SEG_START_TAG_NAME :
				startTag = target.getStartStructuredDocumentRegion();
				if (startTag != null) {
					ITextRegion nameRegion = getNameRegion(startTag);
					if (nameRegion != null) {
						seg = new Segment(startTag.getStartOffset(nameRegion), nameRegion.getTextLength());
					}
				}
				if (seg == null) {
					seg = getSegment(target, SEG_START_TAG);
				}
				break;
			case SEG_END_TAG_NAME :
				endTag = target.getEndStructuredDocumentRegion();
				if (endTag != null) {
					ITextRegion nameRegion = getNameRegion(endTag);
					if (nameRegion != null) {
						seg = new Segment(endTag.getStartOffset(nameRegion), nameRegion.getTextLength());
					}
				}
				if (seg == null) {
					seg = getSegment(target, SEG_END_TAG);
				}
				break;
			case SEG_NONE :
			default :
				return new Segment(0, 0);
		}
		return seg;
	}

	private static ITextRegion getNameRegion(ITextRegionCollection containerRegion) {
		ITextRegionList regions = containerRegion.getRegions();
		ITextRegion nameRegion = null;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion r = regions.get(i);
			if (r.getType() == DOMRegionContext.XML_TAG_NAME) {
				nameRegion = r;
				break;
			}
		}
		return nameRegion ;
	}

}
