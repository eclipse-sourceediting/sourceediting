/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui;

/**
 * @deprecated use org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds instead TODO remove
 */
public interface IHelpContextIds {
	// org.eclipse.wst.sse.ui.
	public static final String PREFIX = EditorPlugin.ID + "."; //$NON-NLS-1$

	// Structured Text Editor Preference Page
	// Appearance - Show line numbers, Tab width, etc
	public static final String PREFSRC_SOURCE_PAGE_HELPID = PREFIX + "xmlp1400"; //$NON-NLS-1$
	// Annotations
	public static final String PREF_ANNOTATION_MAIN_HELPID = PREFIX + "xmlp4000"; //$NON-NLS-1$
	public static final String PREF_ANNOTATION_ANALYZE_VALIDITY_HELPID = PREFIX + "xmlp4200"; //$NON-NLS-1$
	public static final String PREF_ANNOTATION_PRESENTATION_HELPID = PREFIX + "xmlp4300"; //$NON-NLS-1$
	// Navigation TODO infopop needed
	// QuickDiff TODO infopop needed
	
	// Web and XML Preference Page
	// Task Tags TODO infopop needed
	// Read-Only Text Style TODO infopop needed
	
	// Abstract Style Preference Page
	// Source Styles page
	public static final String PREFSTL_SOURCE_STYLES_PAGE_HELPID = PREFIX + "xmlp3000"; //$NON-NLS-1$
	// Text highlighting group box
	public static final String PREFSTL_TEXT_HIGHLIGHTING_HELPID = PREFIX + "xmlp3100"; //$NON-NLS-1$
	// Change button (no longer used??)
	/**
	 * @deprecated looks like no one is using this
	 */
	public static final String PREFSTL_CHANGE_BUTTON_HELPID = PREFIX + "xmlp3010"; //$NON-NLS-1$

	// Abstract Template Preference Page (base provides this entire page, so the below infopops are not required)
	// TODO remove these infopops?
	// Template page
	public static final String PREFMAC_MACROS_PAGE_HELPID = PREFIX + "xmlp2000"; //$NON-NLS-1$
	// List of templates, New.., Edit.., Remove, Restore Removed, Revert to Default
	public static final String PREFMAC_MACROS_LIST_HELPID = PREFIX + "xmlp2010"; //$NON-NLS-1$
	public static final String PREFMAC_ENABLED_AT_LOCATION_HELPID = PREFIX + "xmlp2020"; //$NON-NLS-1$
	// Import.., Export..
	// Pattern field
	public static final String PREFMAC_CONTENT_FIELD_HELPID = PREFIX + "xmlp2030"; //$NON-NLS-1$
	// Use code formatter checkbox
	
	// Abstract Source Editor Context Menu
	// Content Assist
	public static final String CONTMNU_CONTENTASSIST_HELPID = PREFIX + "xmlm1010"; //$NON-NLS-1$
	// Format Document
	public static final String CONTMNU_FORMAT_DOC_HELPID = PREFIX + "xmlm1030"; //$NON-NLS-1$
	// Format Active Elements
	public static final String CONTMNU_FORMAT_ELEMENTS_HELPID = PREFIX + "xmlm1040"; //$NON-NLS-1$
	// Cleanup Document
	public static final String CONTMNU_CLEANUP_DOC_HELPID = PREFIX + "xmlm1050"; //$NON-NLS-1$
	// Preferences TODO infopop needed
	// Properties TODO infopop needed
	
	// Source Editor View
	public static final String XML_SOURCE_VIEW_HELPID = PREFIX + "xmlm2000"; //$NON-NLS-1$
	

	
	
	// XML Files Preference Page
	// Line delimiter group
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String PREFFILE_EOL_CODE_HELPID = PREFIX + "xmlp6000"; //$NON-NLS-1$
	// Encoding group
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String PREFFILE_ENCODING_HELPID = PREFIX + "xmlp6100"; //$NON-NLS-1$
	
	// XML Source Preference Page
	// Formatting group - Split lines, Line width, Set one property per line, Split multiple attributes each on a new line, Indent using tabs, Clear all blank lines
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String PREFSRC_FORMATTING_HELPID = PREFIX + "xmlp1200"; //$NON-NLS-1$
	// Content assist group - Automatically make suggestions, Prompt when these characters are inserted
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String PREFSRC_CONTENT_ASSIST_HELPID = PREFIX + "xmlp1300"; //$NON-NLS-1$
	// Using Inferred Grammar
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String PREF_INFERRED_GRAMMAR_HELPID = PREFIX + "xmlp4400"; //$NON-NLS-1$
	
	// XML Cleanup dialog
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.xml.IXMLEditorHelpContextIds
	 */
	public static final String CLEANUP_XML_HELPID = PREFIX + "xmlm1200"; //$NON-NLS-1$
	
	// HTML Source Preference Page
	// Preferred markup case group
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.html.IHTMLEditorHelpContextIds
	 */
	public static final String PREFSRC_PREFERRED_MARKUP_HELPID = PREFIX + "xmlp1500"; //$NON-NLS-1$
	
	// HTML Cleanup dialog
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.html.IHTMLEditorHelpContextIds
	 */
	public static final String CLEANUP_HTML_HELPID = PREFIX + "xmlm1100"; //$NON-NLS-1$
	
	// HTML Content Settings
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.html.IHTMLEditorHelpContextIds
	 */
	public static final String WEB_CONTENT_SETTINGS_HELPID = PREFIX + "misc0170"; //$NON-NLS-1$
	
	// JavaScript Style Preference Page
	// Change button for the JavaScript style page (no longer used??)
	/**
	 * @deprecated looks like no one is using this
	 */
	public static final String PREFSTL_CHANGE_BUTTON_FOR_JS_HELPID = PREFIX + "xmlp3020"; //$NON-NLS-1$
	
	// JSP Fragment Property Page
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.jsp.IJSPEditorHelpContextIds
	 */
	public static final String JSP_FRAGMENT_HELPID = PREFIX + "jspf1000"; //$NON-NLS-1$
	
	// CSS Cleanup dialog
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.css.ICSSEditorHelpContextIds
	 */
	public static final String CLEANUP_CSS_HELPID = PREFIX + "xmlm1300"; //$NON-NLS-1$

	// CSS Content Settings
	/**
	 * @deprecated moved to org.eclipse.wst.sse.ui.css.ICSSEditorHelpContextIds
	 */
	public static final String CSS_CONTENT_SETTINGS_HELPID = PREFIX + "misc0180"; //$NON-NLS-1$
}
