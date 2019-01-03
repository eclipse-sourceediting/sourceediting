/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching;

import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;

public class JAXPLaunchConfigurationConstants
{
	public static final String INVOKER_DESCRIPTOR = JAXPLaunchingPlugin.PLUGIN_ID + ".INVOKER_DESCRIPTOR"; //$NON-NLS-1$
	public static final String ATTR_PROCESSOR = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_PROCESSOR"; //$NON-NLS-1$	
	public static final String ATTR_OUTPUT_PROPERTIES = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_PROPERTIES"; //$NON-NLS-1$
	public static final String ATTR_ATTRIBUTES = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_ATTRIBUTES"; //$NON-NLS-1$
	public static final String ATTR_DEFAULT_DEBUGGING_INSTALL_ID = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_DEFAULT_DEBUGGING_INSTALL_ID"; //$NON-NLS-1$
	public static final String ATTR_USE_DEFAULT_PROCESSOR = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_USE_DEFAULT_PROCESSOR"; //$NON-NLS-1$
	public static final String ATTR_TRANSFORMER_FACTORY = JAXPLaunchingPlugin.PLUGIN_ID + ".ATTR_TRANSFORMER_FACTORY"; //$NON-NLS-1$

	public static final String XALAN_TYPE_ID = "org.eclipse.wst.xsl.xalan.processorType"; //$NON-NLS-1$
	public static final String SAXON_TYPE_ID = "org.eclipse.wst.xsl.saxon.processorType"; //$NON-NLS-1$
	public static final String SAXON_1_0_TYPE_ID = "org.eclipse.wst.xsl.saxon_1_0.processorType"; //$NON-NLS-1$
}
