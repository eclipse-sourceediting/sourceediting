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
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface ICatalogEvent
{
    /** */
    public static final int CHANGED = 0;

    public static final int ELEMENT_ADDED = 1;

    /** */
    public static final int ELEMENT_REMOVED = 2;

    /** */
    public static final int ELEMENT_CHANGED = 3;

    /**
     * 
     * @return
     */
    public int getEventType();

    /**
     * 
     * @return
     */
    public ICatalog getCatalog();

    /**
     * 
     * @return
     */
    public ICatalogElement getCatalogElement();
}
