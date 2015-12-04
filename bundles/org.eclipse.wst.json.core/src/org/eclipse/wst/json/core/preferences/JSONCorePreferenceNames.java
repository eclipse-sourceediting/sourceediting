/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames
 *                                           modified in order to process JSON Objects.                                     
 *******************************************************************************/
package org.eclipse.wst.json.core.preferences;

/**
 * JSON core preference keys.
 * 
 */
public class JSONCorePreferenceNames {

	private JSONCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * Indicates whether or not Syntax Validator should run as part of JSON
	 * Validation.
	 * <p>
	 * Value is of type <code>boolean</code>.<br />
	 * Possible values: {TRUE, FALSE}
	 * </p>
	 */
	public static final String SYNTAX_VALIDATION = "syntaxValidation"; //$NON-NLS-1$

	/**
	 * Indicates whether or not a message should be produced when validating a
	 * file that specifies not grammar.
	 * <p>
	 * Value is of type <code>integer</code>.<br />
	 * Possible values: {0, 1, 2} (none, warning, error)
	 * </p>
	 */
	public static final String INDICATE_NO_GRAMMAR = "indicateNoGrammar";//$NON-NLS-1$

	public static final String USE_PROJECT_SETTINGS = "use-project-settings";//$NON-NLS-1$

	public static final String MISSING_START_OBJECT = "missingStartObject"; //$NON-NLS-1$
	public static final String MISSING_END_OBJECT = "missingEndObject"; //$NON-NLS-1$
	public static final String MISSING_START_ARRAY = "missingStartArray"; //$NON-NLS-1$
	public static final String MISSING_END_ARRAY = "missingEndArray";//$NON-NLS-1$

	public static final String MISSING_BRACKET = "missingBracket";

	// TODO : clean the following preference names...

	public static final String CASE_IDENTIFIER = "identifierCase"; //$NON-NLS-1$
	public static final String CASE_SELECTOR = "selectorCase"; //$NON-NLS-1$
	public static final String CASE_PROPERTY_NAME = "propNameCase"; //$NON-NLS-1$
	public static final String CASE_PROPERTY_VALUE = "propValueCase"; //$NON-NLS-1$
	public static final String FORMAT_BETWEEN_VALUE = "betweenValue"; //$NON-NLS-1$
	public static final String FORMAT_PROP_POST_DELIM = "postDelim"; //$NON-NLS-1$
	public static final String FORMAT_PROP_PRE_DELIM = "preDelim"; //$NON-NLS-1$
	public static final String FORMAT_QUOTE = "quote"; //$NON-NLS-1$
	public static final String FORMAT_QUOTE_IN_URI = "quoteInURI"; //$NON-NLS-1$
	public static final String FORMAT_SPACE_BETWEEN_SELECTORS = "spaceBetweenSelectors"; //$NON-NLS-1$
	public static final String WRAPPING_NEWLINE_ON_OPEN_BRACE = "newLineOnOpenBrace"; //$NON-NLS-1$
	public static final String WRAPPING_ONE_PER_LINE = "onePropertyPerLine"; //$NON-NLS-1$
	public static final String WRAPPING_PROHIBIT_WRAP_ON_ATTR = "prohibitWrapOnAttr"; //$NON-NLS-1$

	/**
	 * The default extension to use when none is specified in the New CSS File
	 * Wizard.
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
	 * Indicates if all blanks lines should be cleared during formatting. Blank
	 * lines will be kept when false.
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
	 * Possible value for the preference #INDENTATION_CHAR. Indicates to use tab
	 * character when formatting.
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
	 * Indicates whether or not to quote all attribute values during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String QUOTE_ATTR_VALUES = "quoteAttrValues";//$NON-NLS-1$

	/**
	 * Indicates whether or not cleanup processor should format source.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String FORMAT_SOURCE = "formatSource";//$NON-NLS-1$

	/**
	 * Possible value for the case preferences Indicates to leave case as is.
	 * 
	 * @see #LOWER
	 * @see #UPPER
	 */
	public static final int ASIS = 0;

	/**
	 * Possible value for the case preferences Indicates to make name lowercase.
	 * 
	 * @see #ASIS
	 * @see #UPPER
	 */
	public static final int LOWER = 1;

	/**
	 * Possible value for the case preferences Indicates to make name uppercase.
	 * 
	 * @see #LOWER
	 * @see #ASIS
	 */
	public static final int UPPER = 2;

}
