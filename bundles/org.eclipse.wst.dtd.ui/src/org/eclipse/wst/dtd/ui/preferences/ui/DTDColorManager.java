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
package org.eclipse.wst.dtd.ui.preferences.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.dtd.ui.style.dtd.IStyleConstantsDTD;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.preferences.ui.ColorNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DTDColorManager extends PreferenceManager {

	private static DTDColorManager dtdColorManager = null;

	protected DTDColorManager() {
		super();
	}

	protected Element addColor(Node colors, String name, String foreground, String background) {
		Element newColor = newColor(colors.getOwnerDocument(), name, foreground, background);
		colors.appendChild(newColor);
		return newColor;
	}

	/**
	 * <!ELEMENT colors (color)
	 * >
	 * <!ELEMENT color EMPTY
	 * >
	 * <!ATTLIST color
	 *     name       CDATA #REQUIRED 
	 *     foreground CDATA #IMPLIED
	 *     background CDATA #IMPLIED 
	 *     bold CDATA #REQUIRED
	 * >
	 *
	 */
	public Document createDefaultPreferences() {
		Document document = super.createDefaultPreferences();
		if (document == null)
			return document;

		while (document.getChildNodes().getLength() > 0)
			document.removeChild(document.getLastChild());
		Element colors = document.createElement(getRootElementName());
		document.appendChild(colors);
		
		addColor(colors, IStyleConstantsDTD.DTD_DEFAULT, getColorString(0, 0, 0), null);	//black
		addColor(colors, IStyleConstantsDTD.DTD_TAG, getColorString(63, 63, 191), null);	// blue
		addColor(colors, IStyleConstantsDTD.DTD_TAGNAME, getColorString(63, 63, 191), null);	// blue
		addColor(colors, IStyleConstantsDTD.DTD_COMMENT, getColorString(127, 127, 127), null);	// grey
		addColor(colors, IStyleConstantsDTD.DTD_KEYWORD, getColorString(128, 0, 0), null);	// dark red
		addColor(colors, IStyleConstantsDTD.DTD_STRING, getColorString(63, 159, 95), null);	 //green
		addColor(colors, IStyleConstantsDTD.DTD_DATA, getColorString(191, 95, 95), null);	 // light red
		addColor(colors, IStyleConstantsDTD.DTD_SYMBOL, getColorString(128, 0, 0), null);	// dark red
		return document;
	}

	public static String getColorString(int r, int g, int b) {
		return "#" + getHexString(r, 2) + getHexString(g, 2) + getHexString(b, 2);//$NON-NLS-1$
	}

	public String getFilename() {
		if (fileName == null) {
			fileName = Platform.getPlugin(org.eclipse.wst.sse.core.IModelManagerPlugin.ID).getStateLocation().toString() + "/dtdsourcecolors.xml";//$NON-NLS-1$
		}
		return fileName;
	}

	public static String getHexString(int value, int minWidth) {
		String hexString = Integer.toHexString(value);
		for (int i = hexString.length(); i < minWidth; i++) {
			hexString = "0" + hexString;//$NON-NLS-1$
		}
		return hexString;
	}

	/**
	 * The intended name for the root Element of the Document; what is also
	 * listed within the DOCTYPE declaration.
	 * @return String
	 */
	public String getRootElementName() {
		return ColorNames.COLORS;
	}

	public static DTDColorManager getDTDColorManager() {
		if (dtdColorManager == null)
			dtdColorManager = new DTDColorManager();
		return dtdColorManager;
	}

	protected Element newColor(Document doc, String name, String foreground, String background) {
		if (doc == null || name == null || name.length() < 1)
			return null;
		Element newColor = doc.createElement(ColorNames.COLOR);
		newColor.setAttribute(ColorNames.NAME, name);
		if (foreground != null)
			newColor.setAttribute(ColorNames.FOREGROUND, foreground);
		if (background != null)
			newColor.setAttribute(ColorNames.BACKGROUND, background);
		return newColor;
	}

	protected Element newColor(Document doc, String name, String foreground, String background, boolean bold, boolean italic) {
		Element newColor = newColor(doc, name, foreground, background);
		if (newColor == null)
			return null;
		newColor.setAttribute(ColorNames.BOLD, String.valueOf(bold));
		newColor.setAttribute(ColorNames.ITALIC, String.valueOf(italic));
		return newColor;
	}
	
	/**
	 * Return the color element for the given name in the color manager.
	 * @param name preference key to look up
	 * @return color element for the given name, null if it was not found
	 */
	public Element getColorElement(String name) {
		Node colorsElement = getRootElement();
		
		for (Node colorNode = colorsElement.getFirstChild(); colorNode != null; colorNode = colorNode.getNextSibling()) {
			if (colorNode.getNodeType() == Node.ELEMENT_NODE && ((Element) colorNode).getAttribute(ColorHelper.NAME).equals(name)) {
				return (Element) colorNode;
			}
		}
		return null;
	}
}
