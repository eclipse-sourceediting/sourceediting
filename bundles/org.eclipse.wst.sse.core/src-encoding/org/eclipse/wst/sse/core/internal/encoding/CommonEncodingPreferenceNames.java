/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding;

/**
 * Common preference keys used to specify encoding and end of line.
 */
public class CommonEncodingPreferenceNames {
	
	private CommonEncodingPreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * Constant to be used when referring to CR/MAC line delimiter
	 * @deprecated - no longer used
	 */
	public static final String CR = "EOL_Mac"; //$NON-NLS-1$
	/**
	 * Constant to be used when referring to CRLF/WINDOWS line delimiter
	 * @deprecated - no longer used
	 */
	public static final String CRLF = "EOL_Windows"; //$NON-NLS-1$
	/**
	 * The end-of-line character(s) to use.
	 * @deprecated - no longer used
	 */
	public static final String END_OF_LINE_CODE = "endOfLineCode";//$NON-NLS-1$
	/**
	 * The character code to use when reading a file.
	 */
	public static final String INPUT_CODESET = "inputCodeset";//$NON-NLS-1$

	/**
	 * Constant to be used when referring to LF/UNIX line delimiter
	 * @deprecated - no longer used
	 */
	public static final String LF = "EOL_Unix"; //$NON-NLS-1$
	/**
	 * Constant to be used when referring to No translation of line delimiters
	 * @deprecated - no longer used
	 */
	public static final String NO_TRANSLATION = ""; //$NON-NLS-1$
	/**
	 * The character code to use when writing a file.
	 */
	public static final String OUTPUT_CODESET = "outputCodeset";//$NON-NLS-1$

	/**
	 * String representation of CR/MAC line delimiter
	 * @deprecated - no longer used
	 */
	public static final String STRING_CR = "\r";//$NON-NLS-1$

	/**
	 * String representation of CRLF/WINDOWS line delimiter
	 * @deprecated - no longer used
	 */
	public static final String STRING_CRLF = "\r\n";//$NON-NLS-1$

	/**
	 * String representation of LF/UNIX line delimiter
	 * @deprecated - no longer used
	 */
	public static final String STRING_LF = "\n";//$NON-NLS-1$

	/**
	 * String Use 3 byte BOM (Byte Order Mark) when saving UTF-8 encoded files
	 */
	public static final String USE_3BYTE_BOM_WITH_UTF8 = "Use3ByteBOMWithUTF8"; //$NON-NLS-1$
}
