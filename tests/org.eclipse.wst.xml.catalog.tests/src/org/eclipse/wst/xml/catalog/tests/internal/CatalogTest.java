/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.catalog.tests.internal;

import java.util.List;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.Catalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;



public class CatalogTest extends AbstractCatalogTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public CatalogTest(String name) {
		super(name);
	}
    
    public void testCatalog() throws Exception
    {
       Catalog workingUserCatalog = new Catalog(null, "working", null);
       assertNotNull(userCatalog);
       workingUserCatalog.addEntriesFromCatalog(userCatalog);
       
       ICatalogEntry catalogEntry = (ICatalogEntry)userCatalog.createCatalogElement(ICatalogEntry.ENTRY_TYPE_PUBLIC);
       catalogEntry.setKey("testKey");
       catalogEntry.setURI("http://testuri");
       workingUserCatalog.addCatalogElement(catalogEntry);
     
       userCatalog.addEntriesFromCatalog(workingUserCatalog);
	   String userCatalogLocation = userCatalog.getLocation();
	  
       userCatalog.save();
       userCatalog.clear();
       
       userCatalog = getCatalog(XMLCorePlugin.USER_CATALOG_ID, userCatalogLocation);
       
       List entries = getCatalogEntries(userCatalog, ICatalogEntry.ENTRY_TYPE_PUBLIC);
       assertEquals(1, entries.size());
       ICatalogEntry entry = (ICatalogEntry)entries.get(0);
     
       assertEquals("http://testuri", entry.getURI());
       assertEquals("testKey", entry.getKey());

   
    }


   

}
