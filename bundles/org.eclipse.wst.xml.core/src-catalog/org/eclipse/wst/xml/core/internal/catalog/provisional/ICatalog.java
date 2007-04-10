/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.catalog.provisional;

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
public interface ICatalog
{
    /**
     * Returns catalog id string
     * 
     * @return catalog id string
     */
    public String getId();
    
    /**
     * Sets catalog id string
     * 
     */
    public void setId(String id);
    
    
    public void setLocation(String location);
    public String getLocation();
    
    /**
     * Return the applicable PUBLIC or SYSTEM identifier.
     * 
     * <p>
     * This method searches the Catalog and returns the system identifier
     * specified for the given system or public identifiers. If no appropriate
     * PUBLIC or SYSTEM entry is found in the Catalog, null is returned.
     * </p>
     * 
     * @param publicId
     *            The public identifier to locate in the catalog. Public
     *            identifiers are normalized before comparison.
     * @param systemId
     *            The nominal system identifier for the entity in question (as
     *            provided in the source document).
     * 
     * @throws MalformedURLException
     *             The formal system identifier of a subordinate catalog cannot
     *             be turned into a valid URL.
     * @throws IOException
     *             Error reading subordinate catalog file.
     * 
     * @return The system identifier to use. Note that the nominal system
     *         identifier is not returned if a match is not found in the
     *         catalog, instead null is returned to indicate that no match was
     *         found.
     */
    public String resolvePublic(String publicId, String systemId)
            throws MalformedURLException, IOException;

    /**
     * Return the applicable SYSTEM system identifier.
     * 
     * <p>
     * If a SYSTEM entry exists in the Catalog for the system ID specified,
     * return the mapped value.
     * </p>
     * 
     * <p>
     * On Windows-based operating systems, the comparison between the system
     * identifier provided and the SYSTEM entries in the Catalog is
     * case-insensitive.
     * </p>
     * 
     * @param systemId
     *            The system ID to locate in the catalog.
     * 
     * @return The resolved system identifier.
     * 
     * @throws MalformedURLException
     *             The formal system identifier of a subordinate catalog cannot
     *             be turned into a valid URL.
     * @throws IOException
     *             Error reading subordinate catalog file.
     */
    public String resolveSystem(String systemId) throws MalformedURLException,
            IOException;

    /**
     * Return the applicable URI.
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
    public String resolveURI(String uri) throws MalformedURLException,
            IOException;

    /**
     * Adds catalog element to the collection of the catalog elements.
     * 
     * @param element -
     *            catalog element
     */
    public void addCatalogElement(ICatalogElement element);

    /**
     * Removes catalog element from the collection of the catalog elements.
     * 
     * @param element -
     *            catalog element
     */
    public void removeCatalogElement(ICatalogElement element);

    /**
     * Returns an array of catalog elements of type ICatalogElement.TYPE_ENTRY
     * 
     * @return an array of catalog elements
     */
    public ICatalogEntry[] getCatalogEntries();

    /**
     * Returns an array of catalog elements of type
     * ICatalogElement.TYPE_DELEGATE
     * 
     * @return an array of catalog elements
     */
    public INextCatalog[] getNextCatalogs();

    /**
     * Returns new catalog element with the specified type. If the type is one
     * of ELEMENT_TYPE, the result entry will have corresponding interface.
     * 
     * @return
     */
    public ICatalogElement createCatalogElement(int type);

    /**
     * Removes all the elements from this catalog.
     * 
     */
    public void clear();
    
    //public void load() throws IOException;
    
    public void save() throws IOException;

    /*
     * Loads catalog from the specified location. Expecting OASIS XML Catalog
     * format
     * 
     * @param location -
     *            uri of the file where catalog will be saved

     * @throws IOException -
     *             problem loading file
     * 
     */
    //public void load(InputStream inputStream) throws IOException;

    /*
     * Saves catalog to the specified location in OASIS XML Catalog format
     * 
     * @param location -
     *            uri of the file where catalog will be saved

     * @throws IOException -
     *             problem saving file
     * 
     */
    //public void save(OutputStream outputStream) throws IOException;

    /**
     * This method copies all catalog entries from source catalog to this one.
     * 
     * @param catalog -
     *            source catalog
     * @param includeNested -
     *            a boolean flag indicating wether to include entries of the
     *            same type from the nested catalogs
     */
    public void addEntriesFromCatalog(ICatalog catalog);

    /**
     * Adds a listener of the catalog events
     * 
     * @param listener -
     *            listener of the catalog events
     * @see ICatalogEvent
     */
    public void addListener(ICatalogListener listener);

    /**
     * Removes a listener of the catalog events
     * 
     * @param listener -
     *            listener of the catalog events
     * @see ICatalogEvent
     */
    public void removeListener(ICatalogListener listener);      
}
