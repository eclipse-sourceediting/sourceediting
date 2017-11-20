/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.util.Set;

import org.eclipse.wst.json.core.schema.catalog.ICatalog;
import org.eclipse.wst.json.core.schema.catalog.ICatalogElement;
import org.eclipse.wst.json.core.schema.catalog.ICatalogEntry;

public class CatalogUserCatalogReader {

	protected ICatalog catalog;

	protected CatalogUserCatalogReader(ICatalog catalog) {
		this.catalog = catalog;
	}

	protected void readCatalog() {
		int type = ICatalogEntry.ENTRY_TYPE_SCHEMA;
		Set<UserEntry> entries = EntryParser.getUserEntries();
		for (UserEntry ue : entries) {
			ICatalogElement catalogElement = catalog.createCatalogElement(type);
			if (catalogElement instanceof ICatalogEntry) {
				ICatalogEntry entry = (ICatalogEntry) catalogElement;
				entry.setKey(ue.getFileMatch());
				entry.setURI(ue.getUrl().toString());
				entry.setId(ue.getFileMatch());
			}
			catalog.addCatalogElement(catalogElement);
		}
	}

}
