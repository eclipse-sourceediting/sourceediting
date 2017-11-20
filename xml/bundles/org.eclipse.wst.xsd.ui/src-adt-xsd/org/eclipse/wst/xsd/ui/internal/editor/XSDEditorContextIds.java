/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
 * Context help id constants.
 */
public interface XSDEditorContextIds 
{
  public static final String PLUGIN_NAME = "org.eclipse.wst.xsd.ui.internal";

  /* CONTEXT_IDs New XSD Wizard uses the WizardNewFileCreationPage from org.eclipse.ui.dialogs */
 
  /* CONTEXT_IDs for XSDEditor follow the xsdexxx context IDs */

  /* CONTEXT_ID xsde0010 for XSD Editor Design View */
  public static final String XSDE_SCHEMA_DESIGN_VIEW      = PLUGIN_NAME + ".xsde0010";
  /* no CONTEXT_ID for File Name Text Edit (not editable) */
  /* CONTEXT_ID xsde0020 for Version Text Edit */
  public static final String XSDE_SCHEMA_VERSION          = PLUGIN_NAME + ".xsde0020";
  /* CONTEXT_ID xsde0030 for Language Text Edit */
  public static final String XSDE_SCHEMA_LANGUAGE         = PLUGIN_NAME + ".xsde0030";
  /* CONTEXT_ID xsde0040 for Namespace Group */
  public static final String XSDE_SCHEMA_NAMESPACE_GROUP  = PLUGIN_NAME + ".xsde0040";
  /* CONTEXT_ID xsde0050 for Prefix Text Edit */
  public static final String XSDE_SCHEMA_PREFIX           = PLUGIN_NAME + ".xsde0050";
  /* CONTEXT_ID xsde0060 for Target namespace Text Edit */
  public static final String XSDE_SCHEMA_TARGET_NAMESPACE = PLUGIN_NAME + ".xsde0060";
  /* CONTEXT_ID xsde0070 for Apply Push Button */
  public static final String XSDE_SCHEMA_APPLY            = PLUGIN_NAME + ".xsde0070";
  /* CONTEXT_ID xsde0080 for Attribute form default Combo Box */
  public static final String XSDE_SCHEMA_ATTRIBUTE        = PLUGIN_NAME + ".xsde0080";
  /* CONTEXT_ID xsde0090 for Element form default Combo Box */
  public static final String XSDE_SCHEMA_ELEMENT          = PLUGIN_NAME + ".xsde0090";
  /* CONTEXT_ID xsde0100 for Block default Combo Box */
  public static final String XSDE_SCHEMA_BLOCK            = PLUGIN_NAME + ".xsde0100";
  /* CONTEXT_ID xsde0110 for Final Default Combo Box */
  public static final String XSDE_SCHEMA_FINAL            = PLUGIN_NAME + ".xsde0110";

  
  /* CONTEXT_ID xsde0200 for Annotations Comment Group - only used generically */
  /* CONTEXT_ID      - used in Documentation Design View */
  /* CONTEXT_ID      - used in App Info Design View */
  public static final String XSDE_ANNOTATION_COMMENT_GROUP = PLUGIN_NAME + ".xsde0200";
  /* CONTEXT_ID xsde0210 for Annotations Comment Group - only used generically */
  /* CONTEXT_ID      - used in Documentation Design View */
  /* CONTEXT_ID      - used in App Info Design View */
  public static final String XSDE_ANNOTATION_COMMENT       = PLUGIN_NAME + ".xsde0210";
  
  /* CONTEXT_ID xsde0300 for Documentation Design View */
  public static final String XSDE_DOCUMENTATION_DESIGN_VIEW   = PLUGIN_NAME + ".xsde0300";
  /* CONTEXT_ID xsde0310 for Source Text Edit */
  public static final String XSDE_DOCUMENTATION_SOURCE        = PLUGIN_NAME + ".xsde0310";
  /* CONTEXT_ID xsde0320 for Language Text Edit */
  public static final String XSDE_DOCUMENTATION_LANGUAGE      = PLUGIN_NAME + ".xsde0320";
  /* CONTEXT_ID Comment Group is from Annotations Window xsde0200 */
  /* CONTEXT_ID Comment Multi-line Edit is from Annotations Window xsd0210 */

