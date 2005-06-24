package org.eclipse.wst.xml.catalog.tests.internal;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.catalog.Catalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;

public class CatalogWriterTest extends AbstractCatalogTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public CatalogWriterTest(String name) {
		super(name);
	}

	public final void testWrite() throws Exception {

		// read catalog
		String catalogFile = "/data/catalog1.xml";
		URL catalogUrl = TestPlugin.getDefault().getBundle().getEntry(
				catalogFile);
		assertNotNull(catalogUrl);
		URL resolvedURL = Platform.resolve(catalogUrl);

		Catalog testCatalog = (Catalog) getCatalog("catalog1", resolvedURL
				.toString());
		assertNotNull(testCatalog);
		testCatalog.setBase(resolvedURL.toString());
		// CatalogReader.read(testCatalog, resolvedURL.getFile());
		assertNotNull(testCatalog);

		// write catalog
		URL resultsFolder = TestPlugin.getDefault().getBundle().getEntry(
				"/actual_results");
		IPath path = new Path(Platform.resolve(resultsFolder).getFile());
		String resultCatalogFile = path.append("catalog1.xml").toFile().toURI().toString();
		testCatalog.setLocation(resultCatalogFile);
		// write catalog
		testCatalog.save();
		
		// read catalog file from the saved location and test its content
		Catalog catalog = (Catalog) getCatalog("catalog2", testCatalog.getLocation());
		assertNotNull(catalog);

		// test saved catalog - catalog1.xml
		assertEquals(3, catalog.getCatalogEntries().length);

		// test public entries
		List entries = CatalogTest.getCatalogEntries(catalog,
				ICatalogEntry.ENTRY_TYPE_PUBLIC);
		assertEquals(1, entries.size());
		ICatalogEntry entry = (ICatalogEntry) entries.get(0);

		assertEquals("./Invoice/Invoice.dtd", entry.getURI());
		assertEquals("InvoiceId_test", entry.getKey());
		assertEquals("http://webURL", entry.getAttributeValue("webURL"));

		// test system entries
		entries = CatalogTest.getCatalogEntries(catalog,
				ICatalogEntry.ENTRY_TYPE_SYSTEM);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry) entries.get(0);
		assertEquals("./Invoice/Invoice.dtd", entry.getURI());
		assertEquals("Invoice.dtd", entry.getKey());
		assertEquals("yes", entry.getAttributeValue("chached"));
		assertEquals("value1", entry.getAttributeValue("property"));

		// test uri entries
		entries = CatalogTest.getCatalogEntries(catalog,
				ICatalogEntry.ENTRY_TYPE_URI);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry) entries.get(0);
		assertEquals("./Invoice/Invoice.dtd", entry.getURI());
		assertEquals("http://www.test.com/Invoice.dtd", entry.getKey());
		assertEquals("no", entry.getAttributeValue("chached"));
		assertEquals("value2", entry.getAttributeValue("property"));

		// test next catalog - catalog2.xml
		INextCatalog[] nextCatalogEntries = catalog.getNextCatalogs();
		assertEquals(1, nextCatalogEntries.length);

		INextCatalog nextCatalogEntry = (INextCatalog) nextCatalogEntries[0];
		assertNotNull(nextCatalogEntry);

		assertEquals("catalog2.xml", nextCatalogEntry.getCatalogLocation());
	}

}
