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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInvoker;

public class ProcessorInvokerDescriptor implements IProcessorInvoker {

	private final String invokerClass;
	private final String[] classpath;
	private final String id;
	private final String bundleId;

	public ProcessorInvokerDescriptor(String id, String bundleId,
			String invokerClass, String[] classpath) {
		this.id = id;
		this.bundleId = bundleId;
		this.invokerClass = invokerClass;
		this.classpath = classpath;
	}

	public String[] getClasspathEntries() {
		return createEntries(bundleId, classpath);
	}

	public static String[] createEntries(String bundleId, String[] classpath) {
		List<String> entries = new ArrayList<String>();
		try {
			// if in dev mode, use the bin dir
			if (Platform.inDevelopmentMode())
				entries.add(Utils.getFileLocation(bundleId, "/bin")); //$NON-NLS-1$
			for (String jar : classpath) {
				String entry = null;
				if (jar.startsWith("${eclipse_orbit:") && jar.endsWith("}")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					jar = jar.substring("${eclipse_orbit:".length()); //$NON-NLS-1$
					jar = jar.substring(0, jar.length() - 1);
					try {
						File bundleFile = FileLocator.getBundleFile(Platform
								.getBundle(jar));
						if (bundleFile.isDirectory())
							entry = Utils.getPluginLocation(jar) + "/bin"; //$NON-NLS-1$
						else
							entry = Utils.getPluginLocation(jar);
					} catch (IOException e) {
					}
				} else {
					entry = Utils.getFileLocation(bundleId, jar);
				}
				if (entry != null)
					entries.add(entry);
			}
		} catch (CoreException e) {
			JAXPLaunchingPlugin.log(e);
		}
		return entries.toArray(new String[0]);
	}

	/**
	 * The name of the class that implements IProcessorInvoker
	 */
	public String getInvokerClassName() {
		return invokerClass;
	}

	public String getId() {
		return id;
	}
}
