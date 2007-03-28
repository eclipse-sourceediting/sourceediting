/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public final class JSPUITestImages {
	private final static String ICONS_PATH = "icons/full/"; //$NON-NLS-1$

	/* disabled */
	private final static String DLCL = ICONS_PATH + "dlcl16/"; // $NON-NLS-1$
	/* enabled */
	private final static String ELCL = ICONS_PATH + "elcl16/"; //$NON-NLS-1$

	// Images courtesy of org.eclipse.debug.ui
	public static final String IMG_ELCL_SUSPEND = ELCL + "SUSPEND"; //$NON-NLS-1$
	public static final String IMG_ELCL_RESUME = ELCL + "RESUME"; //$NON-NLS-1$
	public static final String IMG_DLCL_SUSPEND = DLCL + "SUSPEND"; //$NON-NLS-1$
	public static final String IMG_DLCL_RESUME = DLCL + "RESUME"; //$NON-NLS-1$
	public static final String IMG_ELCL_REMOVE_ALL = ELCL + "CLEAR"; //$NON-NLS-1$
	public static final String IMG_DLCL_REMOVE_ALL = DLCL + "CLEAR"; //$NON-NLS-1$

	static void initializeImageRegistry(ImageRegistry reg) {
		reg.put(IMG_ELCL_RESUME, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, ELCL + "resume_co.gif"));
		reg.put(IMG_DLCL_RESUME, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, DLCL + "resume_co.gif"));

		reg.put(IMG_ELCL_SUSPEND, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, ELCL + "suspend_co.gif"));
		reg.put(IMG_DLCL_SUSPEND, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, DLCL + "suspend_co.gif"));

		reg.put(IMG_ELCL_REMOVE_ALL, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, ELCL + "rem_all_co.gif"));
		reg.put(IMG_DLCL_REMOVE_ALL, AbstractUIPlugin.imageDescriptorFromPlugin(JSPUITestsPlugin.ID, DLCL + "rem_all_co.gif"));
	}
}
