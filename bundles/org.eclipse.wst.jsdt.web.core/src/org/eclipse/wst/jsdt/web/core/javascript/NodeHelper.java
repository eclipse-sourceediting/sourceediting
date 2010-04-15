/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class NodeHelper {
	protected static final char DOUBLE_QUOTE_CHAR = '\"';
	protected static final String DOUBLE_QUOTE_ENTITY = "&quot;"; //$NON-NLS-1$
	protected static final char SINGLE_QUOTE_CHAR = '\'';
	protected static final String SINGLE_QUOTE_ENTITY = "&#039;"; //$NON-NLS-1$
	
	public static boolean isInArray(String StringArray[], String text) {
		if (StringArray == null || text == null) {
			return false;
		}
		for (int i = 0; i < StringArray.length; i++) {
			if (StringArray[i].equalsIgnoreCase(text.trim())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isQuoted(String string) {
		if ((string == null) || (string.length() < 2)) {
			return false;
		}
		int lastIndex = string.length() - 1;
		char firstChar = string.charAt(0);
		char lastChar = string.charAt(lastIndex);
		return (((firstChar == NodeHelper.SINGLE_QUOTE_CHAR) && (lastChar == NodeHelper.SINGLE_QUOTE_CHAR)) || ((firstChar == NodeHelper.DOUBLE_QUOTE_CHAR) && (lastChar == NodeHelper.DOUBLE_QUOTE_CHAR)));
	}
	protected IStructuredDocumentRegion region;
	
	public NodeHelper(IStructuredDocumentRegion region) {
		this.region = region;
	}
	
	public boolean attrEquals(String attribute, String value) {
		String attValue = getAttributeValue(attribute);
		if(attValue==null) return false;
		return attValue.equalsIgnoreCase(value);
	}
	
	public String AttrToString() {
		if (region == null) {
			return null;
		}
		// For debugging
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		String StructuredValue = Messages.NodeHelper00 + getTagName() + Messages.NodeHelper01; //$NON-NLS-1$ //$NON-NLS-2$
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				StructuredValue += "\t\t" + region.getText(r); //$NON-NLS-1$
				/*
				 * Theres a XML_TAG_ATTRIBUTE_EQUALS after the
				 * XML_TAG_ATTRIBUTE_NAME we have to get rid of
				 */
				if (regionIterator.hasNext()) {
					regionIterator.next();
				}
				if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
					if (regionIterator.hasNext()) {
						r = ((ITextRegion) regionIterator.next());
					}
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						StructuredValue += "\t\t" + stripEndQuotes(region.getText(r)) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		return StructuredValue;
	}
	
	public boolean containsAttribute(String name[]) {
		if (name == null) {
			return false;
		}
		if (region == null) {
			return false;
		}
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				String tagname = region.getText(r).trim();
				/* Attribute values aren't case sensative */
				if (NodeHelper.isInArray(name, tagname)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getAttributeValue(String name) {
		if (region == null) {
			return null;
		}
		if (name == null) {
			return null;
		}
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				String tagname = region.getText(r).trim();
				/*
				 * Attribute values aren't case sensative, also make sure next
				 * region is attrib value
				 */
				if (tagname.equalsIgnoreCase(name)) {
					if (regionIterator.hasNext()) {
						regionIterator.next();
					}
					if (regionIterator.hasNext()) {
						r = ((ITextRegion) regionIterator.next());
					}
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						return stripEndQuotes(region.getText(r));
					}
				}
			}
		}
		return null;
	}
	
	public String getElementAsFlatString() {
		/*
		 * Returns a full string of this element minus and 'illegal' characters
		 * (usefull for identifying the HTML element in a generic JS function)
		 */
		if (region == null) {
			return null;
		}
		String fullRegionText = region.getFullText();
		if (fullRegionText == null) {
			return null;
		}
		return fullRegionText.replaceAll("[^a-zA-Z0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getTagName() {
		if (region == null) {
			return null;
		}
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_NAME) {
				return region.getText(r);
			}
		}
		return null;
	}
	
	public boolean isEndTag() {
		if (region == null) {
			return false;
		}
		return DOMRegionContext.XML_END_TAG_OPEN.equals(region.getFirstRegion().getType());
	}
	
	public boolean isSelfClosingTag() {
		if (region == null) {
			return false;
		}
		return DOMRegionContext.XML_EMPTY_TAG_CLOSE.equals(region.getLastRegion().getType());
	}
	
	public boolean nameEquals(String name) {
		if (region == null || name == null) {
			return false;
		}
		return name.equalsIgnoreCase(getTagName());
	}
	
	public void setDocumentRegion(IStructuredDocumentRegion newRegion) {
		if (newRegion == null)
			throw new IllegalArgumentException();
		region = newRegion;
	}
	
	public String stripEndQuotes(String text) {
		if (text == null) {
			return null;
		}
		if (NodeHelper.isQuoted(text)) {
			return text.substring(1, text.length() - 1);
		}
		return text;
	}
	
	
	public String toString() {
		ITextRegionList t = region.getRegions();
		Iterator regionIterator = t.iterator();
		String nodeText = new String();
		while (regionIterator.hasNext()) {
			ITextRegion r = (ITextRegion) regionIterator.next();
			String nodeType = r.getType();
			nodeText += (Messages.NodeHelper11 + nodeType + Messages.NodeHelper12 + region.getText(r) + "\n"); //$NON-NLS-1$
		}
		return nodeText;
	}
}