  /* CONTEXT_ID xsde0400 for App Info Design View */
  public static final String XSDE_APP_INFO_DESIGN_VIEW = PLUGIN_NAME + ".xsde0400";
  /* CONTEXT_ID xsde0410 for App Info Source Text Edit */
  public static final String XSDE_APP_INFO_SOURCE = PLUGIN_NAME + ".xsde0410";
  /* CONTEXT_ID Comment Group is from Annotations Window xsde0200 */
  /* CONTEXT_ID Comment Multi-line Edit is from Annotations Window xsd0210 */

  /* CONTEXT_ID xsde0500 for Complex Type Design View */
  public static final String XSDE_COMPLEX_DESIGN_VIEW = PLUGIN_NAME + ".xsde0500";
  /* CONTEXT_ID xsde0510 for Name Text Edit */
  public static final String XSDE_COMPLEX_NAME        = PLUGIN_NAME + ".xsde0510";
  /* CONTEXT_ID xsde0520 for Abstract Combo Box */
  public static final String XSDE_COMPLEX_ABSTRACT    = PLUGIN_NAME + ".xsde0520";
  /* CONTEXT_ID xsde0530 for Mixed Combo Box */
  public static final String XSDE_COMPLEX_MIXED       = PLUGIN_NAME + ".xsde0530";
  /* CONTEXT_ID xsde0540 for Block Combo Box */
  public static final String XSDE_COMPLEX_BLOCK       = PLUGIN_NAME + ".xsde0540";
  /* CONTEXT_ID xsde0550 for Final Combo Box */
  public static final String XSDE_COMPLEX_FINAL       = PLUGIN_NAME + ".xsde0550";

  /* CONTEXT_ID xsde0600 for Simple Type Design View */
  public static final String XSDE_SIMPLE_DESIGN_VIEW = PLUGIN_NAME + ".xsde0600";
  /* CONTEXT_ID xsde0610 for Name Text Edit */
  public static final String XSDE_SIMPLE_NAME        = PLUGIN_NAME + ".xsde0610";

  /* CONTEXT_ID for Global Element and Element Design Views are the same */
  /* CONTEXT_ID xsde0700 for Element Design View */
  public static final String XSDE_ELEMENT_DESIGN_VIEW = PLUGIN_NAME + ".xsde0700";
  /* CONTEXT_ID xsde0710 for Element Name Text Edit */
  public static final String XSDE_ELEMENT_NAME         = PLUGIN_NAME + ".xsde0710";
  /* CONTEXT_ID Type Information Group is from Type Helper xsde0900 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID User-defined complex type Radio Button is from Type Helper xsde0940 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */
  /* CONTEXT_ID xsde0720 for Abstract Check Box */
  public static final String XSDE_ELEMENT_ABSTRACT     = PLUGIN_NAME + ".xsde0720";
  /* CONTEXT_ID xsde0730 for Nillable Check Box */
  public static final String XSDE_ELEMENT_NILLABLE     = PLUGIN_NAME + ".xsde0730";
  /* CONTEXT_ID xsde0740 for Value Group */
  public static final String XSDE_ELEMENT_VALUE        = PLUGIN_NAME + ".xsde0740";
  /* CONTEXT_ID xsde0750 for Fixed Radio Button */
  public static final String XSDE_ELEMENT_FIXED        = PLUGIN_NAME + ".xsde0750";
  /* CONTEXT_ID xsde0760 for Default Radio Button */
  public static final String XSDE_ELEMENT_DEFAULT      = PLUGIN_NAME + ".xsde0760";
  /* CONTEXT_ID xsde0770 for Value Group */
  public static final String XSDE_ELEMENT_VALUE_GROUP  = PLUGIN_NAME + ".xsde0770";
  /* CONTEXT_ID xsde0780 for Minimum Text Edit */
  public static final String XSDE_ELEMENT_MINIMUM      = PLUGIN_NAME + ".xsde0780";
  /* CONTEXT_ID xsde0790 for Maximum Text Edit */
  public static final String XSDE_ELEMENT_MAXIMUM      = PLUGIN_NAME + ".xsde0790";
  /* CONTEXT_ID xsde0800 for Block Combo Box */
  public static final String XSDE_ELEMENT_BLOCK        = PLUGIN_NAME + ".xsde0800";
  /* CONTEXT_ID xsde0810 for Final Combo Box */
  public static final String XSDE_ELEMENT_FINAL        = PLUGIN_NAME + ".xsde0810";
  /* CONTEXT_ID xsde0820 for Substitution Group Combo Box */
  public static final String XSDE_ELEMENT_SUBSTITUTION = PLUGIN_NAME + ".xsde0820";
  /* CONTEXT_ID xsde0830 for Form Qualification Combo Box */                    
  public static final String XSDE_ELEMENT_FORM         = PLUGIN_NAME + ".xsde0830";

