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

import java.util.EventListener;


/**
 * The clients of the catalog that want to listen to catalog changes should
 * implement this interface.
 * 
 * @see ICatalog, ICatalogEvent,
 * 
 */
public interface ICatalogListener extends EventListener
{
    /**
     * This method allows to react to catalog events
     * 
     * @param event -
     *            an event that client should react to
     */
    public void catalogChanged(ICatalogEvent event);
}
