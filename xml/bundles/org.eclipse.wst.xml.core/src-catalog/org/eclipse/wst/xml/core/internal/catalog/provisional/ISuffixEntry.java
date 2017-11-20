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
public interface ISuffixEntry extends ICatalogElement
{
    /** The rewriteSystem Catalog type. */
    int SUFFIX_TYPE_SYSTEM = 41;

    /** The URI Catalog Entry type. */
    int SUFFIX_TYPE_URI = 42;

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
    void setSuffix(String suffixString);

    /**
     * 
     * @return
     */
    String getSuffix();

    /**
     * 
     * @return
     */
    String getURI();

    /**
     * 
     * @param uri
     */
    void setURI(String uri);
}
