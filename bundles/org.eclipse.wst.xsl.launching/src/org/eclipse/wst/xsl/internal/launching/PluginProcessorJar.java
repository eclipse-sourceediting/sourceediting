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

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.launching.IProcessorJar;
import org.osgi.framework.Bundle;

public class PluginProcessorJar implements IProcessorJar
{
	private final String pluginId;
	private final IPath path;

	public PluginProcessorJar(String pluginId, IPath path)
	{
		this.pluginId = pluginId;
		this.path = path;
	}

	public URL asURL()
	{
		Bundle bundle = Platform.getBundle(pluginId);
		IPath jarPath = new Path("/" + path);
		URL url = null;
		try
		{
			url = FileLocator.find(bundle, jarPath, null);
			if (url != null)
				url = FileLocator.resolve(url);
		}
		catch (IOException e)
		{
			LaunchingPlugin.log(e);
		}
		return url;
	}

	public IPath getPath()
	{
		return path;
	}

	@Override
	public String toString()
	{
		return "Plugin " + pluginId + ", path " + path;
	}
}
