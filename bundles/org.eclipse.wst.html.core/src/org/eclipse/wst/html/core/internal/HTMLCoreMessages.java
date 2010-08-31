/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by HTML Core
 * 
 * @plannedfor 1.0
 */
public class HTMLCoreMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.html.core.internal.HTMLCorePluginResources";//$NON-NLS-1$

	public static String No_error__UI_;
	public static String Undefined_attribute_name___ERROR_;
	public static String Undefined_attribute_value__ERROR_;
	public static String Multiple_values_specified__ERROR_;
	public static String Attribute_name___0___uses__ERROR_;
	public static String Invalid_attribute_name___0_ERROR_;
	public static String Invalid_attribute___0____ERROR_;
	public static String Invalid_location_of_tag____ERROR_;
	public static String Duplicate_tag___0____ERROR_;
	public static String No_start_tag____0_____ERROR_;
	public static String No_end_tag_____0_____ERROR_;
	public static String End_tag_____0____not_neede_ERROR_;
	public static String Unknown_tag___0____ERROR_;
	public static String Tag_name___0___uses_wrong__ERROR_;
	public static String Invalid_tag_name___0____ERROR_;
	public static String Invalid_JSP_directive___0__ERROR_;
	public static String Invalid_text_string___0____ERROR_;
	public static String Invalid_character_used_in__ERROR_;
	public static String Unknown_error__ERROR_;
	public static String Start_tag____0____not_clos_ERROR_;
	public static String End_tag_____0____not_close_ERROR_;
	public static String Attribute_value___0___uses_ERROR_;
	public static String Comment_not_closed__ERROR_;
	public static String DOCTYPE_declaration_not_cl_ERROR_;
	public static String Processing_instruction_not_ERROR_;
	public static String CDATA_section_not_closed__ERROR_;
	public static String _ERROR_Tag___0___should_be_an_empty_element_tag_1;
	public static String _ERROR_Attribute_value___0___not_closed__1;
	public static String HTMLContentPropertiesManager_Updating;
	public static String HTMLContentPropertiesManager_Problems_Updating;
	public static String _ERROR_Resource_not_found_0;
	public static String Obsolete_attribute_name___ERROR_;
	public static String Obsolete_tag___ERROR_;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, HTMLCoreMessages.class);
	}
	
	private HTMLCoreMessages() {
		// cannot create new instance
	}
}
