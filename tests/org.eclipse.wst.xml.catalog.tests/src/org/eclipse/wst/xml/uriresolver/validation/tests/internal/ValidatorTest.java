package org.eclipse.wst.xml.uriresolver.validation.tests.internal;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.catalog.tests.internal.TestPlugin;

/**
 * To run this test need to add resolver.jar to the classpath.
 * Run as JUnit Plugin test:
 * - put resolver.jar on the boot class path
 * - add VM argument:
 * 
 * -Xbootclasspath/p:<install location>\jre\lib\ext\resolver.jar
 *
 *
 */
public class ValidatorTest extends TestCase {
	
	Validator validator = null;

	protected void setUp() throws Exception {
		super.setUp();
		validator = new Validator();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public ValidatorTest(String name) {
		super(name);
	}
	
	
	public void testValidatingXercesSAXParser1() throws IOException {
		String xmlFile = "/data/Personal/personal-schema.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/catalog2.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesSAXParser(xmlFile, new String[]{catalogFile});
		assertFalse(validator.validationError);
	}
	
	public void testValidatingXercesSAXParser2() throws IOException {
		String xmlFile = "/data/example/example-schema.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/example-catalog.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesSAXParser(xmlFile, new String[]{catalogFile});
		assertFalse(validator.validationError);
	}
	public void testValiatingXercesSAXParser3() throws IOException {
		String xmlFile = "/data/example/example-schema-nonamespace.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/example-catalog.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesSAXParser(xmlFile, new String[]{catalogFile});
		assertFalse(validator.validationError);
	}
	
	public void testValidatingXercesDOMParser1() throws IOException {
		String xmlFile = "/data/example/example-schema-nonamespace.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/example-catalog.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesDOMParser(xmlFile, new String[]{catalogFile});
		assertFalse(validator.validationError);
	}
	
	public void testValidatingJAXPParser1() throws IOException {
		String xmlFile = "/data/example/example-schema.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/example-catalog.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_JAXP(xmlFile, new String[]{catalogFile});	
		assertFalse(validator.validationError);
	}
	
	public void testValidationWithImportedAndIncludedSchema1() throws IOException {
		String xmlFile = "/data/PurchaseOrder/international/report_.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/report-catalog_system.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesDOMParser(xmlFile, new String[]{catalogFile});
		// Included schema will be resolved
		assertFalse(validator.validationError);
	}
	
	public void testValidationWithImportedAndIncludedSchema2() throws IOException {
		String xmlFile = "/data/PurchaseOrder/international/report_.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/report-catalog_public.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesDOMParser(xmlFile, new String[]{catalogFile});
		// Included schema will not be resolved
		assertTrue(validator.validationError);
	}
	
	public void testValidationWithImportedAndIncludedSchema3() throws IOException {
		String xmlFile = "/data/PurchaseOrder/international/report_.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/report-catalog_mappedincluded.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesDOMParser(xmlFile, new String[]{catalogFile});
		// Included schema will not be resolved
		assertTrue(validator.validationError);
	}
	
	public void testValidationWithImportedSchemaNoCatalog() throws IOException {
		String xmlFile = "/data/PurchaseOrder/international/report.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "";
		
		System.out.println("---------" + this.getName() + "---------");
		validator.validateWithSchema_XercesDOMParser(xmlFile, new String[]{catalogFile});
		assertFalse(validator.validationError);
	}
	


}
