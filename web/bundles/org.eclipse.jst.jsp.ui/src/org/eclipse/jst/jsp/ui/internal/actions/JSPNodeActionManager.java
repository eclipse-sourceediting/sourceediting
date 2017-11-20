/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.contentoutline.HTMLNodeActionManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.Node;

public class JSPNodeActionManager extends HTMLNodeActionManager {

	public JSPNodeActionManager(IStructuredModel model, Viewer viewer) {
		super(model, viewer);
	}
	
	protected boolean canContributeChildActions(Node node){
		String nodeName = node.getNodeName().toLowerCase();
		return !(nodeName.equals(JSP11Namespace.ElementName.SCRIPTLET) || nodeName.equals(JSP11Namespace.ElementName.DECLARATION) || nodeName.equals(JSP11Namespace.ElementName.EXPRESSION));
	}
	
	protected void updateCase() {
		if (fModel != null) {
			String modelContentTypeId = fModel.getContentTypeIdentifier();
			if (modelContentTypeId != null) {
				if (modelContentTypeId.equals(ContentTypeIdForJSP.ContentTypeID_JSP)) {
					Preferences prefs = HTMLCorePlugin.getDefault().getPluginPreferences(); //$NON-NLS-1$
					fTagCase = prefs.getInt(HTMLCorePreferenceNames.TAG_NAME_CASE);
					fAttrCase = prefs.getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE);
				}
			}
		}
	}

}
