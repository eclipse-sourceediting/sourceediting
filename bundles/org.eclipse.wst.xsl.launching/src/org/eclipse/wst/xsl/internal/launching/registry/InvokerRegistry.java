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
package org.eclipse.wst.xsl.internal.launching.registry;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xsl.launching.IProcessorInvokerDescriptor;

public class InvokerRegistry
{
	private final Map invokers = new HashMap();

	public InvokerRegistry()
	{
		InvokerRegistryReader registryReader = new InvokerRegistryReader();
		registryReader.addConfigs(this);
	}

	public IProcessorInvokerDescriptor getProcessorInvoker(String id)
	{
		return (IProcessorInvokerDescriptor) invokers.get(id);
	}

	public IProcessorInvokerDescriptor[] getProcessorInvokers()
	{
		return (IProcessorInvokerDescriptor[]) invokers.values().toArray(new IProcessorInvokerDescriptor[0]);
	}

	public void addInvokerDescriptor(IProcessorInvokerDescriptor desc)
	{
		invokers.put(desc.getId(), desc);
	}
}
