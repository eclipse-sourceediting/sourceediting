/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor;

/**
 * 
 * Context Sensitive Help IDs
 *
 */
public class XSDEditorCSHelpIds
{
  public static final String PLUGIN_NAME = "org.eclipse.wst.xsd.ui";
  
  public static final String REGEX_WIZARD_PAGE = PLUGIN_NAME + ".xsduw0010";
  public static final String REGEX_TOKEN_CONTENTS = PLUGIN_NAME + ".xsduw0020";
  public static final String REGEX_JUST_ONCE = PLUGIN_NAME + ".xsduw0030";
  public static final String REGEX_ZERO_OR_MORE = PLUGIN_NAME + ".xsduw0040";
  public static final String REGEX_ONE_OR_MORE = PLUGIN_NAME + ".xsduw0050";
  public static final String REGEX_OPTIONAL = PLUGIN_NAME + ".xsduw0060";
  public static final String REGEX_REPEAT_RADIO = PLUGIN_NAME + ".xsduw0070";
  public static final String REGEX_RANGE_RADIO = PLUGIN_NAME + ".xsduw0080";
  public static final String REGEX_REPEAT_FIELD = PLUGIN_NAME + ".xsduw0090";
  public static final String REGEX_RANGE_MINIMUM_FIELD = PLUGIN_NAME + ".xsduw0100";
  public static final String REGEX_RANGE_MAXIMUM_FIELD = PLUGIN_NAME + ".xsduw0110";
  public static final String REGEX_ADD_BUTTON = PLUGIN_NAME + ".xsduw0120";
  public static final String REGEX_CURRENT_VALUE = PLUGIN_NAME + ".xsduw0130";
  
  public static final String REGEX_TEST_PAGE = PLUGIN_NAME + ".xsduw0200";
  public static final String REGEX_SAMPLE_TEXT = PLUGIN_NAME + ".xsduw0210";
  
  public static final String RENAME_NEW_NAME = PLUGIN_NAME + ".xsduw0300";
  public static final String RENAME_UPDATE_REFERENCES = PLUGIN_NAME + ".xsduw0310";
  
  public static final String NEWTYPE_COMPLEXTYPE = PLUGIN_NAME + ".xsdud0010";
  public static final String NEWTYPE_SIMPLETYPE = PLUGIN_NAME + ".xsdud0020";
  public static final String NEWTYPE_NAME = PLUGIN_NAME + ".xsdud0030";
  
  public static final String SETTYPE_NAME = PLUGIN_NAME + ".xsdud0050"; // these 3 are in common.ui
  public static final String SETTYPE_TYPES = PLUGIN_NAME + ".xsdud0060";
  public static final String SETTYPE_SEARCHSCOPES = PLUGIN_NAME + ".xsdud0070";
  
  public static final String ADD_ENUMERATIONS__NO_NAME = PLUGIN_NAME + ".xsdud0090";
  public static final String ADD_ENUMERATIONS__DELIMITER_CHARS = PLUGIN_NAME + ".xsdud0100";
  public static final String ADD_ENUMERATIONS__PRESERVE_LEAD_AND_TRAIL_WHITESPACES = PLUGIN_NAME + ".xsdud0110";
  
  public static final String ADD_EXTENSIONS_COMPONENTS__EXTENSION_CATEGORIES = PLUGIN_NAME + ".xsdud0130";
  public static final String ADD_EXTENSIONS_COMPONENTS__AVAILABLE_COMPONENTS_TO_ADD = PLUGIN_NAME + ".xsdud0140";
  public static final String ADD_EXTENSIONS_COMPONENTS__ADD = PLUGIN_NAME + ".xsdud0150";
  public static final String ADD_EXTENSIONS_COMPONENTS__DELETE = PLUGIN_NAME + ".xsdud0160";
  public static final String ADD_EXTENSIONS_COMPONENTS__EDIT = PLUGIN_NAME + ".xsdud0170";
  
  public static final String ADD_CATEGORY__NAME = PLUGIN_NAME + ".xsdud0190";
  public static final String ADD_CATEGORY__SCHEMA = PLUGIN_NAME + ".xsdud0200"; // can't invoke help, this is a CLabel, not text Field
  
