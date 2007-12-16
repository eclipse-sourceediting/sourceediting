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

import java.beans.PropertyChangeEvent;

import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public interface IProcessorInstallChangedListener
{
	public static final String PROPERTY_JARS = LaunchingPlugin.PLUGIN_ID + ".PROPERTY_JARS"; 

	public static final String PROPERTY_NAME = LaunchingPlugin.PLUGIN_ID + ".PROPERTY_NAME"; 

	public static final String PROPERTY_TYPE = LaunchingPlugin.PLUGIN_ID + ".PROPERTY_TYPE"; 

	/**
	 * Notification that the workspace default Processor install has changed.
	 */
	public void defaultProcessorInstallChanged(IProcessorInstall previous, IProcessorInstall current);

	/**
	 * Notification that a property of a Processor install has changed.
	 */
	public void processorChanged(PropertyChangeEvent event);

	/**
	 * Notification that a Processor has been created.
	 */
	public void processorAdded(IProcessorInstall processor);

	/**
	 * Notification that a Processor has been disposed.
	 */
	public void processorRemoved(IProcessorInstall processor);

}
