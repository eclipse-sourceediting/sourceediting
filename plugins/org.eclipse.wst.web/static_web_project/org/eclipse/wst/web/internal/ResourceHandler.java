/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.internal;

import org.eclipse.osgi.util.NLS;

public final class ResourceHandler extends NLS {

	private static final String BUNDLE_NAME = "staticwebproject";//$NON-NLS-1$

	private ResourceHandler() {
		// Do not instantiate
	}

	public static String StaticWebProjectCreationWizard_Wizard_Title;
	public static String StaticWebProjectWizardBasePage_Page_Description;
	public static String StaticWebSettingsPropertiesPage_Web_Content_Label;
	public static String StaticContextRootComposite_Context_Root_Label;
	public static String StaticWebProjectWizardBasePage_Page_Title;
	public static String StaticWebSettingsPropertiesPage_Not_available_for_closed_projects;
	public static String TargetRuntime;
	public static String NewDotDotDot;
	public static String InvalidServerTarget;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ResourceHandler.class);
	}
}