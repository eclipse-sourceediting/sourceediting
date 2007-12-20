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
package org.eclipse.wst.xml.core.internal.cleanup;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.cleanup.AbstractStructuredCleanupProcessor;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.cleanup.StructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.formatter.XMLFormatterFormatProcessor;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.w3c.dom.Node;


public class CleanupProcessorXML extends AbstractStructuredCleanupProcessor {
	protected IStructuredCleanupPreferences fCleanupPreferences = null;

	protected IStructuredCleanupHandler getCleanupHandler(Node node) {
		short nodeType = node.getNodeType();
		IStructuredCleanupHandler cleanupHandler = null;
		switch (nodeType) {
			case Node.ELEMENT_NODE : {
				cleanupHandler = new ElementNodeCleanupHandler();
				break;
			}
			case Node.TEXT_NODE : {
				cleanupHandler = new NodeCleanupHandler();
				break;
			}
			default : {
				cleanupHandler = new NodeCleanupHandler();
			}
		}

		// init CleanupPreferences
		cleanupHandler.setCleanupPreferences(getCleanupPreferences());

		return cleanupHandler;
	}

	public IStructuredCleanupPreferences getCleanupPreferences() {
		if (fCleanupPreferences == null) {
			fCleanupPreferences = new StructuredCleanupPreferences();

			Preferences preferences = getModelPreferences();
			if (preferences != null) {
				fCleanupPreferences.setCompressEmptyElementTags(preferences.getBoolean(XMLCorePreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS));
				fCleanupPreferences.setInsertRequiredAttrs(preferences.getBoolean(XMLCorePreferenceNames.INSERT_REQUIRED_ATTRS));
				fCleanupPreferences.setInsertMissingTags(preferences.getBoolean(XMLCorePreferenceNames.INSERT_MISSING_TAGS));
				fCleanupPreferences.setQuoteAttrValues(preferences.getBoolean(XMLCorePreferenceNames.QUOTE_ATTR_VALUES));
				fCleanupPreferences.setFormatSource(preferences.getBoolean(XMLCorePreferenceNames.FORMAT_SOURCE));
				fCleanupPreferences.setConvertEOLCodes(preferences.getBoolean(XMLCorePreferenceNames.CONVERT_EOL_CODES));
				fCleanupPreferences.setEOLCode(preferences.getString(XMLCorePreferenceNames.CLEANUP_EOL_CODE));
			}
		}

		return fCleanupPreferences;
	}

	protected String getContentType() {
		return ContentTypeIdForXML.ContentTypeID_XML;
	}

	protected IStructuredFormatProcessor getFormatProcessor() {
		return new XMLFormatterFormatProcessor();
	}

	protected Preferences getModelPreferences() {
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	protected void refreshCleanupPreferences() {
		fCleanupPreferences = null;
	}
}
