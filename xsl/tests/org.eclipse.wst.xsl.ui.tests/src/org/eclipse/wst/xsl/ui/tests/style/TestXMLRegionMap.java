/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
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
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;
import org.eclipse.wst.xsl.ui.internal.style.XMLRegionMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXMLRegionMap {
	private Map<String, String> regionMap = null;

	@Before
	public void setUp() throws Exception {
		regionMap = getRegionMap();
	}
	
	@After
	public void tearDown() throws Exception {
		regionMap = null;
	}
	
	@Test
	public void testXMLContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_CONTENT));
		assertEquals("Incorrect value", IStyleConstantsXML.XML_CONTENT, regionMap.get(DOMRegionContext.XML_CONTENT));
		
	}
	
	@Test
	public void testXMLDocTypeInternalSubsettRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET));
		assertEquals("Incorrect value", IStyleConstantsXML.XML_CONTENT, regionMap.get(DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET));
		
	}
	
	@Test
	public void testXMLTagOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_BORDER, regionMap.get(DOMRegionContext.XML_TAG_OPEN));
		
	}

	@Test
	public void testXMLTagCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_BORDER, regionMap.get(DOMRegionContext.XML_TAG_CLOSE));
		
	}

	@Test
	public void testXMLEndTagOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_BORDER, regionMap.get(DOMRegionContext.XML_END_TAG_OPEN));
		
	}

	@Test
	public void testXMLCDataOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_CDATA_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.CDATA_BORDER, regionMap.get(DOMRegionContext.XML_CDATA_OPEN));
	}

	@Test
	public void testXMLCDataCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_CDATA_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.CDATA_BORDER, regionMap.get(DOMRegionContext.XML_CDATA_CLOSE));
	}

	@Test
	public void testXMLCDataTextRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_CDATA_TEXT));
		assertEquals("Incorrect value", IStyleConstantsXML.CDATA_TEXT, regionMap.get(DOMRegionContext.XML_CDATA_TEXT));
	}
	
	@Test
	public void testXMLTagAttributeNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_ATTRIBUTE_NAME, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME));
	}
	
	@Test
	public void testXMLDocTypeDeclartionRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_DECLARATION));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_NAME, regionMap.get(DOMRegionContext.XML_DOCTYPE_DECLARATION));
	}
	
	@Test
	public void testXMLTagNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_NAME));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_NAME, regionMap.get(DOMRegionContext.XML_TAG_NAME));
		
	}
	
	@Test
	public void testXMLTagAttributeValueRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
	}
	
	@Test
	public void testXMLTagAttributeEqualsRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, regionMap.get(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS));
	}
	
	@Test
	public void testXMLCommentOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_COMMENT_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.COMMENT_BORDER, regionMap.get(DOMRegionContext.XML_COMMENT_OPEN));
	}
	
	@Test
	public void testXMLCommentCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_COMMENT_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.COMMENT_BORDER, regionMap.get(DOMRegionContext.XML_COMMENT_CLOSE));
	}
	
	@Test
	public void testXMLCommentTextRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_COMMENT_TEXT));
		assertEquals("Incorrect value", IStyleConstantsXML.COMMENT_TEXT, regionMap.get(DOMRegionContext.XML_COMMENT_TEXT));
	}
	
	@Test
	public void testXMLDocTypeNameRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_NAME));
		assertEquals("Incorrect value", IStyleConstantsXML.DOCTYPE_NAME, regionMap.get(DOMRegionContext.XML_DOCTYPE_NAME));
	}
	
	@Test
	public void testXMLCharReferenceRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_CHAR_REFERENCE));
		assertEquals("Incorrect value", IStyleConstantsXML.ENTITY_REFERENCE, regionMap.get(DOMRegionContext.XML_CHAR_REFERENCE));
	}
	
	@Test
	public void testXMLPeReferenceRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_PE_REFERENCE));
		assertEquals("Incorrect value", IStyleConstantsXML.ENTITY_REFERENCE, regionMap.get(DOMRegionContext.XML_PE_REFERENCE));
	}
	
	@Test
	public void testXMLPIContentRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_PI_CONTENT));
		assertEquals("Incorrect value", IStyleConstantsXML.PI_CONTENT, regionMap.get(DOMRegionContext.XML_PI_CONTENT));
	}
	
	@Test
	public void testXMLPIOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_PI_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.PI_BORDER, regionMap.get(DOMRegionContext.XML_PI_OPEN));
	}
	
	@Test
	public void testXMLPICloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_PI_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.PI_BORDER, regionMap.get(DOMRegionContext.XML_PI_CLOSE));
	}
	
	@Test
	public void testXMLDeclartionOpenRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DECLARATION_OPEN));
		assertEquals("Incorrect value", IStyleConstantsXML.DECL_BORDER, regionMap.get(DOMRegionContext.XML_DECLARATION_OPEN));
	}
	
	@Test
	public void testXMLDeclartionCloseRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DECLARATION_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.DECL_BORDER, regionMap.get(DOMRegionContext.XML_DECLARATION_CLOSE));
	}
	
	@Test
	public void testXMLDocTypeExternalIdSysrefRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF));
		assertEquals("Incorrect value", IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF));
	}
	
	@Test
	public void testXMLDocTypeExternalIdPubrefRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF));
		assertEquals("Incorrect value", IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF));
	}
	
	@Test
	public void testXMLDocTypeExternalIdPublicRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC));
		assertEquals("Incorrect value", IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC));
	}
	
	@Test
	public void testXMLDocTypeExternalIdSystemRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM));
		assertEquals("Incorrect value", IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, regionMap.get(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM));
	}
	
	@Test
	public void testUndefinedRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.UNDEFINED));
		assertEquals("Incorrect value", IStyleConstantsXML.CDATA_TEXT, regionMap.get(DOMRegionContext.UNDEFINED));
	}
	
	@Test
	public void testWhiteSpaceRegion() throws Exception {
		assertNotNull("Region Not Found", regionMap.get(DOMRegionContext.WHITE_SPACE));
		assertEquals("Incorrect value", IStyleConstantsXML.XML_CONTENT, regionMap.get(DOMRegionContext.WHITE_SPACE));
	}
	
	@Test
	public void testXMLEmptyTagClose() throws Exception {
		assertNotNull("Region Not Found.", regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
		assertEquals("Incorrect value", IStyleConstantsXML.TAG_BORDER, regionMap.get(DOMRegionContext.XML_EMPTY_TAG_CLOSE));
	}

	private Map<String, String> getRegionMap() {
		XMLRegionMap xmlRegionMap = XMLRegionMap.getInstance();
		Map<String, String> regionMap = xmlRegionMap.getRegionMap();
		return regionMap;
	}

}
