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

import org.eclipse.core.runtime.Preferences;

public interface IStructuredCleanupPreferences {
	int getTagNameCase();

	int getAttrNameCase();

	boolean getCompressEmptyElementTags();

	boolean getInsertRequiredAttrs();

	boolean getInsertMissingTags();

	boolean getQuoteAttrValues();

	boolean getFormatSource();

	boolean getConvertEOLCodes();

	String getEOLCode();

	void setTagNameCase(int tagNameCase);

	void setAttrNameCase(int attrNameCase);

	void setCompressEmptyElementTags(boolean compressEmptyElementTags);

	void setInsertRequiredAttrs(boolean insertRequiredAttrs);

	void setInsertMissingTags(boolean insertMissingTags);

	void setQuoteAttrValues(boolean quoteAttrValues);

	void setFormatSource(boolean formatSource);

	void setConvertEOLCodes(boolean convertEOLCodes);

	void setEOLCode(String EOLCode);

	void setPreferences(Preferences preferences);
}
