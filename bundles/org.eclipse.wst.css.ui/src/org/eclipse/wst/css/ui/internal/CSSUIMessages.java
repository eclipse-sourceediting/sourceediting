/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.css.ui.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class CSSUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.css.ui.internal.CSSUIPluginResources";//$NON-NLS-1$
	private static ResourceBundle fResourceBundle;

	public static String INFO_Not_Categorized_1;
	public static String PrefsLabel_WrappingWithoutAttr;
	public static String PrefsLabel_WrappingInsertLineBreak;
	public static String PrefsLabel_CaseGroup;
	public static String PrefsLabel_CaseIdent;
	public static String PrefsLabel_CasePropName;
	public static String PrefsLabel_CasePropValue;
	public static String PrefsLabel_CaseIdentUpper;
	public static String PrefsLabel_CaseIdentLower;
	public static String PrefsLabel_CasePropNameUpper;
	public static String PrefsLabel_CasePropNameLower;
	public static String PrefsLabel_CasePropValueUpper;
	public static String PrefsLabel_CasePropValueLower;
	public static String PrefsLabel_ColorSample;
	public static String PrefsLabel_ColorNormal;
	public static String PrefsLabel_ColorAtmarkRule;
	public static String PrefsLabel_ColorSelector;
	public static String PrefsLabel_ColorMedia;
	public static String PrefsLabel_ColorComment;
	public static String PrefsLabel_ColorPropertyName;
	public static String PrefsLabel_ColorPropertyValue;
	public static String PrefsLabel_ColorUri;
	public static String PrefsLabel_ColorString;
	public static String PrefsLabel_ColorColon;
	public static String PrefsLabel_ColorSemiColon;
	public static String PrefsLabel_ColorCurlyBrace;
	public static String PrefsLabel_ColorError;
	public static String SortAction_0;
	public static String _UI_WIZARD_NEW_TITLE;
	public static String _UI_WIZARD_NEW_HEADING;
	public static String _UI_WIZARD_NEW_DESCRIPTION;
	public static String _ERROR_FILENAME_MUST_END_CSS;
	public static String Title_InvalidValue;
	public static String Message_InvalidValue;
	public static String FormatMenu_label;
	public static String CleanupDocument_label; // resource bundle
	public static String CleanupDocument_tooltip; // resource bundle
	public static String CleanupDocument_description; // resource bundle

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, CSSUIMessages.class);
	}
	
	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;		
	}
}
