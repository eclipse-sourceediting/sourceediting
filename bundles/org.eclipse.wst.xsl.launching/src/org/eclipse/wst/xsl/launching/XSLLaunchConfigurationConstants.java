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
package org.eclipse.wst.xsl.launching;

import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public class XSLLaunchConfigurationConstants
{
	public static final String INVOKER_DESCRIPTOR = LaunchingPlugin.PLUGIN_ID + ".INVOKER_DESCRIPTOR"; //$NON-NLS-1$
	public static final String ATTR_PROCESSOR = LaunchingPlugin.PLUGIN_ID + ".ATTR_PROCESSOR"; //$NON-NLS-1$
	public static final String ATTR_LAUNCH_TYPE = LaunchingPlugin.PLUGIN_ID + ".ATTR_LAUNCH_TYPE"; //$NON-NLS-1$
	public static final String ATTR_BASE_URI_TYPE = LaunchingPlugin.PLUGIN_ID + ".ATTR_BASE_URI_TYPE"; //$NON-NLS-1$
	public static final String ATTR_BASE_URI_DIRECTORY = LaunchingPlugin.PLUGIN_ID + ".ATTR_BASE_URI_DIRECTORY"; //$NON-NLS-1$
	public static final String ATTR_PROJECT_NAME = LaunchingPlugin.PLUGIN_ID + ".ATTR_PROJECT_NAME"; //$NON-NLS-1$
	public static final String ATTR_INPUT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_INPUT_FILE"; //$NON-NLS-1$
	public static final String ATTR_OUTPUT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_FILE"; //$NON-NLS-1$
	public static final String ATTR_OUTPUT_METHOD = LaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_METHOD"; //$NON-NLS-1$
	public static final String ATTR_OUTPUT_PROPERTIES = LaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_PROPERTIES"; //$NON-NLS-1$
	public static final String ATTR_STYLESHEETS_LIST = LaunchingPlugin.PLUGIN_ID + ".ATTR_STYLESHEETS_LIST"; //$NON-NLS-1$
	public static final String ATTR_RENDER_TO = LaunchingPlugin.PLUGIN_ID + ".ATTR_RENDER_TO"; //$NON-NLS-1$
	public static final String ID_LAUNCH_CONFIG_TYPE = LaunchingPlugin.PLUGIN_ID + ".launchConfigurationType"; //$NON-NLS-1$
	public static final String ATTR_PERFORM_RENDER = LaunchingPlugin.PLUGIN_ID + ".ATTR_PERFORM_RENDER"; //$NON-NLS-1$
	public static final String ATTR_USE_DEFAULT_OUTPUT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_DEFAULT_OUTPUT_FILE"; //$NON-NLS-1$
	public static final String ATTR_OPEN_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_OPEN_FILE"; //$NON-NLS-1$
	public static final String ATTR_PROCESSOR_WORKING_DIR = LaunchingPlugin.PLUGIN_ID + ".ATTR_PROCESSOR_WORKING_DIR"; //$NON-NLS-1$
	public static final String ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR"; //$NON-NLS-1$
	public static final String ATTR_USE_DEFAULT_PROCESSOR = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_DEFAULT_PROCESSOR"; //$NON-NLS-1$
	public static final String ATTR_USE_FEATURES_FROM_PREFERENCES = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_FEATURES_FROM_PREFERENCES"; //$NON-NLS-1$
	public static final String ATTR_USE_PROPERTIES_FROM_PREFERENCES = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_PROPERTIES_FROM_PREFERENCES"; //$NON-NLS-1$
	public static final String ATTR_JRE_DEFAULT_TYPE_TYPE = ".ATTR_JRE_DEFAULT_TYPE_TYPE"; //$NON-NLS-1$
	public static final String ATTR_FEATURES = LaunchingPlugin.PLUGIN_ID + ".ATTR_FEATURES"; //$NON-NLS-1$
	public static final String ATTR_PIPELINE = LaunchingPlugin.PLUGIN_ID + ".ATTR_PIPELINE"; //$NON-NLS-1$
	public static final String ATTR_DEFAULT_DEBUGGING_INSTALL_ID = LaunchingPlugin.PLUGIN_ID + ".ATTR_DEFAULT_DEBUGGING_INSTALL_ID";; //$NON-NLS-1$

	public static final String XALAN_TYPE_ID = "org.eclipse.wst.xsl.xalan.processorType"; //$NON-NLS-1$
	public static final String SAXON_TYPE_ID = "org.eclipse.wst.xsl.saxon.processorType"; //$NON-NLS-1$
	public static final String SAXON_1_0_TYPE_ID = "org.eclipse.wst.xsl.saxon_1_0.processorType"; //$NON-NLS-1$
}
