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
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * Handles the mapping of the Style Constants to Text Attributes for XSL.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLTextAttributeMap {
	
	private static HashMap<String,TextAttribute> textAttributeMap = new HashMap<String,TextAttribute>();
	private static XSLTextAttributeMap xslTextAttributeMap = new XSLTextAttributeMap(); 
	private static IPreferenceStore xslPreferenceStore;
	
	private static IPreferenceStore getXslPreferenceStore() {
		return xslPreferenceStore;
	}
	private XSLTextAttributeMap() {
		xslPreferenceStore = XSLUIPlugin.getDefault().getPreferenceStore();
		addXSLTextAttribute(IStyleConstantsXSL.TAG_NAME);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_BORDER);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
	}
	
	/**
	 * Returns an instance of XSLTextAttributeMap
	 * @return
	 */
	public static XSLTextAttributeMap getInstance() {
		  return xslTextAttributeMap;
	}
	
	/**
	 * Get's a Map of XSL Style Constants to Text Attributes.
	 * @return
	 */
	public Map<String,TextAttribute> getTextAttributeMap() {
		return textAttributeMap;
	}
	
	protected void addXSLTextAttribute(String colorKey) {
		if (getXslPreferenceStore() != null) {
			String prefString = getXslPreferenceStore().getString(colorKey);
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
