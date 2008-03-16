/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.formatter;

import org.eclipse.osgi.util.NLS;

public final class RendererTabMessages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.internal.debug.ui.tabs.renderer.RendererTabMessages"; //$NON-NLS-1$

	public static String XSLFOComboBlock_XSLFOGroupTitle;

	public static String XSLFOComboBlock_None;

	public static String XSLFOComboBlock_SpecificXSLFOProcessor;

	public static String XSLFOComboBlock_ManageXSLFOProcessor;

	public static String OutputBlock_XMLButton;

	public static String OutputBlock_FOButton;

	public static String OutputBlock_HTMLButton;

	public static String OutputBlock_OutputFilesGroupTitle;

	public static String OutputBlock_OutputTypeGroupTitle;

	public static String URIResolverBlock_Working_Directory_8;

	static
	{
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, RendererTabMessages.class);
	}

}
