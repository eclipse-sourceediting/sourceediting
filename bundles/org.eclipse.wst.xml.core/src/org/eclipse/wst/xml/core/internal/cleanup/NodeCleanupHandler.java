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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.cleanup.StructuredCleanupPreferences;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.w3c.dom.Node;


public class NodeCleanupHandler implements IStructuredCleanupHandler {

	protected IStructuredCleanupPreferences fCleanupPreferences = null;
	protected IProgressMonitor fProgressMonitor = null;

	/**
	 * @see com.ibm.sed.partitionCleanup.CleanupHandler#cleanup(com.ibm.sed.model.xml.XMLNode)
	 */
	public Node cleanup(Node node) {

		return node;
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

	protected Preferences getModelPreferences() {
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	public void setCleanupPreferences(IStructuredCleanupPreferences cleanupPreferences) {

		fCleanupPreferences = cleanupPreferences;
	}
}
