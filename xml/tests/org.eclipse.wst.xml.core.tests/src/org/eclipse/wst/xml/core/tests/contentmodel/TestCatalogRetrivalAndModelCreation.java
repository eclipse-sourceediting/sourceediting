/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IDelegateCatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IRewriteEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ISuffixEntry;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.modelquery.XMLCatalogIdResolver;
import org.eclipse.wst.xml.core.tests.util.FileUtil;

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
	public final static String LOCAL_SCHEMA_FILE = "file:///usr/tmp/mySchemaSchema.xsd";
	public final static String LOCAL_SCHEMA_DIR = "file:///usr/tmp/schemas/";
	public final static String LOCAL_SCHEMA_DIR_REWRITTEN = "file:///usr/tmp/schemas/example.dtd";
	
	public void testCatalog11SystemSuffix() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		ISuffixEntry systemSuffix = (ISuffixEntry)xmlCatalog.createCatalogElement(ISuffixEntry.SUFFIX_TYPE_SYSTEM);
		systemSuffix.setSuffix("XMLSchema.xsd");
		systemSuffix.setURI(LOCAL_SCHEMA_FILE);
		xmlCatalog.addCatalogElement(systemSuffix);
		try {
			String resolved = xmlCatalog.resolveSystem("http://www.w3.org/2001/XMLSchema.xsd");
			assertEquals(LOCAL_SCHEMA_FILE, resolved);
		} finally {
			xmlCatalog.removeCatalogElement(systemSuffix);
		}
	}

	public void testCatalog11UriSuffix() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		ISuffixEntry uriSuffix = (ISuffixEntry)xmlCatalog.createCatalogElement(ISuffixEntry.SUFFIX_TYPE_URI);
		uriSuffix.setSuffix("XMLSchema.xsd");
		uriSuffix.setURI(LOCAL_SCHEMA_FILE);
		xmlCatalog.addCatalogElement(uriSuffix);
		try {
			String resolved = xmlCatalog.resolveURI("http://www.w3.org/2001/XMLSchema.xsd");
			assertEquals(LOCAL_SCHEMA_FILE, resolved);
		} finally {
			xmlCatalog.removeCatalogElement(uriSuffix);
		}
	}

	public void testCatalog11RewriteSystem() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		IRewriteEntry systemSuffix = (IRewriteEntry)xmlCatalog.createCatalogElement(IRewriteEntry.REWRITE_TYPE_SYSTEM);
		systemSuffix.setStartString("http://www.example.org/dtds/");
		systemSuffix.setRewritePrefix(LOCAL_SCHEMA_DIR);
		xmlCatalog.addCatalogElement(systemSuffix);
		try {
			String resolved = xmlCatalog.resolveSystem("http://www.example.org/dtds/example.dtd");
			assertEquals(LOCAL_SCHEMA_DIR_REWRITTEN, resolved);
		} finally {
			xmlCatalog.removeCatalogElement(systemSuffix);
		}
	}

	public void testCatalog11RewriteUri() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		IRewriteEntry uriSuffix = (IRewriteEntry)xmlCatalog.createCatalogElement(IRewriteEntry.REWRITE_TYPE_URI);
		uriSuffix.setStartString("urn:fisk:");
		uriSuffix.setRewritePrefix(LOCAL_SCHEMA_DIR);
		xmlCatalog.addCatalogElement(uriSuffix);
		try {
			String resolved = xmlCatalog.resolveURI("urn:fisk:example.dtd");
			assertEquals(LOCAL_SCHEMA_DIR_REWRITTEN, resolved);
		} finally {
			xmlCatalog.removeCatalogElement(uriSuffix);
		}
	}

	public void testCatalog11DelegatePublic() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		IDelegateCatalog publicDelegate = (IDelegateCatalog)xmlCatalog.createCatalogElement(IDelegateCatalog.DELEGATE_TYPE_PUBLIC);

		// Ironically, we don't use WTP's Resolver when loading the catalog XML.
		// Adding <!DOCTYPE catalog PUBLIC "-//OASIS//DTD XML Catalogs V1.1//EN" "http://www.example.org/dtd/catalog.dtd">
		// to the catalog below will make the loading fail... 

		File delegateCat = makeTempFile("delegatePublic", "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\"\r\n" +
			"       prefer=\"public\">\r\n" + 
			"    <public publicId=\"-//Example//an example V1.0.0//EN\"\r\n" + 
			"            uri=\"file:///example.dtd\"/>\r\n" + 
			"</catalog>");

		publicDelegate.setStartString("-//Example//");
		publicDelegate.setCatalogLocation(delegateCat.toURI().toString());
		xmlCatalog.addCatalogElement(publicDelegate);
		try {
			String resolved = xmlCatalog.resolvePublic("-//Example//an example V1.0.0//EN", "example.dtd");
			assertEquals("file:///example.dtd", resolved);
		} finally {
			xmlCatalog.removeCatalogElement(publicDelegate);
		}
		delegateCat.delete();
	}

	public void testCatalog11DelegateSystem() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		IDelegateCatalog systemDelegate = (IDelegateCatalog)xmlCatalog.createCatalogElement(IDelegateCatalog.DELEGATE_TYPE_SYSTEM);

		File delegateCat = makeTempFile("delegateSystem", "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\r\n" + 
			"    <system systemId=\"http://funky.org/dog\" uri=\"file:///funky-dog.dtd\"/>\r\n" + 
			"    <systemSuffix systemIdSuffix=\"/cat.dtd\" uri=\"file:///smellycat.xsd\"/>\r\n" + 
			"    <rewriteSystem systemIdStartString=\"http://funky.org/parrots/\" rewritePrefix=\"file:///dtds/parrots/\"/>\r\n" + 
			"</catalog>");

		systemDelegate.setStartString("http://funky.org/");
		systemDelegate.setCatalogLocation(delegateCat.toURI().toString());
		xmlCatalog.addCatalogElement(systemDelegate);
		try {			
			assertEquals("try systemId entry", "file:///funky-dog.dtd", xmlCatalog.resolveSystem("http://funky.org/dog"));
			assertEquals("try systemSuffix entry", "file:///smellycat.xsd", xmlCatalog.resolveSystem("http://funky.org/some/cat.dtd"));
			assertEquals("try rewriteSystem entry", "file:///dtds/parrots/macaw.xsd", xmlCatalog.resolveSystem("http://funky.org/parrots/macaw.xsd"));
		} finally {
			xmlCatalog.removeCatalogElement(systemDelegate);
		}
		delegateCat.delete();
	}

	public void testCatalog11DelegateUri() throws MalformedURLException, IOException {
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		IDelegateCatalog uriDelegate = (IDelegateCatalog)xmlCatalog.createCatalogElement(IDelegateCatalog.DELEGATE_TYPE_URI);

		File delegateCat = makeTempFile("delegateUri", "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\r\n" + 
			"    <uri name=\"urn:funky:dog\" uri=\"file:///funky-dog.dtd\"/>\r\n" + 
			"    <uriSuffix uriSuffix=\":cat\" uri=\"file:///smellycat.dtd\"/>\r\n" + 
			"    <rewriteURI uriStartString=\"urn:funky:fish:\" rewritePrefix=\"file:///dtds/\"/>\r\n" + 
			"</catalog>");

		uriDelegate.setStartString("urn:funky:");
		uriDelegate.setCatalogLocation(delegateCat.toURI().toString());
		xmlCatalog.addCatalogElement(uriDelegate);
		try {			
			assertEquals("uri entry", "file:///funky-dog.dtd", xmlCatalog.resolveURI("urn:funky:dog"));
			assertEquals("uriSuffix entry", "file:///smellycat.dtd", xmlCatalog.resolveURI("urn:funky:where-is-my:cat"));
			assertEquals("rewriteUri entry", "file:///dtds/parrot.dtd", xmlCatalog.resolveURI("urn:funky:fish:parrot.dtd"));
		} finally {
			xmlCatalog.removeCatalogElement(uriDelegate);
		}
		delegateCat.delete();
	}

	private File makeTempFile(String name, String contents) throws IOException {
		File delegateCat = FileUtil.makeFileFor("catalogs", name, "cat");
		FileWriter fw = new FileWriter(delegateCat);
		fw.write(contents);
		fw.close();
		delegateCat.getParentFile().deleteOnExit();
		return delegateCat;
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
