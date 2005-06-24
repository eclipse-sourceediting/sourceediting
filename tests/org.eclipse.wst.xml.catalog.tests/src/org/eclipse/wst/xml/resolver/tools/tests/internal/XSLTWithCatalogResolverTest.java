package org.eclipse.wst.xml.resolver.tools.tests.internal;

import java.io.File;

import junit.framework.TestCase;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.eclipse.wst.xml.catalog.tests.internal.TestPlugin;


/**
 * Test from http://issues.apache.org/bugzilla/show_bug.cgi?id=16336
 * 
 * To run this test need to add resolver.jar to the classpath.
 * Run as JUnit Plugin test:
 * - put resolver.jar on the boot class path
 * - add VM argument:
 * 
 * -Xbootclasspath/p:<install location>\jre\lib\ext\resolver.jar
 *
 *
 */
public class XSLTWithCatalogResolverTest extends TestCase {

	CatalogResolver catalogResolver = null;
	TraXLiaison xsltLiason = null;
	static String SEP = File.separator;

	public XSLTWithCatalogResolverTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(XSLTWithCatalogResolverTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();	
		catalogResolver = new CatalogResolver();
		xsltLiason = new TraXLiaison();
		xsltLiason.setEntityResolver(catalogResolver);
		xsltLiason.setURIResolver(catalogResolver);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testXSLTwithCatalogResolver() throws Exception {
		
		String testDirName = "data/testXSLTwithCatalogResolver";
		testDirName = TestPlugin.resolvePluginLocation(testDirName);
		
		String catalogFileName = testDirName + SEP + "catalog.xml";	
		String xslFileName = testDirName + SEP + "xmlcatalog.xsl";
		String xmlFileName = testDirName + SEP + "xmlcatalog2.xml";
		String resultFileName = xmlFileName + "-out";
		String idealResultFileName = xmlFileName + "-result";

		//setup catalog 

		File catalogFile = new File(catalogFileName);
		
		assertTrue("Catalog file " + catalogFileName + " should exist for the test", catalogFile.exists());
	
		catalogResolver.getCatalog().parseCatalog(catalogFileName);
		
		File xslFile = new File(xslFileName);
		
		assertTrue("XSL file " + xslFileName + " should exist for the test", xslFile.exists());		
			
		File inFile = new File(xmlFileName);
		
		assertTrue("XML file " + xslFileName + " should exist for the test", xslFile.exists());		
		
		File outFile = FileUtil.createFileAndParentDirectories(resultFileName);

		xsltLiason.setStylesheet(xslFile);
		xsltLiason.addParam("outprop", "testvalue");
	
		xsltLiason.transform(inFile, outFile);
		
		boolean diffFound =
					FileUtil.textualCompare(
						outFile,
						new File(idealResultFileName),
						new File(resultFileName + "-diff"));
			
	   assertTrue("Output file should match the expected results", !diffFound);

		

	}


}
