/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.FormatRegion
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.jface.text.IRegion;

class FormatRegion implements IRegion {

	private int fOffset, fLength;

	FormatRegion(int offset, int length) {
		super();
		set(offset, length);
	}

	@Override
	public int getLength() {
		return fLength;
	}

	@Override
	public int getOffset() {
		return fOffset;
	}

	void set(int offset, int length) {
		this.fOffset = offset;
		this.fLength = length;
	}

	void setLength(int newLength) {
		fLength = newLength;
	}

	void setOffset(int newOffset) {
		fOffset = newOffset;
	}
}