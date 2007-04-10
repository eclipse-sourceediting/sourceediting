/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.modelquery;


import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.w3c.dom.Document;

/**
 * A Class to provide an association between XHTML documents and DTDs for
 * XHTML. This class is intended to be used only in HTMLModelQueryCMProvider.
 */
/*
 * This class closely resemble XMLModelQueryAssociationProvider.
 */
class XHTMLAssociationProvider extends XMLAssociationProvider {

	/**
	 * set USE_QUICK_CACHE to false to test effects of not caching at all.
	 */
	private static final boolean USE_QUICK_CACHE = true;
	protected URIResolver idResolver;
	private String fCachedGrammerURI;
	private String fCachedPublicID;
	private String fCachedSystemID;
	private boolean cached;

	public XHTMLAssociationProvider(CMDocumentCache cache, URIResolver idResolver) {
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
		if (idResolver == null)
			return null;
		String grammerURI = null;
		if (USE_QUICK_CACHE) {
			/*
			 * In parsing a document, we get many identical requests to this
			 * method, so instead of looking up (resolving) grammerURI each
			 * time, we'll just return previously cached one. Probably not
			 * worth have a more complex cache than that.
			 */
			if (cached && sameAs(fCachedPublicID, publicId) && sameAs(fCachedSystemID, systemId)) {
				grammerURI = fCachedGrammerURI;
			}
			else {
				grammerURI = idResolver.resolve(null, publicId, systemId);
				fCachedGrammerURI = grammerURI;
				fCachedPublicID = publicId;
				fCachedSystemID = systemId;
				cached = true;
			}
		}
		else {
			grammerURI = idResolver.resolve(null, publicId, systemId);
		}

		if (grammerURI == null)
			return null;

		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=88896
		 * 
		 * We once called the deprecated 2 argument form of getCMDocument.
		 * 
		 * CMDocument cmDocument = documentManager.getCMDocument(publicId,
		 * grammerURI);
		 * 
		 * which eventually resulted in empty string for type, which I don't
		 * think the infrastructure handles any longer. So, I deleted
		 * deprecated methods, and switched to null for type argument.
		 * 
		 * 'null' means to "create based on uri".
		 * 
		 * FYI, 'dtd' would mean load only those registered as dtd's
		 * 
		 * CMDocument cmDocument = documentManager.getCMDocument(publicId,
		 * grammerURI); CMDocument cmDocument =
		 * documentManager.getCMDocument(publicId, grammerURI, "dtd");
		 */
		CMDocument cmDocument = documentManager.getCMDocument(publicId, grammerURI, null);
		return cmDocument;
	}

	/**
	 */
	protected String resolveGrammarURI(Document document, String publicId, String systemId) {
		return idResolver.resolve(null, publicId, systemId);
	}

	private boolean sameAs(String a, String b) {
		boolean result = false;
		if (a == null) {
			result = b == null;
		}
		else {
			result = a.equals(b);
		}
		return result;
	}

	/**
	 * This added and/or made public specifically for experimentation. It
	 * will change as this functionality becomes API. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=119084
	 */
	public String getCachedGrammerURI() {
		return fCachedGrammerURI;
	}
}
