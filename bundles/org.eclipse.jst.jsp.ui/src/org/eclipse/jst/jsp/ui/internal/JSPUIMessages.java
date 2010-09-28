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
package org.eclipse.jst.jsp.ui.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by JSP UI
 * 
 * @plannedfor 1.0
 */
public class JSPUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jst.jsp.ui.internal.JSPUIPluginResources";//$NON-NLS-1$
	private static ResourceBundle fResourceBundle;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JSPUIMessages.class);
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}

	public static String Sample_JSP_doc;
	public static String JSP_Delimiters_UI_;
	public static String Refactor_label;
	public static String RenameElement_label; // resource bundle
	public static String MoveElement_label; // resource bundle
	public static String MoveElementWizard;
	public static String OK;
	public static String JSP_changes;
	public static String ActionContributorJSP_0;
	public static String JSPRenameElementAction_0;
	public static String JSPMoveElementAction_0;
	public static String BasicRefactorSearchRequestor_0;
	public static String BasicRefactorSearchRequestor_1;
	public static String BasicRefactorSearchRequestor_2;
	public static String BasicRefactorSearchRequestor_3;
	public static String BasicRefactorSearchRequestor_4;
	public static String BasicRefactorSearchRequestor_5;
	public static String BasicRefactorSearchRequestor_6;
	public static String BasicRefactorSearchRequestor_7;
	public static String BreakpointNotAllowed;
	public static String _UI_WIZARD_NEW_TITLE;
	public static String _UI_WIZARD_NEW_HEADING;
	public static String _UI_WIZARD_NEW_DESCRIPTION;
	public static String _UI_WIZARD_TAG_NEW_TITLE;
	public static String _UI_WIZARD_TAG_NEW_HEADING;
	public static String _UI_WIZARD_TAG_NEW_DESCRIPTION;
	public static String _ERROR_FILENAME_MUST_END_JSP;
	public static String _WARNING_FILE_MUST_BE_INSIDE_JAVA_PROJECT;
	public static String _WARNING_FOLDER_MUST_BE_INSIDE_WEB_CONTENT;
	public static String ResourceGroup_nameExists;
	public static String NewJSPTemplatesWizardPage_0;
	public static String NewJSPTemplatesWizardPage_1;
	public static String NewJSPTemplatesWizardPage_2;
	public static String NewJSPTemplatesWizardPage_3;
	public static String NewJSPTemplatesWizardPage_4;
	public static String NewJSPTemplatesWizardPage_5;
	public static String NewJSPTemplatesWizardPage_6;
	public static String NewJSPTemplatesWizardPage_7;
	public static String NewTagTemplatesWizardPage_0;
	public static String NewTagTemplatesWizardPage_1;
	public static String NewTagTemplatesWizardPage_2;
	public static String NewTagTemplatesWizardPage_3;
	public static String NewTagTemplatesWizardPage_4;
	public static String NewTagTemplatesWizardPage_5;
	public static String NewTagTemplatesWizardPage_6;
	public static String NewTagTemplatesWizardPage_7;
	public static String ToggleComment_label; // resource bundle
	public static String ToggleComment_tooltip; // resource bundle
	public static String ToggleComment_description; // resource bundle
	public static String AddBlockComment_label; // resource bundle
	public static String AddBlockComment_tooltip; // resource bundle
	public static String AddBlockComment_description; // resource bundle
	public static String RemoveBlockComment_label; // resource bundle
	public static String RemoveBlockComment_tooltip; // resource bundle
	public static String RemoveBlockComment_description; // resource bundle
	public static String CleanupDocument_label; // resource bundle
	public static String CleanupDocument_tooltip; // resource bundle
	public static String CleanupDocument_description; // resource bundle
	public static String FindOccurrences_label;	// resource bundle
	public static String OccurrencesSearchQuery_0;
	public static String OccurrencesSearchQuery_2;
	public static String Override_method_in;
	public static String Creating_files_encoding;
	public static String Content_Assist_not_availab_UI_;
	public static String Java_Content_Assist_is_not_UI_;
	public static String JSPSourcePreferencePage_0;
	public static String JSPSourcePreferencePage_1;
	public static String JSPSourcePreferencePage_2;
	public static String JSPColorPage_jsp_content;
	public static String JSPFilesPreferencePage_0;
	public static String JSPFilesPreferencePage_1;
	public static String JSPFContentSettingsPropertyPage_0;
	public static String JSPFContentSettingsPropertyPage_1;
	public static String JSPFContentSettingsPropertyPage_2;
	public static String JSPFContentSettingsPropertyPage_3;
	public static String JSPFContentSettingsPropertyPage_4;
	public static String ProjectJSPFContentSettingsPropertyPage_0;
	public static String TagPropertyPage_desc;
	public static String Title_InvalidValue;
	public static String Message_InvalidValue;
	public static String SyntaxColoringPage_0;
	public static String SyntaxColoringPage_2;
	public static String SyntaxColoringPage_3;
	public static String SyntaxColoringPage_4;
	public static String SyntaxColoringPage_5;
	public static String SyntaxColoringPage_6;
	public static String _UI_STRUCTURED_TEXT_EDITOR_PREFS_LINK;
	
	public static String JSPTyping_Auto_Complete;
	public static String JSPTyping_Complete_Scriptlets;
	public static String JSPTyping_Complete_Braces;
	public static String JSPTyping_Complete_Comments;
	public static String JSPTyping_Java_Code;
	public static String JSPTyping_Close_Strings;
	public static String JSPTyping_Close_Brackets;
	public static String JSPValidationPreferencePage_0;

	// below are the strings for the validation page
	public static String Validation_description;
	public static String Validation_Warning;
	public static String Validation_Error;
	public static String Validation_Ignore;

	public static String VALIDATION_HEADER_DIRECTIVE;
	public static String VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS;
	public static String VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS;
	public static String VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX;
	public static String VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR;
	public static String VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR;
	public static String VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND;
	public static String VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED;
	public static String VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND;
	
	public static String VALIDATION_HEADER_JAVA;
	public static String VALIDATION_JAVA_NOTICE;
	public static String VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED;
	public static String VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED;
	public static String VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE;
	public static String VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE;
	public static String VALIDATION_JAVA_UNUSED_IMPORT;

	public static String VALIDATION_HEADER_EL;
	public static String VALIDATION_EL_SYNTAX;
	public static String VALIDATION_EL_LEXER;
	public static String VALIDATION_EL_FUNCTION_UNDEFINED;

	public static String VALIDATION_HEADER_CUSTOM_ACTIONS;
	public static String VALIDATION_ACTIONS_SEVERITY_MISSING_REQUIRED_ATTRIBUTE;
	public static String VALIDATION_ACTIONS_SEVERITY_UNKNOWN_ATTRIBUTE;
	public static String VALIDATION_ACTIONS_SEVERITY_UNEXPECTED_RTEXPRVALUE;
	public static String VALIDATION_ACTIONS_SEVERITY_NON_EMPTY_INLINE_TAG;
	public static String VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE;
	public static String VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND;
	public static String VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED;
	public static String VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION;
	public static String VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND;

	public static String VALIDATION_HEADER_STANDARD_ACTIONS;
	public static String VALIDATION_TRANSLATION_USEBEAN_INVALID_ID;
	public static String VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO;
	public static String VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO;

	public static String Open;
	public static String TLDHyperlink_hyperlinkText;
	public static String CustomTagHyperlink_hyperlinkText;
	public static String TLDContentOutlineConfiguration_0;

	public static String JSPFilesPreferencePage_Search_group;
	public static String JSPFilesPreferencePage_Supply_JSP_search_to_Java_search;
	
	public static String JSPCodeAssist_Insertion;
	public static String JSPCodeAssist_Auto_Import;
	public static String Cycling_UI;
}
