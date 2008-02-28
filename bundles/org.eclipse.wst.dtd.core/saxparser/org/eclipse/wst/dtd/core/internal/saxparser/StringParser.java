/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

/**
 * Reader for processing/parsing xml string
 * 
 * @version
 */
public class StringParser {
	String fData = null;
	int fEndOffset;
	int fMostRecentChar;
	int fCurrentOffset;
	boolean fCalledCharPropInit = false;

	public StringParser(String data) {
		fData = data;
		fCurrentOffset = 0;
		fEndOffset = fData.length();
		fMostRecentChar = fEndOffset == 0 ? -1 : fData.charAt(0);
	}

	public String getString(int offset, int length) {
		if (length == 0)
			return ""; //$NON-NLS-1$
		return fData.substring(offset, offset + length);
	}

	public String getData() {
		return fData;
	}

	public String getRemainingString() {
		return fData.substring(getCurrentOffset());
	}

	public int getCurrentOffset() {
		return fCurrentOffset;
	}

	//
	//
	public int loadNextChar() {
		if (++fCurrentOffset >= fEndOffset) {
			fMostRecentChar = -1;
		}
		else {
			fMostRecentChar = fData.charAt(fCurrentOffset);
		}
		return fMostRecentChar;
	}
	
	public int loadPreviousChar() {
		if (--fCurrentOffset < 0) 
			fMostRecentChar = -1;
		else
			fMostRecentChar = fData.charAt(fCurrentOffset);
		return fMostRecentChar;
	}

	//
	//
	public boolean lookingAtChar(char chr, boolean skipPastChar) {
		int ch = fMostRecentChar;
		if (ch != chr) {
			return false;
		}
		if (skipPastChar) {
			if (++fCurrentOffset >= fEndOffset) {
				fMostRecentChar = -1;
			}
			else {
				fMostRecentChar = fData.charAt(fCurrentOffset);
			}
		}
		return true;
	}

	//
	//
	//
	public boolean lookingAtValidChar(boolean skipPastChar) {
		int ch = fMostRecentChar;
		if (ch < 0xD800) {
			if (ch < 0x20 && ch != 0x09 && ch != 0x0A && ch != 0x0D) {
				return false;
			}
			if (skipPastChar) {
				if (++fCurrentOffset >= fEndOffset) {
					fMostRecentChar = -1;
				}
				else {
					fMostRecentChar = fData.charAt(fCurrentOffset);
				}
			}
			return true;
		}
		if (ch > 0xFFFD) {
			return false;
		}
		if (ch < 0xDC00) {
			if (fCurrentOffset + 1 >= fEndOffset) {
				return false;
			}
			ch = fData.charAt(fCurrentOffset + 1);
			if (ch < 0xDC00 || ch >= 0xE000) {
				return false;
			}
			else if (!skipPastChar) {
				return true;
			}
			else {
				fCurrentOffset++;
			}
		}
		else if (ch < 0xE000) {
			return false;
		}
		if (skipPastChar) {
			if (++fCurrentOffset >= fEndOffset) {
				fMostRecentChar = -1;
			}
			else {
				fMostRecentChar = fData.charAt(fCurrentOffset);
			}
		}
		return true;
	}

	//
	//
	//
	public boolean lookingAtSpace(boolean skipPastChar) {
		int ch = fMostRecentChar;
		if (ch > 0x20)
			return false;
		if (ch == 0x20 || ch == 0x0A || ch == 0x0D || ch == 0x09) {
			if (skipPastChar) {
				loadNextChar();
			}
			return true;
		}
		return false;
	}

	//
	//
	//
	public void skipToChar(char chr, boolean skipPastChar) {
		//
		// REVISIT - this will skip invalid characters without reporting them.
		//
		int ch = fMostRecentChar;
		while (true) {
			if (ch == chr) {
				if (skipPastChar) {
					loadNextChar();
				}
				return;
			}
			if (ch == -1) {
				return;
			}
			ch = loadNextChar();
		}
	}
	
	/**
	 * skips to the last occurrence of the specified character.
	 * if <code>skipPastChar</code> is true, the parser is
	 * incremented past the last occurrence of the character.
	 * This method starts at the end of the character data, and
	 * moves backwards to find the last occurrence of the character
	 */
	public void skipToLastOfChar(char chr, boolean skipPastChar) {
		int ch = -1;
		
		// Move the cursor to the end offset to scan backwards.
		fCurrentOffset = fEndOffset;
		
		do {
			ch = loadPreviousChar();
			
			if(ch == -1)
				return;
			
			if (ch == chr) {
				if(skipPastChar)
					loadNextChar();
				return;
			}
			
		} while(true);
	}

