/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.contenttype;

/**
 * 
 * This is ported from PageDesigner's hpbcom/Kanji.cpp's
 * Kanji::guess_kanji_code(),
 *  
 */
public class EncodingGuesser {
	private static final int ASCII = 0; // ASCII
	// ISO-2022-JP
	private static final int ASCII_IN = 8; // This is after ISO2022's change
	// Shift-JIS
	private static final int EUC_HALFKANA = 6; // This is Half Kana in EUC-JP
	private static final int EUC_JP = 3; // This is EUC-JP
	private static final int ISO2022_JP = 4; // This is ISO-2022-JP
	private static final int JIS_HALFKANA = 7; // THis is Half Kana in
	private static final byte KT_EUC1 = 0x40;
	private static final byte KT_EUC2 = (byte) 0x80;
	// ASCII
	private static final byte KT_JIN = 0x01;
	private static final byte KT_JOUT = 0x02;
	//	private static final byte KT_ESC = 0x04;
	//	private static final byte KT_JIS = 0x08;
	private static final byte KT_SFT1 = 0x10;
	private static final byte KT_SFT2 = 0x20;
	private static final byte ktype[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* 00 */
	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* 10 */
	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x08, 0x08, 0x09, 0x08, 0x08, 0x08, /* !"#$%&' *//* " */
	0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, /* ()*+,-./ */
	0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, /* 01234567 */
	0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, /* 89:; <=>? */
	0x29, 0x28, 0x2b, 0x28, 0x28, 0x28, 0x28, 0x28, /* @ABCDEFG */
	0x2a, 0x28, 0x2a, 0x28, 0x28, 0x28, 0x28, 0x28, /* HIJKLMNO */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, /* PQRSTUVW */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, /* XYZ[\]^_ */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, /* abcdefg */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, /* hijklmno */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, /* pqrstuvw */
	0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x00, /* xyz{|}~ */
	0x20, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, /* 80 */
	0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, /* 90 */
	0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x20, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, /* A0 */
	(byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, /* B0 */
	(byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, /* C0 */
	(byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, /* D0 */
	(byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, /* E0 */
	(byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, /* F0 */
	(byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xc0, (byte) 0xc0, 0x00,};
	//	private static final int ISO8859_1 = 1; // ISO-1
	private static final int SHIFT_JIS = 2; // This is Shift-JIS
	private static final int SJIS_HALFKANA = 5; // This is Half Kana in

	/**
	 * Currently, only Japanese encodings are supported.
	 */
	private static final int UNKNOWN = -1; // Unknown

	/**
	 * @return java.lang.String
	 * @param code
	 *            int
	 * 
	 * Convert private int to IANA Encoding name.
	 */
	private static String convertToIANAEncodingName(int code) {
		String encoding = null;

		switch (code) {
			case SHIFT_JIS :
			case SJIS_HALFKANA :
				encoding = "Shift_JIS";//$NON-NLS-1$
				break;
			case EUC_JP :
			case EUC_HALFKANA :
				encoding = "EUC-JP";//$NON-NLS-1$
				break;
			case ISO2022_JP :
			case JIS_HALFKANA :
				encoding = "ISO-2022-JP";//$NON-NLS-1$
			default :
				break;
		}

		return encoding;
	}

	public static boolean canGuess() {
		// Currently, only Japanese is supported.
		String system_ctype = java.util.Locale.getDefault().getLanguage();
		String jp_ctype = java.util.Locale.JAPANESE.getLanguage();
		return system_ctype.compareTo(jp_ctype) == 0;
	}

	/**
	 * Return guessed Java Encoding name target: bytes to be inspected length:
	 * length of target
	 */
	public static String guessEncoding(byte[] target, int length) {
		int code = UNKNOWN;

		
		if (canGuess()) {
			// Ok, I'm under ja_JP.
			code = ASCII;
			int pos = 0;
			while ((code == ASCII) && (length > 0)) {
				int ch1 = target[pos];
				ch1 = ch1 & 0x000000FF;
				int ch2 = (length >= 2) ? target[pos + 1] : 0;
				ch2 = ch2 & 0x000000FF;
				int ch3 = (length >= 3) ? target[pos + 2] : 0;
				ch3 = ch3 & 0x000000FF;
				code = guessJapaneseKanjiCode(ch1, ch2, ch3, 0);
				pos++;
				length--;
			}
			switch (code) {
				case ISO2022_JP :
				case JIS_HALFKANA :
					code = ISO2022_JP;
					break;
				case EUC_JP :
					code = EUC_JP;
					break;
				default :
					code = SHIFT_JIS;
			}
		}
		return (convertToIANAEncodingName(code));
	}

	/**
	 * Guess the encoding. halfkana_flag = 0x01 ( detect SJIS half kana )
	 * halfkana_flag = 0x02 ( detect EUC half kana )
	 */
	private static int guessJapaneseKanjiCode(int ch1, int ch2, int ch3, int halfkana_flag) {
		boolean sjis_hankaku_flag = ((halfkana_flag & 0x01) != 0) ? true : false;
		boolean euc_hankaku_flag = ((halfkana_flag & 0x02) != 0) ? true : false;

		if (ch1 == 0)
			return UNKNOWN;
		if (sjis_hankaku_flag && ch1 >= 0xa1 && ch1 <= 0xdf)
			return SJIS_HALFKANA;
		else if (euc_hankaku_flag && ch1 == 0x8e && ch2 >= 0xa1 && ch2 <= 0xdf)
			return EUC_HALFKANA;
		else if (((ktype[ch1] & KT_SFT1) != 0) && ((ktype[ch2] & KT_SFT2) != 0))
			return SHIFT_JIS;
		else if (((ktype[ch1] & KT_EUC1) != 0) && ((ktype[ch2] & KT_EUC2) != 0))
			return EUC_JP;
		else if (ch1 == 0x1b && ((ktype[ch2] & KT_JIN) != 0))
			return ISO2022_JP;
		else if (ch1 >= 0xa1 && ch1 <= 0xdf)
			return SJIS_HALFKANA;
		else if (ch1 == 0x1b && ch2 == 0x28/* '(' */&& ch3 == 0x49/* 'I' */)
			return JIS_HALFKANA;
		else if (ch1 == 0x1b && ch2 == 0x28/* '(' */&& ((ktype[ch3] & KT_JOUT) != 0))
			return ASCII_IN;

		return ASCII;
	}

	public EncodingGuesser() {
		super();
	}
}
