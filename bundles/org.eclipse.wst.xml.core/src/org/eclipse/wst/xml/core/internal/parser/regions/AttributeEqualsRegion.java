/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.parser.regions;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;



public class AttributeEqualsRegion implements ITextRegion {
	static private final byte fTextLength = 1;
	static private final String fType = DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS;
	private short fLength;
	private int fStart;


	public AttributeEqualsRegion() {
		super();
	}

	public AttributeEqualsRegion(int start, int textLength, int length) {
		this();
		fStart = start;
		fLength = (short) length;
	}


	public void adjustLength(int i) {
		fLength += i;

	}

	public void adjustStart(int i) {
		fStart += i;

	}

	public void adjustTextLength(int i) {
		// not supported

	}

	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
		fLength = (short) region.getLength();
	}

	public int getEnd() {
		return fStart + fLength;
	}

	public int getLength() {
		return fLength;
	}

	public int getStart() {
		return fStart;
	}

	public int getTextEnd() {
		return fStart + fTextLength;
	}

	public int getTextLength() {
		return fTextLength;
	}

	public String getType() {
		return fType;
	}

	public String toString() {
		return RegionToStringUtil.toString(this);
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// can never be updated
		return null;
	}
}