  public static final String XML_CATALOG_ENTRIES__ENTRIES_TREELIST = PLUGIN_NAME + ".xsdud0220";
  public static final String XML_CATALOG_ENTRIES__DETAILS = PLUGIN_NAME + ".xsdud0230";
  
  public static final String XMLSCHEMAFILES_PREFERENCES__QUALIFY_XMLSCHEMA_LANGUAGE_CONSTRUCTS = PLUGIN_NAME + ".xsduf0010";
  public static final String XMLSCHEMAFILES_PREFERENCES__XML_SCHEMA_LANGUAGE_CONSTRUCTS_PREFIX = PLUGIN_NAME + ".xsduf0020";
  public static final String XMLSCHEMAFILES_PREFERENCES__DEFAULT_TARGETNAMESPACE = PLUGIN_NAME + ".xsduf0030";
  public static final String XMLSCHEMAFILES_PREFERENCES__HONOUR_ALL_SCHEMA_LOCATIONS = PLUGIN_NAME + ".xsduf0040";
  public static final String XMLSCHEMAFILES_PREFERENCES__IMPORT_CLEANUP = PLUGIN_NAME + ".xsduf0050";
  
  public static final String DOCUMENTATION_TAB__NO_LABEL = PLUGIN_NAME + ".xsdup0010";
  
  public static final String EXTENSIONS_TAB__EXTENSIONS = PLUGIN_NAME + ".xsdup0030";
  public static final String EXTENSIONS_TAB__ADD = PLUGIN_NAME + ".xsdup0040";
  public static final String EXTENSIONS_TAB__DELETE = PLUGIN_NAME + ".xsdup0050";
  public static final String EXTENSIONS_TAB__EXTENSIONS_DETAILS = PLUGIN_NAME + ".xsdup0060";
  
  public static final String CONSTRAINTS_TAB__MINIMUM_LENGTH = PLUGIN_NAME + ".xsdup0080";
  public static final String CONSTRAINTS_TAB__MAXIMUM_LENGTH = PLUGIN_NAME + ".xsdup0090";
  public static final String CONSTRAINTS_TAB__COLLAPSE_WHITESPACE = PLUGIN_NAME + ".xsdup0100";
  public static final String CONSTRAINTS_TAB__ENUMERATIONS = PLUGIN_NAME + ".xsdup0110";
  public static final String CONSTRAINTS_TAB__PATTERNS = PLUGIN_NAME + ".xsdup0120";
  public static final String CONSTRAINTS_TAB__NO_LABEL = PLUGIN_NAME + ".xsdup0130";
  
  public static final String GENERAL_TAB__SCHEMA__PREFIX = PLUGIN_NAME + ".xsdup0200";
  public static final String GENERAL_TAB__SCHEMA__TARGETNAMESPACE = PLUGIN_NAME + ".xsdup0210";
  public static final String GENERAL_TAB__SCHEMA__ADVANCED = PLUGIN_NAME + ".xsdup0220";
  
  public static final String ADVANCE_TAB__SCHEMA_ELEMENT_FORM_DEFAULT = PLUGIN_NAME + ".xsdup0450";
  public static final String ADVANCE_TAB__SCHEMA_ATTRIBUTE_FORM_DEFAULT = PLUGIN_NAME + ".xsdup0460";
  public static final String ADVANCE_TAB__SCHEMA_BLOCK_DEFAULT = PLUGIN_NAME + ".xsdup0470";
  public static final String ADVANCE_TAB__SCHEMA_FINAL_DEFAULT = PLUGIN_NAME + ".xsdup0480";
  
  public static final String GENERAL_TAB__ELEMENT__NAME = PLUGIN_NAME + ".xsdup0230";
  public static final String GENERAL_TAB__ELEMENT__TYPE = PLUGIN_NAME + ".xsdup0240";
  public static final String GENERAL_TAB__ELEMENT__REFERENCE = PLUGIN_NAME + ".xsdup0250";
  public static final String GENERAL_TAB__ELEMENT__MIN_OCCURENCE = PLUGIN_NAME + ".xsdup0180";
  public static final String GENERAL_TAB__ELEMENT__MAX_OCCURENCE = PLUGIN_NAME + ".xsdup0190";
  
