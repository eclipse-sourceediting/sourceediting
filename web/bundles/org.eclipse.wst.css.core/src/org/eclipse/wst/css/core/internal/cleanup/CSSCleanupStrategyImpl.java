/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.cleanup;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;



public class CSSCleanupStrategyImpl implements CSSCleanupStrategy {

	static private CSSCleanupStrategy instance = null;
	// initialize with defaults
	protected short fIdentCase = ASIS;
	protected short fPropNameCase = ASIS;
	protected short fPropValueCase = ASIS;
	protected short fSelectorTagCase = UPPER;
	protected boolean fQuoteValues = true;
	protected boolean fFormatSource = true;
	protected short fClassCase = ASIS;
	protected short fIdCase = ASIS;

	/**
	 * CSSCleanupStrategyImpl constructor comment.
	 */
	protected CSSCleanupStrategyImpl() {
		super();
		initialize();
	}

	/**
	 * 
	 * @return short
	 */
	public short getIdentCase() {
		return fIdentCase;
	}

	/**
	 * 
	 * @return org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy
	 */
	public synchronized static CSSCleanupStrategy getInstance() {
		if (instance == null)
			instance = new CSSCleanupStrategyImpl();
		return instance;
	}

	/**
	 * 
	 * @return short
	 */
	public short getPropNameCase() {
		return fPropNameCase;
	}

	/**
	 * 
	 * @return short
	 */
	public short getPropValueCase() {
		return fPropValueCase;
	}

	/**
	 * 
	 * @return short
	 */
	public short getSelectorTagCase() {
		return fSelectorTagCase;
	}

	/**
	 * 
	 */
	private void initialize() {
		Preferences prefs = CSSCorePlugin.getDefault().getPluginPreferences();
		fIdentCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_IDENTIFIER));
		fPropNameCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_NAME));
		fPropValueCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_VALUE));
		fSelectorTagCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_SELECTOR));
		fIdCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_ID_SELECTOR));
		fClassCase = getCleanupCaseValue(prefs.getInt(CSSCorePreferenceNames.CLEANUP_CASE_CLASS_SELECTOR));
		fQuoteValues = prefs.getBoolean(CSSCorePreferenceNames.QUOTE_ATTR_VALUES);
		fFormatSource = prefs.getBoolean(CSSCorePreferenceNames.FORMAT_SOURCE);
	}

	/**
	 * Return the CSSCleanupStrategy equivalent case short value when given an
	 * int
	 * 
	 * @param value
	 * @return equivalent case short or ASIS if cannot be determined
	 */
	private short getCleanupCaseValue(int value) {
		switch (value) {
			case CSSCorePreferenceNames.LOWER :
				return LOWER;
			case CSSCorePreferenceNames.UPPER :
				return UPPER;
		}
		return ASIS;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isFormatSource() {
		return fFormatSource;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isQuoteValues() {
		return fQuoteValues;
	}

	/**
	 * 
	 * @param formatSource
	 *            boolean
	 */
	public void setFormatSource(boolean formatSource) {
		fFormatSource = formatSource;
	}

	/**
	 * 
	 * @param identCase
	 *            short
	 */
	public void setIdentCase(short identCase) {
		fIdentCase = identCase;
	}

	/**
	 * 
	 * @param propNameCase
	 *            short
	 */
	public void setPropNameCase(short propNameCase) {
		fPropNameCase = propNameCase;
	}

	/**
	 * 
	 * @param propValueCase
	 *            short
	 */
	public void setPropValueCase(short propValueCase) {
		fPropValueCase = propValueCase;
	}

	/**
	 * 
	 * @param quoteValues
	 *            boolean
	 */
	public void setQuoteValues(boolean quoteValues) {
		fQuoteValues = quoteValues;
	}

	/**
	 * 
	 * @param selectorTagCase
	 *            short
	 */
	public void setSelectorTagCase(short selectorTagCase) {
		fSelectorTagCase = selectorTagCase;
	}

	// TODO: a saveOptions should be added to CSSCleanupStrategy interface
	public void saveOptions() {
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_IDENTIFIER, fIdentCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_NAME, fPropNameCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_VALUE, fPropValueCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_SELECTOR, fSelectorTagCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_ID_SELECTOR, fIdCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.CLEANUP_CASE_CLASS_SELECTOR, fClassCase);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.QUOTE_ATTR_VALUES, fQuoteValues);
		CSSCorePlugin.getDefault().getPluginPreferences().setValue(CSSCorePreferenceNames.FORMAT_SOURCE, fFormatSource);
		CSSCorePlugin.getDefault().savePluginPreferences();
	}

	public short getClassSelectorCase() {
		return fClassCase;
	}

	public short getIdSelectorCase() {
		return fIdCase;
	}

	public void setClassSelectorCase(short classSelectorCase) {
		fClassCase = classSelectorCase;
	}

	public void setIdSelectorCase(short idSelectorCase) {
		fIdCase = idSelectorCase;
	}
}
