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
package org.eclipse.wst.xml.core.internal.provisional;

/**
 * Keys to use for preference settings.
 * 
 * @plannedfor 1.0
 */

public interface IXMLPreferenceNames {
	String CLEANUP_ATTR_NAME_CASE = "cleanupAttrNameCase";//$NON-NLS-1$
	String CLEANUP_EOL_CODE = "cleanupEOLCode";//$NON-NLS-1$
	// cleanup preference names
	String CLEANUP_TAG_NAME_CASE = "cleanupTagNameCase";//$NON-NLS-1$
	String CONVERT_EOL_CODES = "convertEOLCodes";//$NON-NLS-1$
	String FORMAT_SOURCE = "formatSource";//$NON-NLS-1$
	String INSERT_MISSING_TAGS = "insertMissingTags";//$NON-NLS-1$

	// others
	String LAST_ACTIVE_PAGE = "lastActivePage";//$NON-NLS-1$
	String QUOTE_ATTR_VALUES = "quoteAttrValues";//$NON-NLS-1$

	/*
	 * not used for now // highlighting types String COMMENT_BORDER =
	 * "commentBorder";//$NON-NLS-1$ String COMMENT_TEXT =
	 * "commentText";//$NON-NLS-1$ String CDATA_BORDER =
	 * "cdataBorder";//$NON-NLS-1$ String CDATA_TEXT =
	 * "cdataText";//$NON-NLS-1$ String PI_BORDER = "piBorder";//$NON-NLS-1$
	 * String PI_CONTENT = "piContent";//$NON-NLS-1$ String TAG_BORDER =
	 * "tagBorder";//$NON-NLS-1$ String TAG_NAME = "tagName";//$NON-NLS-1$
	 * String TAG_ATTRIBUTE_NAME = "tagAttributeName";//$NON-NLS-1$ String
	 * TAG_ATTRIBUTE_VALUE = "tagAttributeValue";//$NON-NLS-1$ String
	 * DECL_BORDER = "declBoder";//$NON-NLS-1$ String DOCTYPE_NAME =
	 * "doctypeName";//$NON-NLS-1$ String DOCTYPE_EXTERNAL_ID =
	 * "doctypeExternalId";//$NON-NLS-1$ String DOCTYPE_EXTERNAL_ID_PUBREF =
	 * "doctypeExternalPubref";//$NON-NLS-1$ String DOCTYPE_EXTERNAL_ID_SYSREF =
	 * "doctypeExtrenalSysref";//$NON-NLS-1$ String XML_CONTENT =
	 * "xmlContent";//$NON-NLS-1$ // highlighting preferences String COMMA =
	 * ",";//$NON-NLS-1$ String COLOR = "color";//$NON-NLS-1$ String NAME =
	 * "name";//$NON-NLS-1$ String FOREGROUND = "foreground";//$NON-NLS-1$
	 * String BACKGROUND = "background";//$NON-NLS-1$ String BOLD =
	 * "bold";//$NON-NLS-1$ String ITALIC = "italic";//$NON-NLS-1$
	 */
}
