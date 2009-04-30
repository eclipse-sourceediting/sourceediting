/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moeller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog.provisional;

/**
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface IDelegateCatalog extends ICatalogElement
{
    /** The SYSTEM Catalog Entry type. */
    int DELEGATE_TYPE_PUBLIC = 31;

    /** The SYSTEM Catalog Entry type. */
    int DELEGATE_TYPE_SYSTEM = 32;

    /** The URI Catalog Entry type. */
    int DELEGATE_TYPE_URI = 33;

    /**
     * 
     * @param entryType
     */
    void setEntryType(int entryType);

    /**
     * 
     * @return
     */
    int getEntryType();

    /**
     * 
     * @param key
     */
    void setStartString(String key);

    /**
     * 
     * @return
     */
    String getStartString();
    
    /**
     * Set location of the referenced catalog.
     * 
     * @param uri -
     *            location uri of the referenced catalog
     */
    void setCatalogLocation(String uri);

    /**
     * Get location uri of the referenced catalog.
     * 
     * @return location uri of the referenced catalog
     */
    String getCatalogLocation();
    
    ICatalog getReferencedCatalog();
}
