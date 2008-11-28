/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.modelquery.XMLCatalogIdResolver;

public class TestCatalogRetrivalAndModelCreation extends TestCase {

	/**
	 * Test that a known error case returns null.
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void testKnownNotExist() throws MalformedURLException, IOException {
		String JUNK_STRING = "doesNotExistTest";
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolved = xmlCatalog.resolvePublic(JUNK_STRING, null);
		assertNull("expected no match for " + JUNK_STRING, resolved);
	}

	public void removedtestWAPFORUM() throws MalformedURLException, IOException {
		doTest("-//WAPFORUM//DTD WML 1.1//EN");
	}

	public void testXHTML10() throws MalformedURLException, IOException {
		doTest("-//W3C//DTD XHTML 1.0 Strict//EN");
	}

	public void testXHTML10T() throws MalformedURLException, IOException {
		doTest("-//W3C//DTD XHTML 1.0 Transitional//EN");
	}

	public void testXHTML10F() throws MalformedURLException, IOException {
		doTest("-//W3C//DTD XHTML 1.0 Frameset//EN");
	}

	public void testXHTML10B() throws MalformedURLException, IOException {
		doTest("-//W3C//DTD XHTML Basic 1.0//EN");
	}

	public void testXHTML11() throws MalformedURLException, IOException {
		doTest("-//W3C//DTD XHTML 1.1//EN");
	}
	
	/**
	 * We expect the XMLCatalogResolver and XMLCatalogIdResolver
	 * to resolve to the same URI given a public id in our catalog.
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void testXHTML11_xmlresolver() throws MalformedURLException, IOException {
		String EXPECTED_PUBLICID = "-//W3C//DTD XHTML 1.1//EN";
		
		// bug 117424, here ewe make up a base location to satisfy the resolver API
		// that expects a non-null base location
		String baseLocation = "http://www.example.org/testXHTML11.xml";
		XMLCatalogIdResolver resolver = new XMLCatalogIdResolver(baseLocation, null);
		String resolvedXML_Id = resolver.resolve(baseLocation, EXPECTED_PUBLICID, null);
		
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolvedXL_Cat = xmlCatalog.resolvePublic(EXPECTED_PUBLICID, null);
		assertEquals(resolvedXL_Cat, resolvedXML_Id);		 
	}
	
	public void testCMXHTML11() throws MalformedURLException, IOException {
		doCMTest("-//W3C//DTD XHTML 1.1//EN");
	}
	public void removedtestXHTML10M() throws MalformedURLException, IOException {
		doTest("-//WAPFORUM//DTD XHTML Mobile 1.0//EN");
	}

	public void removedtestWAP13() throws MalformedURLException, IOException {
		doTest("-//WAPFORUM//DTD WML 1.3//EN");
	}

	public void test2001Schema() throws MalformedURLException, IOException {
		doURITest("http://www.w3.org/2001/XMLSchema");
	}

	/**
	 * Tests if dtd NOT in our catalog can have a content model 
	 * created. 
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void testExternalDTD() throws MalformedURLException, IOException {
		doCM_directURITest("http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
	}

	public void test2001SchemaCMDirect() throws MalformedURLException, IOException {
		doCM_directURITest("http://www.w3.org/2001/XMLSchema.xsd");
	}
	
	public void test2001SchemaCMCatalog() throws MalformedURLException, IOException {
		doURI_CMTest("http://www.w3.org/2001/XMLSchema");
	}
	
	public void testInvoiceRemote() throws MalformedURLException, IOException {
		doCM_directURITest_checkElementCount("http://www.eclipse.org/webtools/wst/components/xsd/tests/dtd-references/Invoice.dtd", 18);
	}	
	
	public void testInvoiceRemoteIndirect() throws MalformedURLException, IOException {
		doCM_directURITest_checkElementCount("http://www.eclipse.org/webtools/wst/components/xsd/tests/dtd-references/IndirectInvoice.dtd", 18);
	}
	
	private void doTest(String EXPECTED_PUBLICID) throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolved = xmlCatalog.resolvePublic(EXPECTED_PUBLICID, null);
		assertNotNull("expected to find " + EXPECTED_PUBLICID, resolved);
	}

	private void doCMTest(String EXPECTED_PUBLICID) throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolved = xmlCatalog.resolvePublic(EXPECTED_PUBLICID, null);
		ContentModelManager contentModelManager = ContentModelManager.getInstance();
		CMDocument contentModel = contentModelManager.createCMDocument(resolved, null);
		assertNotNull("expected to create content model for " + EXPECTED_PUBLICID, contentModel);
	}

	private void doCM_directURITest(String EXPECTED_URI) throws MalformedURLException, IOException {
		ContentModelManager contentModelManager = ContentModelManager.getInstance();
		CMDocument contentModel = contentModelManager.createCMDocument(EXPECTED_URI, null);
		assertNotNull("expected to create content model for " + EXPECTED_URI, contentModel);
	}
	
	private void doCM_directURITest_checkElementCount(String EXPECTED_URI, int count) throws MalformedURLException, IOException {
		ContentModelManager contentModelManager = ContentModelManager.getInstance();
		CMDocument contentModel = contentModelManager.createCMDocument(EXPECTED_URI, null);
		assertNotNull("expected to create content model for " + EXPECTED_URI, contentModel);
		int actualCount = contentModel.getElements().getLength(); 
		assertEquals("count of element declarations found for content model create from " + EXPECTED_URI, contentModel.getElements().getLength(), actualCount);		
	}	

	private void doURITest(String EXPECTED_URI) throws MalformedURLException, IOException {
 
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolved = xmlCatalog.resolveURI(EXPECTED_URI);
		assertNotNull("expected to find " + EXPECTED_URI, resolved);

	}
	 void doURI_CMTest(String EXPECTED_URI) throws MalformedURLException, IOException {
		 
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String resolved = xmlCatalog.resolveURI(EXPECTED_URI);
		assertNotNull("expected to find " + EXPECTED_URI, resolved);
		
		ContentModelManager contentModelManager = ContentModelManager.getInstance();
		CMDocument contentModel = contentModelManager.createCMDocument(resolved, null);
		assertNotNull("expected to create content model for " + EXPECTED_URI, contentModel);

	}


}
