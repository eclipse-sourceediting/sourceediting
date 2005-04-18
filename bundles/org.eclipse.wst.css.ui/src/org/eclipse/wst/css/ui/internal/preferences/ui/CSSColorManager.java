/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.preferences.ui;



import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.css.ui.internal.Logger;
import org.eclipse.wst.css.ui.internal.style.IStyleConstantsCSS;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorNames;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated color preference management has moved to base preferences
 */
public class CSSColorManager extends PreferenceManager {

	private static CSSColorManager fInstance = null;

	// names for preference elements ... non-NLS
	public static final String FOREGROUND = "foreground";//$NON-NLS-1$
	public static final String BACKGROUND = "background";//$NON-NLS-1$
	public static final String BOLD = "bold";//$NON-NLS-1$
	public static final String ITALIC = "italic";//$NON-NLS-1$
	public static final String NAME = "name";//$NON-NLS-1$
	public static final String COLOR = "color";//$NON-NLS-1$

	private CSSColorManager() {
		super();
	}

	protected Element addColor(Node colors, String name, String foreground, String background) {
		Element newColor = newColor(colors.getOwnerDocument(), name, foreground, background);
		colors.appendChild(newColor);
		return newColor;
	}

	/**
	 * <!ELEMENT colors (color) > <!ELEMENT color EMPTY > <!ATTLIST color name
	 * CDATA #REQUIRED foreground CDATA #IMPLIED background CDATA #IMPLIED
	 * bold CDATA #REQUIRED >
	 * 
	 */
	public Document createDefaultPreferences() {
		Document prefDocument = super.createDefaultPreferences();
		if (prefDocument == null)
			return prefDocument;

		while (prefDocument.getChildNodes().getLength() > 0)
			prefDocument.removeChild(prefDocument.getLastChild());
		Element colors = prefDocument.createElement(getRootElementName());
		prefDocument.appendChild(colors);

		// current as of 2001-8-13
		addColor(colors, IStyleConstantsCSS.NORMAL, null, null);
		addColor(colors, IStyleConstantsCSS.ATMARK_RULE, getColorString(63, 127, 127), null);
		addColor(colors, IStyleConstantsCSS.SELECTOR, getColorString(63, 127, 127), null);
		addColor(colors, IStyleConstantsCSS.MEDIA, getColorString(42, 0, 225), null);
		addColor(colors, IStyleConstantsCSS.COMMENT, getColorString(63, 95, 191), null);
		addColor(colors, IStyleConstantsCSS.PROPERTY_NAME, getColorString(127, 0, 127), null);
		addColor(colors, IStyleConstantsCSS.PROPERTY_VALUE, getColorString(42, 0, 225), null);
		addColor(colors, IStyleConstantsCSS.URI, getColorString(42, 0, 225), null);
		addColor(colors, IStyleConstantsCSS.STRING, getColorString(42, 0, 225), null);
		addColor(colors, IStyleConstantsCSS.COLON, null, null);
		addColor(colors, IStyleConstantsCSS.SEMI_COLON, null, null);
		addColor(colors, IStyleConstantsCSS.CURLY_BRACE, null, null);
		addColor(colors, IStyleConstantsCSS.ERROR, getColorString(191, 63, 63), null);

		return prefDocument;
	}

	public RGB getBackgroundRGB(String name) {
		Element element = getColorElement(name);
		if (element != null) {
			return getRGB(element.getAttribute(BACKGROUND));
		}
		else {
			return new RGB(255, 255, 255);
		}
	}

	private Element getColorElement(String name) {
		Node colorsElement = getRootElement();
		NodeList colors = colorsElement.getChildNodes();
		for (int i = 0; i < colors.getLength(); i++) {
			Node node = colors.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getAttribute(NAME).equals(name)) {
				return (Element) node;
			}
		}
		return null;
	}

	public static String getColorString(int r, int g, int b) {
		return "#" + getHexString(r, 2) + getHexString(g, 2) + getHexString(b, 2);//$NON-NLS-1$
	}

	public String getFilename() {
		if (fileName == null) {
			fileName = Platform.getStateLocation(Platform.getBundle("org.eclipse.sse.core")).toString() + "/csssourcecolors.xml";//$NON-NLS-1$ //$NON-NLS-2$  
		}
		return fileName;
	}

	public RGB getForegroundRGB(String name) {
		Element element = getColorElement(name);
		if (element != null) {
			return getRGB(element.getAttribute(FOREGROUND));
		}
		else {
			return new RGB(0, 0, 0);
		}
	}

	public static String getHexString(int value, int minWidth) {
		String hexString = Integer.toHexString(value);
		for (int i = hexString.length(); i < minWidth; i++) {
			hexString = "0" + hexString;//$NON-NLS-1$
		}
		return hexString;
	}

	public synchronized static CSSColorManager getInstance() {
		if (fInstance == null) {
			fInstance = new CSSColorManager();
		}
		return fInstance;
	}

	private RGB getRGB(String rgbStr) {
		RGB result = null;
		if (6 < rgbStr.length() && rgbStr.charAt(0) == '#') {
			try {
				int r = Integer.valueOf(rgbStr.substring(1, 3), 16).intValue();
				int g = Integer.valueOf(rgbStr.substring(3, 5), 16).intValue();
				int b = Integer.valueOf(rgbStr.substring(5, 7), 16).intValue();
				result = new RGB(r, g, b);
			}
			catch (NumberFormatException e) {
				Logger.logException("Invalid color string " + rgbStr, e); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * The intended name for the root Element of the Document; what is also
	 * listed within the DOCTYPE declaration.
	 * 
	 * @return String
	 */
	public String getRootElementName() {
		return ColorNames.COLORS;
	}

	public int getStyle(String name) {
		int style = SWT.NORMAL;
		Element element = getColorElement(name);
		if (element != null) {
			if (Boolean.valueOf(element.getAttribute(BOLD)).booleanValue()) {
				style |= SWT.BOLD;
			}
			if (Boolean.valueOf(element.getAttribute(ITALIC)).booleanValue()) {
				style |= SWT.ITALIC;
			}
		}
		return style;
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
}