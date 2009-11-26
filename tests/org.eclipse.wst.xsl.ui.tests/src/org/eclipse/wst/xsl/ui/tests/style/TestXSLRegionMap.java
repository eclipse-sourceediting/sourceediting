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

import junit.framework.TestCase;

public class TestXSLRegionMap extends TestCase {
	Map<String, String> regionMap = null;
	
	@Override
	protected void setUp() throws Exception {
		regionMap = getXSLRegionMap();
	}
	
	@Override
	protected void tearDown() throws Exception {
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
	
	public void testXSLEndTagOpenContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		
	}
	
	public void testXSLTagCloseContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		
	}
	
	public void testXSLEmptyTagCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_BORDER, regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
		
	}
	
	public void testXSLTagAttributeNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_ATTRIBUTE_NAME, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
		
	}
	
	public void testXSLTagNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_NAME));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_NAME, regionMap.get(DOMRegionContext.XML_TAG_NAME));
		
	}
	
	public void testXSLTagAttributeValueRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		assertEquals("Incorrect value", IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		
	}
	
}
