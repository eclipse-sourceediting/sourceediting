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
package org.eclipse.wst.css.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @deprecated preference management has moved to base preferences
 */
public class CSSPreferenceManager extends PreferenceManager {

	private static CSSPreferenceManager fInstance = null;
	//
	private final static String GROUP_COLOR = "color";//$NON-NLS-1$
	private final static String COLOR_ENABLED = "useColor";//$NON-NLS-1$
	//
	private final static String GROUP_ASSIST = "contentAssist"; //$NON-NLS-1$
	private final static String ASSIST_CATEGORIZE = "categorize"; //$NON-NLS-1$

	protected Document fallbackDocument = null;

	/**
	 *  
	 */
	protected CSSPreferenceManager() {
		super();
	}

	/**
	 *  
	 */
	public Document createDefaultPreferences() {
		Document doc = super.createDefaultPreferences();
		if (doc == null) {
			return doc;
		}

		Node preference = doc.getFirstChild();

		Element color = doc.createElement(GROUP_COLOR);
		setBooleanAttribute(color, COLOR_ENABLED, true);
		preference.appendChild(color);

		Element contentAssist = doc.createElement(GROUP_ASSIST);
		setBooleanAttribute(contentAssist, ASSIST_CATEGORIZE, true);
		preference.appendChild(contentAssist);

		return doc;
	}

	public boolean getContentAssistCategorize() {
		return getBooleanAttribute(getGroupElement(GROUP_ASSIST), ASSIST_CATEGORIZE);
	}

	public void setContentAssistCategorize(boolean categorize) {
		setBooleanAttribute(getGroupElement(GROUP_ASSIST), ASSIST_CATEGORIZE, categorize);
	}

	/**
	 *  
	 */
	protected boolean getBooleanAttribute(Element element, String name) {
		String str = element.getAttribute(name);
		if (str == null || str.length() <= 0) {
			element = getDefaultGroupElement(element.getTagName());
			if (element != null)
				str = element.getAttribute(name);
		}
		return (str == null) ? false : str.equals(Boolean.TRUE.toString());
	}

	/**
	 *  
	 */
	public boolean getColorEnabled() {
		return getBooleanAttribute(getGroupElement(GROUP_COLOR), COLOR_ENABLED);
	}

	/**
	 *  
	 */
	protected String getFilename() {
		if (fileName == null) {
			fileName = Platform.getPlugin(IModelManagerPlugin.ID).getStateLocation().toString() + "/cssprefs.xml";//$NON-NLS-1$
		}
		return fileName;
	}

	/**
	 *  
	 */
	protected Element getGroupElement(String name) {
		Node node = getNamedChild(getRootElement(), name);
		return (node instanceof Element) ? (Element) node : getDefaultGroupElement(name);
	}

	/**
	 *  
	 */
	protected Element getDefaultGroupElement(String name) {
		Node node = getNamedChild(getDefaultRootElement(), name);
		return (node instanceof Element) ? (Element) node : null;
	}

	/**
	 *  
	 */
	protected Node getDefaultRootElement() {
		if (fallbackDocument == null)
			fallbackDocument = createDefaultPreferences();
		return getRootElement(fallbackDocument);
	}

	/**
	 *  
	 */
	public synchronized static CSSPreferenceManager getInstance() {
		if (fInstance == null) {
			fInstance = new CSSPreferenceManager();
		}
		return fInstance;
	}

	/**
	 *  
	 */
	protected int getIntAttribute(Element element, String name) {
		int value = 0;
		try {
			value = Integer.parseInt(element.getAttribute(name));
		} catch (NumberFormatException e) {
			element = getDefaultGroupElement(element.getTagName());
			try {
				value = Integer.parseInt(element.getAttribute(name));
			} catch (NumberFormatException ee) {
			}
		}
		return value;
	}

	/**
	 *  
	 */
	protected String getStringAttribute(Element element, String name) {
		if (element.getAttributeNode(name) == null) {
			element = getDefaultGroupElement(element.getTagName());
		}
		return element.getAttribute(name);
	}

	/**
	 *  
	 */
	protected void setBooleanAttribute(Element element, String name, boolean value) {
		element.setAttribute(name, new Boolean(value).toString());
	}

	/**
	 *  
	 */
	public void setColorEnabled(boolean enabled) {
		setBooleanAttribute(getGroupElement(GROUP_COLOR), COLOR_ENABLED, enabled);
	}

	/**
	 *  
	 */
	protected void setStringAttribute(Element element, String name, String value) {
		element.setAttribute(name, value);
	}

}