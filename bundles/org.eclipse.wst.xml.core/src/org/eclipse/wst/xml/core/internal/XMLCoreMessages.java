/**********************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 * Jesper Steen Moeller - Added XML Catalogs 1.1 support
 **********************************************************************/
package org.eclipse.wst.xml.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by XML Core
 * 
 * @plannedfor 1.0
 */
public class XMLCoreMessages {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xml.core.internal.XMLCorePluginResources";//$NON-NLS-1$
	
	public static String Invalid_character_lt_fo_ERROR_;
	public static String Invalid_character_gt_fo_ERROR_;
	public static String Invalid_character_amp_fo_ERROR_;
	public static String Invalid_character__f_EXC_;
	public static String loading;
	public static String Catalog_entry_key_not_set;
	public static String Catalog_entry_uri_not_set;
	public static String Catalog_rewrite_startString_not_set;
	public static String Catalog_rewrite_prefix_not_set;
	public static String Catalog_suffix_string_not_set;
	public static String Catalog_suffix_uri_not_set;
	public static String Catalog_delegate_prefix_not_set;
	public static String Catalog_delegate_catalog_not_set;
	public static String Catalog_next_catalog_location_uri_not_set;
	public static String Catalog_resolution_null_catalog;
	public static String Catalog_resolution_malformed_url;
	public static String Catalog_resolution_io_exception;
	public static String CMDocument_load_exception;
	public static String End_tag_has_attributes;
	public static String Attribute__is_missing_a_value;
	public static String Attribute__has_no_value;
	public static String Missing_end_tag_;
	public static String Missing_start_tag_;
	public static String ReconcileStepForMarkup_0;
	public static String ReconcileStepForMarkup_1;
	public static String ReconcileStepForMarkup_2;
	public static String ReconcileStepForMarkup_3;
	public static String ReconcileStepForMarkup_4;
	public static String ReconcileStepForMarkup_5;
	public static String ReconcileStepForMarkup_6;
	public static String Indicate_no_grammar_specified_severities_error;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, XMLCoreMessages.class);
	}
	
	private XMLCoreMessages() {
		// cannot create new instance
	}
}
