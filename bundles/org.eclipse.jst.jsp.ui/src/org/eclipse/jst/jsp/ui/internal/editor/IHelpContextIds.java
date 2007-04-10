/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.editor;

import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;

/**
 * Help context ids for the JSP Source Editor.
 * <p>
 * This interface contains constants only; it is not intended to be
 * implemented.
 * </p>
 * 
 */
public interface IHelpContextIds {
	// org.eclipse.jst.jsp.ui.
	public static final String PREFIX = JSPUIPlugin.ID + "."; //$NON-NLS-1$


	// // figured out on the fly
	// // JSP Source page editor
	// public static final String JSP_SOURCEVIEW_HELPID =
	// ContentTypeIdForJSP.ContentTypeID_JSP +"_source_HelpId"; //$NON-NLS-1$

	// JSP Files Preference page
	public static final String JSP_PREFWEBX_FILES_HELPID = PREFIX + "webx0050"; //$NON-NLS-1$
	// JSP Styles Preference page
	public static final String JSP_PREFWEBX_STYLES_HELPID = PREFIX + "webx0051"; //$NON-NLS-1$
	// JSP Templates Preference page
	public static final String JSP_PREFWEBX_TEMPLATES_HELPID = PREFIX + "webx0052"; //$NON-NLS-1$


	// JSP Fragment Property Page
	public static final String JSP_FRAGMENT_HELPID = PREFIX + "jspf1000"; //$NON-NLS-1$

	// JSP Source Editor Context Menu
	// Refactor Rename
	public static final String JSP_REFACTORRENAME_HELPID = PREFIX + "jspr0010"; //$NON-NLS-1$

	// Refactor Move
	public static final String JSP_REFACTORMOVE_HELPID = PREFIX + "jspr0020"; //$NON-NLS-1$

	// JSP New File Wizard - Template Page
	public static final String JSP_NEWWIZARD_TEMPLATE_HELPID = PREFIX + "jspw0010"; //$NON-NLS-1$
}
