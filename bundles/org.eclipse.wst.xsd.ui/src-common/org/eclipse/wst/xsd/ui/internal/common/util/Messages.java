/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
  static 
  {
    NLS.initializeMessages("org.eclipse.wst.xsd.ui.internal.common.util.messages", Messages.class); //$NON-NLS-1$
  }

  public Messages()
  {
    super();
  }
  
  public static String _UI_ACTION_OPEN_IN_NEW_EDITOR;
  public static String _UI_ACTION_ADD_ATTRIBUTE_GROUP;
  public static String _UI_ACTION_ADD_ATTRIBUTE_GROUP_REF;
  public static String _UI_ACTION_ADD_ATTRIBUTE_GROUP_DEFINITION;
  public static String _UI_ACTION_ADD_GROUP_REF;
  public static String _UI_ACTION_ADD_GROUP;
  public static String _UI_ACTION_DELETE;
  public static String _UI_ACTION_ADD_COMPLEX_TYPE;
  public static String _UI_ACTION_ADD_ATTRIBUTE;
  public static String _UI_ACTION_ADD_SIMPLE_TYPE;
  public static String _UI_ACTION_UPDATE_ELEMENT_REFERENCE;
  public static String _UI_LABEL_NO_ITEMS_SELECTED;
  public static String _UI_ACTION_ADD;
  public static String _UI_ACTION_ADD_WITH_DOTS;
  public static String _UI_ACTION_EDIT_WITH_DOTS;
  public static String _UI_ACTION_CHANGE_PATTERN;
  public static String _UI_ACTION_ADD_ENUMERATION;
  public static String _UI_ACTION_INSERT_ENUMERATION;
  public static String _UI_ACTION_ADD_PATTERN;
  public static String _UI_ACTION_ADD_ENUMERATIONS;
  public static String _UI_ACTION_DELETE_CONSTRAINTS;
  public static String _UI_ACTION_DELETE_PATTERN;
  public static String _UI_ACTION_DELETE_ENUMERATION;
  public static String _UI_ACTION_SET_ENUMERATION_VALUE;
  public static String _UI_LABEL_PATTERN;
  public static String _UI_ACTION_CHANGE_MAXIMUM_OCCURRENCE;
  public static String _UI_ERROR_INVALID_VALUE_FOR_MAXIMUM_OCCURRENCE;
  public static String _UI_ACTION_CHANGE_MINIMUM_OCCURRENCE;
  public static String _UI_ACTION_ADD_APPINFO_ELEMENT;
  public static String _UI_ACTION_ADD_APPINFO_ATTRIBUTE;
  public static String _UI_ACTION_DELETE_APPINFO_ELEMENT;
  public static String _UI_ACTION_DELETE_APPINFO_ATTRIBUTE;
  public static String _UI_ACTION_CHANGE_CONTENT_MODEL;
  public static String _UI_ACTION_RENAME;
  public static String _UI_ACTION_CHANGE_ENUMERATION_VALUE;
  public static String _UI_ERROR_INVALID_NAME;
  public static String _UI_LABEL_NAME;
  public static String _UI_LABEL_REFERENCE;
  public static String _UI_ACTION_UPDATE_MAXIMUM_OCCURRENCE;
  public static String _UI_ACTION_UPDATE_MINIMUM_OCCURRENCE;
  public static String _UI_LABEL_READONLY;
  public static String _UI_LABEL_INCLUSIVE;
  public static String _UI_LABEL_COLLAPSE_WHITESPACE;
  public static String _UI_LABEL_SPECIFIC_CONSTRAINT_VALUES;
  public static String _UI_LABEL_RESTRICT_VALUES_BY;
  public static String _UI_LABEL_ENUMERATIONS;
  public static String _UI_LABEL_PATTERNS;
  public static String _UI_LABEL_MINIMUM_LENGTH;
  public static String _UI_LABEL_MAXIMUM_LENGTH;
  public static String _UI_LABEL_CONSTRAINTS_ON_LENGTH_OF;
  public static String _UI_LABEL_CONSTRAINTS_ON_VALUE_OF;
  public static String _UI_LABEL_MINIMUM_VALUE;
  public static String _UI_LABEL_MAXIMUM_VALUE;
  public static String _UI_LABEL_CONTRAINTS_ON;
  public static String _UI_LABEL_TYPE;
  public static String _UI_LABEL_BLOCKDEFAULT;
  public static String _UI_LABEL_FINALDEFAULT;
  public static String _UI_ACTION_CONSTRAIN_LENGTH;
  public static String _UI_ACTION_UPDATE_BOUNDS;
  public static String _UI_ACTION_COLLAPSE_WHITESPACE;
  public static String _UI_LABEL_BASE;
  public static String _UI_ERROR_INVALID_FILE;
  public static String _UI_LABEL_EXTENSIONS;
  public static String _UI_ACTION_ADD_EXTENSION_COMPONENT;
  public static String _UI_ACTION_DELETE_EXTENSION_COMPONENT;
  public static String _UI_LABEL_UP;
  public static String _UI_LABEL_DOWN;
  public static String _UI_LABEL_EXTENSION_DETAILS;
  public static String _UI_ACTION_ADD_DOCUMENTATION;
  public static String _UI_ACTION_ADD_EXTENSION_COMPONENTS;
  public static String _UI_LABEL_EXTENSION_CATEGORIES;
  public static String _UI_LABEL_ADD_WITH_DOTS;
  public static String _UI_LABEL_DELETE;
  public static String _UI_LABEL_EDIT;
  public static String _UI_LABEL_AVAILABLE_COMPONENTS_TO_ADD;
  public static String _UI_LABEL_EDIT_CATEGORY;
  public static String _UI_ERROR_INVALID_CATEGORY;
  public static String _UI_ERROR_FILE_CANNOT_BE_PARSED;
  public static String _UI_ERROR_VALIDATE_THE_FILE;
  public static String _UI_LABEL_SCHEMA;
  public static String _UI_LABEL_ADD_CATEGORY;
  public static String _UI_ERROR_NAME_ALREADY_USED;
  public static String _UI_ACTION_BROWSE_WORKSPACE;
  public static String _UI_LABEL_SELECT_XSD_FILE;
  public static String _UI_DESCRIPTION_CHOOSE_XSD_FILE;
  public static String _UI_ACTION_BROWSE_CATALOG;
  public static String _UI_ACTION_ADD_ANY_ELEMENT;
  public static String _UI_ACTION_ADD_ANY_ATTRIBUTE;
  public static String _UI_ACTION_SET_BASE_TYPE;
  public static String _UI_TOOLTIP_RENAME_REFACTOR;
  public static String _UI_VALUE_COLON;
  public static String _UI_ID;
  public static String _UI_REF;
  public static String _UI_FIXED;
  public static String _UI_DEFAULT;
  public static String _UI_FORM;
  public static String _UI_USAGE;
  public static String _UI_ACTION_CHANGE;
  public static String _UI_ACTION_CLOSE_SCHEMA_PREVIEW_POPUP;
  public static String _UI_ACTION_NAMESPACE_INFORMATION_CHANGE;
  public static String _UI_LABEL_ABSTRACT;
  public static String _UI_LABEL_BLOCK;
  public static String _UI_LABEL_FINAL;
  public static String _UI_LABEL_SUBSTITUTION_GROUP;
  public static String _UI_LABEL_MIXED;
  public static String _UI_LABEL_VALUE;
  public static String _UI_LABEL_FORM;
  public static String _UI_LABEL_NILLABLE;
  public static String _UI_LABEL_ELEMENTFORMDEFAULT;
  public static String _UI_LABEL_ATTRIBUTEFORMDEFAULT;
  public static String _UI_LABEL_WORKSPACE;
  public static String _UI_LABEL_CATALOG;
  public static String _UI_DELETE_BUTTON;
}