  /* CONTEXT_ID xsde0900 for Type Helper Group - only used generically */
  /* CONTEXT_ID      - used in Global Element Design View */
  /* CONTEXT_ID      - used in Global Attribute Design View */
  /* CONTEXT_ID      - used in Simple Content Design View */
  /* CONTEXT_ID      - used in Restriction Design View */
  /* CONTEXT_ID      - used in List Design View */
  /* CONTEXT_ID      - used in Union Design View */
  public static final String XSDE_TYPE_HELPER_GROUP    = PLUGIN_NAME + ".xsde0900";
  /* CONTEXT_ID xsde0910 for None Radio Button - only used generically */
  /* CONTEXT_ID      - used in Simple Content Design View */
  /* CONTEXT_ID      - used in Restriction Design View */
  /* CONTEXT_ID      - used in List Design View */
  /* CONTEXT_ID      - used in Union Design View */
  public static final String XSDE_TYPE_HELPER_NONE     = PLUGIN_NAME + ".xsde0910";
  /* CONTEXT_ID xsde0920 for Built-in simple type Radio Button - only used generically */
  /* CONTEXT_ID      - used in Global Element Design View */
  /* CONTEXT_ID      - used in Global Attribute Design View */
  /* CONTEXT_ID      - used in Simple Content Design View */
  /* CONTEXT_ID      - used in Restriction Design View */
  /* CONTEXT_ID      - used in List Design View */
  /* CONTEXT_ID      - used in Union Design View */
  public static final String XSDE_TYPE_HELPER_BUILT_IN = PLUGIN_NAME + ".xsde0920";
  /* CONTEXT_ID xsde0930 for User-defined simple type Radio Button - only used generically */
  /* CONTEXT_ID      - used in Global Element Design View */
  /* CONTEXT_ID      - used in Global Attribute Design View */
  /* CONTEXT_ID      - used in Simple Content Design View */
  /* CONTEXT_ID      - used in Restriction Design View */
  /* CONTEXT_ID      - used in List Design View */
  /* CONTEXT_ID      - used in Union Design View */
  public static final String XSDE_TYPE_HELPER_USER_DEFINED_SIMPLE = PLUGIN_NAME + ".xsde0930";
  /* CONTEXT_ID xsde0940 for User-defined complex type Radio Button - only used generically */
  /* CONTEXT_ID      - used in Global Element Design View */
  public static final String XSDE_TYPE_HELPER_USER_DEFINED_COMPLEX = PLUGIN_NAME + ".xsde0940";
  /* CONTEXT_ID xsde0950 for Type information Combo Box - only used generically */
  /* CONTEXT_ID      - used in Global Element Design View */
  /* CONTEXT_ID      - used in Global Attribute Design View */
  /* CONTEXT_ID      - used in Simple Content Design View */
  /* CONTEXT_ID      - used in Restriction Design View */
  /* CONTEXT_ID      - used in List Design View */
  public static final String XSDE_TYPE_HELPER_TYPE = PLUGIN_NAME + ".xsde0950";

