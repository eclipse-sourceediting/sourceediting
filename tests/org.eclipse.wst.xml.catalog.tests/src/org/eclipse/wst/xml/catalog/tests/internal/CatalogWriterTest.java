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

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
		URL resolvedURL = FileLocator.resolve(catalogUrl);

		Catalog testCatalog = (Catalog) getCatalog("catalog1", resolvedURL
				.toString());
		assertNotNull(testCatalog);
		testCatalog.setBase(resolvedURL.toString());
		// CatalogReader.read(testCatalog, resolvedURL.getFile());
		assertNotNull(testCatalog);

		// write catalog
		URL resultsFolder = TestPlugin.getDefault().getBundle().getEntry(
				"/");
		IPath path = new Path(FileLocator.resolve(resultsFolder).getFile());
		String resultCatalogFile = path.append("/catalog1.xml").toFile().toURI().toString();
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

		assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
		assertEquals("InvoiceId_test", entry.getKey());
		assertEquals("http://webURL", entry.getAttributeValue("webURL"));

		// test system entries
		entries = CatalogTest.getCatalogEntries(catalog,
				ICatalogEntry.ENTRY_TYPE_SYSTEM);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry) entries.get(0);
		assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
		assertEquals("Invoice.dtd", entry.getKey());
		assertEquals("yes", entry.getAttributeValue("chached"));
		assertEquals("value1", entry.getAttributeValue("property"));

		// test uri entries
		entries = CatalogTest.getCatalogEntries(catalog,
				ICatalogEntry.ENTRY_TYPE_URI);
		assertEquals(1, entries.size());
		entry = (ICatalogEntry) entries.get(0);
		assertEquals(getFileLocation("data/Invoice/Invoice.dtd"), entry.getURI());
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
	
    public final void testBug235445() throws Exception {
      // read catalog
      String catalogFile = "/data/deletemecatalog.xml";
      URL catalogUrl = TestPlugin.getDefault().getBundle().getEntry(
              catalogFile);
      assertNotNull(catalogUrl);
      URL resolvedURL = FileLocator.resolve(catalogUrl);

      Catalog testCatalog = (Catalog) getCatalog("deletemecatalog", resolvedURL
              .toString());
      assertNotNull(testCatalog);
      testCatalog.setBase(resolvedURL.toString());
      // CatalogReader.read(testCatalog, resolvedURL.getFile());
      assertNotNull(testCatalog);

      // write catalog
      URL resultsFolder = TestPlugin.getDefault().getBundle().getEntry(
              "/");
      IPath path = new Path(FileLocator.resolve(resultsFolder).getFile());
      final File catalogIOFile = path.append("/deletemecatalog.xml").toFile();
      String resultCatalogFile = catalogIOFile.toURI().toString();
      testCatalog.setLocation(resultCatalogFile);
      
      // write catalog
      testCatalog.save();
      
      IWorkspaceRunnable deleteOp = new IWorkspaceRunnable() {
        public void run(IProgressMonitor monitor) throws CoreException
        {
          try {
            catalogIOFile.delete();
          } catch (Exception e) {
            assertTrue("exception thrown when trying to delete catalog", false);
          }
        }
      };
      
      ResourcesPlugin.getWorkspace().run(deleteOp, null);
      assertFalse("catalog file was not deleted", catalogIOFile.exists());
    }


	/*
	 * Class under test for void read(ICatalog, String)
	 */
	public void testReadAndWriteComplexCatalog() throws Exception {

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

		// write catalog so we can read it back in
		URL resultsFolder = TestPlugin.getDefault().getBundle().getEntry(
				"/");
		IPath path = new Path(FileLocator.resolve(resultsFolder).getFile());
		String resultCatalogFile = path.append("/catalog11-x.xml").toFile().toURI().toString();
		catalog.setLocation(resultCatalogFile);
		
		
		
		// write catalog
		catalog.save();
		
		// read catalog file from the saved location and test its content
		catalog = (Catalog) getCatalog("catalog2", catalog.getLocation());
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

}
