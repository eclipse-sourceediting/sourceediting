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

import org.eclipse.core.runtime.Preferences;

/**
 * @deprecated renamed to IStructuredCleanupPreferences
 *
 * TODO will delete in C5
 */
public interface XMLCleanupPreferences {
	int getTagNameCase();

	int getAttrNameCase();

	boolean getInsertMissingTags();

	boolean getQuoteAttrValues();

	boolean getFormatSource();

	boolean getConvertEOLCodes();

	String getEOLCode();

	void setTagNameCase(int tagNameCase);

	void setAttrNameCase(int attrNameCase);

	void setInsertMissingTags(boolean insertMissingTags);

	void setQuoteAttrValues(boolean quoteAttrValues);

	void setFormatSource(boolean formatSource);

	void setConvertEOLCodes(boolean convertEOLCodes);

	void setEOLCode(String EOLCode);

	//void setPreferenceStore(IPreferenceStore preferenceStore);
	void setPreferences(Preferences preferences);
}
