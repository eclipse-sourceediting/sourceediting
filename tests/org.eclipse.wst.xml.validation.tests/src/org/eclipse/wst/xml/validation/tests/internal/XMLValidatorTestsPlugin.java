/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.validation.tests.internal;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;

/**
 * The plugin class for this test plugin.
 */
public class XMLValidatorTestsPlugin extends Plugin {
	private static XMLValidatorTestsPlugin plugin = null;

	public XMLValidatorTestsPlugin() {
		plugin = this;
	}

	public static String getPluginLocation() throws IOException {
		String file = FileLocator.resolve(plugin.getBundle().getEntry("/")).getFile();
		return new Path(file).removeTrailingSeparator().toString();
	}
	
	public static XMLValidatorTestsPlugin getPlugin()
	{
		return plugin;
	}
}
