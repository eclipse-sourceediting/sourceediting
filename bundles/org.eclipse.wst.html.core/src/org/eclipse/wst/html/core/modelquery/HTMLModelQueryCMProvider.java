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
package org.eclipse.wst.html.core.modelquery;


import java.util.Hashtable;

import org.eclipse.wst.html.core.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.document.HTMLDocumentTypeEntry;
import org.eclipse.wst.html.core.document.HTMLDocumentTypeRegistry;
import org.eclipse.wst.xml.core.contentmodel.CMDocType;
import org.eclipse.wst.xml.core.document.DOMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 * CMDocument provider for HTML and XHTML documents.
 */
public class HTMLModelQueryCMProvider implements ModelQueryCMProvider {


	private static CMDocument staticHTML = HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE);
	private static CMDocument staticCHTML = HTMLCMDocumentFactory.getCMDocument(CMDocType.CHTML_DOC_TYPE);
	private static HTMLDocumentTypeRegistry doctypeRegistry = HTMLDocumentTypeRegistry.getInstance();
	private static Hashtable buddyCache = new Hashtable();

	private XHTMLAssociationProvider xhtmlassoc = null;

	public HTMLModelQueryCMProvider(CMDocumentCache cache, IdResolver idResolver) {
		super();
		xhtmlassoc = new XHTMLAssociationProvider(cache, idResolver);
	}

	/**
	 * Returns the CMDocument that corresponds to the DOM Node.
	 * or null if no CMDocument is appropriate for the DOM Node.
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		DOMDocument owner = getOwnerXMLDocument(node);
		if (owner == null)
			return null;

		String pid = getPublicId(owner);
		if (pid == null)
			return staticHTML;

		HTMLDocumentTypeEntry entry = doctypeRegistry.getEntry(pid);
		if (entry == null)
			return staticHTML;

		pid = entry.getPublicId();
		CMDocument dtdcm = xhtmlassoc.getXHTMLCMDocument(pid, entry.getSystemId());
		if (dtdcm == null) {
			if (pid != null && pid.equals(HTMLDocumentTypeRegistry.CHTML_PUBLIC_ID)) {
				return staticCHTML;
			}
			return staticHTML;
		}

		CMDocument buddycm = (CMDocument) buddyCache.get(pid);
		if (buddycm != null)
			return buddycm;

		buddycm = new CMDocumentForBuddySystem(dtdcm, entry.isXMLType());
		buddyCache.put(pid, buddycm);
		return buddycm;
	}

	// private methods
	private DOMDocument getOwnerXMLDocument(Node node) {
		if (node == null)
			return null;
		Document owner = (node.getNodeType() == Node.DOCUMENT_NODE) ? (Document) node : node.getOwnerDocument();
		if (owner == null)
			return null;
		if (!(owner instanceof DOMDocument))
			return null;
		return (DOMDocument) owner;
	}

	private String getPublicId(DOMDocument doc) {
		if (doc == null)
			return null;
		DocumentType doctype = doc.getDoctype();
		return (doctype != null) ? doctype.getPublicId() : doc.getDocumentTypeId();
	}
}