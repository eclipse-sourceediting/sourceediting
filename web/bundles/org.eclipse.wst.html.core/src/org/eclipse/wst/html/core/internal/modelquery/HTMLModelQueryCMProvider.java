/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.modelquery;


import java.util.Hashtable;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeEntry;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeRegistry;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 * CMDocument provider for HTML and XHTML documents.
 * 
 * This added and/or made public specifically for experimentation. It will
 * change as this functionality becomes API. See
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=119084
 */


public class HTMLModelQueryCMProvider implements ModelQueryCMProvider {


	private static CMDocument staticHTML5 = HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML5_DOC_TYPE);
	private static CMDocument staticHTML = HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE);
	private static CMDocument staticCHTML = HTMLCMDocumentFactory.getCMDocument(CMDocType.CHTML_DOC_TYPE);
	private static HTMLDocumentTypeRegistry doctypeRegistry = HTMLDocumentTypeRegistry.getInstance();
	private static Hashtable buddyCache = new Hashtable();

	private XHTMLAssociationProvider xhtmlassoc = null;

	public HTMLModelQueryCMProvider(CMDocumentCache cache, URIResolver idResolver) {
		super();
		xhtmlassoc = new XHTMLAssociationProvider(cache, idResolver);
	}

	/**
	 * Returns the CMDocument that corresponds to the DOM Node. or null if no
	 * CMDocument is appropriate for the DOM Node.
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		IDOMDocument owner = getOwnerXMLDocument(node);
		if (owner == null)
			return null;

		String pid = getPublicId(owner);
		// no PID, always return the currently-supported HTML version
		if (pid == null || "".equals(pid)){
			return staticHTML5;
		}

		HTMLDocumentTypeEntry entry = doctypeRegistry.getEntry(pid);
		if (entry == null)
			return staticHTML;
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=151000 - use internal content model
		if (entry.useInternalModel()) {
			if (pid != null && pid.equals(HTMLDocumentTypeRegistry.CHTML_PUBLIC_ID)) {
				return staticCHTML;
			}
			return staticHTML;
		}

		pid = entry.getPublicId();
		String sid = entry.getSystemId();

		CMDocument dtdcm = xhtmlassoc.getXHTMLCMDocument(pid, sid);
		if (dtdcm == null) {
			if (pid != null && pid.equals(HTMLDocumentTypeRegistry.CHTML_PUBLIC_ID)) {
				return staticCHTML;
			}
			return staticHTML;
		}

		String grammarURI = xhtmlassoc.getCachedGrammerURI();
		CMDocument buddycm = (CMDocument) buddyCache.get(grammarURI);
		if (buddycm != null)
			return buddycm;

		buddycm = new CMDocumentForBuddySystem(dtdcm, entry.isXMLType());
		buddyCache.put(grammarURI, buddycm);
		return buddycm;
	}

	// private methods
	private IDOMDocument getOwnerXMLDocument(Node node) {
		if (node == null)
			return null;
		Document owner = (node.getNodeType() == Node.DOCUMENT_NODE) ? (Document) node : node.getOwnerDocument();
		if (owner == null)
			return null;
		if (!(owner instanceof IDOMDocument))
			return null;
		return (IDOMDocument) owner;
	}

	private String getPublicId(IDOMDocument doc) {
		if (doc == null)
			return null;
		DocumentType doctype = doc.getDoctype();
		//doctype.
		return (doctype != null) ? doctype.getPublicId() : doc.getDocumentTypeId();
	}
	
	
	
}