/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.sse.core.internal.util.AbstractMemoryListener;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

public class GlobalCMDocumentCache {
	
	private static GlobalCMDocumentCache globalDocumentCache = new GlobalCMDocumentCache();
	private Map systemCatalogEntries = getSystemCatalogEntries();
	private MemoryListener memoryListener;
	private final static String CATEGORY = "CMDocumentGlobalCache"; //$NON-NLS-1$
	
	protected GlobalCMDocumentCache() {
		Logger.trace(CATEGORY, "Cache initialized"); //$NON-NLS-1$
		memoryListener = new MemoryListener();
		memoryListener.connect();
	}

	public static GlobalCMDocumentCache getInstance() {
		return globalDocumentCache;
	}
	
	public synchronized GlobalCacheQueryResponse getCMDocument(String grammarURI) {
		//Logger.trace(CATEGORY, "Query for: " + grammarURI); //$NON-NLS-1$
		Object systemCatalogEntry = systemCatalogEntries.get(grammarURI);
		if(systemCatalogEntry != null) {
			//Logger.trace(CATEGORY, "Document " + grammarURI + " is in system catalog"); //$NON-NLS-1$ //$NON-NLS-2$
			Object object = ((SoftReference)systemCatalogEntry).get();
			if(object != null) {
				//Logger.trace(CATEGORY, "Document " + grammarURI + "is in cache, returning cached version"); //$NON-NLS-1$ //$NON-NLS-2$
				return new GlobalCacheQueryResponse((CMDocument)object, true);
			} else {
				//Logger.trace(CATEGORY, "Document " + grammarURI + " is not in cache"); //$NON-NLS-1$ //$NON-NLS-2$
				return new GlobalCacheQueryResponse(null, true);
			}
		}
		//Logger.trace(CATEGORY, "Document " + grammarURI + " is not in system catalog, not cached"); //$NON-NLS-1$ //$NON-NLS-2$
		return new GlobalCacheQueryResponse(null, false);
	}
	
	public synchronized void putCMDocument(String grammarURI, CMDocument cmDocument) {
		//Logger.trace(CATEGORY, "Document: " + grammarURI + " added to global cache"); //$NON-NLS-1$ //$NON-NLS-2$
		systemCatalogEntries.put(grammarURI, new SoftReference(cmDocument));
	}
	
	private synchronized static HashMap getSystemCatalogEntries(){
		HashMap systemCatalogURIs = new HashMap();
		ICatalog systemCatalog = null;
		ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
		for (int i = 0; i < nextCatalogs.length; i++) {
			INextCatalog catalog = nextCatalogs[i];
			ICatalog referencedCatalog = catalog.getReferencedCatalog();
			if (referencedCatalog != null) {
				if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId())) {
					systemCatalog = referencedCatalog;
				}
			}
		}
		if(systemCatalog != null) {
			ICatalogEntry[] catalogEntries = systemCatalog.getCatalogEntries();
			for (int i = 0; i < catalogEntries.length; i++) {
				systemCatalogURIs.put(catalogEntries[i].getURI(), new SoftReference(null));
			}
		}
		return systemCatalogURIs;
	}
	
	public class GlobalCacheQueryResponse {
		
		private CMDocument cachedDocument;
		private boolean documentCacheable;
		
		public GlobalCacheQueryResponse(CMDocument cachedCMDocument, boolean documentCacheable) {
			this.cachedDocument = cachedCMDocument;
			this.documentCacheable = documentCacheable;
		}
		
		public CMDocument getCachedCMDocument() {
			return cachedDocument;
		}
		
		public boolean isDocumentCacheable() {
			return documentCacheable;
		}
		
	}

	private class MemoryListener extends AbstractMemoryListener {
		protected void handleMemoryEvent(Event event) {
			Object topic = event.getProperty(EventConstants.EVENT_TOPIC);
			if(SEV_SERIOUS.equals(topic) || SEV_CRITICAL.equals(topic)) {
				Logger.trace(CATEGORY, "Serious severity low memory event received, flushing global CMDocument cache."); //$NON-NLS-1$
				systemCatalogEntries = getSystemCatalogEntries();
			}
		}
	}

}
