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
package org.eclipse.wst.xml.ui.preferences.ui;



import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.ColorNames;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class XMLColorManager extends PreferenceManager {

	private static XMLColorManager xmlColorManager = null;
	// highlighting types
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String COMMENT_BORDER = "commentBorder";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String COMMENT_TEXT = "commentText";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String CDATA_BORDER = "cdataBorder";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String CDATA_TEXT = "cdataText";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String PI_BORDER = "piBorder";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String PI_CONTENT = "piContent";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String TAG_BORDER = "tagBorder";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String TAG_NAME = "tagName";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String TAG_ATTRIBUTE_NAME = "tagAttributeName";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String TAG_ATTRIBUTE_VALUE = "tagAttributeValue";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String DECL_BORDER = "declBoder";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String DOCTYPE_NAME = "doctypeName";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String DOCTYPE_EXTERNAL_ID = "doctypeExternalId";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String DOCTYPE_EXTERNAL_ID_PUBREF = "doctypeExternalPubref";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String DOCTYPE_EXTERNAL_ID_SYSREF = "doctypeExtrenalSysref";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String XML_CONTENT = "xmlContent";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */	
	public static final String SCRIPT_AREA_BORDER = "SCRIPT_AREA_BORDER";//$NON-NLS-1$
	/** @deprecated use IStyleConstantsXML instead TODO remove in C5 or earlier */
	public static final String SCRIPT_AREA = "SCRIPT_AREA";//$NON-NLS-1$

	protected XMLColorManager() {
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

		if (org.eclipse.wst.sse.core.util.Debug.displayInfo)
			System.out.println(getClass().getName() + " creating default preferences"); //$NON-NLS-1$
		Document document = super.createDefaultPreferences();
		if (document == null)
			return document;

		while (document.getChildNodes().getLength() > 0)
			document.removeChild(document.getLastChild());
		Element colors = document.createElement(getRootElementName());
		document.appendChild(colors);

		// current as of 2001-8-13
		addColor(colors, IStyleConstantsXML.TAG_ATTRIBUTE_NAME, getColorString(127, 0, 127), null);
		addColor(colors, IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, getColorString(42, 0, 255), null);
		addColor(colors, IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, null, null); // specified value is black; leaving as widget default

		addColor(colors, IStyleConstantsXML.COMMENT_BORDER, getColorString(63, 95, 191), null);
		addColor(colors, IStyleConstantsXML.COMMENT_TEXT, getColorString(63, 95, 191), null);

		addColor(colors, IStyleConstantsXML.DECL_BORDER, getColorString(0, 128, 128), null);
		addColor(colors, IStyleConstantsXML.DOCTYPE_NAME, getColorString(0, 0, 128), null);
		addColor(colors, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, getColorString(128, 128, 128), null);
		addColor(colors, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, getColorString(0, 0, 128), null);
		addColor(colors, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, getColorString(63, 127, 95), null);

		addColor(colors, IStyleConstantsXML.XML_CONTENT, null, null); // specified value is black; leaving as widget default

		addColor(colors, IStyleConstantsXML.TAG_BORDER, getColorString(0, 128, 128), null);
		addColor(colors, IStyleConstantsXML.TAG_NAME, getColorString(63, 127, 127), null);

		addColor(colors, IStyleConstantsXML.PI_BORDER, getColorString(0, 128, 128), null);
		addColor(colors, IStyleConstantsXML.PI_CONTENT, null, null); // specified value is black; leaving as widget default

		addColor(colors, IStyleConstantsXML.CDATA_BORDER, getColorString(0, 128, 128), null);
		addColor(colors, IStyleConstantsXML.CDATA_TEXT, getColorString(0, 0, 0), null);

		return document;
	}

	public static String getColorString(int r, int g, int b) {
		return "#" + getHexString(r, 2) + getHexString(g, 2) + getHexString(b, 2);//$NON-NLS-1$
	}

	public String getFilename() {
		if (fileName == null) {
			fileName = Platform.getPlugin(org.eclipse.wst.sse.core.IModelManagerPlugin.ID).getStateLocation().toString() + "/xmlsourcecolors.xml";//$NON-NLS-1$
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

	public static XMLColorManager getXMLColorManager() {
		if (xmlColorManager == null)
			xmlColorManager = new XMLColorManager();
		return xmlColorManager;
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
