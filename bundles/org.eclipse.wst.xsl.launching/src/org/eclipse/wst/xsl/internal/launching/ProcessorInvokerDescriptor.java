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

import org.eclipse.wst.xsl.launching.IProcessorInvokerDescriptor;

public class ProcessorInvokerDescriptor implements IProcessorInvokerDescriptor
{

	private final String invokerClass;
	private final String[] classpath;
	private final String id;
	private final String bundleId;

	public ProcessorInvokerDescriptor(String id, String bundleId, String invokerClass, String[] classpath)
	{
		this.id = id;
		this.bundleId = bundleId;
		this.invokerClass = invokerClass;
		this.classpath = classpath;
	}

	public String[] getClasspathEntries()
	{
		return classpath;
	}

	/**
	 * The name of the class that implements IProcessorInvoker
	 */
	public String getInvokerClassName()
	{
		return invokerClass;
	}

	public String getBundleId()
	{
		return bundleId;
	}

	public String getId()
	{
		return id;
	}

}
