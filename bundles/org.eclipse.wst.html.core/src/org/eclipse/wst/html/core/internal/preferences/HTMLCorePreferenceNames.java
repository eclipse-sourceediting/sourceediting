/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
	 * Indicates if end brackets of start tags should be placed on a new line
	 * if the start tag spans more than one line.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String ALIGN_END_BRACKET = "alignEndBracket";//$NON-NLS-1$

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
	 * Preferred elements to be considered as inline for the purposes of formatting
	 * <p>
	 * Value is a comma-separated list of element names
	 * </p>
	 */
	public static final String INLINE_ELEMENTS = "inlineElements"; //$NON-NLS-1$

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
	
	public static final String USE_PROJECT_SETTINGS = "use-project-settings";//$NON-NLS-1$
	
	public static final String ATTRIBUTE_UNDEFINED_NAME = "attrUndefName";//$NON-NLS-1$
	public static final String ATTRIBUTE_UNDEFINED_VALUE = "attrUndefValue";//$NON-NLS-1$
	public static final String ATTRIBUTE_NAME_MISMATCH = "attrNameMismatch";//$NON-NLS-1$
	public static final String ATTRIBUTE_INVALID_NAME = "attrInvalidName";//$NON-NLS-1$
	public static final String ATTRIBUTE_INVALID_VALUE = "attrInvalidValue";//$NON-NLS-1$
	public static final String ATTRIBUTE_DUPLICATE = "attrDuplicate";//$NON-NLS-1$
	public static final String ATTRIBUTE_VALUE_MISMATCH = "attrValueMismatch";//$NON-NLS-1$
	public static final String ATTRIBUTE_VALUE_UNCLOSED = "attrValueUnclosed";//$NON-NLS-1$
	public static final String ATTRIBUTE_VALUE_RESOURCE_NOT_FOUND = "resourceNotFound";//$NON-NLS-1$
	public static final String ATTRIBUTE_OBSOLETE_NAME = "attrObsoleteName";//$NON-NLS-1$
	public static final String ATTRIBUTE_VALUE_EQUALS_MISSING = "attrValueEqualsMissing";//$NON-NLS-1$
	
	
	public static final String ELEM_UNKNOWN_NAME = "elemUnknownName";//$NON-NLS-1$
	public static final String ELEM_INVALID_NAME = "elemInvalidName";//$NON-NLS-1$
	public static final String ELEM_START_INVALID_CASE = "elemStartInvalidCase";//$NON-NLS-1$
	public static final String ELEM_END_INVALID_CASE = "elemEndInvalidCase";//$NON-NLS-1$
	public static final String ELEM_MISSING_START = "elemMissingStart";//$NON-NLS-1$
	public static final String ELEM_MISSING_END = "elemMissingEnd";//$NON-NLS-1$
	public static final String ELEM_UNNECESSARY_END = "elemUnnecessaryEnd";//$NON-NLS-1$
	public static final String ELEM_INVALID_DIRECTIVE = "elemInvalidDirective";//$NON-NLS-1$
	public static final String ELEM_INVALID_CONTENT = "elemInvalidContent";//$NON-NLS-1$
	public static final String ELEM_DUPLICATE = "elemDuplicate";//$NON-NLS-1$
	public static final String ELEM_COEXISTENCE = "elemCoexistence";//$NON-NLS-1$
	public static final String ELEM_UNCLOSED_START_TAG = "elemUnclosedStartTag";//$NON-NLS-1$
	public static final String ELEM_UNCLOSED_END_TAG = "elemUnclosedEndTag";//$NON-NLS-1$
	public static final String ELEM_INVALID_EMPTY_TAG = "elemInvalidEmptyTag";//$NON-NLS-1$
	public static final String ELEM_OBSOLETE_NAME = "elemObsoleteName";//$NON-NLS-1$
	public static final String ELEM_INVALID_TEXT = "elemInvalidText";//$NON-NLS-1$

	public static final String DOC_DUPLICATE = "docDuplicateTag";//$NON-NLS-1$
	public static final String DOC_INVALID_CONTENT = "docInvalidContent";//$NON-NLS-1$
	public static final String DOC_DOCTYPE_UNCLOSED = "docDoctypeUnclosed";//$NON-NLS-1$
	
	public static final String TEXT_INVALID_CONTENT = "docInvalidContent";//$NON-NLS-1$
	public static final String TEXT_INVALID_CHAR = "docInvalidChar";//$NON-NLS-1$
	
	public static final String COMMENT_INVALID_CONTENT = "commentInvalidContent";//$NON-NLS-1$
	public static final String COMMENT_UNCLOSED = "commentUnclosed";//$NON-NLS-1$
	
	public static final String CDATA_INVALID_CONTENT = "cdataInvalidContent";//$NON-NLS-1$
	public static final String CDATA_UNCLOSED = "cdataUnclosed";//$NON-NLS-1$
	
	public static final String PI_INVALID_CONTENT = "piInvalidContent";//$NON-NLS-1$
	public static final String PI_UNCLOSED = "piUnclosed";//$NON-NLS-1$
	
	public static final String REF_INVALID_CONTENT = "refInvalidContent";//$NON-NLS-1$
	public static final String REF_UNDEFINED = "piUndefined";//$NON-NLS-1$
}