  /* CONTEXT_ID xsde1000 for Attribute Design View */
  public static final String XSDE_ATTRIBUTE_DESIGN_VIEW = PLUGIN_NAME + ".xsde1000";
  /* CONTEXT_ID xsde1010 for Attribute Name Text Edit */
  public static final String XSDE_ATTRIBUTE_NAME        = PLUGIN_NAME + ".xsde1010";
  /* CONTEXT_ID Type Information Group is from Type Helper xsde0900 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */
  /* CONTEXT_ID xsde1020 for Value Group */
  public static final String XSDE_ATTRIBUTE_VALUE_GROUP = PLUGIN_NAME + ".xsde1020";
  /* CONTEXT_ID xsde1030 for Fixed Radio Button */
  public static final String XSDE_ATTRIBUTE_FIXED       = PLUGIN_NAME + ".xsde1030";
  /* CONTEXT_ID xsde1040 for Default Radio Button */
  public static final String XSDE_ATTRIBUTE_DEFAULT     = PLUGIN_NAME + ".xsde1040";
  /* CONTEXT_ID xsde1050 for Value Text Edit */
  public static final String XSDE_ATTRIBUTE_VALUE       = PLUGIN_NAME + ".xsde1050";
  /* CONTEXT_ID xsde1060 for Usage Combo Box */
  public static final String XSDE_ATTRIBUTE_USAGE       = PLUGIN_NAME + ".xsde1060";
  /* CONTEXT_ID xsde1070 for Form qualificaiton Combo Box */
  public static final String XSDE_ATTRIBUTE_FORM        = PLUGIN_NAME + ".xsde1070";

  /* CONTEXT_ID xsde1100 for Element Ref Window Design View */
  public static final String XSDE_ELEMENT_REF_DESIGN_VIEW = PLUGIN_NAME + ".xsde1100";
  /* CONTEXT_ID xsde1110 for Reference Name Combo Box */
  public static final String XSDE_ELEMENT_REF_REFERENCE   = PLUGIN_NAME + ".xsde1110";
  /* CONTEXT_ID xsde1120 for Minimum Text Edit */
  public static final String XSDE_ELEMENT_REF_MINIMUM     = PLUGIN_NAME + ".xsde1120";
  /* CONTEXT_ID xsde1130 for Maximum Text Edit */
  public static final String XSDE_ELEMENT_REF_MAXIMUM     = PLUGIN_NAME + ".xsde1130";
  
  /* CONTEXT_ID xsde1200 for Simple Content Design View - used generically */
  /* CONTEXT_ID      - used in Simple Content Design View */ 
  /* CONTEXT_ID      - used in Complex Content Design View */
    public static final String XSDE_SIMPLE_CONTENT_DESIGN_VIEW = PLUGIN_NAME + ".xsde1200";
  /* CONTEXT_ID Base Type Group is from Type Helper xsde0900 */
  /* CONTEXT_ID None Radio Button is from Type Helper xsde0910 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */
  /* CONTEXT_ID xsde1210 for Derived by Combo Box - used generically */
  /* CONTEXT_ID      - used in Simple Content Design View */ 
  /* CONTEXT_ID      - used in Complex Content Design View */
  public static final String XSDE_SIMPLE_CONTENT_DERIVED = PLUGIN_NAME + ".xsde1210";

  /* CONTEXT_ID xsde1300 for Restriction Design View */
  public static final String XSDE_RESTRICTION_DESIGN_VIEW  = PLUGIN_NAME + ".xsde1300";
  /* CONTEXT_ID Base Type Group is from Type Helper xsde0900 */
  /* CONTEXT_ID None Radio Button is from Type Helper xsde0910 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */
  /* CONTEXT_ID xsde1310 for Facets Group */
  public static final String XSDE_RESTRICTION_FACETS_GROUP = PLUGIN_NAME + ".xsde1310";
  /* CONTEXT_ID xsde1320 for Facets Table */
  public static final String XSDE_RESTRICTION_FACETS       = PLUGIN_NAME + ".xsde1320";

  /* CONTEXT_ID xsde1400 for List Design View */
  public static final String XSDE_LIST_DESIGN_VIEW  = PLUGIN_NAME + ".xsde1400";
  /* CONTEXT_ID Base Type Group is from Type Helper xsde0900 */
  /* CONTEXT_ID None Radio Button is from Type Helper xsde0910 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */

  /* CONTEXT_ID xsde1500 for Attribute Group Design View */
  public static final String XSDE_ATTRIBUTE_GROUP_DESIGN_VIEW = PLUGIN_NAME + ".xsde1500";
  /* CONTEXT_ID xsde1510 for Name Text Edit */
  public static final String XSDE_ATTRIBUTE_GROUP_NAME = PLUGIN_NAME + ".xsde1510";

