/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class Utils
{

	public static String getFileLocation(String bundleId, String path) throws CoreException
	{
		String location = null;
		try
		{
			URL url = FileLocator.find(Platform.getBundle(bundleId), new Path(path), null);
			if (url != null)
			{
				URL fileUrl = FileLocator.toFileURL(url);
				File file = new File(fileUrl.getFile());
				location = file.getAbsolutePath();
			}
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR, Messages.Utils_0 + path + Messages.Utils_1 + bundleId, e)); 
		} 
		return location;
	}

	public static String getPluginLocation(String bundleId) throws CoreException
	{
		String location = null;
		try
		{
			URL url = new URL("platform:/plugin/"+bundleId); //$NON-NLS-1$
			if (url != null)
			{
				URL fileUrl = FileLocator.toFileURL(url);
				File file = new File(fileUrl.getFile());
				location = file.getAbsolutePath();
			}
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR, Messages.Utils_0 + bundleId + Messages.Utils_1 + bundleId, e)); 
		} 
		return location;
	}

}
