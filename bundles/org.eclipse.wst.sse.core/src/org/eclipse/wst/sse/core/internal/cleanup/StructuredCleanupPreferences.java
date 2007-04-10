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
package org.eclipse.wst.sse.core.internal.cleanup;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;


public class StructuredCleanupPreferences implements IStructuredCleanupPreferences {
	private int fAttrNameCase;
	private boolean fCompressEmptyElementTags;
	private boolean fConvertEOLCodes;
	private String fEOLCode;
	private boolean fFormatSource;
	private boolean fInsertMissingTags;
	private boolean fInsertRequiredAttrs;
	//private IPreferenceStore fPreferenceStore = null;
	private Preferences fPreferences = null;
	private boolean fQuoteAttrValues;

	private int fTagNameCase;

	public int getAttrNameCase() {

		return fAttrNameCase;
	}

	public boolean getCompressEmptyElementTags() {

		return fCompressEmptyElementTags;
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

	public boolean getInsertRequiredAttrs() {

		return fInsertRequiredAttrs;
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

	public void setCompressEmptyElementTags(boolean compressEmptyElementTags) {

		fCompressEmptyElementTags = compressEmptyElementTags;
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

	public void setInsertRequiredAttrs(boolean insertRequiredAttrs) {

		fInsertRequiredAttrs = insertRequiredAttrs;
	}

	public void setPreferences(Preferences prefs) {

		fPreferences = prefs;
	}

	public void setQuoteAttrValues(boolean quoteAttrValues) {

		fQuoteAttrValues = quoteAttrValues;
	}

	public void setTagNameCase(int tagNameCase) {

		fTagNameCase = tagNameCase;
	}
}
