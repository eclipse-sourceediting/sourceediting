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
package org.eclipse.wst.sse.core.internal.text.rules;

import org.eclipse.jface.text.IRegion;



/**
 * Similar to jface region except we wanted a setting on length
 */
public class SimpleStructuredRegion implements IStructuredRegion {
	/** The region length */
	private int fLength;

	/** The region offset */
	private int fOffset;

	/**
	 * Create a new region.
	 * 
	 * @param offset
	 *            the offset of the region
	 * @param length
	 *            the length of the region
	 */
	public SimpleStructuredRegion(int offset, int length) {
		fOffset = offset;
		fLength = length;
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
	 * @see Object#hashCode hascode is overridden since we provide our own
	 *      equals.
	 */
	public int hashCode() {
		return (fOffset << 24) | (fLength << 16);
	}

	/**
	 * Sets the length.
	 * 
	 * @param length
	 *            The length to set
	 */
	public void setLength(int length) {
		fLength = length;
	}

	public void setOffset(int offset) {
		fOffset = offset;
	}

}
