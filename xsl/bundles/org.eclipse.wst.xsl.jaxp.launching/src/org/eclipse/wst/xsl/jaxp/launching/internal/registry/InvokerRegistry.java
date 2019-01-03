/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
