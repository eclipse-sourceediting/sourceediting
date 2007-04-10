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

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.cleanup.StructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.w3c.dom.Node;

public class HTMLCleanupProcessorImpl extends AbstractStructuredCleanupProcessor {
	private IStructuredCleanupPreferences fCleanupPreferences = null;
	
	protected String getContentType() {
		return ContentTypeIdForHTML.ContentTypeID_HTML;
	}

	protected IStructuredCleanupHandler getCleanupHandler(Node node) {
		return HTMLCleanupHandlerFactory.getInstance().createHandler(node, getCleanupPreferences());
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
	
	protected IStructuredFormatProcessor getFormatProcessor() {
		return new HTMLFormatProcessorImpl();
	}

	protected Preferences getModelPreferences() {
		return HTMLCorePlugin.getDefault().getPluginPreferences();
	}
	
	protected void refreshCleanupPreferences() {
		fCleanupPreferences = null;
	}
}
