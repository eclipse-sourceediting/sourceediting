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
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIPlugin;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;

/**
 * Configuration for outline view page which shows JSP content.
 * 
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 * @since 1.0
 */
public class JSPContentOutlineConfiguration extends
		HTMLContentOutlineConfiguration {

	// private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	// TODO: Automate the loading of a real configuration based on the model
	// type at
	// creation time; clear on unConfigure so that a new embedded
	// configuration can
	// be used
	// private StructuredContentOutlineConfiguration fEmbeddedConfiguration =
	// null;

	/**
	 * Create new instance of JSPContentOutlineConfiguration
	 */
	public JSPContentOutlineConfiguration() {
		// Must have empty constructor to createExecutableExtension
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
}
