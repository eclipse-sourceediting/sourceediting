/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.taginfo;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
class JsWordFinder {
	public static IRegion findWord(IDocument document, int offset) {
		int start = -1;
		int end = -1;
		try {
			int pos = offset;
			char c;
			while (pos >= 0) {
				c = document.getChar(pos);
				// System.out.println("JavaWordFinder.findWord() Test java char
				// (--):" + c);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				--pos;
			}
			start = pos;
			pos = offset;
			int length = document.getLength();
			while (pos < length) {
				c = document.getChar(pos);
				// System.out.println("JavaWordFinder.findWord() Test java char
				// (++):" + c);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				++pos;
			}
			end = pos;
			// System.out.println("Start:" + start + "End:"+end);
			// System.out.println("JavaWordFinder.findWord() Retrieved java
			// token of:" + document.get(start, end-start) );
		} catch (BadLocationException x) {
		}
		if (start > -1 && end > -1) {
			if (start == offset && end == offset) {
				return new Region(offset, 0);
			} else if (start == offset) {
				return new Region(start, end - start);
			} else {
				return new Region(start + 1, end - start - 1);
			}
		}
		return null;
	}
}
