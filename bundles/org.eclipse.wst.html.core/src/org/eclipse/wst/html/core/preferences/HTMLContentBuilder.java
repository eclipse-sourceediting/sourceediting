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
package org.eclipse.wst.html.core.preferences;



import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.common.contentmodel.CMNode;
import org.eclipse.wst.html.core.HTMLCMProperties;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class HTMLContentBuilder extends org.eclipse.wst.common.contentmodel.util.DOMContentBuilderImpl {

	private int fTagCase;
	private int fAttrCase;

	/**
	 * DOMContentBuilder constructor comment.
	 * @param document org.w3c.dom.Document
	 */
	public HTMLContentBuilder(Document document) {
		super(document);
		Preferences prefs = Platform.getPlugin("org.eclipse.wst.html.core").getPluginPreferences();//$NON-NLS-1$
		fTagCase = prefs.getInt(CommonModelPreferenceNames.TAG_NAME_CASE);
		fAttrCase = prefs.getInt(CommonModelPreferenceNames.ATTR_NAME_CASE);
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
				if (fTagCase == CommonModelPreferenceNames.LOWER)
					name = name.toLowerCase();
				else if (fTagCase == CommonModelPreferenceNames.UPPER)
					name = name.toUpperCase();
				// else do nothing
			}
			else if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) {
				if (fAttrCase == CommonModelPreferenceNames.LOWER)
					name = name.toLowerCase();
				else if (fAttrCase == CommonModelPreferenceNames.UPPER)
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