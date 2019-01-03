/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEvent;


public class CatalogEvent implements ICatalogEvent
{
  protected ICatalog catalog;
  protected ICatalogElement catalogElement;
  protected int eventType;

  public CatalogEvent(Catalog catalog, ICatalogElement element, int eventType)
  {
    this.catalog = catalog;
    this.catalogElement = element;
    this.eventType = eventType;
  }

  public ICatalog getCatalog()
  {
    return catalog;
  }

  public ICatalogElement getCatalogElement()
  {
    return catalogElement;
  }

  public int getEventType()
  {
    return eventType;
  }
}
