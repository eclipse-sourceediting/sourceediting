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
package org.eclipse.wst.html.core.internal.validate;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class Segment {

	private int offset = 0;
	private int length = 0;

	/**
	 */
	public Segment(int offset, int length) {
		super();
		this.offset = offset;
		this.length = length;
	}

	public Segment(IStructuredDocumentRegion region) {
		super();
		this.offset = region.getStartOffset();
		this.length = region.getLength();
	}

	/**
	 * NOTE: 'start' and 'end' must be the start and end of the contiguous regions.
	 * Otherwise, this class cannot work correctly.
	 */
	public Segment(IStructuredDocumentRegion start, IStructuredDocumentRegion end) {
		super();
		this.offset = start.getStartOffset();
		int endOffset = (end == null) ? start.getEndOffset() : end.getEndOffset();
		this.length = endOffset - this.offset;
	}

	//public Segment(ITextRegion start, ITextRegion end) {
	//	super();
	//	this.offset = start.getStartOffset();
	//	int endOffset = (end == null) ? start.getEndOffset() : end.getEndOffset();
	//	this.length = endOffset - this.offset;
	//}
	/**
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 */
	public int getOffset() {
		return this.offset;
	}
}