	//
	//
	//
	public void skipPastSpaces() {
		int ch = fMostRecentChar;
		if (ch == -1) {
			// changeReaders().skipPastSpaces();
			return;
		}
		while (true) {
			if (ch > 0x20 || (ch != 0x20 && ch != 0x0A && ch != 0x09 && ch != 0x0D)) {
				fMostRecentChar = ch;
				return;
			}
			if (++fCurrentOffset >= fEndOffset) {
				return;
			}
			ch = fData.charAt(fCurrentOffset);
		}
	}

	//
	//
	//

	public void skipPastNameAndPEReference(char fastcheck) {
		int ch = fMostRecentChar;

		if (ch != '%') {
			if (ch < 0x80) {
				if (ch == -1 || XMLCharacterProperties.fgAsciiInitialNameChar[ch] == 0)
					return;
			}
			else {
				if (!fCalledCharPropInit) {
					XMLCharacterProperties.initCharFlags();
					fCalledCharPropInit = true;
				}
				if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_InitialNameCharFlag) == 0)
					return;
			}
		}
		while (true) {
			ch = loadNextChar();
			if (fastcheck == ch)
				return;
			if (ch == '%' || ch == ';') {
				continue;
			}

			if (ch < 0x80) {
				if (ch == -1 || XMLCharacterProperties.fgAsciiNameChar[ch] == 0)
					return;
			}
			else {
				if (!fCalledCharPropInit) {
					XMLCharacterProperties.initCharFlags();
					fCalledCharPropInit = true;
				}
				if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_NameCharFlag) == 0)
					return;
			}
		}
	}

	public void skipPastName(char fastcheck) {
		int ch = fMostRecentChar;
		// System.out.println("skippastName ch: " +ch);

		if (ch < 0x80) {
			if (ch == -1 || XMLCharacterProperties.fgAsciiInitialNameChar[ch] == 0)
				return;
		}
		else {
			if (!fCalledCharPropInit) {
				XMLCharacterProperties.initCharFlags();
				fCalledCharPropInit = true;
			}
			if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_InitialNameCharFlag) == 0)
				return;
		}

		while (true) {
			ch = loadNextChar();
			if (fastcheck == ch)
				return;
			if (ch < 0x80) {
				if (ch == -1 || XMLCharacterProperties.fgAsciiNameChar[ch] == 0)
					return;
			}
			else {
				if (!fCalledCharPropInit) {
					XMLCharacterProperties.initCharFlags();
					fCalledCharPropInit = true;
				}
				if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_NameCharFlag) == 0)
					return;
			}
		}
	}

	//
	//
	//

	public void skipPastNmtoken(char fastcheck) {
		int ch = fMostRecentChar;
		while (true) {
			if (fastcheck == ch)
				return;
			if (ch < 0x80) {
				if (ch == -1 || XMLCharacterProperties.fgAsciiNameChar[ch] == 0)
					return;
			}
			else {
				if (!fCalledCharPropInit) {
					XMLCharacterProperties.initCharFlags();
					fCalledCharPropInit = true;
				}
				if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_NameCharFlag) == 0)
					return;
			}
			ch = loadNextChar();
		}
	}

	public void skipPastNmtokenAndPEReference(char fastcheck) {
		int ch = fMostRecentChar;
		while (true) {
			if (fastcheck == ch)
				return;
			if (ch == '%' || ch == ';') {
				ch = loadNextChar();
				continue;
			}
			if (ch < 0x80) {
				if (ch == -1 || XMLCharacterProperties.fgAsciiNameChar[ch] == 0)
					return;
			}
			else {
				if (!fCalledCharPropInit) {
					XMLCharacterProperties.initCharFlags();
					fCalledCharPropInit = true;
				}
				if ((XMLCharacterProperties.fgCharFlags[ch] & XMLCharacterProperties.E_NameCharFlag) == 0)
					return;
			}
			ch = loadNextChar();
		}
	}

	//
	//
	//
	public boolean skippedString(char[] s) {
		int ch = fMostRecentChar;
		if (ch != s[0]) {
			return false;
		}
		if (fCurrentOffset + s.length > fEndOffset)
			return false;
		for (int i = 1; i < s.length; i++) {
			if (fData.charAt(fCurrentOffset + i) != s[i])
				return false;
		}
		fCurrentOffset += (s.length - 1);
		loadNextChar();
		return true;
	}

	//
	//
	//
	public int scanInvalidChar() throws Exception {
		int ch = fMostRecentChar;
		loadNextChar();
		return ch;
	}

	//
	//
	//
	/*
	 * public int scanCharRef(boolean hex) throws Exception { int ch =
	 * fMostRecentChar; if (ch == -1) // return
	 * changeReaders().scanCharRef(hex); return ch; int num = 0; if (hex) { if
	 * (ch > 'f' || XMLCharacterProperties.fgAsciiXDigitChar[ch] == 0) return
	 * XMLEntityHandler.CHARREF_RESULT_INVALID_CHAR; num = ch - (ch < 'A' ?
	 * '0' : (ch < 'a' ? 'A' : 'a') - 10); } else { if (ch < '0' || ch > '9')
	 * return XMLEntityHandler.CHARREF_RESULT_INVALID_CHAR; num = ch - '0'; }
	 * boolean toobig = false; while (true) { ch = loadNextChar(); if (ch ==
	 * -1) return XMLEntityHandler.CHARREF_RESULT_SEMICOLON_REQUIRED; if (hex) {
	 * if (ch > 'f' || XMLCharacterProperties.fgAsciiXDigitChar[ch] == 0)
	 * break; } else { if (ch < '0' || ch > '9') break; } if (hex) { int dig =
	 * ch - (ch < 'A' ? '0' : (ch < 'a' ? 'A' : 'a') - 10); num = (num << 4) +
	 * dig; } else { int dig = ch - '0'; num = (num * 10) + dig; } if (num >
	 * 0x10FFFF) { toobig = true; num = 0; } } if (ch != ';') return
	 * XMLEntityHandler.CHARREF_RESULT_SEMICOLON_REQUIRED; loadNextChar(); if
	 * (toobig) return XMLEntityHandler.CHARREF_RESULT_OUT_OF_RANGE; return
	 * num; }
	 */
	//
	//
	//
	/*
	 * public int scanStringLiteral() throws Exception { boolean single; if
	 * (!(single = lookingAtChar('\'', true)) && !lookingAtChar('\"', true)) {
	 * return XMLEntityHandler.STRINGLIT_RESULT_QUOTE_REQUIRED; } int offset =
	 * fCurrentOffset; char qchar = single ? '\'' : '\"'; while
	 * (!lookingAtChar(qchar, false)) { if (!lookingAtValidChar(true)) {
	 * return XMLEntityHandler.STRINGLIT_RESULT_INVALID_CHAR; } } // int
	 * stringIndex = addString(offset, fCurrentOffset - offset); int
	 * stringIndex = addString(offset, fCurrentOffset - offset);
	 * lookingAtChar(qchar, true); // move past qchar return stringIndex; }
	 */

	//
	//
	public boolean scanAttValue(char qchar) {
		boolean result = true;
		while (true) {
			if (lookingAtChar(qchar, false)) {
				break;
			}
			if (lookingAtChar(' ', true)) {
				continue;
			}
			if (!lookingAtValidChar(true)) {
				result = false;
			}
		}
		lookingAtChar(qchar, true);
		return result;
	}

	//
	// [9] EntityValue ::= '"' ([^%&"] | PEReference | Reference)* '"'
	// | "'" ([^%&'] | PEReference | Reference)* "'"
	//
	// The values in the following table are defined as:
	//
	// 0 - not special
	// 1 - quote character
	// 2 - reference
	// 3 - peref
	// 4 - invalid
	//
	public static final byte fgAsciiEntityValueChar[] = {4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 4, 4, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 1, 0, 0, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, // '\"',
																																																			// '%',
																																																			// '&',
																																																			// '\''
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	/*
	 * public int scanEntityValue(int qchar, boolean createString) throws
	 * Exception { int offset = fCurrentOffset; int ch = fMostRecentChar;
	 * while (true) { if (ch == -1) { changeReaders(); // do not call next
	 * reader, our caller may need to change the parameters return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_END_OF_INPUT; } if (ch < 0x80) {
	 * switch (fgAsciiEntityValueChar[ch]) { case 1: // quote char if (ch ==
	 * qchar) { if (!createString) return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_FINISHED; int length =
	 * fCurrentOffset - offset; int result = length == 0 ?
	 * StringPool.EMPTY_STRING : addString(offset, length); loadNextChar();
	 * return result; } // the other quote character is not special // fall
	 * through case 0: // non-special char if (++fCurrentOffset >= fEndOffset) {
	 * if (oweTrailingSpace) { oweTrailingSpace = false; ch = fMostRecentChar = ' '; }
	 * else { ch = fMostRecentChar = -1; } } else { ch = fMostRecentChar =
	 * fData.charAt(fCurrentOffset); } continue; case 2: // reference return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_REFERENCE; case 3: // peref return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_PEREF; case 4: // invalid return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_INVALID_CHAR; } } else if (ch <
	 * 0xD800) { ch = loadNextChar(); } else if (ch >= 0xE000 && (ch <= 0xFFFD ||
	 * (ch >= 0x10000 && ch <= 0x10FFFF))) { // // REVISIT - needs more code
	 * to check surrogates. // ch = loadNextChar(); } else { return
	 * XMLEntityHandler.ENTITYVALUE_RESULT_INVALID_CHAR; } } }
	 */


}
