/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.provisional.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;

/**
 * @deprecated just access preferences directly
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
	public String getBetweenValueString() {
		return getPreferences().getString(CSSCorePreferenceNames.FORMAT_BETWEEN_VALUE);
	}

	/**
	 * 
	 */
	public String getIndentString() {
		StringBuffer indent = new StringBuffer();
		
		Preferences preferences = getPreferences();
		if (preferences != null) {
			char indentChar = ' ';
			String indentCharPref = preferences.getString(CSSCorePreferenceNames.INDENTATION_CHAR);
			if (CSSCorePreferenceNames.TAB.equals(indentCharPref)) {
				indentChar = '\t';
			}
			int indentationWidth = preferences.getInt(CSSCorePreferenceNames.INDENTATION_SIZE);
	
			for (int i = 0; i < indentationWidth; i++) {
				indent.append(indentChar);
			}
		}
		return indent.toString();
	}

	/**
	 * 
	 */
	public int getMaxLineWidth() {
		Preferences prefs = getPreferences();
		return prefs.getInt(CSSCorePreferenceNames.LINE_WIDTH);
	}

	/**
	 * 
	 */
	public String getQuoteString(ICSSModel model) {
		// nakamori_TODO css pref transition
		return getPreferences().getString(CSSCorePreferenceNames.FORMAT_QUOTE);
	}

	/**
	 * 
	 */
	public int getSpacesPostDelimiter() {
		return getPreferences().getInt(CSSCorePreferenceNames.FORMAT_PROP_POST_DELIM);
	}

	/**
	 * 
	 */
	public int getSpacesPreDelimiter() {
		return getPreferences().getInt(CSSCorePreferenceNames.FORMAT_PROP_PRE_DELIM);
	}

	/**
	 * 
	 */
	public boolean isIdentUpperCase() {
		if (getPreferences().getInt(CSSCorePreferenceNames.CASE_IDENTIFIER) == CSSCorePreferenceNames.UPPER)
			return true;
		return false;
	}

	/**
	 * 
	 */
	public boolean isNewLineOnOpenBrace() {
		return getPreferences().getBoolean(CSSCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE);
	}

	/**
	 * 
	 */
	public boolean isOnePropertyPerLine() {
		return getPreferences().getBoolean(CSSCorePreferenceNames.WRAPPING_ONE_PER_LINE);
	}

	/**
	 * 
	 */
	public boolean isProhibitWrapOnAttr() {
		return getPreferences().getBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR);
	}

	/**
	 * 
	 */
	public boolean isPropNameUpperCase() {
		if (getPreferences().getInt(CSSCorePreferenceNames.CASE_PROPERTY_NAME) == CSSCorePreferenceNames.UPPER)
			return true;
		return false;
	}

	/**
	 * 
	 */
	public boolean isPropValueUpperCase() {
		if (getPreferences().getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER)
			return true;
		return false;
	}

	/**
	 * 
	 */
	public boolean isQuoteInURI() {
		return getPreferences().getBoolean(CSSCorePreferenceNames.FORMAT_QUOTE_IN_URI);
	}

	/**
	 * 
	 */
	protected void setBetweenValueString(String newBetweenValueString) {
		getPreferences().setValue(CSSCorePreferenceNames.FORMAT_BETWEEN_VALUE, newBetweenValueString);
	}

	/**
	 * 
	 */
	protected void setIdentUpperCase(boolean newIdentUpperCase) {
		int theCase = CSSCorePreferenceNames.LOWER;
		if (newIdentUpperCase)
			theCase = CSSCorePreferenceNames.UPPER;
		getPreferences().setValue(CSSCorePreferenceNames.CASE_IDENTIFIER, theCase);
	}

	/**
	 * 
	 */
	protected void setOnePropertyPerLine(boolean newOnePropertyPerLine) {
		getPreferences().setValue(CSSCorePreferenceNames.WRAPPING_ONE_PER_LINE, newOnePropertyPerLine);
	}

	/**
	 * 
	 */
	protected void setProhibitWrapOnAttr(boolean newProhibitWrapOnAttr) {
		getPreferences().setValue(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR, newProhibitWrapOnAttr);
	}

	/**
	 * 
	 */
	protected void setPropNameUpperCase(boolean newPropNameUpperCase) {
		int theCase = CSSCorePreferenceNames.LOWER;
		if (newPropNameUpperCase)
			theCase = CSSCorePreferenceNames.UPPER;
		getPreferences().setValue(CSSCorePreferenceNames.CASE_PROPERTY_NAME, theCase);
	}

	/**
	 * 
	 */
	protected void setPropValueUpperCase(boolean newPropValueUpperCase) {
		int theCase = CSSCorePreferenceNames.LOWER;
		if (newPropValueUpperCase)
			theCase = CSSCorePreferenceNames.UPPER;
		getPreferences().setValue(CSSCorePreferenceNames.CASE_PROPERTY_VALUE, theCase);
	}

	/**
	 * 
	 */
	public void setQuoteString(String quote) {
		getPreferences().setValue(CSSCorePreferenceNames.FORMAT_QUOTE, quote);
	}

	/**
	 * 
	 */
	public void setSpacesPostDelimiter(int num) {
		getPreferences().setValue(CSSCorePreferenceNames.FORMAT_PROP_POST_DELIM, num);
	}

	/**
	 * 
	 */
	public void setSpacesPreDelimiter(int num) {
		getPreferences().setValue(CSSCorePreferenceNames.FORMAT_PROP_PRE_DELIM, num);
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
