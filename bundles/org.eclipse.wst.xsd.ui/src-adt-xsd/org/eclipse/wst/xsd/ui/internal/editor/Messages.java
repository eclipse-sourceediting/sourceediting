/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver, Standards for Technology in Automotive Retail, bug 1147033
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
  static 
  {
    NLS.initializeMessages("org.eclipse.wst.xsd.ui.internal.editor.messages", Messages.class); //$NON-NLS-1$
  }

  public Messages()
  {
    super();
  }
  
  public static String UI_LABEL_BASE_TYPE;
  public static String UI_LABEL_DERIVED_BY;
  public static String UI_LABEL_INHERIT_FROM;
  public static String UI_LABEL_INHERIT_BY;
  public static String UI_LABEL_DOCUMENTATION;
  public static String UI_LABEL_APP_INFO;
  public static String UI_LABEL_SET_TYPE;
  public static String UI_LABEL_TYPE;
  public static String UI_LABEL_NAME;
  public static String UI_LABEL_KIND;
  public static String UI_LABEL_MINOCCURS;
  public static String UI_LABEL_MAXOCCURS;
  public static String UI_NO_TYPE;
  public static String UI_PAGE_HEADING_REFERENCE;
  public static String UI_LABEL_READ_ONLY;
  public static String UI_LABEL_COMPONENTS;

  public static String _UI_GRAPH_TYPES;
  public static String _UI_GRAPH_REDEFINE_TYPES;
  public static String _UI_GRAPH_ELEMENTS;
  public static String _UI_GRAPH_ATTRIBUTES;
  public static String _UI_GRAPH_ATTRIBUTE_GROUPS;
  public static String _UI_GRAPH_REDEFINE_ATTRIBUTE_GROUPS;
  public static String _UI_GRAPH_NOTATIONS;
  public static String _UI_GRAPH_IDENTITY_CONSTRAINTS;
  public static String _UI_GRAPH_ANNOTATIONS;
  public static String _UI_GRAPH_DIRECTIVES;
  public static String _UI_GRAPH_GROUPS;
  public static String _UI_GRAPH_REDEFINE_GROUPS;
  
  public static String _UI_LABEL_NO_LOCATION_SPECIFIED;
  public static String _UI_NO_TYPE_DEFINED;
  public static String _UI_ACTION_UPDATE_NAME;
  public static String _UI_LABEL_ABSENT;
  public static String _UI_ACTION_ADD_FIELD;
  public static String _UI_ACTION_SET_MULTIPLICITY;
  public static String _UI_LABEL_OPTIONAL;
  public static String _UI_LABEL_ZERO_OR_MORE;
  public static String _UI_LABEL_ONE_OR_MORE;
  public static String _UI_LABEL_REQUIRED;
  public static String _UI_LABEL_ARRAY;
  public static String _UI_ACTION_SET_TYPE;
  public static String _UI_LABEL_LOCAL_TYPE;
  
  public static String _UI_GRAPH_UNKNOWN_OBJECT;
  public static String _UI_GRAPH_XSDSCHEMA;
  public static String _UI_GRAPH_XSDSCHEMA_NO_NAMESPACE;
  public static String _UI_LABEL_SET_COMMON_BUILT_IN_TYPES;
  public static String _UI_LABEL_SELECT_TYPES_FILTER_OUT;
  public static String _UI_LABEL_NEW_TYPE;
  public static String _UI_VALUE_NEW_TYPE;  
  public static String _UI_LABEL_COMPLEX_TYPE;
  public static String _UI_LABEL_SIMPLE_TYPE;
  public static String _UI_LABEL_NEW_ELEMENT;
  public static String _UI_LABEL_NEW_ATTRIBUTE;
  public static String _UI_MENU_XSD_EDITOR;
  public static String _UI_LABEL_SOURCE;
  public static String _UI_ACTION_BEFORE;
  public static String _UI_ACTION_AFTER;
  public static String _UI_ACTION_ADD_ELEMENT;
  public static String _UI_ACTION_INSERT_ELEMENT;
  public static String _UI_ACTION_ADD_ELEMENT_REF;
  public static String _UI_ACTION_NEW;
  public static String _UI_ACTION_BROWSE;
  public static String _UI_ACTION_UPDATE_ELEMENT_REFERENCE;
  public static String _UI_ACTION_UPDATE_ATTRIBUTE_REFERENCE;
  public static String _UI_LABEL_TARGET_NAMESPACE;
  public static String _UI_LABEL_NO_NAMESPACE;
  public static String _UI_ACTION_ADD_COMPLEX_TYPE;
  public static String _UI_ACTION_ADD_SIMPLE_TYPE;
  public static String _UI_LABEL_NAME_SEARCH_FILTER_TEXT;
  public static String _UI_LABEL_ELEMENTS_COLON;
  public static String _UI_LABEL_ATTRIBUTES_COLON;
  public static String _UI_LABEL_ATTRIBUTES_PROCESSCONTENTS;
  public static String _UI_LABEL_ATTRIBUTES_NAMESPACE;
  public static String _UI_LABEL_SET_ELEMENT_REFERENCE;
  public static String _UI_LABEL_SET_ATTRIBUTE_REFERENCE;
  public static String _UI_LABEL_REDEFINE_COMPONENT;
  public static String _UI_LABEL_TYPES_COLON;
  public static String _UI_LABEL_SET_TYPE;

  public static String _UI_TEXT_INDENT_LABEL;
  public static String _UI_TEXT_INDENT_SPACES_LABEL; 
  public static String _UI_TEXT_XSD_NAMESPACE_PREFIX;
  public static String _UI_TEXT_XSD_DEFAULT_PREFIX;
  public static String _UI_QUALIFY_XSD;
  public static String _UI_TEXT_XSD_DEFAULT_TARGET_NAMESPACE;
  public static String _UI_VALIDATING_FILES;
  public static String _UI_FULL_CONFORMANCE;
  public static String _UI_TEXT_HONOUR_ALL_SCHEMA_LOCATIONS;
  
  public static String _ERROR_LABEL_INVALID_PREFIX;
  public static String _UI_ACTION_ADD_INCLUDE;
  public static String _UI_ACTION_ADD_IMPORT;
  public static String _UI_ACTION_ADD_REDEFINE;
  public static String _UI_ACTION_ADD_ATTRIBUTE;
  public static String _UI_ACTION_INSERT_ATTRIBUTE;
  public static String _UI_ACTION_ADD_ATTRIBUTE_REF;
  public static String _UI_ACTION_DRAG_DROP_ELEMENT;
  public static String _UI_ACTION_DRAG_DROP_ATTRIBUTE;
  public static String _UI_ACTION_REDEFINE_COMPLEX_TYPE;
  public static String _UI_ACTION_REDEFINE_SIMPLE_TYPE;
  public static String _UI_ACTION_REDEFINE_ATTRIBUTE_GROUP;
  public static String _UI_ACTION_REDEFINE_MODEL_GROUP;
  public static String _UI_ACTION_RENAME;  
  
  public static String _UI_IMAGE_COMPLEX_TYPE;
  public static String _UI_IMAGE_SIMPLE_TYPE;
  public static String _UI_IMAGE_MODEL_GROUP;
  public static String _UI_IMAGE_ATTRIBUTE_GROUP;

  // TODO: TO REMOVE
  public static String _UI_LABEL_ELEMENTFORMDEFAULT;
  // TODO: TO REMOVE
  public static String _UI_LABEL_ATTRIBUTEFORMDEFAULT;
  public static String _UI_LABEL_CREATE_ANON_TYPE;  
  public static String _UI_XML_TEXT_EDITOR_PREFS_LINK;
  public static String _UI_XML_VALIDATOR_PREFS_LINK;
  
  public static String _UI_TEXT_ENABLE_AUTO_IMPORT_CLEANUP;
  public static String _UI_TEXT_ENABLE_AUTO_OPEN_SCHEMA_DIALOG;
  public static String _UI_GRAPH_REDEFINE_SCHEMA;

  public static String _UI_COMBO_NEW;
  public static String _UI_COMBO_BROWSE;

  public static String _UI_ERROR_INVALID_NAME;  
}
