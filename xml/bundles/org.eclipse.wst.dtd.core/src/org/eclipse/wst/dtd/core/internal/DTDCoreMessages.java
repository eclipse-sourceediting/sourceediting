/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by DTD Core
 * 
 * @plannedfor 1.0
 */
public class DTDCoreMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.dtd.core.internal.DTDCorePluginResources";//$NON-NLS-1$

	public static String _UI_NONE_DESC;
	public static String _UI_CHARACTER_DATA_DESC;
	public static String _UI_IDENTIFIER_DESC;
	public static String _UI_ID_REFERENCE_DESC;
	public static String _UI_ID_REFERENCES_DESC;
	public static String _UI_ENTITY_NAME_DESC;
	public static String _UI_ENTITY_NAMES_DESC;
	public static String _UI_NAME_TOKEN_DESC;
	public static String _UI_NAME_TOKENS_DESC;
	public static String _UI_ENUM_NAME_TOKENS_DESC;
	public static String _UI_ENUM_NOTATION_DESC;
	public static String _UI_LABEL_ATTR_DEFAULT_VAL;
	public static String _UI_LABEL_ATTR_DEFAULT_KIND;
	public static String _UI_LABEL_ATTR_TYPE;
	public static String _UI_LABEL_ATTR_ENUM_ITEMS;
	public static String _UI_LABEL_ATTR_LIST_ADD;
	public static String _UI_LABEL_CM_GRP_NODE_CONNECTOR;
	public static String _UI_LABEL_CM_GRP_NODE_INSERT_ELEMENT;
	public static String _UI_LABEL_CM_GRP_NODE_ADD_GRP;
	public static String _UI_LABEL_CM_GRP_NODE_ADD_CHILD;
	public static String _UI_LABEL_CM_NODE_MIX_CONTENT;
	public static String _UI_LABEL_CM_NODE_CHILD_CONTENT;
	public static String _UI_LABEL_CM_NODE_SET_MIX_CONTENT;
	public static String _UI_LABEL_CM_NODE_SET_CHILD_CONTENT;
	public static String _UI_LABEL_CM_NODE_SET;
	public static String _UI_LABEL_CM_NODE_CONTENT;
	public static String _UI_LABEL_CM_NODE_PCDATA;
	public static String _UI_LABEL_CM_NODE_ANY;
	public static String _UI_LABEL_CM_NODE_EMPTY;
	public static String _UI_LABEL_CM_REP_NODE_CHG_OCCUR;
	public static String _UI_LABEL_COMMENT_CHG;
	public static String _UI_LABEL_DTD_FILE_ADD_ELEMENT;
	public static String _UI_LABEL_DTD_FILE_ADD_ENTITY;
	public static String _UI_LABEL_DTD_FILE_ADD_COMMENT;
	public static String _UI_LABEL_DTD_FILE_ADD_PARM_ENTITY_REF;
	public static String _UI_LABEL_DTD_FILE_ADD_NOTATION;
	public static String _UI_LABEL_DTD_FILE_ADD_ATTR_LIST;
	public static String _UI_LABEL_DTD_FILE_DELETE;
	public static String _UI_LABEL_DTD_NODE_NAME_CHG;
	public static String _UI_LABEL_DTD_NODE_DELETE;
	public static String _UI_LABEL_ELEMENT_ADD_ATTR;
	public static String _UI_LABEL_ELEMENT_ADD_GRP;
	public static String _UI_LABEL_ELEMENT_ADD_CHILD;
	public static String _UI_LABEL_ENTITY_SET_PARM_ENTITY;
	public static String _UI_LABEL_ENTITY_SET_GENERAL_ENTITY;
	public static String _UI_LABEL_ENTITY_SET_EXT_ENTITY;
	public static String _UI_LABEL_ENTITY_SET_INT_ENTITY;
	public static String _UI_LABEL_ENTITY_VALUE_CHG;
	public static String _UI_LABEL_ENTITY_NDATA_CHANGE;
	public static String _UI_LABEL_EXT_NODE_PUBLIC_ID_CHG;
	public static String _UI_LABEL_EXT_NODE_SYSTEM_ID_CHG;
	public static String _UI_LABEL_NODE_LIST_ELEMENTS;
	public static String _UI_LABEL_NODE_LIST_ENTITIES;
	public static String _UI_LABEL_NODE_LIST_NOTATIONS;
	public static String _UI_LABEL_NODE_LIST_COMMENTS;
	public static String _UI_LABEL_NODE_LIST_OTHER;
	public static String _UI_LABEL_NODE_LIST_ATTRIBUTES;
	public static String _UI_LABEL_PARM_ENTITY_REF_CHG_ENTITY_REF;
	public static String _UI_LABEL_PARM_ENTITY_REF_COMMENT_CHG;
	public static String _UI_LABEL_TOP_LEVEL_NODE_DELETE;
	public static String _ERROR_INCL_FILE_LOAD_FAILURE;
	public static String _ERROR_UNDECLARED_ELEMENT_1;
	public static String _UI_ERRORPART_UNDECLARED_ELEMENT_2;
	public static String _EXC_OPERATION_NOT_SUPPORTED;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DTDCoreMessages.class);
	}
}