  /* CONTEXT_ID for Global Attribute and Attribute Design Views are the same */
  /* CONTEXT_ID xsde1600 for Attribute Group Reference Design View */
  public static final String XSDE_ATTRIBUTE_GROUP_REF_DESIGN_VIEW = PLUGIN_NAME + ".xsde1600";
  /* CONTEXT_ID xsde1610 for Reference Name Combo Box */
  public static final String XSDE_ATTRIBUTE_GROUP_REF_NAME = PLUGIN_NAME + ".xsde1610";

  /* CONTEXT_ID xsde1700 for Attribute Reference Design View */
  public static final String XSDE_ATTRIBUTE_REF_DESIGN_VIEW = PLUGIN_NAME + ".xsde1700";
  /* CONTEXT_ID xsde1710 for Reference Name Combo Box */
  public static final String XSDE_ATTRIBUTE_REF_NAME = PLUGIN_NAME + ".xsde1710";

  /* CONTEXT_ID xsde1800 for Pattern Design View */
  public static final String XSDE_PATTERN_DESIGN_VIEW = PLUGIN_NAME + ".xsde1800";
  /* CONTEXT_ID xsde1810 for Value Text Edit */
  public static final String XSDE_PATTERN_VALUE   = PLUGIN_NAME + ".xsde1810";
  /* CONTEXT_ID xsde1820 for Create Regular Expression Push Button */
  public static final String XSDE_PATTERN_REGULAR = PLUGIN_NAME + ".xsde1820";

  /* CONTEXT_ID xsde1900 for Enum Design View */
  public static final String XSDE_ENUM_DESIGN_VIEW = PLUGIN_NAME + ".xsde1900";
  /* CONTEXT_ID xsde1910 for Value Text Edit */
  public static final String XSDE_ENUM_VALUE       = PLUGIN_NAME + ".xsde1910";
  
  /* CONTEXT_ID xsde2000 for Include Design Page */
  public static final String XSDE_INCLUDE_DESIGN_VIEW = PLUGIN_NAME + ".xsde2000";
  /* no CONTEXT_ID for Schema Location Text Edit (not editable) */
  /* CONTEXT_ID Select Push Button is from Include Helper xsde2100 */ 
  
  /* CONTEXT_ID xsde2100 for Include Helper Select Push Button - used generically */
  /* CONTEXT_ID      - used in Include Design View */
  /* CONTEXT_ID      - used in Import Design View */
  public static final String XSDE_INCLUDE_HELPER_SELECT = PLUGIN_NAME + ".xsde2100";

  /* CONTEXT_ID xsde2200 for Import Design Page */
  public static final String XSDE_IMPORT_DESIGN_VIEW = PLUGIN_NAME + ".xsde2200";
  /* no CONTEXT_ID for Schema Location Text Edit (not editable) */
  /* CONTEXT_ID Select Push Button is from Include Helper xsde2100 */
  /* CONTEXT_ID xsde2210 for Prefix Text Edit */
  public static final String XSDE_IMPORT_PREFIX      = PLUGIN_NAME + ".xsde2210";
  /* no CONTEXT_ID for Namespace Text Edit (not editable) */

  /* CONTEXT_ID xsde2300 for Redefine Design View */
  public static final String XSDE_REDEFINE_DESIGN_VIEW = PLUGIN_NAME + ".xsde2300";
  /* no CONTEXT_ID for Schema Location Text Edit (not editable) */
  /* CONTEXT_ID Select Push Button is from Include Helper xsde2100 */

  /* CONTEXT_ID xsde2400 for Group Design View */
  public static final String XSDE_GROUP_DESIGN_VIEW = PLUGIN_NAME + ".xsde2400";
  /* CONTEXT_ID xsde2410 for Name Text Edit */
  public static final String XSDE_GROUP_NAME        = PLUGIN_NAME + ".xsde2410";

