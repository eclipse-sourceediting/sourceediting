/*
 */
package org.eclipse.wst.xml.core.internal.catalog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.Logger;



public class CatalogSet {
	protected Map uriResourceMap = new HashMap();
	protected Map catalogPersistenceLocations = new HashMap();
	public CatalogSet() {
		super();
	}

	/**
	 * Find a Catalog with the given ID.  If one is not found, create one at the given URI.
	 * 
	 * @param id
	 * @param uri - the URI, the parent of this file path must already exist
	 * @return
	 */
	public Catalog lookupOrCreateCatalog(String id, String uri) {
		Catalog catalog = getCatalog(id, uri);
		if (catalog == null) {
			catalog = new Catalog(this, id, uri);
			try {
				catalog.load();
				uriResourceMap.put(uri, catalog);
			}
			catch (Exception e) {
				// we catch and log all exceptions, to disallow
				// one bad extension interfering with others
				Logger.logException("error loading catalog: " + id + " " + uri, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return catalog;
	}

	private Catalog getCatalog(String id, String uri) {
		return (Catalog) uriResourceMap.get(uri);
	}

	public void putCatalogPersistenceLocation(String logicalURI, String actualURI) {
		catalogPersistenceLocations.put(logicalURI, actualURI);
	}

	// Never used?
	public String getCatalogPersistenceLocation(String id) {
		return (String) catalogPersistenceLocations.get(id);
	}
}
