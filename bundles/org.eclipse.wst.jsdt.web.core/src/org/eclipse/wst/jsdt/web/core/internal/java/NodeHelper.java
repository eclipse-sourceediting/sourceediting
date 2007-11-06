package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

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
	protected final IStructuredDocumentRegion region;
	
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
		// For debuging
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		String StructuredValue = Messages.getString("NodeHelper.0") + getTagName() + Messages.getString("NodeHelper.1"); //$NON-NLS-1$ //$NON-NLS-2$
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				int start = r.getStart();
				int offset = r.getTextEnd();
				StructuredValue += "\t\t" + region.getText().substring(start, offset); //$NON-NLS-1$
				/*
				 * Theres a XML_TAG_ATTRIBUTE_EQUALS after the
				 * XML_TAG_ATTRIBUTE_NAME we have to get rid of
				 */
				if (regionIterator.hasNext()) {
					regionIterator.next();
				}
				if (regionIterator.hasNext()) {
					r = ((ITextRegion) regionIterator.next());
				}
				System.out.println(Messages.getString("NodeHelper.3")); //$NON-NLS-1$
				if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					int valStart = r.getStart();
					int valOffset = r.getTextEnd();
					StructuredValue += "\t\t" + stripEndQuotes(region.getText().substring(valStart, valOffset)) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
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
				int start = r.getStart();
				int offset = r.getTextEnd();
				String tagname = region.getText().substring(start, offset).trim();
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
				int start = r.getStart();
				int offset = r.getTextEnd();
				String tagname = region.getText().substring(start, offset).trim();
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
						int valStart = r.getStart();
						int valOffset = r.getTextEnd();
						return stripEndQuotes(region.getText().substring(valStart, valOffset));
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
				int start = r.getStart();
				int offset = r.getTextEnd();
				return region.getText().substring(start, offset);
			}
		}
		return null;
	}
	
	public boolean isEndTag() {
		if (region == null) {
			return false;
		}
		ITextRegionList t = region.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_END_TAG_OPEN) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSelfClosingTag() {
		if (region == null) {
			return false;
		}
		if (region == null) {
			return false;
		}
		ITextRegionList regions = region.getRegions();
		ITextRegion r = regions.get(regions.size() - 1);
		return r.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE;
	}
	
	public boolean nameEquals(String name) {
		if (region == null) {
			return false;
		}
		String tagName;
		return ((tagName = getTagName()) != null && tagName.equalsIgnoreCase(name));
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
			nodeText += (Messages.getString("NodeHelper.11") + nodeType + Messages.getString("NodeHelper.12") + region.getText().substring(r.getStart(), r.getTextEnd()) + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return nodeText;
	}
}