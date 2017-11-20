/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 */
class HTMLDocumentTypeRegistryReader {

	//
	private final static String PLUGIN_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	private final static String EXTENSION_POINT_ID = "documentTypes"; //$NON-NLS-1$
	private final static String TAG_NAME = "documentType"; //$NON-NLS-1$
	private final static String ATT_PID = "publicID"; //$NON-NLS-1$
	private final static String ATT_SID = "systemID"; //$NON-NLS-1$
	private final static String ATT_IS_XHTML = "isXHTML"; //$NON-NLS-1$
	private final static String ATT_IS_WML = "isWML"; //$NON-NLS-1$
	private final static String ATT_HAS_FRAMESET = "hasFrameset"; //$NON-NLS-1$
	private final static String ATT_NSURI = "namespaceURI"; //$NON-NLS-1$
	private final static String ATT_ENAME = "elementName"; //$NON-NLS-1$
	private final static String ATT_DNAME = "displayName"; //$NON-NLS-1$
	private final static String ATT_IS_DEFAULT_XHTML = "defaultXHTML"; //$NON-NLS-1$
	private final static String ATT_IS_DEFAULT_WML = "defaultWML"; //$NON-NLS-1$
	private final static String ATV_TRUE = "true"; //$NON-NLS-1$
	private final static String ATV_NULL_STRING = ""; //$NON-NLS-1$

	/**
	 */
	HTMLDocumentTypeRegistryReader() {
		super();
	}

	/**
	 */
	void readRegistry(HTMLDocumentTypeRegistry reg) {
		if (reg == null)
			return;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				HTMLDocumentTypeEntry doctype = readElement(elements[i]);
				// null can be returned if there's an error reading the element
				if (doctype != null) {
					reg.regist(doctype.getPublicId(), doctype);
				}
			}
		}
	}

	/**
	 */
	private HTMLDocumentTypeEntry readElement(IConfigurationElement element) {
		HTMLDocumentTypeEntry doctype = null;
		String pid = null;
		String sid = null;
		String nsuri = null;
		String root = null;
		boolean xhtml = true;
		boolean frameset = false;
		String dname = null;
		boolean defaultXhtml = false;
		boolean defaultWML = false;
		boolean isWML = false;

		if (element.getName().equals(TAG_NAME)) {
			pid = element.getAttribute(ATT_PID);
			// publicID attribute is mandatory.
			if (pid == null || pid.equals(ATV_NULL_STRING))
				return null;

			sid = element.getAttribute(ATT_SID);
			nsuri = element.getAttribute(ATT_NSURI);
			root = element.getAttribute(ATT_ENAME);
			xhtml = getBoolean(element, ATT_IS_XHTML);
			frameset = getBoolean(element, ATT_HAS_FRAMESET);
			dname = element.getAttribute(ATT_DNAME);
			defaultXhtml = getBoolean(element, ATT_IS_DEFAULT_XHTML);
			defaultWML = getBoolean(element, ATT_IS_DEFAULT_WML);
			isWML = getBoolean(element, ATT_IS_WML);
			doctype = new HTMLDocumentTypeEntry(root, pid, sid, nsuri, xhtml, frameset, dname, defaultXhtml, defaultWML, isWML);
		}
		return doctype;
	}

	/**
	 */
	private boolean getBoolean(IConfigurationElement element, String att) {
		String value = element.getAttribute(att);
		if (value != null && value.equals(ATV_TRUE))
			return true;
		return false;
	}
}
