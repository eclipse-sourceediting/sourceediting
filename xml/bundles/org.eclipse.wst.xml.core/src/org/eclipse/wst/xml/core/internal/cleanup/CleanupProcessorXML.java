/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.eclipse.wst.xml.core.internal.formatter.XMLFormatterFormatProcessor;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;


public class CleanupProcessorXML extends AbstractStructuredCleanupProcessor {
	protected IStructuredCleanupPreferences fCleanupPreferences = null;
	
	public void cleanupModel(IStructuredModel structuredModel) {
		Preferences preferences = getModelPreferences();
		if (preferences != null && preferences.getBoolean(XMLCorePreferenceNames.FIX_XML_DECLARATION)) {
			IDOMDocument document = ((DOMModelImpl) structuredModel).getDocument();
			if (!fixExistingXmlDecl(document)) {
				String encoding = preferences.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
				Node xml = document.createProcessingInstruction("xml", "version=\"1.0\" " + "encoding=\"" + encoding +"\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				document.insertBefore(xml, document.getFirstChild());
			}
		}
		super.cleanupModel(structuredModel);
	}

	/**
	 * Is the node an XML declaration
	 * @param node
	 * @return true if the node is an XML declaration; otherwise, false.
	 */
	private boolean isXMLDecl(IDOMNode node) {
		return node != null && node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && "xml".equalsIgnoreCase(((ProcessingInstruction) node).getTarget()); //$NON-NLS-1$
	}

	private boolean fixExistingXmlDecl(IDOMDocument document) {
		IDOMNode node = (IDOMNode) document.getFirstChild();
		while (node != null && node.getNodeType() == Node.TEXT_NODE && ((TextImpl) node).isWhitespace())
			node = (IDOMNode) node.getNextSibling();
		if (isXMLDecl(node)) {
			document.insertBefore(node, document.getFirstChild());
			return true;
		}
		return false;
	}

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