  /* CONTEXT_ID xsde2500 for Group Scope Design View */
  public static final String XSDE_GROUP_SCOPE_DESIGN_VIEW   = PLUGIN_NAME + ".xsde2500";
  /* CONTEXT_ID xsde2510 for Content model Group */
  public static final String XSDE_GROUP_SCOPE_CONTENT_GROUP = PLUGIN_NAME + ".xsde2510";
  /* CONTEXT_ID xsde2520 for Sequence Radio Button */
  public static final String XSDE_GROUP_SCOPE_SEQUENCE = PLUGIN_NAME + ".xsde2520";
  /* CONTEXT_ID xsde2530 for Choice Radio Button */
  public static final String XSDE_GROUP_SCOPE_CHOICE   = PLUGIN_NAME + ".xsde2530";
  /* CONTEXT_ID xsde2540 for All Radio Button */
  public static final String XSDE_GROUP_SCOPE_ALL      = PLUGIN_NAME + ".xsde2540";
  /* CONTEXT_ID xsde2550 for Minimum Text Edit */
  public static final String XSDE_GROUP_SCOPE_MINIMUM  = PLUGIN_NAME + ".xsde2550";
  /* CONTEXT_ID xsde2560 for Maximum Text Edit*/
  public static final String XSDE_GROUP_SCOPE_MAXIMUM  = PLUGIN_NAME + ".xsde2560";

  /* CONTEXT_ID xsde2600 for Group Ref Design View */
  public static final String XSDE_GROUP_REF_DESIGN_VIEW = PLUGIN_NAME + ".xsde2600";
  /* CONTEXT_ID xsde2610 for Reference name Combo Box */
  public static final String XSDE_GROUP_REF_REFERENCE   = PLUGIN_NAME + ".xsde2610";
  /* CONTEXT_ID xsde2620 for Minimum Text Edit */
  public static final String XSDE_GROUP_REF_MINIMUM     = PLUGIN_NAME + ".xsde2620";
  /* CONTEXT_ID xsde2630 for Maximum Text Edit */
  public static final String XSDE_GROUP_REF_MAXIMUM     = PLUGIN_NAME + ".xsde2630";

  /* CONTEXT_ID xsde2700 for Unique Design View */
  public static final String XSDE_UNIQUE_DESIGN_VIEW = PLUGIN_NAME + ".xsde2700";
  /* CONTEXT_ID Name Text Edit is from Unique Base xsde2800 */
  /* CONTEXT_ID Selector Group is from Unique Base xsde2810 */
  /* CONTEXT_ID Selector Mulit-line Edit is from Unique Base xsde2820 */
  /* CONTEXT_ID Fields Group is from Unique Base xsde2830 */
  /* CONTEXT_ID Source Text Edit is from Unique Base xsde2840 */
  /* CONTEXT_ID Add Push Button is from Unique Base xsde2850 */
  /* CONTEXT_ID Remove Push Button is from Unique Base xsde2860 */
  /* CONTEXT_ID Target List Box is from Unique Base xsde2870 */
  
  /* CONTEXT_ID xsde2800 for Unique Base Name Text Edit - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_NAME = PLUGIN_NAME + ".xsde2800";
  /* CONTEXT_ID xsde2810 for Selector Group - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_SELECTOR_GROUP = PLUGIN_NAME + ".xsde2810";
  /* CONTEXT_ID xsde2820 for Selector Multi-line Edit - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_SELECTOR       = PLUGIN_NAME + ".xsde2820";
  /* CONTEXT_ID xsde2830 for Fields Group - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_FIELDS_GROUP   = PLUGIN_NAME + ".xsde2830";
  /* CONTEXT_ID xsde2840 for Source Text Edit - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_SOURCE         = PLUGIN_NAME + ".xsde2840";
  /* CONTEXT_ID xsde2850 for Add Push Button - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_ADD            = PLUGIN_NAME + ".xsde2850";
  /* CONTEXT_ID xsde2860 for Remove Push Button - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_REMOVE         = PLUGIN_NAME + ".xsde2860";
  /* CONTEXT_ID xsde2870 for Target List Box - used generically */
  /* CONTEXT_ID      - used in Unique Design View */
  /* CONTEXT_ID      - used in Key Design View */
  /* CONTEXT_ID      - used in Key Ref Design View */
  public static final String XSDE_UNIQUE_BASE_TARGET         = PLUGIN_NAME + ".xsde2870";

