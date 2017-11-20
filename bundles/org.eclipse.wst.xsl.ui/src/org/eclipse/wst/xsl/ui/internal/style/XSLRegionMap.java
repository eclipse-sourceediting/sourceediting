/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 213775 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * XSLRegionMap handles the mapping of XML Regions to XSL Style Constants.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLRegionMap {
	
	private static HashMap<String,String> regionMap = new HashMap<String,String>();
	private static XSLRegionMap xslRegionMap = new XSLRegionMap(); 
	
	private XSLRegionMap() {
		regionMap.put(DOMRegionContext.XML_TAG_OPEN, IStyleConstantsXSL.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_END_TAG_OPEN, IStyleConstantsXSL.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_TAG_CLOSE, IStyleConstantsXSL.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_EMPTY_TAG_CLOSE, IStyleConstantsXSL.TAG_BORDER);
		regionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME, IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
		regionMap.put(DOMRegionContext.XML_TAG_NAME, IStyleConstantsXSL.TAG_NAME);
		regionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE, IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
	
	}
	
	/**
	 * Returns an instance of XSLRegionMap.
	 * @return
	 */
	public static XSLRegionMap getInstance() {
		  return xslRegionMap;
	}
	
	/**
	 * Returns a MAP of XSL Regions and Style Constants.
	 * @return
	 */
	public Map<String,String> getRegionMap() {
		return regionMap;
	}

}