  public static final String GENERAL_TAB__COMPLEX_TYPE__NAME = PLUGIN_NAME + ".xsdup0260";
  public static final String GENERAL_TAB__COMPLEX_TYPE__INHERIT_FROM = PLUGIN_NAME + ".xsdup0270";
  public static final String GENERAL_TAB__COMPLEX_TYPE__INHERIT_BY = PLUGIN_NAME + ".xsdup0280";
  
  public static final String GENERAL_TAB__SIMPLE_TYPE__NAME = PLUGIN_NAME + ".xsdup0290";
  public static final String GENERAL_TAB__SIMPLE_TYPE__VARIETY = PLUGIN_NAME + ".xsdup0300";
  public static final String GENERAL_TAB__SIMPLE_TYPE__BASE_TYPE = PLUGIN_NAME + ".xsdup0310";
  
  public static final String GENERAL_TAB__ATTRIBUTE__NAME = PLUGIN_NAME + ".xsdup0320";
  public static final String GENERAL_TAB__ATTRIBUTE__TYPE = PLUGIN_NAME + ".xsdup0330";
  
  public static final String GENERAL_TAB__ATTRIBUTEGROUP__NAME = PLUGIN_NAME + ".xsdup0340";
  
  public static final String GENERAL_TAB__MODELGROUP_DEFINITION__NAME = PLUGIN_NAME + ".xsdup0350";
  
  public static final String GENERAL_TAB__INCLUDE_REDEFINE__SCHEMALOCATION = PLUGIN_NAME + ".xsdup0360";
  
  public static final String GENERAL_TAB__IMPORT__SCHEMALOCATION = PLUGIN_NAME + ".xsdup0370";
  public static final String GENERAL_TAB__IMPORT__NAMESPACE = PLUGIN_NAME + ".xsdup0380";
  public static final String GENERAL_TAB__IMPORT__PREFIX = PLUGIN_NAME + ".xsdup0390";
  
  public static final String GENERAL_TAB__ANYELEMENT__NAMESPACE = PLUGIN_NAME + ".xsdup0400";
  public static final String GENERAL_TAB__ANYELEMENT__PROCESSCONTENTS = PLUGIN_NAME + ".xsdup0410";
  public static final String GENERAL_TAB__ANYELEMENT__MIN_OCCURENCE = PLUGIN_NAME + ".xsdup0180";
  public static final String GENERAL_TAB__ANYELEMENT__MAX_OCCURENCE = PLUGIN_NAME + ".xsdup0190";
  
  public static final String GENERAL_TAB__ANYATTRIBUTE__NAMESPACE = PLUGIN_NAME + ".xsdup0400";  // these 4 are not used, same as anyelement above
  public static final String GENERAL_TAB__ANYATTRIBUTE__PROCESSCONTENTS = PLUGIN_NAME + ".xsdup0410";
  public static final String GENERAL_TAB__ANYATTRIBUTE__MIN_OCCURENCE = PLUGIN_NAME + ".xsdup0180";
  public static final String GENERAL_TAB__ANYATTRIBUTE__MAX_OCCURENCE = PLUGIN_NAME + ".xsdup0190";
  
  public static final String GENERAL_TAB__MODELGROUP__KIND = PLUGIN_NAME + ".xsdup0420";
  public static final String GENERAL_TAB__MODELGROUP__MIN_OCCURENCE = PLUGIN_NAME + ".xsdup0180"; //can't invoke help
  public static final String GENERAL_TAB__MODELGROUP__MAX_OCCURENCE = PLUGIN_NAME + ".xsdup0190"; // can't invoke help
  
  public static final String GENERAL_TAB__MODELGROUP_REFS__REF = PLUGIN_NAME + ".xsdup0430";
  
  public static final String GENERAL_TAB__ATTRIBUTEGROUP_REFS__REF = PLUGIN_NAME + ".xsdup0440";
  
}
