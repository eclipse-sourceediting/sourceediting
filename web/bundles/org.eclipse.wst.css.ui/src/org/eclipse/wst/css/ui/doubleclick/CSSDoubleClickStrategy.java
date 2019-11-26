/*******************************************************************************
 * Copyright (c) 2013, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.wst.css.ui.doubleclick;

import java.util.Arrays;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class CSSDoubleClickStrategy extends DefaultTextDoubleClickStrategy {
	private static final char[] NON_BREAKERS = new char[]{'-'};
	static {
		Arrays.sort(NON_BREAKERS);
	}

	@Override
	protected IRegion findExtendedDoubleClickSelection(IDocument document, int offset) {
		IRegion word = super.findExtendedDoubleClickSelection(document, offset);
		if (word != null)
			return word;
		word = findWord(document, offset);
		if (word != null) {
			// see if it needs expansion past a non-breaker
			try {
				IRegion line = document.getLineInformationOfOffset(offset);
				if (offset == line.getOffset() + line.getLength())
					return null;
				int start = word.getOffset();
				int end = start + word.getLength();
				while (start > 0 && Arrays.binarySearch(NON_BREAKERS, document.getChar(start - 1)) > -1) {
					IRegion previous = findWord(document, --start);
					if (previous != null) {
						start = previous.getOffset();
					}
					else {
						break;
					}
				}
				while (end < line.getOffset() + line.getLength() && Arrays.binarySearch(NON_BREAKERS, document.getChar(end)) > -1) {
					IRegion next = findExtendedDoubleClickSelection(document, end + 1);
					if (next != null) {
						end = next.getOffset() + next.getLength();
					}
					else {
						break;
					}
				}
				if (start == end)
					return null;
				return new Region(start, end - start);
			}
			catch (BadLocationException e) {
				return null;
			}
		}
		return null;
	}
}
