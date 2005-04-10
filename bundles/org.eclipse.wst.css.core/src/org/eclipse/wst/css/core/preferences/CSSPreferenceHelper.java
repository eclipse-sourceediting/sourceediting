/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;

/**
 * this a temp dummy class to map to ModelPreferences
 */
public class CSSPreferenceHelper {

	/**
	 * 
	 */
	public synchronized static CSSPreferenceHelper getInstance() {
		if (fInstance == null) {
			fInstance = new CSSPreferenceHelper();
		}
		return fInstance;
	}

	/**
	 * 
	 */
	public static void createDefaultPreferences(Preferences prefs) {
		// setBooleanAttribute(COLOR_ENABLED, true);
		prefs.setDefault(CSSModelPreferenceNames.FORMAT_PROP_PRE_DELIM, 0);
		prefs.setDefault(CSSModelPreferenceNames.FORMAT_PROP_POST_DELIM, 1);
		prefs.setDefault(CSSModelPreferenceNames.FORMAT_QUOTE, "\"");//$NON-NLS-1$
		prefs.setDefault(CSSModelPreferenceNames.FORMAT_BETWEEN_VALUE, " ");//$NON-NLS-1$
		prefs.setDefault(CSSModelPreferenceNames.FORMAT_QUOTE_IN_URI, true);
		prefs.setDefault(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE, true);
		prefs.setDefault(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR, true);
		prefs.setDefault(CSSModelPreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE, false);
		prefs.setDefault(CSSModelPreferenceNames.CASE_IDENTIFIER, CommonModelPreferenceNames.UPPER);
		prefs.setDefault(CSSModelPreferenceNames.CASE_PROPERTY_NAME, CommonModelPreferenceNames.LOWER);
		prefs.setDefault(CSSModelPreferenceNames.CASE_PROPERTY_VALUE, CommonModelPreferenceNames.LOWER);
		prefs.setDefault(CSSModelPreferenceNames.CASE_PRESERVE_CASE, true);
		// setBooleanAttribute(ASSIST_CATEGORIZE, true);

		// CSS cleanup preferences
		prefs.setDefault(CSSModelPreferenceNames.CLEANUP_CASE_IDENTIFIER, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CSSModelPreferenceNames.CLEANUP_CASE_PROPERTY_NAME, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CSSModelPreferenceNames.CLEANUP_CASE_PROPERTY_VALUE, CommonModelPreferenceNames.ASIS);
		prefs.setDefault(CSSModelPreferenceNames.CLEANUP_CASE_SELECTOR, CommonModelPreferenceNames.ASIS);
	}

	/**
	 * 
	 */
	public String getBetweenValueString() {
		return getPreferences().getString(CSSModelPreferenceNames.FORMAT_BETWEEN_VALUE);
	}

	/**
	 * 
	 */
	public String getIndentString() {
		Preferences prefs = getPreferences();
		boolean bUseTab = prefs.getBoolean(CommonModelPreferenceNames.INDENT_USING_TABS);
		if (bUseTab) {
			return "\t"; //$NON-NLS-1$
		}
		else {
			int n = SSECorePlugin.getDefault().getPluginPreferences().getInt(CommonModelPreferenceNames.TAB_WIDTH);
			StringBuffer buf = new StringBuffer();
			while (0 < n--) {
				buf.append(" "); //$NON-NLS-1$
			}
			return buf.toString();
		}
	}

	/**
	 * 
	 */
	public int getMaxLineWidth() {
		Preferences prefs = getPreferences();
		return prefs.getInt(CommonModelPreferenceNames.LINE_WIDTH);
	}

	/**
	 * 
	 */
	public String getQuoteString(ICSSModel model) {
		// nakamori_TODO css pref transition
		return getPreferences().getString(CSSModelPreferenceNames.FORMAT_QUOTE);
	}

	/**
	 * 
	 */
	public int getSpacesPostDelimiter() {
		return getPreferences().getInt(CSSModelPreferenceNames.FORMAT_PROP_POST_DELIM);
	}

	/**
	 * 
	 */
	public int getSpacesPreDelimiter() {
		return getPreferences().getInt(CSSModelPreferenceNames.FORMAT_PROP_PRE_DELIM);
	}

	/**
	 * 
	 */
	public boolean isIdentUpperCase() {
		if (getPreferences().getInt(CSSModelPreferenceNames.CASE_IDENTIFIER) == CommonModelPreferenceNames.UPPER)
			return true;
		else
			return false;
	}

