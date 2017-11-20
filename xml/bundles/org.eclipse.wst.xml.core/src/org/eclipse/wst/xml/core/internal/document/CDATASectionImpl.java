/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;


/**
 * CDATASectionImpl class
 */
public class CDATASectionImpl extends TextImpl implements CDATASection {

	/**
	 * CDATASectionImpl constructor
	 */
	protected CDATASectionImpl() {
		super();
	}

	/**
	 * CDATASectionImpl constructor
	 * 
	 * @param that
	 *            CDATASectionImpl
	 */
	protected CDATASectionImpl(CDATASectionImpl that) {
		super(that);
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		CDATASectionImpl cloned = new CDATASectionImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 * getData method
	 * 
	 * @return java.lang.String
	 */
	public String getData() throws DOMException {
		// instead of super(TextImpl).getData(), call getCharacterData()
		char[] data = getCharacterData();
		if (data == null) {
			String sdata = getData(getStructuredDocumentRegion());
			if (sdata != null)
				return sdata;
			return NodeImpl.EMPTY_STRING;
		}
		return new String(data);
	}

	/**
	 */
	private String getData(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return null;
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return null;

		ITextRegion contentRegion = null;
		StringBuffer buffer = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_CDATA_OPEN || regionType == DOMRegionContext.XML_CDATA_CLOSE) {
				continue;
			}
			if (contentRegion == null) { // first content
				contentRegion = region;
			} else { // multiple contents
				if (buffer == null) {
					buffer = new StringBuffer(flatNode.getText(contentRegion));
				}
				buffer.append(flatNode.getText(region));
			}
		}

		if (buffer != null)
			return buffer.toString();
		if (contentRegion != null)
			return flatNode.getText(contentRegion);
		return null;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#cdata-section";//$NON-NLS-1$
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return CDATA_SECTION_NODE;
	}

	/**
	 */
	public boolean isClosed() {
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return true; // will be generated
		String regionType = StructuredDocumentRegionUtil.getLastRegionType(flatNode);
		return (regionType == DOMRegionContext.XML_CDATA_CLOSE);
	}
}
