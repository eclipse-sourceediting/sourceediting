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
package org.eclipse.wst.xsl.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LaunchingPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.launching"; //$NON-NLS-1$

	// The shared instance
	private static LaunchingPlugin plugin;

	/**
	 * The constructor
	 */
	public LaunchingPlugin()
	{
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LaunchingPlugin getDefault()
	{
		return plugin;
	}

	public static void log(Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, "", e)); //$NON-NLS-1$
	}

	public static void log(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}
}
