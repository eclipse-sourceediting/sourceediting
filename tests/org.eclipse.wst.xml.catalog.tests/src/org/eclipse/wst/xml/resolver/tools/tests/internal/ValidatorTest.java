package org.eclipse.wst.xml.resolver.tools.tests.internal;

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
	
	

	protected void setUp() throws Exception {
		super.setUp();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public ValidatorTest(String name) {
		super(name);
	}
	
	
	public void testValidatingParser() throws IOException {
		String xmlFile = "/data/Personal/personal-schema.xml";
		xmlFile = TestPlugin.resolvePluginLocation(xmlFile);
		
		String catalogFile = "/data/catalog2.xml";
		catalogFile = TestPlugin.resolvePluginLocation(catalogFile);
				
		System.out.println("---------" + this.getName() + "---------");
		ResolvingXMLParser parser = new ResolvingXMLParser();
		parser.parseAndValidate(xmlFile, new String[]{catalogFile});
		assertFalse(parser.validationError);
	}

}
