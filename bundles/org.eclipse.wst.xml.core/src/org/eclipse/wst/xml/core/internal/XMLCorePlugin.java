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
package org.eclipse.wst.xml.core.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;


/**
 * The main plugin class to be used in the desktop.
 */
public class XMLCorePlugin extends Plugin {
	//The shared instance.
	private static XMLCorePlugin plugin;	

	/**
	 * Returns the shared instance.
	 */
	public static XMLCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * @deprecated use ResourcesPlugin.getWorkspace();
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public XMLCorePlugin() {
		super();
		plugin = this;
	}
}
