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
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;

public class ProcessorJar implements IProcessorJar {
	private final IPath path;

	public ProcessorJar(IPath path) {
		this.path = path;
	}

	public URL asURL() {
		URL url = null;
		try {
			// first try to resolve as workspace-relative path
			IPath rootPath = ResourcesPlugin.getWorkspace().getRoot()
					.getLocation();
			File file = new File(rootPath.append(path).toOSString());
			if (file.exists())
				url = file.toURI().toURL();
			else {
				// now try to resolve as an absolute path
				file = new File(path.toOSString());
				url = file.toURI().toURL();
			}
		} catch (MalformedURLException e) {
			JAXPLaunchingPlugin.log(e);
		}
		return url;
	}

	public IPath getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path.toString();
	}
}
