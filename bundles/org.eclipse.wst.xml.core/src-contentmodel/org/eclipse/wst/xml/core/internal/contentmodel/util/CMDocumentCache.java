/*******************************************************************************
 * Copyright (c) 2002, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

/**
 * A singleton cache of CMDocuments. Because of the CM
 */
public class CMDocumentCache {
	class CacheEntry {
		String uri;
		int status = STATUS_NOT_LOADED;
		float progress;
		private SoftReference cmDocumentReference;

		CacheEntry(String uri) {
			this.uri = uri;
		}

		CacheEntry(String uri, int status, CMDocument document) {
			this.uri = uri;
			this.status = status;
			this.cmDocumentReference = new SoftReference(document);
		}

		CMDocument getCMDocument() {
			if (cmDocumentReference == null)
				return null;
			return (CMDocument) cmDocumentReference.get();
		}

		public void setCMDocument(CMDocument document) {
			cmDocumentReference = new SoftReference(document);
		}
	}

	public static final int STATUS_NOT_LOADED = 0;

	public static final int STATUS_LOADING = 2;

	public static final int STATUS_LOADED = 3;

	public static final int STATUS_ERROR = 4;

	static final boolean DEBUG_CACHE = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.xml.core/debug/cmdocumentcache")).booleanValue(); //$NON-NLS-1$

	private static CMDocumentCache SHARED_INSTANCE = new CMDocumentCache();

	public static CMDocumentCache getInstance() {
		return SHARED_INSTANCE;
	}

	Map fSavedDocuments = new HashMap();

	protected Hashtable fCachetable;
	protected List listenerList = new ArrayList();

	private CMDocumentCache() {
		fCachetable = new Hashtable();
	}

	public void addListener(CMDocumentCacheListener listener) {
		listenerList.add(listener);
	}

	/**
	 * 
	 */
	public void clear() {
		synchronized (fCachetable) {
			fCachetable.clear();
		}
		notifyCacheCleared();
	}

	/**
	 * 
	 */
	public CMDocument getCMDocument(String grammarURI) {
		CMDocument result = null;
		boolean cachedDocumentWasRemoved = false;
		if (grammarURI != null) {
			CacheEntry entry = null;
			synchronized (fCachetable) {
				entry = (CacheEntry) fCachetable.get(grammarURI);
				if (entry != null) {
					result = entry.getCMDocument();
					cachedDocumentWasRemoved = ((entry.status == STATUS_LOADED) && (result == null));
				}
			}

			if (cachedDocumentWasRemoved) {
				if (DEBUG_CACHE)
					System.out.println("CMDocumentCache hit on reclaimed document " + grammarURI);
				/* a previously loaded document has been reclaimed, */
				notifyCacheUpdated(grammarURI, entry.status, STATUS_NOT_LOADED, result);
				/* wait until after notification to actually remove the entry */
				synchronized (fCachetable) {
					fCachetable.remove(grammarURI);
				}
			}
			else if (DEBUG_CACHE && entry != null) {
				System.out.println("CMDocumentCache hit on " + grammarURI);
			}
		}
		else if (DEBUG_CACHE) {
			System.out.println("CMDocumentCache miss on " + grammarURI);
		}
		return result;
	}

	public synchronized List getCMDocuments() {
		List list = new ArrayList();
		for (Iterator i = fCachetable.values().iterator(); i.hasNext();) {
			CacheEntry entry = (CacheEntry) i.next();
			CMDocument document = entry.getCMDocument();
			if (document != null) {
				list.add(document);
			}
		}
		return list;
	}

	/**
	 * 
	 */
	public int getStatus(String grammarURI) {
		int result = STATUS_NOT_LOADED;
		boolean cacheDocumentWasRemoved = false;
		if (grammarURI != null) {
			CacheEntry entry = null;
			synchronized (fCachetable) {
				entry = (CacheEntry) fCachetable.get(grammarURI);
				if (entry != null) {
					cacheDocumentWasRemoved = ((entry.status == STATUS_LOADED) && (entry.getCMDocument() == null));
				}
			}

			if (cacheDocumentWasRemoved) {
				/* a previously loaded document has been reclaimed, */
				notifyCacheUpdated(grammarURI, entry.status, STATUS_NOT_LOADED, null);
				/* wait until after notification to actually update the entry */
				entry.status = STATUS_NOT_LOADED;
			}
			if (entry != null) {
				result = entry.status;
			}
		}
		return result;
	}


	/**
	 * Gets a corresponding CacheEntry for the given grammarURI, creating one
	 * if needed. Assumes that fCachetable is locked.
	 * 
	 * @param grammarURI
	 * @return
	 */
	CacheEntry lookupOrCreate(String grammarURI) {
		CacheEntry entry = null;
		entry = (CacheEntry) fCachetable.get(grammarURI);
		if (entry == null) {
			entry = new CacheEntry(grammarURI);
			fCachetable.put(grammarURI, entry);
		}
		return entry;
	}

	/**
	 * 
	 */
	protected void notifyCacheCleared() {
		CMDocumentCacheListener[] listeners = (CMDocumentCacheListener[]) listenerList.toArray(new CMDocumentCacheListener[0]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].cacheCleared(this);
		}
	}

	/**
	 * 
	 */
	protected void notifyCacheUpdated(String uri, int oldStatus, int newStatus, CMDocument cmDocument) {
		CMDocumentCacheListener[] listeners = (CMDocumentCacheListener[]) listenerList.toArray(new CMDocumentCacheListener[0]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].cacheUpdated(this, uri, oldStatus, newStatus, cmDocument);
		}
	}

	/**
	 * 
	 */
	public void putCMDocument(String grammarURI, CMDocument cmDocument) {
		if (grammarURI != null && cmDocument != null) {
			CacheEntry entry = null;
			int oldStatus = STATUS_NOT_LOADED;
			synchronized (fCachetable) {
				entry = lookupOrCreate(grammarURI);
				oldStatus = entry.status;
				entry.status = STATUS_LOADED;
				entry.setCMDocument(cmDocument);
			}
			notifyCacheUpdated(grammarURI, oldStatus, entry.status, cmDocument);
		}
	}

	public void removeListener(CMDocumentCacheListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * 
	 */
	public void setStatus(String grammarURI, int status) {
		if (grammarURI != null) {
			CacheEntry entry = lookupOrCreate(grammarURI);
			int oldStatus = entry.status;
			entry.status = status;
			notifyCacheUpdated(grammarURI, oldStatus, entry.status, entry.getCMDocument());
		}
	}
}
