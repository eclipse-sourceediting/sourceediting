/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for JSON UI Plugin.
 *
 */
public class JSONUIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.json.ui.internal.JSONUIMessages";//$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

	// Validation
	public static String Not_an_integer;
	public static String Missing_integer;
	
	// Outline
	public static String refreshoutline_0;
	public static String SortAction_0;

	// Syntax color preferences page
	public static String Sample_JSON_doc;
	public static String SyntaxColoringPage_0;
	public static String SyntaxColoringPage_2;
	public static String SyntaxColoringPage_3;
	public static String SyntaxColoringPage_4;
	public static String SyntaxColoringPage_5;
	public static String SyntaxColoringPage_6;
	public static String CURLY_BRACE_UI_;
	public static String COLON_UI_;
	public static String COMMA_UI_;
	public static String NORMAL_UI_;
	public static String OBJECT_KEY_UI_;
	public static String VALUE_BOOLEAN_UI_;
	public static String VALUE_NULL_UI_;
	public static String VALUE_NUMBER_UI_;
	public static String VALUE_STRING_UI_;
	public static String COMMENT_UI_;
	
	// Validation preferences page
	public static String SyntaxValidation_files;
	public static String SyntaxValidation_files_label;
	public static String Severity_error;
	public static String Severity_warning;
	public static String Severity_ignore;
	public static String Missing_start_object;
	public static String Missing_end_object;
	public static String Missing_start_array;
	public static String Missing_end_array;
	
	// Preferences
	public static String EmptyFilePreferencePage_0;
	public static String EncodingSettings_0;
	public static String EncodingSettings_1;
	
	// JSONFilesPreferencePage preferences
	public static String Creating_files;
	public static String Encoding_desc;
	public static String Encoding;
	public static String JSONFilesPreferencePage_ExtensionLabel;
	public static String JSONFilesPreferencePage_ExtensionError;
	
	// JSONContentAssistPreferencePage
	public static String JSONContentAssistPreferencePage_Auto_Activation_UI_;
	public static String Automatically_make_suggest_UI_;
	public static String Auto_Activation_Delay;
	public static String Prompt_when_these_characte_UI_;
	public static String Suggestion_Strategy;
	public static String Suggestion_Strategy_Lax;
	public static String Suggestion_Strategy_Strict;
	public static String Group_label_Insertion;
	public static String Insert_single_proposals;
	public static String JSONContentAssistPreferencePage_Cycling_UI_;
	
	// Content assist
	public static String Content_Assist_not_availab_UI_;
	
	


	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JSONUIMessages.class);
	}

	private JSONUIMessages() {
		// cannot create new instance
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null) {
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
			}
		} catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}
}
