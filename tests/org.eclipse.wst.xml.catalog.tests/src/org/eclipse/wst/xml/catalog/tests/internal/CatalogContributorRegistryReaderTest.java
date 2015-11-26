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
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;



public class CatalogContributorRegistryReaderTest extends AbstractCatalogTest
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public CatalogContributorRegistryReaderTest(String name)
    {
        super(name);
    }

    public final void testReadRegistry() throws Exception
    {
        assertNotNull(defaultCatalog);
        assertEquals(XMLCorePlugin.DEFAULT_CATALOG_ID, defaultCatalog.getId());
        assertEquals(2, defaultCatalog.getNextCatalogs().length);

		String pluginId = TestPlugin.getDefault().getBundle().getSymbolicName();
    
		  // test system entries
        assertNotNull(systemCatalog);
        List entries = CatalogTest.getCatalogEntries(systemCatalog, ICatalogEntry.ENTRY_TYPE_SYSTEM);
		for (int i = 0; i < entries.size(); i++)
		{
			ICatalogEntry entry = (ICatalogEntry)entries.get(i);
			if("testSystemId".equals(entry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "data/Personal/personal.dtd");
				assertEquals(resolvedURI, entry.getURI());
				assertEquals("http://personal/personal.dtd", entry.getKey());
			}
		}
		
		// test public entries
        entries = CatalogTest.getCatalogEntries(systemCatalog, ICatalogEntry.ENTRY_TYPE_PUBLIC);      
		for (int i = 0; i < entries.size(); i++)
		{
			ICatalogEntry entry = (ICatalogEntry)entries.get(i);
			if("testPublicId1".equals(entry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "data/Invoice/Invoice.dtd");
				assertEquals(resolvedURI, entry.getURI());
				assertEquals("InvoiceId_test", entry.getKey());
				// test user defined attributes
				assertEquals("http://org.eclipse.wst.xml.example/Invoice.dtd", entry.getAttributeValue("webURL"));

			}
			
			else if("testMappingInfo".equals(entry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "platform:/plugin/org.eclipse.xsd/cache/www.w3.org/2001/XMLSchema.xsd");
				assertEquals(resolvedURI, entry.getURI());
				assertEquals("http://www.w3.org/2001/XMLSchema1", entry.getKey());
			}
	     
		}
      
        // test uri entries
        entries = CatalogTest.getCatalogEntries(systemCatalog, ICatalogEntry.ENTRY_TYPE_URI);
		for (int i = 0; i < entries.size(); i++)
		{
			ICatalogEntry entry = (ICatalogEntry)entries.get(i);
			if("testURIId1".equals(entry.getId()))
			{
				  String resolvedURI = resolvePath(pluginId, "data/example/example.xsd");
			      assertEquals(resolvedURI, entry.getURI());
			      assertEquals("http://apache.org/xml/xcatalog/example", entry.getKey());
			}
			else if("testURIId2".equals(entry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "platform:/plugin/org.eclipse.xsd/cache/www.w3.org/2001/XMLSchema.xsd");
				assertEquals(resolvedURI, entry.getURI());
				assertEquals("http://www.w3.org/2001/XMLSchema", entry.getKey());
			}
			else if("testURIId3".equals(entry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "jar:platform:/plugin/org.eclipse.wst.xml.catalog.tests/data/schemas.jar!/data/catalog.xsd");
				assertEquals(resolvedURI, entry.getURI());
				assertEquals("http://oasis.names.tc.entity.xmlns.xml.catalog", entry.getKey());
			}
		}
      
        // test tested catalog
        INextCatalog[] nextCatalogEntries = systemCatalog.getNextCatalogs();
        for (int i = 0; i < nextCatalogEntries.length; i++)
		{
			INextCatalog nextCatalogEntry = (INextCatalog) nextCatalogEntries[i];
			if("testNestedCatalog".equals(nextCatalogEntry.getId()))
			{
				String resolvedURI = resolvePath(pluginId, "data/catalog1.xml");
				assertEquals(resolvedURI, nextCatalogEntry.getCatalogLocation());
				ICatalog nextCatalog = nextCatalogEntry.getReferencedCatalog();
				assertNotNull(nextCatalog);
				assertEquals(3, nextCatalog.getCatalogEntries().length);
				// test public entries
				entries = CatalogTest.getCatalogEntries(nextCatalog,
						ICatalogEntry.ENTRY_TYPE_PUBLIC);
				assertEquals(1, entries.size());
				ICatalogEntry entry = (ICatalogEntry) entries.get(0);
				//URI uri = URIHelper.getURIForFilePath(resolvedURI);
				//resolvedURI = URIHelper.makeAbsolute(uri.toURL(), "./Invoice/Invoice.dtd");
				assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
				assertEquals("InvoiceId_test", entry.getKey());
				// test system entries
				entries = CatalogTest.getCatalogEntries(nextCatalog,
						ICatalogEntry.ENTRY_TYPE_SYSTEM);
				assertEquals(1, entries.size());
				entry = (ICatalogEntry) entries.get(0);
				assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
				assertEquals("Invoice.dtd", entry.getKey());
				// test uri entries
				entries = CatalogTest.getCatalogEntries(nextCatalog,
						ICatalogEntry.ENTRY_TYPE_URI);
				assertEquals(1, entries.size());
				entry = (ICatalogEntry) entries.get(0);
				assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
				assertEquals("http://www.test.com/Invoice.dtd", entry.getKey());
			}
		}
    }
}
