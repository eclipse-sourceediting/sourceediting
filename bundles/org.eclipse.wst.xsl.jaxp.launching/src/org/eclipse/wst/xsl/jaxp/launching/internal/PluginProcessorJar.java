/*******************************************************************************
 * Copyright (c) 2007, 2013 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     Jesper Steen Moller - Bug 404956: Launching an XML file as 'XSL Transformation' doesn't transform anything
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
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
		URL url = null;
		try
		{
			// FIXME very clumsy way to get location orbit jar file
			// There is surely a better way, but I can'd find it.
			if (path == null)
			{
				Bundle bundle = Platform.getBundle(pluginId);
				if (bundle == null) return null;
				url = bundle.getEntry("/"); //$NON-NLS-1$
				url = FileLocator.resolve(url);
				String s = url.getPath();
				if (s.endsWith("!/")) //$NON-NLS-1$
				{
					s = s.substring(0,s.length()-2);
				}
				url = new URL(s);
			}
			else
			{
				Bundle bundle = Platform.getBundle(pluginId);
				IPath jarPath = new Path("/" + path); //$NON-NLS-1$
				url = FileLocator.find(bundle, jarPath, null);
				if (url != null)
					url = FileLocator.resolve(url);
			}
		}
		catch (IOException e)
		{
			JAXPLaunchingPlugin.log(e);
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
		return "Plugin " + pluginId + Messages.PluginProcessorJar_0 + path; //$NON-NLS-1$ 
	}
}
