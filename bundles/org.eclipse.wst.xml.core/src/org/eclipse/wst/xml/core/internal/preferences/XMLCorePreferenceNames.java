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
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.preferences;


/**
 * Common preference keys used by XML core
 * 
 * @plannedfor 1.0
 */
public class XMLCorePreferenceNames {
	private XMLCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * The default extension to use when none is specified in the New File
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
	 * Indicates whether or not to insert missing XML declarations during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>
	 * </p>
	 */
	public static final String FIX_XML_DECLARATION = "fixXMLDeclaration"; //$NON-NLS-1$

	/**
	 * Indicates whether or not to convert all line delimiters during cleanup.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * @deprecated - no longer used
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
	 * Indicates whether or not a warning should be produced when validating a
	 * file that specifies not grammar.
	 * <p>
	 * Value is of type <code>boolean</code>.<br />
	 * Possible values: {TRUE, FALSE}
	 * </p>
	 * @deprecated
	 */
	public static final String WARN_NO_GRAMMAR = "warnNoGrammar";//$NON-NLS-1$
	
	/**
	 * Indicates whether or not a message should be produced when validating a
	 * file that specifies not grammar.
	 * <p>
	 * Value is of type <code>integer</code>.<br />
	 * Possible values: {0, 1, 2} (none, warning, error)
	 * </p>
	 */
	public static final String INDICATE_NO_GRAMMAR = "indicateNoGrammar";//$NON-NLS-1$

	/**
	 * Indicates whether or not a message should be produced when validating a file that does not contain
	 * a document element
	 * <p>
	 * Value is of type <code>integer</code>.<br/>
	 * Possible values: {-1, 1, 2} (none, warning, error)
	 * </p>
	 */
	public static final String INDICATE_NO_DOCUMENT_ELEMENT = "indiciateNoDocumentElement"; //$NON-NLS-1$

 	/**
	 * Indicates whether or not xincludes should be processed before
	 * validation.
	 * <p>
	 * Value is of type <code>boolean</code>.<br />
	 * Possible values: {TRUE, FALSE}
	 * </p>
	 * 
	 */
	public static final String USE_XINCLUDE = "xinclude";//$NON-NLS-1$

	/**
	 * Indicates if all whitespace in tags with CDATA content should be
	 * preserved.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String PRESERVE_CDATACONTENT = "preserveCDATAContent";//$NON-NLS-1$

	/**
	 * Indicates if end brackets of start tags should be placed on a new line
	 * if the start tag spans more than one line.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String ALIGN_END_BRACKET = "alignEndBracket";//$NON-NLS-1$
	
	/**
	 * Indicates if an empty close tag should have a space inserted before
	 * closing.
	 * <p>
	 * Value is of type <code>Boolean</code>
	 * </p>
	 */
	public static final String SPACE_BEFORE_EMPTY_CLOSE_TAG = "spaceBeforeEmptyCloseTag";//$NON-NLS-1$
	
    /**
     * Indicates whether or not all schema locations for XSD should be honoured
     * during XSD validation of XML.
     * <p>
     * Value is of type <code>boolean</code>.<br />
     * Possible values: {TRUE, FALSE}
     * </p>
     * 
     */
    public static final String HONOUR_ALL_SCHEMA_LOCATIONS = "honourAllSchemaLocations";//$NON-NLS-1$

    /**
     * Indicates whether or not the content of comments should be formatted
     * <p>
     * Value is of type <code>boolean</code><br />
     * Possible values: {TRUE, FALSE}
     * </p>
     */
    public static final String FORMAT_COMMENT_TEXT = "formatCommentText"; //$NON-NLS-1$

    /**
     * Indicates whether or not the lines of comments should be joined when formatting
     * <p>
     * Value is of type <code>boolean</code>
     */
    public static final String FORMAT_COMMENT_JOIN_LINES = "formatCommentJoinLines"; //$NON-NLS-1$
    
    /**
     * Indicates whether or not CMDocuments should be globally cached
     * <p>
     * Value is of type <code>boolean</code>
     */
    public static final String CMDOCUMENT_GLOBAL_CACHE_ENABLED = "cmDocumentGlobalCacheEnabled"; //$NON-NLS-1$
    
    /**
	 * Indicates whether or not MarkUpValidator should run as part of XMl Validation.
	 * <p>
	 * Value is of type <code>boolean</code>.<br />
	 * Possible values: {TRUE, FALSE} 
	 * </p>
	 */
    public static final String MARKUP_VALIDATION = "markupValidation"; //$NON-NLS-1$
    
    /**
	 * Indicates whether or not a message should be produced when validating a
	 * file that specifies following condition.
	 * <p>
	 * Value is of type <code>integer</code>.<br />
	 * Possible values: {0, 1, 2} (none, warning, error)
	 * </p>
	 */
    public static final String ATTRIBUTE_HAS_NO_VALUE = "attributeHasNoValue"; //$NON-NLS-1$
    public static final String END_TAG_WITH_ATTRIBUTES = "endTagWithAttributes"; //$NON-NLS-1$
    public static final String WHITESPACE_BEFORE_TAGNAME = "whitespaceBeforeTagName"; //$NON-NLS-1$
    public static final String MISSING_CLOSING_BRACKET = "missingClosingBracket"; //$NON-NLS-1$
    public static final String MISSING_CLOSING_QUOTE = "missingClosingQuote"; //$NON-NLS-1$
    public static final String MISSING_END_TAG = "missingEndTag"; //$NON-NLS-1$
    public static final String MISSING_START_TAG = "missingStartTag"; //$NON-NLS-1$
    public static final String MISSING_QUOTES = "missingQuotes"; //$NON-NLS-1$
    public static final String NAMESPACE_IN_PI_TARGET = "namespaceInPITarget"; //$NON-NLS-1$
    public static final String MISSING_TAG_NAME = "missingTagName"; //$NON-NLS-1$
    public static final String WHITESPACE_AT_START = "whitespaceAtStart"; //$NON-NLS-1$
    public static final String USE_PROJECT_SETTINGS = "use-project-settings";//$NON-NLS-1$
}
