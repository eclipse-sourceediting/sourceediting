/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR)  - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

/**
 * The XMLRegionMap class handles mapping duties from an XML Region to
 * a Line Style Constant.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XMLRegionMap {
	
	private static HashMap<String,String> regionMap = new HashMap<String,String>();
	private static XMLRegionMap xmlRegionMap = new XMLRegionMap(); 
	
	private XMLRegionMap() {
		
		regionMap.put(DOMRegionContext.XML_CONTENT, IStyleConstantsXML.XML_CONTENT);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET, IStyleConstantsXML.XML_CONTENT);
		regionMap.put(DOMRegionContext.XML_TAG_OPEN, IStyleConstantsXML.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_TAG_CLOSE, IStyleConstantsXML.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_END_TAG_OPEN, IStyleConstantsXML.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_EMPTY_TAG_CLOSE, IStyleConstantsXML.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_CDATA_OPEN, IStyleConstantsXML.CDATA_BORDER);
		regionMap.put(DOMRegionContext.XML_CDATA_CLOSE, IStyleConstantsXML.CDATA_BORDER);
		regionMap.put(DOMRegionContext.XML_CDATA_TEXT, IStyleConstantsXML.CDATA_TEXT);
		regionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME, IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_DECLARATION, IStyleConstantsXML.TAG_NAME);
		regionMap.put(DOMRegionContext.XML_TAG_NAME, IStyleConstantsXML.TAG_NAME);
		regionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE, IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		regionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS, IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS);
		regionMap.put(DOMRegionContext.XML_COMMENT_OPEN, IStyleConstantsXML.COMMENT_BORDER);
		regionMap.put(DOMRegionContext.XML_COMMENT_CLOSE, IStyleConstantsXML.COMMENT_BORDER);
		regionMap.put(DOMRegionContext.XML_COMMENT_TEXT, IStyleConstantsXML.COMMENT_TEXT);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_NAME, IStyleConstantsXML.DOCTYPE_NAME);
		regionMap.put(DOMRegionContext.XML_CHAR_REFERENCE, IStyleConstantsXML.ENTITY_REFERENCE);
		regionMap.put(DOMRegionContext.XML_PE_REFERENCE, IStyleConstantsXML.ENTITY_REFERENCE);
		regionMap.put(DOMRegionContext.XML_PI_CONTENT, IStyleConstantsXML.PI_CONTENT);
		regionMap.put(DOMRegionContext.XML_PI_OPEN, IStyleConstantsXML.PI_BORDER);
		regionMap.put(DOMRegionContext.XML_PI_CLOSE, IStyleConstantsXML.PI_BORDER);
		regionMap.put(DOMRegionContext.XML_DECLARATION_OPEN, IStyleConstantsXML.DECL_BORDER);
		regionMap.put(DOMRegionContext.XML_DECLARATION_CLOSE, IStyleConstantsXML.DECL_BORDER);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		regionMap.put(DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		regionMap.put(DOMRegionContext.UNDEFINED, IStyleConstantsXML.CDATA_TEXT);
		regionMap.put(DOMRegionContext.WHITE_SPACE, IStyleConstantsXML.XML_CONTENT);
	}
	
	/**
	 * Returns an instance of the XMLRegionMap Class
	 * @return
	 */
	public static XMLRegionMap getInstance() {
		  return xmlRegionMap;
	}
	
	/**
	 * Returns a mapping of XML Regions to Style Constants
	 * @return
	 */
	public Map<String,String> getRegionMap() {
		return regionMap;
	}

}
