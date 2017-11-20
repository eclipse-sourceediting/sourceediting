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



/**
 * 
 * A representation of the nextCatalog OASIS XML catalog element. Object of the
 * class that implements this interface would serve as a reference to the
 * catalog object.
 * 
 * @see ICatalog, ICatalogElement
 * 
 * This interface currently is used only by the catalog itself. Need to find if
 * there are any clients that need it.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface INextCatalog extends ICatalogElement
{
    /**
     * Set location of the referenced catalog.
     * 
     * @param uri -
     *            location uri of the referenced catalog
     */
    public void setCatalogLocation(String uri);

    /**
     * Get location uri of the referenced catalog.
     * 
     * @return location uri of the referenced catalog
     */
    public String getCatalogLocation();
    
    public ICatalog getReferencedCatalog();

   
}
