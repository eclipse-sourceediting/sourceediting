/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.core.schema.catalog;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A representation of the model object '<em><b>Catalog</b></em>'.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface ICatalog {
	/**
	 * Returns catalog id string
	 * 
	 * @return catalog id string
	 */
	String getId();

	/**
	 * Sets catalog id string
	 * 
	 */
	void setId(String id);

	void setLocation(String location);

	String getLocation();

	/**
	 * Return the applicable schema.
	 * 
	 * <p>
	 * If a URI entry exists in the Catalog for the URI specified, return the
	 * mapped value.
	 * </p>
	 * 
	 * <p>
	 * URI comparison is case sensitive.
	 * </p>
	 * 
	 * @param uri
	 *            The URI to locate in the catalog.
	 * 
	 * @return The resolved URI.
	 * 
	 * @throws MalformedURLException
	 *             The system identifier of a subordinate catalog cannot be
	 *             turned into a valid URL.
	 * @throws IOException
	 *             Error reading subordinate catalog file.
	 */
	String resolveSchema(String uri) throws MalformedURLException, IOException;

	/**
	 * Adds catalog element to the collection of the catalog elements.
	 * 
	 * @param element
	 *            - catalog element
	 */
	void addCatalogElement(ICatalogElement element);

	/**
	 * Removes catalog element from the collection of the catalog elements.
	 * 
	 * @param element
	 *            - catalog element
	 */
	void removeCatalogElement(ICatalogElement element);

	/**
	 * Returns an array of catalog elements of type ICatalogElement.TYPE_ENTRY
	 * 
	 * @return an array of catalog elements
	 */
	ICatalogEntry[] getCatalogEntries();

	/**
	 * Returns an array of catalog elements of type ICatalogElement.TYPE_REWRITE
	 * 
	 * @return an array of rewrite catalog elements
	 */
	IRewriteEntry[] getRewriteEntries();

	/**
	 * Returns an array of catalog elements of type ICatalogElement.TYPE_SUFFIX
	 * 
	 * @return an array of suffix entry elements
	 */
	ISuffixEntry[] getSuffixEntries();

	/**
	 * Returns an array of catalog elements of type
	 * ICatalogElement.TYPE_DELEGATE
	 * 
	 * @return an array of delegate catalog elements
	 */
	IDelegateCatalog[] getDelegateCatalogs();

	/**
	 * Returns an array of catalog elements of type
	 * ICatalogElement.TYPE_DELEGATE
	 * 
	 * @return an array of catalog elements
	 */
	INextCatalog[] getNextCatalogs();

	/**
	 * Returns new catalog element with the specified type. If the type is one
	 * of ELEMENT_TYPE, the result entry will have corresponding interface.
	 * 
	 * @return
	 */
	ICatalogElement createCatalogElement(int type);

	/**
	 * Removes all the elements from this catalog.
	 * 
	 */
	void clear();

	// void load() throws IOException;

	void save() throws IOException;

	/**
	 * This method copies all catalog entries from source catalog to this one.
	 * 
	 * @param catalog
	 *            - source catalog
	 * @param includeNested
	 *            - a boolean flag indicating wether to include entries of the
	 *            same type from the nested catalogs
	 */
	void addEntriesFromCatalog(ICatalog catalog);

	/**
	 * Adds a listener of the catalog events
	 * 
	 * @param listener
	 *            - listener of the catalog events
	 * @see ICatalogEvent
	 */
	void addListener(ICatalogListener listener);

	/**
	 * Removes a listener of the catalog events
	 * 
	 * @param listener
	 *            - listener of the catalog events
	 * @see ICatalogEvent
	 */
	void removeListener(ICatalogListener listener);

}
