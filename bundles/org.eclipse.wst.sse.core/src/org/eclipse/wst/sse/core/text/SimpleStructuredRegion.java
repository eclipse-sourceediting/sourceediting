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
package org.eclipse.wst.sse.core.text;

import org.eclipse.jface.text.IRegion;



/**
 * Similar to jface region except we wanted a setting on length
 */
public class SimpleStructuredRegion implements StructuredRegion {

	/** The region offset */
	private int fOffset;
	/** The region length */
	private int fLength;

	/**
	 * Create a new region.
	 *
	 * @param offset the offset of the region
	 * @param length the length of the region
	 */
	public SimpleStructuredRegion(int offset, int length) {
		fOffset = offset;
		fLength = length;
	}

	/*
	 * @see IRegion#getLength
	 */
	public int getLength() {
		return fLength;
	}

	/*
	 * @see IRegion#getOffset
	 */
	public int getOffset() {
		return fOffset;
	}

	/**
	 * Two regions are equal if they have the same offset and length.
	 *
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (o instanceof IRegion) {
			IRegion r = (IRegion) o;
			return r.getOffset() == fOffset && r.getLength() == fLength;
		}
		return false;
	}

	/**
	 * @see Object#hashCode
	 * hascode is overridden since we provide our own equals. 
	 */
	public int hashCode() {
		return (fOffset << 24) | (fLength << 16);
	}

	/**
	 * Sets the length.
	 * @param length The length to set
	 */
	public void setLength(int length) {
		fLength = length;
	}

	public void setOffset(int offset) {
		fOffset = offset;
	}

}
