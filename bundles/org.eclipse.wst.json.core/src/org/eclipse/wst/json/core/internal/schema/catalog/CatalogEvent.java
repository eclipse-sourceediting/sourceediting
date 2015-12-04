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
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.catalog.CatalogEvent
 *                                           modified in order to process JSON Objects.       
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import org.eclipse.wst.json.core.schema.catalog.ICatalog;
import org.eclipse.wst.json.core.schema.catalog.ICatalogElement;
import org.eclipse.wst.json.core.schema.catalog.ICatalogEvent;

public class CatalogEvent implements ICatalogEvent {
	protected ICatalog catalog;
	protected ICatalogElement catalogElement;
	protected int eventType;

	public CatalogEvent(Catalog catalog, ICatalogElement element, int eventType) {
		this.catalog = catalog;
		this.catalogElement = element;
		this.eventType = eventType;
	}

	public ICatalog getCatalog() {
		return catalog;
	}

	public ICatalogElement getCatalogElement() {
		return catalogElement;
	}

	public int getEventType() {
		return eventType;
	}
}
