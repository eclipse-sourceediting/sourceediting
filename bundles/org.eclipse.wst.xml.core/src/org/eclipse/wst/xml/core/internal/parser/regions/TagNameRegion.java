/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.parser.regions;

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


public class TagNameRegion implements ITextRegion {
	static private final String fType = XMLRegionContext.XML_TAG_NAME;
	private short fTextLength;
	private short fLength;
	private int fStart;


	public TagNameRegion() {
		super();
	}

	public TagNameRegion(int start, int textLength, int length) {
		this();
		fStart = start;
		fTextLength = (short) textLength;
		fLength = (short) length;
	}

	public void adjustLengthWith(int i) {
		fLength += i;

	}

	public void adjustStart(int i) {
		fStart += i;

	}

	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
		fLength = (short) region.getLength();
		fTextLength = (short) region.getTextLength();
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

	public StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// can never be updated
		return null;
	}

	public String toString() {
		return RegionToStringUtil.toString(this);
	}

	/* (non-Javadoc)
	 */
	public void adjustTextLength(int i) {
		fTextLength += i;

	}

}
