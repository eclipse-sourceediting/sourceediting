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
package org.eclipse.wst.xsl.jaxp.launching.internal.registry;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xsl.jaxp.launching.IProcessorInvoker;

public class InvokerRegistry
{
	private final Map<String, IProcessorInvoker> invokers = new HashMap<String, IProcessorInvoker>();

	public InvokerRegistry()
	{
		InvokerRegistryReader registryReader = new InvokerRegistryReader();
		registryReader.addConfigs(this);
	}

	public IProcessorInvoker getProcessorInvoker(String id)
	{
		return invokers.get(id);
	}

	public IProcessorInvoker[] getProcessorInvokers()
	{
		return invokers.values().toArray(new IProcessorInvoker[0]);
	}

	public void addInvokerDescriptor(IProcessorInvoker desc)
	{
		invokers.put(desc.getId(), desc);
	}
}