  /* CONTEXT_ID xsde2900 for Key Design View */
  public static final String XSDE_KEY_DESIGN_VIEW = PLUGIN_NAME + ".xsde2900";
  /* CONTEXT_ID Name Text Edit is from Unique Base xsde2800 */
  /* CONTEXT_ID Selector Group is from Unique Base xsde2810 */
  /* CONTEXT_ID Selector Mulit-line Edit is from Unique Base xsde2820 */
  /* CONTEXT_ID Fields Group is from Unique Base xsde2830 */
  /* CONTEXT_ID Source Text Edit is from Unique Base xsde2840 */
  /* CONTEXT_ID Add Push Button is from Unique Base xsde2850 */
  /* CONTEXT_ID Remove Push Button is from Unique Base xsde2860 */
  /* CONTEXT_ID Target List Box is from Unique Base xsde2870 */
  /* CONTEXT_ID xsde2900 for Key Design View */
  
  /* CONTEXT_ID xsde2950 for Key Ref Design View */
  public static final String XSDE_KEY_REF_DESIGN_VIEW = PLUGIN_NAME + ".xsde2950";
  /* CONTEXT_ID Name Text Edit is from Unique Base xsde2800 */
  /* CONTEXT_ID xsde2960 for Reference Key Combo Box */
  public static final String XSDE_KEY_REF_REFERENCE = PLUGIN_NAME + ".xsde2960";
  /* CONTEXT_ID Selector Group is from Unique Base xsde2810 */
  /* CONTEXT_ID Selector Mulit-line Edit is from Unique Base xsde2820 */
  /* CONTEXT_ID Fields Group is from Unique Base xsde2830 */
  /* CONTEXT_ID Source Text Edit is from Unique Base xsde2840 */
  /* CONTEXT_ID Add Push Button is from Unique Base xsde2850 */
  /* CONTEXT_ID Remove Push Button is from Unique Base xsde2860 */
  /* CONTEXT_ID Target List Box is from Unique Base xsde2870 */

  /* CONTEXT_ID xsde3000 for Any Element Design View */
  public static final String XSDE_ANY_ELEMENT_VIEW = PLUGIN_NAME + ".xsde3000";
  /* CONTEXT_ID xsde3010 for Namespace Text Edit */
  public static final String XSDE_ANY_ELEMENT_NAMESPACE = PLUGIN_NAME + ".xsde3010";
  /* CONTEXT_ID xsde3020 for Process Contents Combo Box */
  public static final String XSDE_ANY_ELEMENT_PROCESS   = PLUGIN_NAME + ".xsde3020";
  /* CONTEXT_ID xsde3030 for Minimum Text Edit */
  public static final String XSDE_ANY_ELEMENT_MINIMUM   = PLUGIN_NAME + ".xsde3030";
  /* CONTEXT_ID xsde3040 for Maximum Text Edit */
  public static final String XSDE_ANY_ELEMENT_MAXIMUM   = PLUGIN_NAME + ".xsde3040";

  /* CONTEXT_ID xsde3100 for Any Attribute Design View */
  public static final String XSDE_ANY_ATTRIBUTE_VIEW = PLUGIN_NAME + ".xsde3100";
  /* CONTEXT_ID xsde3110 for Namespace Text Edit */
  public static final String XSDE_ANY_ATTRIBUTE_NAMESPACE = PLUGIN_NAME + ".xsde3110";
  /* CONTEXT_ID xsde3120 for Process Contents Combo Box */
  public static final String XSDE_ANY_ATTRIBUTE_PROCESS   = PLUGIN_NAME + ".xsde3120";

  /* no CONTEXT_ID for Union Design View - uses a generic interface */
  /* CONTEXT_ID Type Information Group is from Type Helper xsde0900 */
  /* CONTEXT_ID Built-in simple type Radio Button is from Type Helper xsde0920 */
  /* CONTEXT_ID User-defined simple type Radio Button is from Type Helper xsde0930 */
  /* CONTEXT_ID Type information Combo Box is from Type Helper xsde0950 */

  /* CONTEXT_ID xsde3200 for Notation Design View */
  public static final String XSDE_NOTATION_VIEW = PLUGIN_NAME + ".xsde3200";

