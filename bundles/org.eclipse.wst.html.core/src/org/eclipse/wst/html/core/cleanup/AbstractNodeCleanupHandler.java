/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.cleanup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.HTMLCorePlugin;
import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.cleanup.StructuredCleanupPreferences;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

abstract class AbstractNodeCleanupHandler implements IStructuredCleanupHandler {

	protected IStructuredCleanupPreferences fCleanupPreferences = null;
	protected IProgressMonitor fProgressMonitor = null;

	public void setCleanupPreferences(IStructuredCleanupPreferences cleanupPreferences) {

		fCleanupPreferences = cleanupPreferences;
	}

	/**
	 * @see com.ibm.sed.partitionCleanup.CleanupHandler#getCleanupPreferences()
	 */
	public IStructuredCleanupPreferences getCleanupPreferences() {
		if (fCleanupPreferences == null) {
			fCleanupPreferences = new StructuredCleanupPreferences();
	
			Preferences preferences = getModelPreferences();
			if (preferences != null) {
				fCleanupPreferences.setTagNameCase(preferences.getInt(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE));
				fCleanupPreferences.setAttrNameCase(preferences.getInt(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE));
				fCleanupPreferences.setCompressEmptyElementTags(preferences.getBoolean(CommonModelPreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS));
				fCleanupPreferences.setInsertRequiredAttrs(preferences.getBoolean(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS));
				fCleanupPreferences.setInsertMissingTags(preferences.getBoolean(CommonModelPreferenceNames.INSERT_MISSING_TAGS));
				fCleanupPreferences.setQuoteAttrValues(preferences.getBoolean(CommonModelPreferenceNames.QUOTE_ATTR_VALUES));
				fCleanupPreferences.setFormatSource(preferences.getBoolean(CommonModelPreferenceNames.FORMAT_SOURCE));
				fCleanupPreferences.setConvertEOLCodes(preferences.getBoolean(CommonModelPreferenceNames.CONVERT_EOL_CODES));
				fCleanupPreferences.setEOLCode(preferences.getString(CommonModelPreferenceNames.CLEANUP_EOL_CODE));
			}
		}

		return fCleanupPreferences;
	}

	/**
	 * @see com.ibm.sed.partitionCleanup.CleanupHandler#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {

		fProgressMonitor = progressMonitor;
	}

	static protected StructuredDocumentEvent replaceSource(XMLModel model, Object requester, int offset, int length, String source) {

		StructuredDocumentEvent result = null;
		if (model == null)
			return result;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return result;
		if (source == null)
			source = new String();
		if (structuredDocument.containsReadOnly(offset, length))
			return result;
		if (requester == null) {
			requester = structuredDocument;
		}
		return structuredDocument.replaceText(requester, offset, length, source);
	}

	protected Preferences getModelPreferences() {
		return HTMLCorePlugin.getDefault().getPluginPreferences();
	}
}