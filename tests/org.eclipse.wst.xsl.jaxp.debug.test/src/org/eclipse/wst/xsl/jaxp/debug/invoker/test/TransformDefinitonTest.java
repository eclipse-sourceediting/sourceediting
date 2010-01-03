package org.eclipse.wst.xsl.jaxp.debug.invoker.test;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.xalan.templates.OutputProperties;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TypedValue;

import junit.framework.TestCase;

public class TransformDefinitonTest extends TestCase {
	TransformDefinition tdef = null;
	
	@Override
	protected void setUp() throws Exception {
		tdef = new TransformDefinition();
	}
	
	public void testDefaultResolver() throws Exception {
		String resolver = tdef.getResolverClass();
		assertEquals("Did not find default resolver", TransformDefinition.DEFAULT_CATALOG_RESOLVER, resolver);
	}
	
	public void testChangeResolverFromDefault() throws Exception {
		String resolver = "org.eclipse.wst.xml.catalog.URIResolver";
		tdef.setResolverClass(resolver);
		assertEquals("Problem setting a new resolver", resolver, tdef.getResolverClass());
	}
	
	public void testAddParameters() throws Exception {
		setupParameters();
		assertNotNull("Missing paramerters", tdef.getParameters());
	}
	
	public void testGetParameters() throws Exception {
		setupParameters();
		Set<TypedValue> parmSet = tdef.getParameters();
		assertEquals("Incorrect size returned.", 1, parmSet.size());
	}
	
	public void testGetParametersAsMap() throws Exception {
		setupParameters();
		Map<String, Object> parmMap = tdef.getParametersAsMap();
		assertNotNull("Missing parmater MAP.", parmMap);
	}
	
	public void testStyleSheetSource() throws Exception {
		String stylesheet = "http://www.example.org/stylesheet.xsl";
		tdef.setStylesheetURL(stylesheet);
		assertNotNull("Missing stylesheet.", tdef.getStylesheetURL());
		assertEquals("Incorrect stylesheet.", stylesheet, tdef.getStylesheetURL());
	}
	
	public void testRemoveParameter() throws Exception {
		TypedValue param = setupParameters();
		assertTrue(tdef.getParameters().size() > 0);
		tdef.removeParameter(param);
		assertTrue("Found parameters when there should be zero.", tdef.getParameters().size() == 0);
	}
	
	public void testSetOutputProperty() throws Exception {
		tdef.setOutputProperty("output", "test");
		String value = tdef.getOutputProperties().getProperty("output");
		assertEquals("Incorrect value", "test", value);
	}
	
	public void testNoOutputProperties() throws Exception {
		assertEquals("Found output properties when there should be none.", 0, tdef.getOutputProperties().size());
	}

	private TypedValue setupParameters() {
		TypedValue param = new TypedValue("param", TypedValue.TYPE_STRING, "test");
		tdef.addParameter(param);
		return param;
	}
	
}
