/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding.util;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * SupportedJavaEncoding is a utility class to provide IANA tag to java
 * encoding identifier mappings. It also contains the human readable
 * description for the IANA tag equivalent to be used in user interfaces. The
 * description is NL aware based on locale. The data is populated via the
 * charset.properties file only once, based on static initialization of the
 * hashtables.
 * 
 * The IANA tags are based on reference information found at the
 * http://www.iana.org site. Specifically see
 * http://www.iana.org/assignments/character-sets
 * 
 * @deprecated - hard deprecated. will be removed within days. see
 *             CommonCharsetNames may need a similar class to allow
 *             "overriding" of default mappings, but JRE 1.4 mappings seem to
 *             suffice.
 */
public class SupportedJavaEncoding {


	// Pair of supported alias IANA/real IANA names
	private static final String aliases[][] = {
	// the key (1st param) is the alias IANA name, names are case-insensitive
				// the value (2nd param) is the real IANA name, names are
				// case-insensitive
				// Japanese
				{"X-EUC-JP", "EUC-JP"}, // EUC encoding,
				// Japanese//$NON-NLS-2$//$NON-NLS-1$
				{"X-SJIS", "SHIFT_JIS"}, // Shift-JIS,
				// Japanese//$NON-NLS-2$//$NON-NLS-1$
				{"ANSI_X3.4-1968", "US-ASCII"}}; //$NON-NLS-1$ //$NON-NLS-2$
	private static ArrayList ianaEncodings = null, javaEncodings = null;

	// The following is no longer used. Remove eventually

	// Pair of supported IANA/Java Encoding names
	// As for IANA name, see http://www.iana.org/assignments/character-sets
	/**
	 * @deprecated - left here temporarily for documentation
	 */
	static final String oldEncodings[][] = {
	// the key (1st param) is the XML encoding name, names are
				// case-insensitive
				// the value (2nd param) is the Java encoding name, names are
				// case-sensitive
				// One XML encoding names can be assigned to the one Java
				// Encoding, and
				// everything else needs to be alias. See
				// getIANAEncodingName().
				{"US-ASCII", "ASCII"}, // US ASCII//$NON-NLS-2$//$NON-NLS-1$
				// changed to Cp1252 for MS compatibility
				//{"ISO-8859-1", "ISO8859_1"} // ISO Latin-1
				{"ISO-8859-1", "Cp1252"}, // ISO
				// Latin-1//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-2", "ISO8859_2"}, // Central/East European (Slavic:
				// Czech, Croat, German,
				// Hungarian, Polish, Romanian,
				// Slovak,
				// Slovenian)//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-3", "ISO8859_3"}, // Southern European (Esperanto,
				// Galician, Maltese,
				// Turkish)//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-4", "ISO8859_4"}, // Cyrillic (Estonian, Latvian,
				// Lithuanian)//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-5", "ISO8859_5"}, // Cyrillic (Bulgarian,
				// Byelorussian, Macedonian,
				// Serbian,
				// Ukrainian)//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-6", "ISO8859_6"}, // Arabic(Logical)//$NON-NLS-2$//$NON-NLS-1$
				{"WINDOWS-1256", "Cp1256"}, // Arabic//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-7", "ISO8859_7"}, // Greek//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-8-I", "ISO8859_8"}, // Hebrew(Logical)//$NON-NLS-2$//$NON-NLS-1$
				// The above is tricky. but in the code conversion point of
				// view,
				// ISO-8 and ISO-8-I are same.
				{"WINDOWS-1255", "Cp1255"}, // Hebrew//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-8859-9", "ISO8859_9"}, // Turkish//$NON-NLS-2$//$NON-NLS-1$
				// Japanese
				{"EUC-JP", "EUC_JP"}, // EUC encoding,
				// Japanese//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-2022-JP", "ISO2022JP"}, // ISO 2022,
				// Japanese//$NON-NLS-2$//$NON-NLS-1$
				// changed for MS compatibility
				//{"SHIFT_JIS", "SJIS"}, // Shift-JIS, Japanese
				{"SHIFT_JIS", "MS932"}, // Shift-JIS,
				// Japanese//$NON-NLS-2$//$NON-NLS-1$
				// Korean
				// changed for MS compatibility
				//{"EUC-KR", "EUC_KR"}, // EUC encoding, Korean
				{"EUC-KR", "MS949"}, // EUC encoding,
				// Korean//$NON-NLS-2$//$NON-NLS-1$
				{"ISO-2022-KR", "ISO2022KR"}, // ISO 2022,
				// Korean//$NON-NLS-2$//$NON-NLS-1$
				// Traditional Chinese
				// changed for MS compatibility
				//{"BIG5", "Big5"}, // Big5, Traditional Chinese
				{"BIG5", "MS950"}, // Big5, Traditional
				// Chinese//$NON-NLS-2$//$NON-NLS-1$
				// Simplified Chinese(Use IANA MIME preferred name)
				//{"GB_2312-80", "GBK"}, // GBK, Simplified Chinese
				{"GB2312", "MS936"}, // GBK, Simplified
				// Chinese//$NON-NLS-2$//$NON-NLS-1$
				{"GB18030", "GB18030"}, // GB18030, new Chinese encoding
				// //$NON-NLS-1$ //$NON-NLS-2$
				// Thai
				{"TIS-620", "TIS620"}, // Thai. Thai Industrial Standards
				// Institute(TISI)//$NON-NLS-2$//$NON-NLS-1$
				{"WINDOWS-874", "MS874"}, // Microsoft
				// Thai//$NON-NLS-2$//$NON-NLS-1$
				// Unicode
				{"UTF-8", "UTF8"}, // ISO 10646/Unicode, one-byte
				// encoding//$NON-NLS-2$//$NON-NLS-1$
				{"UTF-16", "UnicodeBig"}, // ISO 10646/Unicode, two-byte
				// encoding//$NON-NLS-2$//$NON-NLS-1$
				{"UTF-16BE", "UnicodeBig"}, // Unicode
				// BigEndian//$NON-NLS-2$//$NON-NLS-1$
				{"UTF-16LE", "UnicodeLittle"} // Unicode
	// LittleEndian//$NON-NLS-2$//$NON-NLS-1$
	};
	private static Hashtable supportedAliasNames = null;
	private static Hashtable supportedEncodingDisplayNames = null;
	private static Hashtable supportedEncodingNames = null;
	private static Hashtable supportedIANAEncodingNames = null;

