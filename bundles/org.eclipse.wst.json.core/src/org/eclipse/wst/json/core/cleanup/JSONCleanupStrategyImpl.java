/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org/eclipse/wst/xml/core/internal/validation/core/AbstractNestedValidator.java
 *                                           modified in order to process JSON Objects.      
 *******************************************************************************/
package org.eclipse.wst.json.core.cleanup;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;

public class JSONCleanupStrategyImpl implements IJSONCleanupStrategy {

	static private IJSONCleanupStrategy instance = null;
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
	 * JSONCleanupStrategyImpl constructor comment.
	 */
	protected JSONCleanupStrategyImpl() {
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
	 * @return org.eclipse.wst.css.core.internal.cleanup.JSONCleanupStrategy
	 */
	public synchronized static IJSONCleanupStrategy getInstance() {
		if (instance == null)
			instance = new JSONCleanupStrategyImpl();
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
		Preferences prefs = JSONCorePlugin.getDefault().getPluginPreferences();
//		fIdentCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_IDENTIFIER));
//		fPropNameCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_PROPERTY_NAME));
//		fPropValueCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_PROPERTY_VALUE));
//		fSelectorTagCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_SELECTOR));
//		fIdCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_ID_SELECTOR));
//		fClassCase = getCleanupCaseValue(prefs
//				.getInt(JSONCorePreferenceNames.CLEANUP_CASE_CLASS_SELECTOR));
		fQuoteValues = prefs
				.getBoolean(JSONCorePreferenceNames.QUOTE_ATTR_VALUES);
		fFormatSource = prefs.getBoolean(JSONCorePreferenceNames.FORMAT_SOURCE);
	}

	/**
	 * Return the JSONCleanupStrategy equivalent case short value when given an
	 * int
	 * 
	 * @param value
	 * @return equivalent case short or ASIS if cannot be determined
	 */
	private short getCleanupCaseValue(int value) {
		switch (value) {
		case JSONCorePreferenceNames.LOWER:
			return LOWER;
		case JSONCorePreferenceNames.UPPER:
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

	// TODO: a saveOptions should be added to JSONCleanupStrategy interface
	public void saveOptions() {
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_IDENTIFIER,
//						fIdentCase);
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_PROPERTY_NAME,
//						fPropNameCase);
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_PROPERTY_VALUE,
//						fPropValueCase);
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_SELECTOR,
//						fSelectorTagCase);
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_ID_SELECTOR,
//						fIdCase);
//		JSONCorePlugin
//				.getDefault()
//				.getPluginPreferences()
//				.setValue(JSONCorePreferenceNames.CLEANUP_CASE_CLASS_SELECTOR,
//						fClassCase);
		JSONCorePlugin
				.getDefault()
				.getPluginPreferences()
				.setValue(JSONCorePreferenceNames.QUOTE_ATTR_VALUES,
						fQuoteValues);
		JSONCorePlugin.getDefault().getPluginPreferences()
				.setValue(JSONCorePreferenceNames.FORMAT_SOURCE, fFormatSource);
		JSONCorePlugin.getDefault().savePluginPreferences();
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
