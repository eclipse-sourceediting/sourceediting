/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.jface.text.IRegion;

/**
 * 
 */
class FormatRegion implements IRegion {

	private int fOffset, fLength;

	/**
	 * 
	 */
	FormatRegion(int offset, int length) {
		super();
		set(offset, length);
	}

	/**
	 * Returns the length of the region.
	 * 
	 * @return the length of the region
	 */
	public int getLength() {
		return fLength;
	}

	/**
	 * Returns the offset of the region.
	 * 
	 * @return the offset of the region
	 */
	public int getOffset() {
		return fOffset;
	}

	/**
	 * 
	 */
	void set(int offset, int length) {
		this.fOffset = offset;
		this.fLength = length;
	}

	/**
	 * 
	 */
	void setLength(int newLength) {
		fLength = newLength;
	}

	/**
	 * 
	 */
	void setOffset(int newOffset) {
		fOffset = newOffset;
	}
}