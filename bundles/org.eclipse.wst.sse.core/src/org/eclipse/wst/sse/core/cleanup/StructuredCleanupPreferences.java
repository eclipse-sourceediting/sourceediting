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
package org.eclipse.wst.sse.core.cleanup;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.IModelManagerPlugin;


public class StructuredCleanupPreferences implements IStructuredCleanupPreferences {

	private int fTagNameCase;
	private int fAttrNameCase;
	private boolean fCompressEmptyElementTags;
	private boolean fInsertRequiredAttrs;
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

	public boolean getCompressEmptyElementTags() {

		return fCompressEmptyElementTags;
	}

	public boolean getInsertRequiredAttrs() {

		return fInsertRequiredAttrs;
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

	public void setCompressEmptyElementTags(boolean compressEmptyElementTags) {

		fCompressEmptyElementTags = compressEmptyElementTags;
	}

	public void setInsertRequiredAttrs(boolean insertRequiredAttrs) {

		fInsertRequiredAttrs = insertRequiredAttrs;
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
	}
}
