/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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

/**
 * @deprecated renamed to IStructuredCleanupPreferences
 * 
 * TODO will delete in C5
 */
public interface XMLCleanupPreferences {

	int getAttrNameCase();

	boolean getConvertEOLCodes();

	String getEOLCode();

	boolean getFormatSource();

	boolean getInsertMissingTags();

	boolean getQuoteAttrValues();

	int getTagNameCase();

	void setAttrNameCase(int attrNameCase);

	void setConvertEOLCodes(boolean convertEOLCodes);

	void setEOLCode(String EOLCode);

	void setFormatSource(boolean formatSource);

	void setInsertMissingTags(boolean insertMissingTags);

	//void setPreferenceStore(IPreferenceStore preferenceStore);
	void setPreferences(Preferences preferences);

	void setQuoteAttrValues(boolean quoteAttrValues);

	void setTagNameCase(int tagNameCase);
}