	/**
	 * 
	 */
	public boolean isNewLineOnOpenBrace() {
		return getPreferences().getBoolean(CSSModelPreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE);
	}

	/**
	 * 
	 */
	public boolean isOnePropertyPerLine() {
		return getPreferences().getBoolean(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE);
	}

	/**
	 * 
	 */
	public boolean isPreserveCase() {
		return getPreferences().getBoolean(CSSModelPreferenceNames.CASE_PRESERVE_CASE);
	}

	/**
	 * 
	 */
	public boolean isProhibitWrapOnAttr() {
		return getPreferences().getBoolean(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR);
	}

	/**
	 * 
	 */
	public boolean isPropNameUpperCase() {
		if (getPreferences().getInt(CSSModelPreferenceNames.CASE_PROPERTY_NAME) == CommonModelPreferenceNames.UPPER)
			return true;
		else
			return false;
	}

	/**
	 * 
	 */
	public boolean isPropValueUpperCase() {
		if (getPreferences().getInt(CSSModelPreferenceNames.CASE_PROPERTY_VALUE) == CommonModelPreferenceNames.UPPER)
			return true;
		else
			return false;
	}

	/**
	 * 
	 */
	public boolean isQuoteInURI() {
		return getPreferences().getBoolean(CSSModelPreferenceNames.FORMAT_QUOTE_IN_URI);
	}

	/**
	 * 
	 */
	protected void setBetweenValueString(String newBetweenValueString) {
		getPreferences().setValue(CSSModelPreferenceNames.FORMAT_BETWEEN_VALUE, newBetweenValueString);
	}

	/**
	 * 
	 */
	protected void setIdentUpperCase(boolean newIdentUpperCase) {
		int theCase = CommonModelPreferenceNames.LOWER;
		if (newIdentUpperCase)
			theCase = CommonModelPreferenceNames.UPPER;
		getPreferences().setValue(CSSModelPreferenceNames.CASE_IDENTIFIER, theCase);
	}

	/**
	 * 
	 */
	protected void setOnePropertyPerLine(boolean newOnePropertyPerLine) {
		getPreferences().setValue(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE, newOnePropertyPerLine);
	}

	/**
	 * 
	 */
	protected void setPreserveCase(boolean newPreserveCase) {
		getPreferences().setValue(CSSModelPreferenceNames.CASE_PRESERVE_CASE, newPreserveCase);
	}

	/**
	 * 
	 */
	protected void setProhibitWrapOnAttr(boolean newProhibitWrapOnAttr) {
		getPreferences().setValue(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR, newProhibitWrapOnAttr);
	}

	/**
	 * 
	 */
	protected void setPropNameUpperCase(boolean newPropNameUpperCase) {
		int theCase = CommonModelPreferenceNames.LOWER;
		if (newPropNameUpperCase)
			theCase = CommonModelPreferenceNames.UPPER;
		getPreferences().setValue(CSSModelPreferenceNames.CASE_PROPERTY_NAME, theCase);
	}

	/**
	 * 
	 */
	protected void setPropValueUpperCase(boolean newPropValueUpperCase) {
		int theCase = CommonModelPreferenceNames.LOWER;
		if (newPropValueUpperCase)
			theCase = CommonModelPreferenceNames.UPPER;
		getPreferences().setValue(CSSModelPreferenceNames.CASE_PROPERTY_VALUE, theCase);
	}

	/**
	 * 
	 */
	public void setQuoteString(String quote) {
		getPreferences().setValue(CSSModelPreferenceNames.FORMAT_QUOTE, quote);
	}

	/**
	 * 
	 */
	public void setSpacesPostDelimiter(int num) {
		getPreferences().setValue(CSSModelPreferenceNames.FORMAT_PROP_POST_DELIM, num);
	}

	/**
	 * 
	 */
	public void setSpacesPreDelimiter(int num) {
		getPreferences().setValue(CSSModelPreferenceNames.FORMAT_PROP_PRE_DELIM, num);
	}

	private Preferences getPreferences() {
		CSSCorePlugin cssModelPlugin = CSSCorePlugin.getDefault();
		return cssModelPlugin.getPluginPreferences();
	}

	private CSSPreferenceHelper() {
		super();
	}

	private static CSSPreferenceHelper fInstance = null;
}