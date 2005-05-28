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
package org.eclipse.wst.html.ui.internal.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.contentoutline.HTMLNodeActionManager;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.xml.ui.internal.views.contentoutline.XMLContentOutlineConfiguration;

public class HTMLContentOutlineConfiguration extends XMLContentOutlineConfiguration {

	/**
	 * @param editor
	 */
	public HTMLContentOutlineConfiguration() {
		super();
	}

	protected XMLNodeActionManager createNodeActionManager(TreeViewer treeViewer) {
		return new HTMLNodeActionManager(getEditor().getModel(), treeViewer);
	}
	protected IPreferenceStore getPreferenceStore() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}
}