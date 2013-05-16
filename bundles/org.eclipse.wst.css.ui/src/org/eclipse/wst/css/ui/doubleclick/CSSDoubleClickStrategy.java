/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.wst.css.ui.doubleclick;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class CSSDoubleClickStrategy extends DefaultTextDoubleClickStrategy {
	
	protected IRegion findExtendedDoubleClickSelection(IDocument document, int offset) {
		IRegion word= super.findExtendedDoubleClickSelection(document, offset);
		if (word != null)
			return word;
		word = findWord(document, offset);
		IRegion line;
		try {
			line = document.getLineInformationOfOffset(offset);
			if (offset == line.getOffset() + line.getLength())
				return null;
			int start= word.getOffset();
			int end= start + word.getLength();
			if (start > 0 && document.getChar(start - 1) == '#'){
				start --;
			}
			else if (end == offset && end == start + 1 && end < line.getOffset() + line.getLength() && document.getChar(end) == '#') {
				return findExtendedDoubleClickSelection(document, offset + 1);
			}
			if (start == end)
				return null;
			return new Region(start, end - start);
		} catch (BadLocationException e) {
			return null;
		}
	}

}
