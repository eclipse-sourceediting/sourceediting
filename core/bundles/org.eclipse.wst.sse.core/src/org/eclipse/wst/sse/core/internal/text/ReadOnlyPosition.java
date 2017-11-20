/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.jface.text.Position;

class ReadOnlyPosition extends Position {
	private boolean fIncludeStartOffset = false;

	public ReadOnlyPosition(int newOffset, int newLength, boolean includeStart) {
		super(newOffset, newLength);
		fIncludeStartOffset = includeStart;
	}

	public boolean overlapsWith(int newOffset, int newLength) {
		boolean overlapsWith = super.overlapsWith(newOffset, newLength);
		if (overlapsWith) {
			/*
			 * BUG157526 If at the start of the read only region and length =
			 * 0 most likely asking to insert and want to all inserting before
			 * read only region
			 */
			if (fIncludeStartOffset && (newLength == 0) && (this.length != 0) && (newOffset == this.offset)) {
				overlapsWith = false;
			}
		}
		return overlapsWith;
	}
}
