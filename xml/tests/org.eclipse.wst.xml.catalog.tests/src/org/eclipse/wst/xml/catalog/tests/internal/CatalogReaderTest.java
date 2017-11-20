/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.catalog.tests.internal;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.wst.xml.core.internal.catalog.Catalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;


public class CatalogReaderTest extends AbstractCatalogTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public CatalogReaderTest(String name) {
		super(name);
	}


	/*
	 * Class under test for void read(ICatalog, String)
	 */
	public void testReadCatalog()throws Exception {

		//read catalog
		String catalogFile = "/data/catalog1.xml";
		URL catalogUrl = TestPlugin.getDefault().getBundle().getEntry(catalogFile);
		assertNotNull(catalogUrl);
		URL base = FileLocator.resolve(catalogUrl);

		Catalog catalog = (Catalog)getCatalog("catalog1", base.toString());
		//CatalogReader.read(catalog, catalogFilePath);
		assertNotNull(catalog);
		
		// test main catalog - catalog1.xml
		//assertEquals("cat1", catalog.getId());
		assertEquals(3, catalog.getCatalogEntries().length);
			
		// test public entries
		List entries = CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_PUBLIC);
		assertEquals(1, entries.size());
		ICatalogEntry entry = (ICatalogEntry)entries.get(0);
		//String resolvedURI = URIHelper.makeAbsolute(base, "./Invoice/Invoice.dtd");

		
		assertEquals(getFileLocation("/data/Invoice/Invoice.dtd"), entry.getURI());
		assertEquals("InvoiceId_test", entry.getKey());
		assertEquals("http://webURL", entry.getAttributeValue("webURL"));

		
		//  test system entries
		entries = CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_SYSTEM);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry)entries.get(0);
		assertEquals(getFileLocation("/data/Invoice/Invoice.dtd"), entry.getURI());
		assertEquals("Invoice.dtd", entry.getKey());
		assertEquals("yes", entry.getAttributeValue("chached"));
		assertEquals("value1", entry.getAttributeValue("property"));


		//  test uri entries
		entries = CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_URI);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry)entries.get(0);
		assertEquals(getFileLocation("/data/Invoice/Invoice.dtd"), entry.getURI());
		assertEquals("http://www.test.com/Invoice.dtd", entry.getKey());
		assertEquals("no", entry.getAttributeValue("chached"));
		assertEquals("value2", entry.getAttributeValue("property"));
		
		//  test next catalog - catalog2.xml
		INextCatalog[] nextCatalogEntries = catalog.getNextCatalogs();
		assertEquals(1, nextCatalogEntries.length);
	
		INextCatalog nextCatalogEntry = (INextCatalog)nextCatalogEntries[0];
		assertNotNull(nextCatalogEntry);
		
//		String catalogRefId = nextCatalogEntry.getCatalogRefId();
//		assertEquals("nextCatalog1", catalogRefId);
		//resolvedURI = URIHelper.makeAbsolute(base, "catalog2.xml");
		assertEquals("catalog2.xml", nextCatalogEntry.getCatalogLocation());

		ICatalog nextCatalog = nextCatalogEntry.getReferencedCatalog();

		assertNotNull(nextCatalog);
		assertEquals(4, nextCatalog.getCatalogEntries().length);
		
		// test public entries
		entries = CatalogTest.getCatalogEntries(nextCatalog, ICatalogEntry.ENTRY_TYPE_PUBLIC);
		assertEquals(2, entries.size());
		entry = (ICatalogEntry)entries.get(0);
		//resolvedURI = URIHelper.makeAbsolute(nextCatalog.getBase(), "./PublicationCatalogue/Catalogue.xsd");
		assertEquals(getFileLocation("/data/PublicationCatalogue/Catalogue.xsd"), entry.getURI());
		assertEquals("http://www.eclipse.org/webtools/Catalogue_001", entry.getKey());
		
		// test public entry from a group
		entry = (ICatalogEntry)entries.get(1);
		//resolvedURI = URIHelper.makeAbsolute(nextCatalog.getBase(), "./PublicationCatalogue/Catalogue.xsd");
		assertEquals(getFileLocation("/data/PublicationCatalogue/Catalogue.xsd"), entry.getURI());
		assertEquals("http://www.eclipse.org/webtools/Catalogue_002", entry.getKey());

		//  test system entries
		entries = CatalogTest.getCatalogEntries(nextCatalog, ICatalogEntry.ENTRY_TYPE_SYSTEM);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry)entries.get(0);
		assertEquals(getFileLocation("/data/PublicationCatalogue/Catalogue.xsd"), entry.getURI());
		assertEquals("Catalogue.xsd", entry.getKey());
		//  test uri entries
		entries = CatalogTest.getCatalogEntries(nextCatalog, ICatalogEntry.ENTRY_TYPE_URI);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry)entries.get(0);
		assertEquals("http://www.eclipse.org/webtools/Catalogue/Catalogue.xsd", entry.getURI());
		assertEquals("http://www.eclipse.org/webtools/Catalogue.xsd", entry.getKey());
		

	}

	/*
	 * Class under test for void read(ICatalog, String)
	 */
	public void testReadComplexCatalog() throws Exception {

		//read catalog
		String catalogFile = "/data/delegateAndRewrite/catalog11.xml";
		URL catalogUrl = TestPlugin.getDefault().getBundle().getEntry(catalogFile);
		assertNotNull(catalogUrl);
		URL base = FileLocator.resolve(catalogUrl);

		Catalog catalog = (Catalog)getCatalog("catalog11", base.toString());
		//CatalogReader.read(catalog, catalogFilePath);
		assertNotNull(catalog);
		
		// test main catalog - catalog1.xml
		//assertEquals("cat1", catalog.getId());
		assertEquals(13, catalog.getCatalogElements().length);
			
		// test public entries
		assertEquals(2, CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_PUBLIC).size());

		//  test system entries
		assertEquals(2, CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_SYSTEM).size());

		//  test uri entries
		assertEquals(1, CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_URI).size());

		//  test next catalog - catalog2.xml
		INextCatalog[] nextCatalogEntries = catalog.getNextCatalogs();
		assertEquals(1, nextCatalogEntries.length);
	
		INextCatalog nextCatalogEntry = (INextCatalog)nextCatalogEntries[0];
		assertNotNull(nextCatalogEntry);
		
		assertEquals("catalog.xml", nextCatalogEntry.getCatalogLocation());
	}

	
	public void testCompatabilityReader() throws Exception {
		//	read catalog
		String catalogFile = "/data/compatabilityTest.xmlcatalog";
		URL catalogUrl = TestPlugin.getDefault().getBundle().getEntry(catalogFile);
		assertNotNull(catalogUrl);
		URL base = FileLocator.resolve(catalogUrl);

		Catalog catalog = (Catalog)getCatalog("compatabilityCatalog", base.toString());
		//CatalogReader.read(catalog, catalogFilePath);
		assertNotNull(catalog);
		List entries = CatalogTest.getCatalogEntries(catalog, ICatalogEntry.ENTRY_TYPE_PUBLIC);
		assertEquals(1, entries.size());
		ICatalogEntry entry = (ICatalogEntry)entries.get(0);
		assertEquals("platform:/resource/XMLExamples/Invoice2/Invoice.dtd", entry.getURI());
		assertEquals("InvoiceId", entry.getKey());
	}
}
