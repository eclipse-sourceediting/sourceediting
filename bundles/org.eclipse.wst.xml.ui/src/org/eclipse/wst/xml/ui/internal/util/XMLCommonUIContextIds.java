/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/


package org.eclipse.wst.xml.ui.internal.util;

/**
 * Context help id constants.
 */
public interface XMLCommonUIContextIds {
	public static final String PLUGIN_NAME = "com.ibm.etools.xml.common.ui"; //$NON-NLS-1$


	/* CONTEXT_IDs for "Assign an XSL Stylesheet To The XML File" dialog */
	/* CONTEXT_ID comn0010 for the dropdown menu. Follow comnxxxx */
	public static final String XCUI_ASSIGN_XSL_TO_XML_DROPDOWN = PLUGIN_NAME + ".xcui1000"; //$NON-NLS-1$
	/* CONTEXT_ID comn0030 for the import button. Follow comnxxxx */
	public static final String XCUI_ASSIGN_XSL_TO_XML_IMPORT = PLUGIN_NAME + ".xcui3000"; //$NON-NLS-1$
	/* CONTEXT_ID comn0020 for the workbench button. Follow comnxxxx */
	public static final String XCUI_ASSIGN_XSL_TO_XML_WORKBENCH = PLUGIN_NAME + ".xcui2000"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0500 for Edit Attribute Instruction Dialog */
	public static final String XCUI_ATTRIBUTE_DIALOG = PLUGIN_NAME + ".xcui0500"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0400 for Select XML Catalog ID Dialog */
	public static final String XCUI_CATALOG_DIALOG = PLUGIN_NAME + ".xcui0400"; //$NON-NLS-1$

	/* CONTEXT_IDs for XML Common UI use xcuixxx context IDs */

	/* CONTEXT_ID xcui0010 for Edit Doctype Dialog */
	public static final String XCUI_DOCTYPE_DIALOG = PLUGIN_NAME + ".xcui0010"; //$NON-NLS-1$
	/* CONTEXT_ID xcui0030 for Public ID Text Edit */
	public static final String XCUI_DOCTYPE_PUBLIC = PLUGIN_NAME + ".xcui0030"; //$NON-NLS-1$
	/* CONTEXT_ID xcui0030 for Public ID Browse Button */
	public static final String XCUI_DOCTYPE_PUBLIC_BROWSE = PLUGIN_NAME + ".xcui0040"; //$NON-NLS-1$
	/* CONTEXT_ID xcui0020 for Root Element Name Text Edit */
	public static final String XCUI_DOCTYPE_ROOT = PLUGIN_NAME + ".xcui0020"; //$NON-NLS-1$
	/* CONTEXT_ID xcui0040 for System ID Text Edit */
	public static final String XCUI_DOCTYPE_SYSTEM = PLUGIN_NAME + ".xcui0050"; //$NON-NLS-1$
	/* CONTEXT_ID xcui0030 for System ID Browse Button */
	public static final String XCUI_DOCTYPE_SYSTEM_BROWSE = PLUGIN_NAME + ".xcui0060"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0300 for Edit Element Instruction Dialog */
	public static final String XCUI_ELEMENT_DIALOG = PLUGIN_NAME + ".xcui0600"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0200 for Edit Namespace Dialog */
	public static final String XCUI_NAMESPACE_DIALOG = PLUGIN_NAME + ".xcui0200"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0300 for Edit Processing Instruction Dialog */
	public static final String XCUI_PROCESSING_DIALOG = PLUGIN_NAME + ".xcui0300"; //$NON-NLS-1$

	/* CONTEXT_ID xcui0100 for Edit Schema Information Dialog */
	public static final String XCUI_SCHEMA_INFO_DIALOG = PLUGIN_NAME + ".xcui0100"; //$NON-NLS-1$
}