	/**
	 * SupportedJavaEncoding constructor comment.
	 */
	public SupportedJavaEncoding() {
		super();
		initHashTables();
		initSupportedAliasNames();
	}

	/**
	 * Returns display (translated) string for IANA encoding name
	 * 
	 * @param String
	 *            IANA encoding name
	 * @return String translated encoding name from CharsetResourceHandler
	 */
	public String getDisplayString(String name) {
		if (name == null)
			return null;

		return (String) supportedEncodingDisplayNames.get(name);
	}

	/**
	 * @return java.lang.String
	 * @param iana
	 *            java.lang.String
	 * 
	 * Convert Java Converter name to IANA encoding Name.
	 */
	public String getIANAEncodingName(String javaenc) {
		if (javaenc != null) {
			return ((String) supportedIANAEncodingNames.get(javaenc.toUpperCase()));
		}
		return null;
	}

	/**
	 * @return java.lang.String
	 * @param iana
	 *            java.lang.String
	 * 
	 * Convert IANA encoding name to Java Converter Name.
	 */
	public String getJavaConverterName(String iana) {
		String iana_name = getUniquefromAlias(iana); // to see if iana is an
		// alias
		if (iana_name != null) {
			return ((String) supportedEncodingNames.get(iana_name.toUpperCase()));
		}
		return null;
	}

	/**
	 * Returns list of all supported IANA encodings
	 * 
	 * @return String[]
	 */
	public String[] getSupportedIANAEncodings() {
		String[] iana = new String[ianaEncodings.size()];
		ianaEncodings.toArray(iana);
		return iana;
	}

	/**
	 * Returns list of all supported Java encodings
	 * 
	 * @return String[]
	 */
	public String[] getSupportedJavaEncodings() {
		String[] java = new String[javaEncodings.size()];
		javaEncodings.toArray(java);
		return java;
	}

	/**
	 * @return java.lang.String unique IANA name
	 * @param java.lang.String
	 *            possibly alias IANA name (ex: x-..)
	 */
	public String getUniquefromAlias(String string) {
		String real = null;
		if (string != null) {
			// convert alias IANA(x-...) to 'real' IANA name
			real = (String) supportedAliasNames.get(string.toUpperCase());
		}
		if (real != null)
			return real;
		return string;
	}

	private void initHashTables() {
		if (supportedEncodingNames == null) {
			// Initialize hash table for encoding table
			supportedEncodingNames = new Hashtable();
			supportedIANAEncodingNames = new Hashtable();
			supportedEncodingDisplayNames = new Hashtable();
			ianaEncodings = new ArrayList();
			javaEncodings = new ArrayList();

			String totalNumString = CharsetResourceHandler.getString("totalnumber");//$NON-NLS-1$
			int totalNum = 0;
			if (totalNumString.length() != 0) {
				totalNum = Integer.valueOf(totalNumString).intValue();
			}

			for (int i = 0; i < totalNum; i++) {
				String enc = CharsetResourceHandler.getString("codeset." + i + ".java");//$NON-NLS-2$//$NON-NLS-1$
				String iana = CharsetResourceHandler.getString("codeset." + i + ".iana");//$NON-NLS-2$//$NON-NLS-1$
				String displayName = CharsetResourceHandler.getString("codeset." + i + ".label");//$NON-NLS-2$//$NON-NLS-1$

				ianaEncodings.add(iana);
				supportedEncodingNames.put(iana, enc);
				supportedEncodingDisplayNames.put(iana, displayName);

				// if ianaenc == UTF-16BE, skip this. Dirty ?
				// agreeed. but...
				if (iana.compareToIgnoreCase("UTF-16BE") != 0)//$NON-NLS-1$
				{
					// note that the same java encoding can be used my
					// multiple iana tags (eg, aliases or codepages that
					// have the same codepoints) thus we only add the first
					// one as that is the most popular
					if (!supportedIANAEncodingNames.containsKey(enc.toUpperCase())) {
						supportedIANAEncodingNames.put(enc.toUpperCase(), iana);
						javaEncodings.add(enc);
					}
				}
			}
		}
	}

	/**
	 */
	private void initSupportedAliasNames() {
		if (supportedAliasNames == null) {
			supportedAliasNames = new Hashtable();
			int langs = aliases.length;
			for (int i = 0; i < langs; i++) {
				String aliasenc = aliases[i][0];
				String realenc = aliases[i][1];
				supportedAliasNames.put(aliasenc, realenc);
			}
		}
	}

}
