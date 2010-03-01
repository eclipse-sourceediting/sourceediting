/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
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

/**
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface ICatalogEntry extends ICatalogElement
{
    /** The PUBLIC Catalog Entry type. */
    public static final int ENTRY_TYPE_PUBLIC = 2;

    /** The SYSTEM Catalog Entry type. */
    public static final int ENTRY_TYPE_SYSTEM = 3;

    /** The URI Catalog Entry type. */
    public static final int ENTRY_TYPE_URI = 4;

    /** Attribute name for Web address of catalog entry */
    public static final String ATTR_WEB_URL = "webURL"; //$NON-NLS-1$

    /**
     * 
     * @param entryType
     */
    public void setEntryType(int entryType);

    /**
     * 
     * @return
     */
    public int getEntryType();

    /**
     * 
     * @param key
     */
    public void setKey(String key);

    /**
     * 
     * @return
     */
    public String getKey();

    /**
     * 
     * @return
     */
    public String getURI();

    /**
     * 
     * @param uri
     */
    public void setURI(String uri);
}
