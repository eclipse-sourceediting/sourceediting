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
package org.eclipse.jst.jsp.ui.internal.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;

//public class JSPContentOutlineConfiguration extends StructuredContentOutlineConfiguration {
public class JSPContentOutlineConfiguration extends HTMLContentOutlineConfiguration {

	//	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	// TODO: Automate the loading of a real configuration based on the model type at
	// creation time; clear on unConfigure so that a new embedded configuration can
	// be used
	//private StructuredContentOutlineConfiguration fEmbeddedConfiguration = null;

	/**
	 * @param editor
	 */
	public JSPContentOutlineConfiguration() {
		super();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
}
