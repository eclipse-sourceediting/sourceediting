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
package org.eclipse.wst.dtd.core.internal.parser;

public interface DTDRegionTypes {
	public static final String regionPrefix = "org.eclipse.wst.dtd.core.internal.util.parser.DTDRegionTypes."; //$NON-NLS-1$

	public static final String NAME = regionPrefix + "NAME"; //$NON-NLS-1$
	public static final String START_TAG = regionPrefix + "START_TAG"; //$NON-NLS-1$
	public static final String END_TAG = regionPrefix + "END_TAG"; //$NON-NLS-1$
	public static final String LEFT_PAREN = regionPrefix + "LEFT_PAREN"; //$NON-NLS-1$
	public static final String RIGHT_PAREN = regionPrefix + "RIGHT_PAREN"; //$NON-NLS-1$
	public static final String WHITESPACE = regionPrefix + "WHITESPACE"; //$NON-NLS-1$
	public static final String CONNECTOR = regionPrefix + "CONNECTOR"; //$NON-NLS-1$

	public static final String OCCUR_TYPE = regionPrefix + "OCCUR_TYPE"; //$NON-NLS-1$
	public static final String EXCLAMATION = regionPrefix + "EXCLAMATION"; //$NON-NLS-1$
	public static final String PERCENT = regionPrefix + "PERCENT"; //$NON-NLS-1$
	public static final String SEMICOLON = regionPrefix + "SEMICOLON"; //$NON-NLS-1$
	public static final String COMMENT_START = regionPrefix + "COMMENT"; //$NON-NLS-1$
	public static final String COMMENT_CONTENT = regionPrefix + "COMMENT_CONTENT"; //$NON-NLS-1$
	public static final String COMMENT_END = regionPrefix + "COMMENT_END"; //$NON-NLS-1$

	public static final String NOTATION_TAG = regionPrefix + "NOTATION_TAG"; //$NON-NLS-1$
	public static final String NOTATION_CONTENT = regionPrefix + "NOTATION_CONTENT"; //$NON-NLS-1$

	public static final String ENTITY_TAG = regionPrefix + "ENTITY_TAG"; //$NON-NLS-1$
	public static final String ENTITY_PARM = regionPrefix + "ENTITY_PARM"; //$NON-NLS-1$
	public static final String ENTITY_CONTENT = regionPrefix + "ENTITY_CONTENT"; //$NON-NLS-1$
	public static final String NDATA_VALUE = regionPrefix + "NDATA_VALUE"; //$NON-NLS-1$

	public static final String ELEMENT_TAG = regionPrefix + "ELEMENT_TAG"; //$NON-NLS-1$
	public static final String ELEMENT_CONTENT = regionPrefix + "ELEMENT_CONTENT"; //$NON-NLS-1$
	public static final String CONTENT_EMPTY = regionPrefix + "CONTENT_EMPTY"; //$NON-NLS-1$
	public static final String CONTENT_ANY = regionPrefix + "CONTENT_ANY"; //$NON-NLS-1$
	public static final String CONTENT_PCDATA = regionPrefix + "CONTENT_PCDATA"; //$NON-NLS-1$

	public static final String ATTLIST_TAG = regionPrefix + "ATTLIST_TAG"; //$NON-NLS-1$

	public static final String SYSTEM_KEYWORD = regionPrefix + "SYSTEM_KEYWORD"; //$NON-NLS-1$
	public static final String PUBLIC_KEYWORD = regionPrefix + "PUBLIC_KEYWORD"; //$NON-NLS-1$
	public static final String NDATA_KEYWORD = regionPrefix + "NDATA_KEYWORD"; //$NON-NLS-1$
	public static final String SINGLEQUOTED_LITERAL = regionPrefix + "SINGLEQUOTED_LITERAL"; //$NON-NLS-1$
	public static final String DOUBLEQUOTED_LITERAL = regionPrefix + "DOUBLEQUOTED_LITERAL"; //$NON-NLS-1$
	public static final String UNKNOWN_CONTENT = regionPrefix + "UNKNOWN_CONTENT"; //$NON-NLS-1$

	public static final String ATTRIBUTE_NAME = regionPrefix + "ATTRIBUTE_NAME"; //$NON-NLS-1$

	// attribute type keywords
	public static final String CDATA_KEYWORD = regionPrefix + "CDATA_KEYWORD"; //$NON-NLS-1$
	public static final String ID_KEYWORD = regionPrefix + "ID_KEYWORD"; //$NON-NLS-1$
	public static final String IDREF_KEYWORD = regionPrefix + "IDREF_KEYWORD"; //$NON-NLS-1$
	public static final String IDREFS_KEYWORD = regionPrefix + "IDREFS_KEYWORD"; //$NON-NLS-1$
	public static final String ENTITY_KEYWORD = regionPrefix + "ENTITY_KEYWORD"; //$NON-NLS-1$
	public static final String ENTITIES_KEYWORD = regionPrefix + "ENTITIES_KEYWORD"; //$NON-NLS-1$
	public static final String NMTOKEN_KEYWORD = regionPrefix + "NMTOKEN_KEYWORD"; //$NON-NLS-1$
	public static final String NMTOKENS_KEYWORD = regionPrefix + "NMTOKENS_KEYWORD"; //$NON-NLS-1$
	public static final String NOTATION_KEYWORD = regionPrefix + "NOTATION_KEYWORD"; //$NON-NLS-1$
	public static final String ENUM_CHOICE = regionPrefix + "ENUM_CHOICE"; //$NON-NLS-1$
	public static final String PARM_ENTITY_TYPE = regionPrefix + "PARM_ENTITY_TYPE"; //$NON-NLS-1$

	// attribute defaults keywords
	public static final String REQUIRED_KEYWORD = regionPrefix + "REQUIRED_KEYWORD"; //$NON-NLS-1$
	public static final String IMPLIED_KEYWORD = regionPrefix + "IMPLIED_KEYWORD"; //$NON-NLS-1$
	public static final String FIXED_KEYWORD = regionPrefix + "FIXED_KEYWORD"; //$NON-NLS-1$
}// DTDRegionTypes
