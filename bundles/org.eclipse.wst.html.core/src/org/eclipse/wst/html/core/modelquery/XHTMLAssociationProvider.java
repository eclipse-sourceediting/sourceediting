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


import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.sse.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.w3c.dom.Document;

/**
 * A Class to provide an association between XHTML documents and DTDs for
 * XHTML. This class is intended to be used only in HTMLModelQueryCMProvider.
 */
/*
 * This class closely resemble XMLModelQueryAssociationProvider.
 */
class XHTMLAssociationProvider extends XMLAssociationProvider {

	protected IdResolver idResolver = null;
	private String fCachedGrammerURI;
	private String fCachedPublicID;
	private String fCachedSystemID;
	private static final boolean USE_QUICK_CACHE = true;

	public XHTMLAssociationProvider(CMDocumentCache cache, IdResolver idResolver) {
		super(cache);
		this.idResolver = idResolver;
	}

	/**
	 * 
	 * @param publicId
	 * @param systemId
	 * @return
	 */
	public CMDocument getXHTMLCMDocument(String publicId, String systemId) {
		String grammerURI = null;
		if (USE_QUICK_CACHE) { 
			// In parsing a document, we get many identiical requests to this
			// method, so instead of looking up (resolving) grammerURI each
			// time,
			// we'll just return previously cached one. Probably not worth
			// have a
			// more complex cache than that.
			if (fCachedGrammerURI != null && fCachedPublicID.equals(publicId) && fCachedSystemID.equals(systemId)) {
				grammerURI = fCachedGrammerURI;
			}
			else {
				grammerURI = idResolver.resolveId(publicId, systemId);
				fCachedGrammerURI = grammerURI;
				fCachedPublicID = publicId;
				fCachedSystemID = systemId;
			}
		}
		else {
			grammerURI = idResolver.resolveId(publicId, systemId);
		}

		if (grammerURI == null)
			return null;
		CMDocument cmDocument = documentManager.getCMDocument(publicId, grammerURI);
		return cmDocument;
	}

	/**
	 */
	protected String resolveGrammarURI(Document document, String publicId, String systemId) {
		return idResolver.resolveId(publicId, systemId);
	}
}