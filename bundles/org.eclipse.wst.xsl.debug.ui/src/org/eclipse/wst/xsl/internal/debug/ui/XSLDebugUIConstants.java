/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui;

/**
 * Constants for the XSL Debug UI.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLDebugUIConstants {
	private static final String PREFIX = XSLDebugUIPlugin.PLUGIN_ID + '.';
	/**
	 * Constant used to store column setting preferences for the
	 * <code>InstalledProcessorsBlock</code>
	 */
	public static final String PROCESSOR_DETAILS_DIALOG = PREFIX
			+ "PROCESSOR_DETAILS_DIALOG"; //$NON-NLS-1$
	/**
	 * Constant used to store column setting preferences for the
	 * <code>ParametersBlock</code>
	 */
	public static final String MAIN_PARAMATERS_BLOCK = PREFIX
			+ "MAIN_PARAMATERS_BLOCK"; //$NON-NLS-1$
	/**
	 * Constant used to store column setting preferences for the
	 * <code>TransformsBlock</code>
	 */
	public static final String MAIN_TRANSFORMS_BLOCK = PREFIX
			+ "MAIN_TRANSFORMS_BLOCK"; //$NON-NLS-1$
	/**
	 * Constant used to store column setting preferences for the
	 * <code>OutputBlock</code>
	 */
	public static final String OUTPUT_BLOCK = PREFIX + "OUTPUT_BLOCK"; //$NON-NLS-1$
	/**
	 * The id of the XSL editor
	 */
	public static final String XSL_EDITOR_ID = "org.eclipse.wst.xsl.ui.XSLEditor"; //$NON-NLS-1$
}
