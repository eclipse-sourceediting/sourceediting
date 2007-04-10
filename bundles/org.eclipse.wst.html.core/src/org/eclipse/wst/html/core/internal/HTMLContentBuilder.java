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
package org.eclipse.wst.html.core.internal;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMContentBuilderImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class HTMLContentBuilder extends DOMContentBuilderImpl {

	private int fTagCase;
	private int fAttrCase;

	/**
	 * DOMContentBuilder constructor comment.
	 * @param document org.w3c.dom.Document
	 */
	public HTMLContentBuilder(Document document) {
		super(document);
		Preferences prefs = HTMLCorePlugin.getDefault().getPluginPreferences();
		fTagCase = prefs.getInt(HTMLCorePreferenceNames.TAG_NAME_CASE);
		fAttrCase = prefs.getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE);
		//	Element caseSettings = HTMLPreferenceManager.getHTMLInstance().getElement(PreferenceNames.PREFERRED_CASE);
		//	fTagCase = caseSettings.getAttribute(PreferenceNames.TAGNAME);
		//	fAttrCase = caseSettings.getAttribute(PreferenceNames.ATTRIBUTENAME);
	}

	public String computeName(CMNode cmnode, Node parent) {
		String name = super.computeName(cmnode, parent);
		// don't change the case unless we're certain it is meaningless
		//	if (cmnode instanceof HTMLCMNode && ((HTMLCMNode) cmnode).shouldIgnoreCase()) {
		if (shouldIgnoreCase(cmnode)) {
			if (cmnode.getNodeType() == CMNode.ELEMENT_DECLARATION) {
				if (fTagCase == HTMLCorePreferenceNames.LOWER)
					name = name.toLowerCase();
				else if (fTagCase == HTMLCorePreferenceNames.UPPER)
					name = name.toUpperCase();
				// else do nothing
			}
			else if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) {
				if (fAttrCase == HTMLCorePreferenceNames.LOWER)
					name = name.toLowerCase();
				else if (fAttrCase == HTMLCorePreferenceNames.UPPER)
					name = name.toUpperCase();
				// else do nothing
			}
		}
		return name;

	}

	private boolean shouldIgnoreCase(CMNode cmnode) {
		if (!cmnode.supports(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return false;
		return ((Boolean) cmnode.getProperty(HTMLCMProperties.SHOULD_IGNORE_CASE)).booleanValue();
	}
}
