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
package org.eclipse.wst.xml.core.internal.regions;

public interface DOMRegionContext {

	public static final String BLOCK_TEXT = "BLOCK_TEXT"; //$NON-NLS-1$

	public static final String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$

	public static final String WHITE_SPACE = "WHITE_SPACE"; //$NON-NLS-1$
	public static final String XML_ATTLIST_DECL_CLOSE = "XML_ATTLIST_DECL_CLOSE"; //$NON-NLS-1$
	public static final String XML_ATTLIST_DECL_CONTENT = "XML_ATTLIST_DECL_CONTENT"; //$NON-NLS-1$
	public static final String XML_ATTLIST_DECL_NAME = "XML_ATTLIST_DECL_NAME"; //$NON-NLS-1$

	public static final String XML_ATTLIST_DECLARATION = "XML_ATTLIST_DECLARATION"; //$NON-NLS-1$
	public static final String XML_CDATA_CLOSE = "XML_CDATA_CLOSE"; //$NON-NLS-1$
	public static final String XML_CDATA_OPEN = "XML_CDATA_OPEN"; //$NON-NLS-1$
	public static final String XML_CDATA_TEXT = "XML_CDATA_TEXT"; //$NON-NLS-1$
	public static final String XML_CHAR_REFERENCE = "XML_CHAR_REFERENCE"; //$NON-NLS-1$
	public static final String XML_COMMENT_CLOSE = "XML_COMMENT_CLOSE"; //$NON-NLS-1$

	public static final String XML_COMMENT_OPEN = "XML_COMMENT_OPEN"; //$NON-NLS-1$
	public static final String XML_COMMENT_TEXT = "XML_COMMENT_TEXT"; //$NON-NLS-1$

	public static final String XML_CONTENT = "XML_CONTENT"; //$NON-NLS-1$
	public static final String XML_DECLARATION_CLOSE = "XML_DECLARATION_CLOSE"; //$NON-NLS-1$

	public static final String XML_DECLARATION_OPEN = "XML_DECLARATION_OPEN"; //$NON-NLS-1$

	public static final String XML_DOCTYPE_DECLARATION = "XML_DOCTYPE_DECLARATION"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_DECLARATION_CLOSE = "XML_DOCTYPE_DECLARATION_CLOSE"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_EXTERNAL_ID_PUBLIC = "XML_DOCTYPE_EXTERNAL_ID_PUBLIC"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_EXTERNAL_ID_PUBREF = "XML_DOCTYPE_EXTERNAL_ID_PUBREF"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_EXTERNAL_ID_SYSREF = "XML_DOCTYPE_EXTERNAL_ID_SYSREF"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_EXTERNAL_ID_SYSTEM = "XML_DOCTYPE_EXTERNAL_ID_SYSTEM"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_INTERNAL_SUBSET = "XML_DOCTYPE_INTERNAL_SUBSET"; //$NON-NLS-1$
	public static final String XML_DOCTYPE_NAME = "XML_DOCTYPE_NAME"; //$NON-NLS-1$
	public static final String XML_ELEMENT_DECL_CLOSE = "XML_ELEMENT_DECL_CLOSE"; //$NON-NLS-1$
	public static final String XML_ELEMENT_DECL_CONTENT = "XML_ELEMENT_DECL_CONTENT"; //$NON-NLS-1$
	public static final String XML_ELEMENT_DECL_NAME = "XML_ELEMENT_DECL_NAME"; //$NON-NLS-1$

	public static final String XML_ELEMENT_DECLARATION = "XML_ELEMENT_DECLARATION"; //$NON-NLS-1$
	public static final String XML_EMPTY_TAG_CLOSE = "XML_EMPTY_TAG_CLOSE"; //$NON-NLS-1$
	public static final String XML_END_TAG_OPEN = "XML_END_TAG_OPEN"; //$NON-NLS-1$
	public static final String XML_ENTITY_REFERENCE = "XML_ENTITY_REFERENCE"; //$NON-NLS-1$

	public static final String XML_PE_REFERENCE = "XML_PE_REFERENCE"; //$NON-NLS-1$
	public static final String XML_PI_CLOSE = "XML_PI_CLOSE"; //$NON-NLS-1$
	public static final String XML_PI_CONTENT = "XML_PI_CONTENT"; //$NON-NLS-1$
	public static final String XML_PI_OPEN = "XML_PI_OPEN"; //$NON-NLS-1$
	public static final String XML_TAG_ATTRIBUTE_EQUALS = "XML_TAG_ATTRIBUTE_EQUALS"; //$NON-NLS-1$
	public static final String XML_TAG_ATTRIBUTE_NAME = "XML_TAG_ATTRIBUTE_NAME"; //$NON-NLS-1$
	public static final String XML_TAG_ATTRIBUTE_VALUE = "XML_TAG_ATTRIBUTE_VALUE"; //$NON-NLS-1$
	public static final String XML_TAG_CLOSE = "XML_TAG_CLOSE"; //$NON-NLS-1$
	public static final String XML_TAG_NAME = "XML_TAG_NAME"; //$NON-NLS-1$

	public static final String XML_TAG_OPEN = "XML_TAG_OPEN"; //$NON-NLS-1$
}
