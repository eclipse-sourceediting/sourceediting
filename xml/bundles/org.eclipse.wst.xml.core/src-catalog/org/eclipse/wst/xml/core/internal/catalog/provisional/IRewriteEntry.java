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
public interface IRewriteEntry extends ICatalogElement
{
    /** The rewriteSystem Catalog type. */
    int REWRITE_TYPE_SYSTEM = 21;

    /** The URI Catalog Entry type. */
    int REWRITE_TYPE_URI = 22;

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
    void setStartString(String startString);

    /**
     * 
     * @return
     */
    String getStartString();

    /**
     * 
     * @return
     */
    String getRewritePrefix();

    /**
     * 
     * @param uri
     */
    void setRewritePrefix(String uri);
}
