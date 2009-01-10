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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

/**
 * XMlTextAttributeMap handles the mapping of XML Style Constants to TextAttributes.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XMLTextAttributeMap {
	
	private static HashMap<String,TextAttribute> textAttributeMap = new HashMap<String,TextAttribute>();
	private static XMLTextAttributeMap xmlTextAttributeMap = new XMLTextAttributeMap(); 
	private static IPreferenceStore xmlPreferenceStore;
	
	private static IPreferenceStore getXMLPreferenceStore() {
		return xmlPreferenceStore;
	}
	private XMLTextAttributeMap() {
		xmlPreferenceStore = XMLUIPlugin.getDefault().getPreferenceStore();
		addXMLTextAttribute(IStyleConstantsXML.TAG_NAME);
		addXMLTextAttribute(IStyleConstantsXML.TAG_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS);
		addXMLTextAttribute(IStyleConstantsXML.COMMENT_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.COMMENT_TEXT);
		addXMLTextAttribute(IStyleConstantsXML.CDATA_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.CDATA_TEXT);
		addXMLTextAttribute(IStyleConstantsXML.DECL_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_NAME);
		addXMLTextAttribute(IStyleConstantsXML.PI_CONTENT);
		addXMLTextAttribute(IStyleConstantsXML.PI_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.XML_CONTENT);
		addXMLTextAttribute(IStyleConstantsXML.ENTITY_REFERENCE);
	}
	
	/**
	 * An instance of XMLTextAttributeMap.
	 * @return
	 */
	public static XMLTextAttributeMap getInstance() {
		  return xmlTextAttributeMap;
	}
	
	/**
	 * Returns a Map of Style Constants and the corresponding Text Attribute
	 * @return
	 */
	public Map<String,TextAttribute> getTextAttributeMap() {
		return textAttributeMap;
	}
	
	private void addXMLTextAttribute(String colorKey) {
		if (getXMLPreferenceStore() != null) {
			String prefString = getXMLPreferenceStore().getString(colorKey);
			String[] stylePrefs = ColorHelper.unpackStylePreferences(prefString);
			if (stylePrefs != null) {
				RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
				RGB background = ColorHelper.toRGB(stylePrefs[1]);
				boolean bold = Boolean.valueOf(stylePrefs[2]).booleanValue();
				boolean italic = Boolean.valueOf(stylePrefs[3]).booleanValue();
				boolean strikethrough = Boolean.valueOf(stylePrefs[4]).booleanValue();
				boolean underline = Boolean.valueOf(stylePrefs[5]).booleanValue();
				int style = SWT.NORMAL;
				if (bold) {
					style = style | SWT.BOLD;
				}
				if (italic) {
					style = style | SWT.ITALIC;
				}
				if (strikethrough) {
					style = style | TextAttribute.STRIKETHROUGH;
				}
				if (underline) {
					style = style | TextAttribute.UNDERLINE;
				}

				TextAttribute createTextAttribute = createTextAttribute(foreground, background, style);
				textAttributeMap.put(colorKey, createTextAttribute);
			}
		}
	}

	protected TextAttribute createTextAttribute(RGB foreground, RGB background, int style) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, style);
	}

}
