/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.cleanup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.cleanup.StructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

abstract class AbstractNodeCleanupHandler implements IStructuredCleanupHandler {

	protected IStructuredCleanupPreferences fCleanupPreferences = null;
	protected IProgressMonitor fProgressMonitor = null;

	public void setCleanupPreferences(IStructuredCleanupPreferences cleanupPreferences) {

		fCleanupPreferences = cleanupPreferences;
	}


	public IStructuredCleanupPreferences getCleanupPreferences() {
		if (fCleanupPreferences == null) {
			fCleanupPreferences = new StructuredCleanupPreferences();
	
			Preferences preferences = getModelPreferences();
			if (preferences != null) {
				fCleanupPreferences.setTagNameCase(preferences.getInt(HTMLCorePreferenceNames.CLEANUP_TAG_NAME_CASE));
				fCleanupPreferences.setAttrNameCase(preferences.getInt(HTMLCorePreferenceNames.CLEANUP_ATTR_NAME_CASE));
				fCleanupPreferences.setCompressEmptyElementTags(preferences.getBoolean(HTMLCorePreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS));
				fCleanupPreferences.setInsertRequiredAttrs(preferences.getBoolean(HTMLCorePreferenceNames.INSERT_REQUIRED_ATTRS));
				fCleanupPreferences.setInsertMissingTags(preferences.getBoolean(HTMLCorePreferenceNames.INSERT_MISSING_TAGS));
				fCleanupPreferences.setQuoteAttrValues(preferences.getBoolean(HTMLCorePreferenceNames.QUOTE_ATTR_VALUES));
				fCleanupPreferences.setFormatSource(preferences.getBoolean(HTMLCorePreferenceNames.FORMAT_SOURCE));
				fCleanupPreferences.setConvertEOLCodes(preferences.getBoolean(HTMLCorePreferenceNames.CONVERT_EOL_CODES));
				fCleanupPreferences.setEOLCode(preferences.getString(HTMLCorePreferenceNames.CLEANUP_EOL_CODE));
			}
		}

		return fCleanupPreferences;
	}


	public void setProgressMonitor(IProgressMonitor progressMonitor) {

		fProgressMonitor = progressMonitor;
	}

	static protected StructuredDocumentEvent replaceSource(IDOMModel model, Object requester, int offset, int length, String source) {

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
