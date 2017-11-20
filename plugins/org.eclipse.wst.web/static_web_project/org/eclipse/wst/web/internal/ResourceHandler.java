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
	public static String ConfigureSettings;
	public static String Context_Root_cannot_be_empty_2;
	public static String Names_cannot_contain_whitespace;
	public static String The_character_is_invalid_in_a_context_root;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ResourceHandler.class);
	}
	
	public static String getString(String key, Object[] args) {
		return NLS.bind(key, args);
	}
}