/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.style;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.core.preferences.PreferenceChangeListener;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.preferences.ui.XMLColorManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class LineStyleProviderForXML extends AbstractLineStyleProvider implements LineStyleProvider, PreferenceChangeListener {
	public LineStyleProviderForXML() {
		super();
		loadColors();
		//XMLColorPreferenceManager.getXMLColorPreferenceManager().addPreferenceChangeListener(this);
	}

	protected void clearColors() {
		getTextAttributes().clear();
	}
	
	protected TextAttribute getAttributeFor(ITextRegion region) {
		/**
		 * a method to centralize all the "format rules" for regions 
		 * specifically associated for how to "open" the region.
		 */
		// not sure why this is coming through null, but just to catch it
		if (region == null) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		String type = region.getType();
		if ((type == XMLRegionContext.XML_CONTENT) || (type == XMLRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		else if ((type == XMLRegionContext.XML_TAG_OPEN) || (type == XMLRegionContext.XML_END_TAG_OPEN) || (type == XMLRegionContext.XML_TAG_CLOSE) || (type == XMLRegionContext.XML_EMPTY_TAG_CLOSE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_BORDER);
		}
		else if ((type == XMLRegionContext.XML_CDATA_OPEN) || (type == XMLRegionContext.XML_CDATA_CLOSE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.CDATA_BORDER);
		}
		else if (type == XMLRegionContext.XML_CDATA_TEXT) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		else if (type == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		}
		else if (type == XMLRegionContext.XML_DOCTYPE_DECLARATION) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_NAME);
		}
		else if (type == XMLRegionContext.XML_TAG_NAME) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_NAME);
		}
		else if ((type == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		}
		else if (type == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS);
		}
		else if ((type == XMLRegionContext.XML_COMMENT_OPEN) || (type == XMLRegionContext.XML_COMMENT_CLOSE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.COMMENT_BORDER);
		}
		else if (type == XMLRegionContext.XML_COMMENT_TEXT) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.COMMENT_TEXT);
		}
		else if (type == XMLRegionContext.XML_DOCTYPE_NAME) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.DOCTYPE_NAME);
		}
		else if (type == XMLRegionContext.XML_PI_CONTENT) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.PI_CONTENT);
		}
		else if ((type == XMLRegionContext.XML_PI_OPEN) || (type == XMLRegionContext.XML_PI_CLOSE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.PI_BORDER);
		}
		else if ((type == XMLRegionContext.XML_DECLARATION_OPEN) || (type == XMLRegionContext.XML_DECLARATION_CLOSE)) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.DECL_BORDER);
		}
		else if (type == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
		}
		else if (type == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		}
		else if (type == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC || type == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		}
		else if (type == XMLRegionContext.UNDEFINED) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		else if (type == XMLRegionContext.WHITE_SPACE) {
			// white space is normall not on its own ... but when it is, we'll treat as content
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		else if ((type == XMLRegionContext.XML_CHAR_REFERENCE) || (type == XMLRegionContext.XML_ENTITY_REFERENCE) || (type == XMLRegionContext.XML_PE_REFERENCE)) {
			// we may want to character and entity references to have it own color in future,
			// but for now, we'll make attribute value
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		}
		else {
			// default, return null to signal "not handled"
			// in which case, other factories should be tried
			return null;
		}
	}

	/**
	 * @deprecated - obsolete
	 */
	protected StyleRange getCachedStyleRange(ITextRegion region) {
		return null;
	}

	protected PreferenceManager getColorManager() {
		return XMLColorManager.getXMLColorManager();
	}

	protected void loadColors() {
		PreferenceManager mgr = getColorManager();
		Node colors = mgr.getRootElement();
		if (colors == null)
			return;
		clearColors();
		//----------------------------------------------------------------------
		// (pa) 20021217
		// cmvc defect 235554
		// performance enhancement: using child.getNextSibling() rather than
		// nodeList(item) for O(n) vs. O(n*n)
		//----------------------------------------------------------------------
		for (Node colorNode = colors.getFirstChild(); colorNode != null; colorNode = colorNode.getNextSibling()) {
			//		NodeList colorList = colors.getChildNodes();
			//		for (int i = 0; i < colorList.getLength(); i++) {
			//			Node colorNode = colorList.item(i);
			if (colorNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element color = (Element) colorNode;
			String colorName = color.getAttribute(ColorHelper.NAME);
			if (colorName == null || colorName.length() < 1)
				continue;
			RGB foreground = ColorHelper.toRGB(color.getAttribute(ColorHelper.FOREGROUND));
			RGB background = ColorHelper.toRGB(color.getAttribute(ColorHelper.BACKGROUND));
			boolean bold = Boolean.valueOf(color.getAttribute(ColorHelper.BOLD)).booleanValue();
			if (colorName.equals(IStyleConstantsXML.TAG_NAME)) {
				getTextAttributes().put(IStyleConstantsXML.TAG_NAME, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.TAG_BORDER)) {
				getTextAttributes().put(IStyleConstantsXML.TAG_BORDER, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.TAG_ATTRIBUTE_NAME)) {
				getTextAttributes().put(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE)) {
				getTextAttributes().put(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS)) {
				getTextAttributes().put(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.COMMENT_BORDER)) {
				getTextAttributes().put(IStyleConstantsXML.COMMENT_BORDER, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.COMMENT_TEXT)) {
				getTextAttributes().put(IStyleConstantsXML.COMMENT_TEXT, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.CDATA_BORDER)) {
				getTextAttributes().put(IStyleConstantsXML.CDATA_BORDER, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.CDATA_TEXT)) {
				getTextAttributes().put(IStyleConstantsXML.CDATA_TEXT, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.DECL_BORDER)) {
				getTextAttributes().put(IStyleConstantsXML.DECL_BORDER, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID)) {
				getTextAttributes().put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF)) {
				getTextAttributes().put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF)) {
				getTextAttributes().put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.DOCTYPE_NAME)) {
				getTextAttributes().put(IStyleConstantsXML.DOCTYPE_NAME, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.PI_CONTENT)) {
				getTextAttributes().put(IStyleConstantsXML.PI_CONTENT, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.PI_BORDER)) {
				getTextAttributes().put(IStyleConstantsXML.PI_BORDER, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsXML.XML_CONTENT)) {
				getTextAttributes().put(IStyleConstantsXML.XML_CONTENT, createTextAttribute(foreground, background, bold));
			}
		}
	}

	/**
	 * preferencesChanged method comment.
	 */
	public void preferencesChanged() {
		loadColors();
		super.preferencesChanged();
	}

	/**
	 * @deprecated - obsolete
	 */
	protected void setCachedStyleRange(ITextRegion region, StyleRange range) {
	}
}
