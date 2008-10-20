/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.preferences;

/**
 * Common preference keys used by HTML core
 * 
 * @plannedfor 1.0
 */
public class HTMLCorePreferenceNames {
	private HTMLCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * The default extension to use when none is specified in the New HTML
	 * File Wizard.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String DEFAULT_EXTENSION = "defaultExtension"; //$NON-NLS-1$

	/**
	 * The maximum width of a line before a line split is needed.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public static final String LINE_WIDTH = "lineWidth";//$NON-NLS-1$

	/**
	 * Indicates if all blanks lines should be cleared during formatting.
	 * Blank lines will be kept when false.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String CLEAR_ALL_BLANK_LINES = "clearAllBlankLines";//$NON-NLS-1$

	/**
	 * The number of #INDENTATION_CHAR for 1 indentation.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public static final String INDENTATION_SIZE = "indentationSize";//$NON-NLS-1$

	/**
	 * The character used for indentation.
	 * <p>
	 * Value is of type <code>String</code>.<br />
	 * Possible values: {TAB, SPACE}
	 * </p>
	 */
	public static final String INDENTATION_CHAR = "indentationChar";//$NON-NLS-1$

	/**
	 * Possible value for the preference #INDENTATION_CHAR. Indicates to use
	 * tab character when formatting.
	 * 
	 * @see #SPACE
	 * @see #INDENTATION_CHAR
	 */
	public static final String TAB = "tab"; //$NON-NLS-1$

	/**
	 * Possible value for the preference #INDENTATION_CHAR. Indicates to use
	 * space character when formatting.
	 * 
	 * @see #TAB
	 * @see #INDENTATION_CHAR
	 */
	public static final String SPACE = "space"; //$NON-NLS-1$

	/**
	 * Indicates if tags with multiple attributes should be formatted
	 * (splitting each attr on a new line).
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String SPLIT_MULTI_ATTRS = "splitMultiAttrs";//$NON-NLS-1$

	/**
	 * Indicates whether or not cleanup processor should format source.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String FORMAT_SOURCE = "formatSource";//$NON-NLS-1$

	/**
	 * Indicates whether or not empty elements should be compressed during
	 * cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String COMPRESS_EMPTY_ELEMENT_TAGS = "compressEmptyElementTags";//$NON-NLS-1$

	/**
	 * Indicates whether or not to insert required attributes during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String INSERT_REQUIRED_ATTRS = "insertRequiredAttrs";//$NON-NLS-1$

	/**
	 * Indicates whether or not to insert missing tags during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String INSERT_MISSING_TAGS = "insertMissingTags";//$NON-NLS-1$

	/**
	 * Indicates whether or not to quote all attribute values during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String QUOTE_ATTR_VALUES = "quoteAttrValues";//$NON-NLS-1$

	/**
	 * Indicates whether or not to convert all line delimiters during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String CONVERT_EOL_CODES = "convertEOLCodes";//$NON-NLS-1$

	/**
	 * Indicates the line delimiter to use during cleanup if converting line
	 * delimiters.
	 * <p>
	 * Value is of type <code>String</code>.<br />
	 * Possible values: {CR, CRLF, LF, NO_TRANSLATION}
	 * </p>
	 * 
	 */
	public static final String CLEANUP_EOL_CODE = "cleanupEOLCode";//$NON-NLS-1$

	/**
	 * Indicates case to use on all tag names during cleanup.
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {LOWER, UPPER, ASIS}
	 * </p>
	 */
	public static final String CLEANUP_TAG_NAME_CASE = "cleanupTagNameCase";//$NON-NLS-1$

	/**
	 * Indicates case to use on all attribute names during cleanup.
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {LOWER, UPPER, ASIS}
	 * </p>
	 */
	public static final String CLEANUP_ATTR_NAME_CASE = "cleanupAttrNameCase";//$NON-NLS-1$

	/**
	 * Preferred markup case for tag names in code generation
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {LOWER, UPPER}
	 * </p>
	 */
	public static final String TAG_NAME_CASE = "tagNameCase";//$NON-NLS-1$

	/**
	 * Preferred markup case for attribute names in code generation
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {LOWER, UPPER}
	 * </p>
	 */
	public static final String ATTR_NAME_CASE = "attrNameCase";//$NON-NLS-1$

	/**
	 * Possible value for the preference #TAG_NAME_CASE or #ATTR_NAME_CASE.
	 * Indicates to leave case as is.
	 * 
	 * @see #LOWER
	 * @see #UPPER
	 * @see #TAG_NAME_CASE
	 * @see #ATTR_NAME_CASE
	 */
	public static final int ASIS = 0;

	/**
	 * Possible value for the preference #TAG_NAME_CASE or #ATTR_NAME_CASE.
	 * Indicates to make name lowercase.
	 * 
	 * @see #ASIS
	 * @see #UPPER
	 * @see #TAG_NAME_CASE
	 * @see #ATTR_NAME_CASE
	 */
	public static final int LOWER = 1;

	/**
	 * Possible value for the preference #TAG_NAME_CASE or #ATTR_NAME_CASE.
	 * Indicates to make name uppercase.
	 * 
	 * @see #LOWER
	 * @see #ASIS
	 * @see #TAG_NAME_CASE
	 * @see #ATTR_NAME_CASE
	 */
	public static final int UPPER = 2;
	
	/**
	 * Validation severity for elements that are missing a start tag
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {IGNORE, ERROR, WARNING}
	 * </p>
	 */
	public static final String ELEM_MISSING_START = "elemMissingStart";//$NON-NLS-1$
	
	/**
	 * Validation severity for elements that are missing an end tag
	 * <p>
	 * Value is of type <code>Integer</code>.<br />
	 * Possible values: {IGNORE, ERROR, WARNING}
	 * </p>
	 */
	public static final String ELEM_MISSING_END = "elemMissingEnd";//$NON-NLS-1$
}
