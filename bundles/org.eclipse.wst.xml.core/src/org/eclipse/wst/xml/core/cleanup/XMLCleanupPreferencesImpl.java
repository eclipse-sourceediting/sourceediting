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
package org.eclipse.wst.xml.core.cleanup;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.xml.core.XMLPreferenceNames;


/**
 * @deprecated renamed to StructuredCleanupPreferences
 *
 * TODO will delete in C5
 */
public class XMLCleanupPreferencesImpl implements XMLCleanupPreferences {

	private static XMLCleanupPreferencesImpl fInstance;
	private int fTagNameCase;
	private int fAttrNameCase;
	private boolean fInsertMissingTags;
	private boolean fQuoteAttrValues;
	private boolean fFormatSource;
	private boolean fConvertEOLCodes;
	private String fEOLCode;
	//private IPreferenceStore fPreferenceStore = null;
	private Preferences fPreferences = null;

	public int getTagNameCase() {

		return fTagNameCase;
	}

	public int getAttrNameCase() {

		return fAttrNameCase;
	}

	public boolean getInsertMissingTags() {

		return fInsertMissingTags;
	}

	public boolean getQuoteAttrValues() {

		return fQuoteAttrValues;
	}

	public boolean getFormatSource() {

		return fFormatSource;
	}

	public boolean getConvertEOLCodes() {

		return fConvertEOLCodes;
	}

	public String getEOLCode() {

		return fEOLCode;
	}

	public void setTagNameCase(int tagNameCase) {

		fTagNameCase = tagNameCase;
	}

	public void setAttrNameCase(int attrNameCase) {

		fAttrNameCase = attrNameCase;
	}

	public void setInsertMissingTags(boolean insertMissingTags) {

		fInsertMissingTags = insertMissingTags;
	}

	public void setQuoteAttrValues(boolean quoteAttrValues) {

		fQuoteAttrValues = quoteAttrValues;
	}

	public void setFormatSource(boolean formatSource) {

		fFormatSource = formatSource;
	}

	public void setConvertEOLCodes(boolean convertEOLCodes) {

		fConvertEOLCodes = convertEOLCodes;
	}

	public void setEOLCode(String EOLCode) {

		fEOLCode = EOLCode;
	}

	public Preferences getPreferences() {

		if (fPreferences == null) {
			fPreferences = getModelManagerPlugin().getPluginPreferences();
		}
		return fPreferences;
	}

	private IModelManagerPlugin getModelManagerPlugin() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin;
	}

	public void setPreferences(Preferences prefs) {

		fPreferences = prefs;
		updateOptions();
	}

	protected void updateOptions() {

		Preferences p = getPreferences();
		fTagNameCase = p.getInt(XMLPreferenceNames.CLEANUP_TAG_NAME_CASE);
		fAttrNameCase = p.getInt(XMLPreferenceNames.CLEANUP_ATTR_NAME_CASE);
		fInsertMissingTags = p.getBoolean(XMLPreferenceNames.INSERT_MISSING_TAGS);
		fQuoteAttrValues = p.getBoolean(XMLPreferenceNames.QUOTE_ATTR_VALUES);
		fFormatSource = p.getBoolean(XMLPreferenceNames.FORMAT_SOURCE);
		fConvertEOLCodes = p.getBoolean(XMLPreferenceNames.CONVERT_EOL_CODES);
		fEOLCode = p.getString(XMLPreferenceNames.CLEANUP_EOL_CODE);
	}

	public synchronized static XMLCleanupPreferencesImpl getInstance() {

		// added for one method in CleanupDialog ... may be better way
		if (fInstance == null) {
			fInstance = new XMLCleanupPreferencesImpl();
		}
		return fInstance;
	}
}
