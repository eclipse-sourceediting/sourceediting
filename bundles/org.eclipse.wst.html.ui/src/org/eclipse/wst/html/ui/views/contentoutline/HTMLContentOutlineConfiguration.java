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
package org.eclipse.wst.html.ui.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLNodeActionManager;

public class HTMLContentOutlineConfiguration extends XMLContentOutlineConfiguration {

	/**
	 * @param editor
	 */
	public HTMLContentOutlineConfiguration() {
		super();
	}

	/**
	 * @see com.ibm.sse.editor.xml.views.contentoutline.XMLContentOutlineConfiguration#createNodeActionManager(org.eclipse.jface.viewers.TreeViewer)
	 */
	protected XMLNodeActionManager createNodeActionManager(TreeViewer treeViewer) {
		return new HTMLNodeActionManager(getModel(treeViewer), treeViewer);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}
}