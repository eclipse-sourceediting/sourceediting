/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.editor;

import org.eclipse.wst.jsdt.web.ui.internal.JsUIPlugin;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public interface IHelpContextIds {
	// JSP Fragment Property Page
	public static final String JSP_FRAGMENT_HELPID = IHelpContextIds.PREFIX + "jspf1000"; //$NON-NLS-1$
	// // figured out on the fly
	// // JSP Source page editor
	// public static final String JSP_SOURCEVIEW_HELPID =
	// ContentTypeIdForJSP.ContentTypeID_JSP +"_source_HelpId"; //$NON-NLS-1$
	// JSP New File Wizard - Template Page
	public static final String JSP_NEWWIZARD_TEMPLATE_HELPID = IHelpContextIds.PREFIX + "jspw0010"; //$NON-NLS-1$
	// JSP Files Preference page
	public static final String JSP_PREFWEBX_FILES_HELPID = IHelpContextIds.PREFIX + "webx0050"; //$NON-NLS-1$
	// JSP Styles Preference page
	public static final String JSP_PREFWEBX_STYLES_HELPID = IHelpContextIds.PREFIX + "webx0051"; //$NON-NLS-1$
	// JSP Templates Preference page
	public static final String JSP_PREFWEBX_TEMPLATES_HELPID = IHelpContextIds.PREFIX + "webx0052"; //$NON-NLS-1$
	// Refactor Move
	public static final String JSP_REFACTORMOVE_HELPID = IHelpContextIds.PREFIX + "jspr0020"; //$NON-NLS-1$
	// JSP Source Editor Context Menu
	// Refactor Rename
	public static final String JSP_REFACTORRENAME_HELPID = IHelpContextIds.PREFIX + "jspr0010"; //$NON-NLS-1$
	// org.eclipse.wst.jsdt.web.ui.
	public static final String PREFIX = JsUIPlugin.ID + "."; //$NON-NLS-1$
}
