/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


public class TagCloseRegion implements ITextRegion {
	static private final byte fLength = 1;
	static private final byte fTextLength = 1;
	static private final String fType = XMLRegionContext.XML_TAG_CLOSE;
	private int fStart;


	public TagCloseRegion() {
		super();
	}

	public TagCloseRegion(int start) {
		this();
		fStart = start;
	}

	public void adjustLengthWith(int i) {
		throw new SourceEditingRuntimeException("invalid for this region type"); //$NON-NLS-1$

	}

	public void adjustStart(int i) {
		fStart += i;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		// not supported

	}

	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
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
