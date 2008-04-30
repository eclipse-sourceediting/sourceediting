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
package org.eclipse.wst.jsdt.web.ui.internal.text;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
class JsPairMatcher implements ICharacterPairMatcher {
	protected int fAnchor;
	protected IDocument fDocument;
	protected int fEndPos;
	protected int fOffset;
	protected char[] fPairs;
	protected JsCodeReader fReader = new JsCodeReader();
	protected int fStartPos;
	
	public JsPairMatcher(char[] pairs) {
		fPairs = pairs;
	}
	
	/*
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
	 */
	public void clear() {
		if (fReader != null) {
			try {
				fReader.close();
			} catch (IOException x) {
				// ignore
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
	 */
	public void dispose() {
		clear();
		fDocument = null;
		fReader = null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
	 */
	public int getAnchor() {
		return fAnchor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument,
	 *      int)
	 */
	public IRegion match(IDocument document, int offset) {
		fOffset = offset;
		if (fOffset < 0) {
			return null;
		}
		fDocument = document;
		if (fDocument != null && matchPairsAt() && fStartPos != fEndPos) {
			return new Region(fStartPos, fEndPos - fStartPos + 1);
		}
		return null;
	}
	
	protected boolean matchPairsAt() {
		int i;
		int pairIndex1 = fPairs.length;
		int pairIndex2 = fPairs.length;
		fStartPos = -1;
		fEndPos = -1;
		// get the chars preceding and following the start position
		try {
			char prevChar = fDocument.getChar(Math.max(fOffset - 1, 0));
			// modified behavior for
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=16879
			// char nextChar= fDocument.getChar(fOffset);
			// search for opening peer character next to the activation point
			for (i = 0; i < fPairs.length; i = i + 2) {
				// if (nextChar == fPairs[i]) {
				// fStartPos= fOffset;
				// pairIndex1= i;
				// } else
				if (prevChar == fPairs[i]) {
					fStartPos = fOffset - 1;
					pairIndex1 = i;
				}
			}
			// search for closing peer character next to the activation point
			for (i = 1; i < fPairs.length; i = i + 2) {
				if (prevChar == fPairs[i]) {
					fEndPos = fOffset - 1;
					pairIndex2 = i;
				}
				// else if (nextChar == fPairs[i]) {
				// fEndPos= fOffset;
				// pairIndex2= i;
				// }
			}
			if (fEndPos > -1) {
				fAnchor = ICharacterPairMatcher.RIGHT;
				fStartPos = searchForOpeningPeer(fEndPos, fPairs[pairIndex2 - 1], fPairs[pairIndex2], fDocument);
				if (fStartPos > -1) {
					return true;
				} else {
					fEndPos = -1;
				}
			} else if (fStartPos > -1) {
				fAnchor = ICharacterPairMatcher.LEFT;
				fEndPos = searchForClosingPeer(fStartPos, fPairs[pairIndex1], fPairs[pairIndex1 + 1], fDocument);
				if (fEndPos > -1) {
					return true;
				} else {
					fStartPos = -1;
				}
			}
		} catch (BadLocationException x) {
		} catch (IOException x) {
		}
		return false;
	}
	
	protected int searchForClosingPeer(int offset, int openingPeer, int closingPeer, IDocument document) throws IOException {
		fReader.configureForwardReader(document, offset + 1, document.getLength(), true, true);
		int stack = 1;
		int c = fReader.read();
		while (c != JsCodeReader.EOF) {
			if (c == openingPeer && c != closingPeer) {
				stack++;
			} else if (c == closingPeer) {
				stack--;
			}
			if (stack == 0) {
				return fReader.getOffset();
			}
			c = fReader.read();
		}
		return -1;
	}
	
	protected int searchForOpeningPeer(int offset, int openingPeer, int closingPeer, IDocument document) throws IOException {
		fReader.configureBackwardReader(document, offset, true, true);
		int stack = 1;
		int c = fReader.read();
		while (c != JsCodeReader.EOF) {
			if (c == closingPeer && c != openingPeer) {
				stack++;
			} else if (c == openingPeer) {
				stack--;
			}
			if (stack == 0) {
				return fReader.getOffset();
			}
			c = fReader.read();
		}
		return -1;
	}
}