  /* CONTEXT_ID xsde4000 for Source View */
  public static final String XSDE_SOURCE_VIEW = PLUGIN_NAME + ".xsde4000";

  /* CONTEXT_IDs for Regular Expression Wizard follow the xsdrxxx context IDs */
  
  /* CONTEXT_ID xsdr0010 for Compose Regular Expression Page */
  public static final String XSDR_COMPOSITION_PAGE         = PLUGIN_NAME + ".xsdr0010";
  /* CONTEXT_ID xsdr0015 for Token Contents Combo Box */
  public static final String XSDR_COMPOSITION_TOKEN = PLUGIN_NAME + ".xsdr0015";
  /* CONTEXT_ID xsdr0020 for Occurrece Group */
  public static final String XSDR_COMPOSITION_OCCURRENCE_GROUP = PLUGIN_NAME + ".xsdr0020";
  /* CONTEXT_ID xsdr0030 for Just once Radio Button */
  public static final String XSDR_COMPOSITION_JUST_ONCE    = PLUGIN_NAME + ".xsdr0030";
  /* CONTEXT_ID xsdr0040 for Zero or more Radio Button */
  public static final String XSDR_COMPOSITION_ZERO_OR_MORE = PLUGIN_NAME + ".xsdr0040";
  /* CONTEXT_ID xsdr0050 for One or more Radio Button */
  public static final String XSDR_COMPOSITION_ONE_OR_MORE  = PLUGIN_NAME + ".xsdr0050";
  /* CONTEXT_ID xsdr0060 for Optional Radio Button */
  public static final String XSDR_COMPOSITION_OPTIONAL     = PLUGIN_NAME + ".xsdr0060";
  /* CONTEXT_ID xsdr0070 for Repeat Radio Button */
  public static final String XSDR_COMPOSITION_REPEAT       = PLUGIN_NAME + ".xsdr0070";
  /* CONTEXT_ID xsdr0080 for Range Radio Button */
  public static final String XSDR_COMPOSITION_RANGE        = PLUGIN_NAME + ".xsdr0080";
  /* CONTEXT_ID xsdr0090 for Repeat Text Edit */
  public static final String XSDR_COMPOSITION_REPEAT_TEXT  = PLUGIN_NAME + ".xsdr0090";
  /* CONTEXT_ID xsdr0100 for Range Minimum Text Edit */
  public static final String XSDR_COMPOSITION_RANGE_MIN    = PLUGIN_NAME + ".xsdr0100";
  /* CONTEXT_ID xsdr0110 for Range Maximum Text Edit */
  public static final String XSDR_COMPOSITION_RANGE_MAX    = PLUGIN_NAME + ".xsdr0110";
  /* CONTEXT_ID xsdr0120 for Add Push Button */
  public static final String XSDR_COMPOSITION_ADD          = PLUGIN_NAME + ".xsdr0120";
  /* CONTEXT_ID xsdr0130 for Current Regular Expression Text Edit */
  public static final String XSDR_COMPOSITION_CURRENT      = PLUGIN_NAME + ".xsdr0130";

  /* CONTEXT_ID xsdr0200 for Test Regular Expression Page */
  public static final String XSDR_TEST_PAGE   = PLUGIN_NAME + ".xsdr0200";
  /* no CONTEXT_ID for Regular Expression Text Edit (not editable) */
  /* CONTEXT_ID xsdr0210 for Sample Text Text Edit */
  public static final String XSDR_TEST_SAMPLE = PLUGIN_NAME + ".xsdr0210";

  /* CONTEXT_IDs for Preferences Page follows the xsdpxxx context IDs */
  
  /* CONTEXT_ID xsdp0010 for XML Schema Preferences Page */
  public static final String XSDP_PREFERENCE_PAGE = PLUGIN_NAME + ".xsdp0010";
  /* CONTEXT_ID xsdp0020 for XML Schema Editor Preferences Page */
  public static final String XSDP_EDITOR_PREFERENCE_PAGE = PLUGIN_NAME + ".xsdp0020";
  /* CONTEXT_ID xsdp0030 for XML Schema Validator Preferences Page */
  public static final String XSDP_VALIDATOR_PREFERENCE_PAGE = PLUGIN_NAME + ".xsdp0030";
}
