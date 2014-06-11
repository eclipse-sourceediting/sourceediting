/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.parserz;

/**
 */
public interface CSSRegionContexts {
	public static final String CSS_COMMENT = "COMMENT"; //$NON-NLS-1$
	public static final String CSS_CDO = "CDO"; //$NON-NLS-1$
	public static final String CSS_CDC = "CDC"; //$NON-NLS-1$
	public static final String CSS_S = "S"; //$NON-NLS-1$

	public static final String CSS_DELIMITER = "DELIMITER"; //$NON-NLS-1$
	public static final String CSS_LBRACE = "LBRACE"; //$NON-NLS-1$
	public static final String CSS_RBRACE = "RBRACE"; //$NON-NLS-1$

	public static final String CSS_IMPORT = "IMPORT"; //$NON-NLS-1$
	public static final String CSS_PAGE = "PAGE"; //$NON-NLS-1$
	public static final String CSS_MEDIA = "MEDIA"; //$NON-NLS-1$
	public static final String CSS_FONT_FACE = "FONT_FACE"; //$NON-NLS-1$
	public static final String CSS_CHARSET = "CHARSET"; //$NON-NLS-1$
	public static final String CSS_ATKEYWORD = "ATKEYWORD"; //$NON-NLS-1$

	public static final String CSS_STRING = "STRING"; //$NON-NLS-1$
	public static final String CSS_URI = "URI"; //$NON-NLS-1$
	public static final String CSS_MEDIUM = "MEDIUM"; //$NON-NLS-1$
	public static final String CSS_MEDIA_SEPARATOR = "MEDIA_SEPARATOR"; //$NON-NLS-1$

	public static final String CSS_CHARSET_NAME = "CHARSET_NAME"; //$NON-NLS-1$

	public static final String CSS_PAGE_SELECTOR = "CSS_PAGE_SELECTOR"; //$NON-NLS-1$

	public static final String CSS_SELECTOR_ELEMENT_NAME = "SELECTOR_ELEMENT_NAME"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_UNIVERSAL = "SELECTOR_UNIVERSAL"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_PSEUDO = "SELECTOR_PSEUDO"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_CLASS = "SELECTOR_CLASS"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_ID = "SELECTOR_ID"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_COMBINATOR = "SELECTOR_COMBINATOR"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_SEPARATOR = "SELECTOR_SEPARATOR"; //$NON-NLS-1$

	public static final String CSS_SELECTOR_ATTRIBUTE_START = "SELECTOR_ATTRIBUTE_START"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_ATTRIBUTE_END = "SELECTOR_ATTRIBUTE_END"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_ATTRIBUTE_NAME = "SELECTOR_ATTRIBUTE_NAME"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_ATTRIBUTE_VALUE = "SELECTOR_ATTRIBUTE_VALUE"; //$NON-NLS-1$
	public static final String CSS_SELECTOR_ATTRIBUTE_OPERATOR = "SELECTOR_ATTRIBUTE_OPERATOR"; //$NON-NLS-1$

	public static final String CSS_DECLARATION_PROPERTY = "DECLARATION_PROPERTY"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_SEPARATOR = "DECLARATION_SEPARATOR"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_DELIMITER = "DECLARATION_DELIMITER"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_IDENT = "DECLARATION_VALUE_IDENT"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_DIMENSION = "DECLARATION_VALUE_DIMENSION"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_PERCENTAGE = "DECLARATION_VALUE_PERCENTAGE"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_NUMBER = "DECLARATION_VALUE_NUMBER"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_FUNCTION = "DECLARATION_VALUE_FUNCTION"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE = "DECLARATION_VALUE_PARENTHESIS_CLOSE"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_STRING = "DECLARATION_VALUE_STRING"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_URI = "DECLARATION_VALUE_URI"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_HASH = "DECLARATION_VALUE_HASH"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_UNICODE_RANGE = "DECLARATION_VALUE_UNICODE_RANGE"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_IMPORTANT = "CSS_DECLARATION_VALUE_IMPORTANT"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_OPERATOR = "DECLARATION_VALUE_OPERATOR"; //$NON-NLS-1$
	public static final String CSS_DECLARATION_VALUE_S = "DECLARATION_VALUE_S"; //$NON-NLS-1$

	public static final String CSS_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

	// For null object : CSSTokenizer never set this value
	public static final String CSS_UNDEFINED = "UNDEFINED"; //$NON-NLS-1$
	/**
 	 * currently provided this field but may be removed in future.
	 */
	public static final String CSS_FOREIGN_ELEMENT = "FOREIGN_ELEMENT"; //$NON-NLS-1$
}
