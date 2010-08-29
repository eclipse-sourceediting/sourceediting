/*******************************************************************************
 *Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR)  - bug 249716 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.style;

import java.util.Map;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xsl.ui.internal.style.IStyleConstantsXSL;
import org.eclipse.wst.xsl.ui.internal.style.XSLRegionMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import junit.framework.TestCase;

public class TestXSLRegionMap {
	Map<String, String> regionMap = null;
	
	@Before
	public void setUp() throws Exception {
		regionMap = getXSLRegionMap();
	}
	
	@After
	public void tearDown() throws Exception {
		regionMap = null;
	}
	
	private Map<String, String> getXSLRegionMap() {
		XSLRegionMap xslRegionMap = XSLRegionMap.getInstance();
		return xslRegionMap.getRegionMap();
	}
	
	public void testXSLTagOpenContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_TAG_OPEN));
		
	}
	
	@Test
	public void testXSLEndTagOpenContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		
	}
	
	@Test
	public void testXSLTagCloseContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		
	}
	
	@Test
	public void testXSLEmptyTagCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
		
	}
	
	@Test
	public void testXSLTagAttributeNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_ATTRIBUTE_NAME, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
		
	}
	
	@Test
	public void testXSLTagNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_NAME));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_NAME, regionMap.get(DOMRegionContext.XML_TAG_NAME));
		
	}
	
	@Test
	public void testXSLTagAttributeValueRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		
	}
	
}
