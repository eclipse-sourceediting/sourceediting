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


import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
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
	 * set CACHE_FIXED_DOCUMENTS to false to test effects of not caching certain catalog-contributed schemas.
	 */
	private static final boolean CACHE_FIXED_DOCUMENTS = true;
	private static final String[] STANDARD_SCHEMA_BUNDLES = new String[] {"org.eclipse.wst.standard.schemas","org.eclipse.jst.standard.schemas"};
	private static final String XML_CATALOG_EXT_POINT = "org.eclipse.wst.xml.core.catalogContributions"; 
	private static Collection fFixedPublicIDs = null;
	private static Map fFixedCMDocuments = new HashMap();

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
		   
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=136399. If the CM document URI
		// is resolved and cached at this level instruct the CM model manager to avoid 
		// re-resolving the URI.

		if (USE_QUICK_CACHE) {
		  documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_PERFORM_URI_RESOLUTION, false);
		}
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
		
		CMDocument cmDocument = null;
		if (CACHE_FIXED_DOCUMENTS) {
			Reference ref = (Reference) fFixedCMDocuments.get(publicId);
			if (ref != null) {
				cmDocument = (CMDocument) ref.get();
				if (cmDocument != null) {
					return cmDocument;
				}
			}
		}
		
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
		synchronized (grammerURI) {
			cmDocument = documentManager.getCMDocument(publicId, grammerURI, null);
		}
		
		if (CACHE_FIXED_DOCUMENTS && getFixedPublicIDs().contains(publicId)) {
			fFixedCMDocuments.put(publicId, new SoftReference(cmDocument));
		}
		
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

	/**
	 * @return the fFixedPublicIDs, a collection of contributed Public
	 *         Identifiers from the known schema plug-ins.
	 */
	private static Collection getFixedPublicIDs() {
		/**
		 * public:publicId
		 * TODO: system:systemId and uri:name in their own methods and maps?
		 */
		synchronized (STANDARD_SCHEMA_BUNDLES) {
			if (fFixedPublicIDs == null) {
				fFixedPublicIDs = new HashSet();
				for (int i = 0; i < STANDARD_SCHEMA_BUNDLES.length; i++) {
					IExtension[] extensions = Platform.getExtensionRegistry().getExtensions(STANDARD_SCHEMA_BUNDLES[i]);
					for (int j = 0; j < extensions.length; j++) {
						if (XML_CATALOG_EXT_POINT.equals(extensions[j].getExtensionPointUniqueIdentifier())) {
							IConfigurationElement[] configurationElements = extensions[j].getConfigurationElements();
							for (int k = 0; k < configurationElements.length; k++) {
								IConfigurationElement[] publics = configurationElements[k].getChildren("public");
								for (int l = 0; l < publics.length; l++) {
									String publicId = publics[l].getAttribute("publicId");
									if (publicId != null && publicId.length() > 0) {
										fFixedPublicIDs.add(publicId);
									}
								}
							}
						}
					}
				}
			}
		}
		return fFixedPublicIDs;
	}
}
