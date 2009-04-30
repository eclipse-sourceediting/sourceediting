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
public interface ICatalogElement
{
    /** Types of the catalog entries */
    /** The PUBLIC, URI or SYSTEM Catalog Entry. */
    int TYPE_ENTRY = 1;

    /** The NEXT_CATALOG Catalog Entry type. */
    int TYPE_NEXT_CATALOG = 10;

    /** Rewrite types (since XML Catalogs 1.1) */
    int TYPE_REWRITE = 20;

    /** Delegate types (sinceXML Catalogs 1.1) */
    int TYPE_DELEGATE = 30;
    
    /** Suffix types (since XML Catalogs 1.1)  */
    int TYPE_SUFFIX = 40;

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * 
     * @return the value of the '<em>Type</em>' attribute.
     */
    int getType();

    /**
     * Returns the value of the attribute with specified name.
     * 
     * @return the value of the attribute with specified name.
     * @see #setAttributeValue(String)
     */
    String getAttributeValue(String name);

    /**
     * Sets the value of the named attribute.
     * 
     * @param name
     *            the name of the attribute that will be set
     * @param value
     *            the new value of the named attribute.
     * @see #getAttributeValue()
     */
    void setAttributeValue(String name, String value);

    /**
     * Returns an array of attribute names for any dynamic attributes that may exist
     * 
     * @return array of attribute names
     * @see #getAttributeValue()
     * @see #setAttributeValue(String)
     */
    String[] getAttributes();
    
    /**
     * Returns element's id string
     * 
     * @return element's id string
     */
    public String getId();
    
    /**
     * Sets element's id string
     * 
     */
    public void setId(String id);
    
    public void setOwnerCatalog(ICatalog catalog);
    
    public ICatalog getOwnerCatalog();

	void setBase(String base);

	String getBase();
}
