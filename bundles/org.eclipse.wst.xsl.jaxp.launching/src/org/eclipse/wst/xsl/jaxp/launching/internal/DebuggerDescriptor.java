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

import org.eclipse.wst.xsl.jaxp.launching.IDebugger;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class DebuggerDescriptor implements IDebugger
{
	private final String[] classpath;
	private final String id;
	private final String bundleId;
	private final String name;
	private final String processorTypeId;
	private final String className;
	private final String transformerFactory;

	public DebuggerDescriptor(String id, String bundleId, String className, String[] classpath, String name, String processorTypeId, String transformerFactory)
	{
		this.id = id;
		this.classpath = classpath;
		this.bundleId = bundleId;
		this.name = name;
		this.processorTypeId = processorTypeId;
		this.className = className;
		this.transformerFactory = transformerFactory;
	}

	public String getClassName()
	{
		return className;
	}

	public String[] getClassPath()
	{
		return ProcessorInvokerDescriptor.createEntries(bundleId, classpath);
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public IProcessorType getProcessorType()
	{
		return JAXPRuntime.getProcessorType(processorTypeId);
	}

	public String getTransformerFactory()
	{
		return transformerFactory;
	}
}
