/*******************************************************************************
 * Copyright (c) 2010 Intalio Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker.test;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TypedValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TransformDefinitonTest  {
	TransformDefinition tdef = null;
	
	@Before
	public void setUp() throws Exception {
		tdef = new TransformDefinition();
	}
	
	@After
	public void tearDown() throws Exception {
		tdef = null;
	}
	
	private TypedValue setupParameters() {
		TypedValue param = new TypedValue("param", TypedValue.TYPE_STRING, "test");
		tdef.addParameter(param);
		return param;
	}
	
	@Test
	public void testDefaultResolver() throws Exception {
		String resolver = tdef.getResolverClass();
		assertEquals("Did not find default resolver", TransformDefinition.DEFAULT_CATALOG_RESOLVER, resolver);
	}
	
	@Test
	public void testChangeResolverFromDefault() throws Exception {
		String resolver = "org.eclipse.wst.xml.catalog.URIResolver";
		tdef.setResolverClass(resolver);
		assertEquals("Problem setting a new resolver", resolver, tdef.getResolverClass());
	}
	
	@Test
	public void testAddParameters() throws Exception {
		setupParameters();
		assertNotNull("Missing paramerters", tdef.getParameters());
	}
	
	@Test
	public void testGetParameters() throws Exception {
		setupParameters();
		Set<TypedValue> parmSet = tdef.getParameters();
		assertEquals("Incorrect size returned.", 1, parmSet.size());
	}
	
	@Test
	public void testGetParametersAsMap() throws Exception {
		setupParameters();
		Map<String, Object> parmMap = tdef.getParametersAsMap();
		assertNotNull("Missing parmater MAP.", parmMap);
	}
	
	@Test
	public void testStyleSheetSource() throws Exception {
		String stylesheet = "http://www.example.org/stylesheet.xsl";
		tdef.setStylesheetURL(stylesheet);
		assertNotNull("Missing stylesheet.", tdef.getStylesheetURL());
		assertEquals("Incorrect stylesheet.", stylesheet, tdef.getStylesheetURL());
	}
	
	@Test
	public void testRemoveParameter() throws Exception {
		TypedValue param = setupParameters();
		assertTrue(tdef.getParameters().size() > 0);
		tdef.removeParameter(param);
		assertTrue("Found parameters when there should be zero.", tdef.getParameters().size() == 0);
	}
	
	@Test
	public void testSetOutputProperty() throws Exception {
		tdef.setOutputProperty("output", "test");
		String value = tdef.getOutputProperties().getProperty("output");
		assertEquals("Incorrect value", "test", value);
	}
	
	@Test
	public void testNoOutputProperties() throws Exception {
		assertEquals("Found output properties when there should be none.", 0, tdef.getOutputProperties().size());
	}
	
	@Test
	public void testRemoveOutputProperty() throws Exception {
		tdef.setOutputProperty("output", "test");
		Properties properties = tdef.getOutputProperties();
		assertEquals(1,properties.size());
		tdef.removeOutputProperty("output");
	}

	
}
