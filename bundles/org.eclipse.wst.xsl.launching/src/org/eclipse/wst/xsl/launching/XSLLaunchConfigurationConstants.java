/*******************************************************************************
 * Copyright (c) 2007, 2013 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     Jesper S Moller - 405223 - Processing and file name/type doesn't match output type from XSL
 *******************************************************************************/
package org.eclipse.wst.xsl.launching;

import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public class XSLLaunchConfigurationConstants
{
	public static final String ATTR_INPUT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_INPUT_FILE"; //$NON-NLS-1$
	public static final String ATTR_USE_DEFAULT_OUTPUT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_USE_DEFAULT_OUTPUT_FILE"; //$NON-NLS-1$
	
	/**
	 * @since 1.0
	 */
	public static final String ATTR_OUTPUT_FOLDER = LaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_FOLDER"; //$NON-NLS-1$
	
	/**
	 * @since 1.0
	 */
	public static final String ATTR_OUTPUT_FILENAME = LaunchingPlugin.PLUGIN_ID + ".ATTR_OUTPUT_FILENAME"; //$NON-NLS-1$
	public static final String ID_LAUNCH_CONFIG_TYPE = LaunchingPlugin.PLUGIN_ID + ".launchConfigurationType"; //$NON-NLS-1$
	public static final String ATTR_PIPELINE = LaunchingPlugin.PLUGIN_ID + ".ATTR_PIPELINE"; //$NON-NLS-1$
	public static final String ATTR_OPEN_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_OPEN_FILE"; //$NON-NLS-1$
	
	/**
	 * @since 1.0
	 */
	public static final String ATTR_FORMAT_FILE = LaunchingPlugin.PLUGIN_ID + ".ATTR_FORMAT_FILE"; //$NON-NLS-1$
	/**
	 * @since 1.0
	 */
	public static final String ATTR_WORKING_DIR = LaunchingPlugin.PLUGIN_ID + ".ATTR_WORKING_DIR"; //$NON-NLS-1$;
	
	/**
	 * @since 1.1
	 */
	public static final String ATTR_DEFAULT_OUTPUT_METHOD = LaunchingPlugin.PLUGIN_ID + ".ATTR_DEFAULT_OUTPUT_METHOD"; //$NON-NLS-1$;
	
	
}
