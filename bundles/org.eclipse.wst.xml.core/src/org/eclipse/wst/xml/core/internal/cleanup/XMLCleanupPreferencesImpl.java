/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.cleanup;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.xml.core.internal.provisional.IXMLPreferenceNames;

/**
 * @deprecated renamed to StructuredCleanupPreferences
 * 
 * TODO will delete in C5
 */
public class XMLCleanupPreferencesImpl {

	private static XMLCleanupPreferencesImpl fInstance;

	public synchronized static XMLCleanupPreferencesImpl getInstance() {

		// added for one method in CleanupDialog ... may be better way
		if (fInstance == null) {
			fInstance = new XMLCleanupPreferencesImpl();
		}
		return fInstance;
	}

	private int fAttrNameCase;
	private boolean fConvertEOLCodes;
	private String fEOLCode;
	private boolean fFormatSource;
	private boolean fInsertMissingTags;
	// private IPreferenceStore fPreferenceStore = null;
	private Preferences fPreferences = null;
	private boolean fQuoteAttrValues;
	private int fTagNameCase;

	public int getAttrNameCase() {

		return fAttrNameCase;
	}

	public boolean getConvertEOLCodes() {

		return fConvertEOLCodes;
	}

	public String getEOLCode() {

		return fEOLCode;
	}

	public boolean getFormatSource() {

		return fFormatSource;
	}

	public boolean getInsertMissingTags() {

		return fInsertMissingTags;
	}

	public Preferences getPreferences() {

		if (fPreferences == null) {
			fPreferences = SSECorePlugin.getDefault().getPluginPreferences();
		}
		return fPreferences;
	}

	public boolean getQuoteAttrValues() {

		return fQuoteAttrValues;
	}

	public int getTagNameCase() {

		return fTagNameCase;
	}

	public void setAttrNameCase(int attrNameCase) {

		fAttrNameCase = attrNameCase;
	}

	public void setConvertEOLCodes(boolean convertEOLCodes) {

		fConvertEOLCodes = convertEOLCodes;
	}

	public void setEOLCode(String EOLCode) {

		fEOLCode = EOLCode;
	}

	public void setFormatSource(boolean formatSource) {

		fFormatSource = formatSource;
	}

	public void setInsertMissingTags(boolean insertMissingTags) {

		fInsertMissingTags = insertMissingTags;
	}

	public void setPreferences(Preferences prefs) {

		fPreferences = prefs;
		updateOptions();
	}

	public void setQuoteAttrValues(boolean quoteAttrValues) {

		fQuoteAttrValues = quoteAttrValues;
	}

	public void setTagNameCase(int tagNameCase) {

		fTagNameCase = tagNameCase;
	}

	protected void updateOptions() {

		Preferences p = getPreferences();
		fTagNameCase = p.getInt(IXMLPreferenceNames.CLEANUP_TAG_NAME_CASE);
		fAttrNameCase = p.getInt(IXMLPreferenceNames.CLEANUP_ATTR_NAME_CASE);
		fInsertMissingTags = p.getBoolean(IXMLPreferenceNames.INSERT_MISSING_TAGS);
		fQuoteAttrValues = p.getBoolean(IXMLPreferenceNames.QUOTE_ATTR_VALUES);
		fFormatSource = p.getBoolean(IXMLPreferenceNames.FORMAT_SOURCE);
		fConvertEOLCodes = p.getBoolean(IXMLPreferenceNames.CONVERT_EOL_CODES);
		fEOLCode = p.getString(IXMLPreferenceNames.CLEANUP_EOL_CODE);
	}
}
