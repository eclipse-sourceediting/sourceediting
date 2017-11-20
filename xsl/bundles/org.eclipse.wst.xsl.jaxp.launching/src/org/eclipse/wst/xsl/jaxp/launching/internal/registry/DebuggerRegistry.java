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

import org.eclipse.wst.xsl.jaxp.launching.IDebugger;
import org.eclipse.wst.xsl.jaxp.launching.internal.DebuggerDescriptor;

public class DebuggerRegistry
{
	private final Map<String, DebuggerDescriptor> debuggers = new HashMap<String, DebuggerDescriptor>();

	public DebuggerRegistry()
	{
		DebuggerRegistryReader registryReader = new DebuggerRegistryReader();
		registryReader.addConfigs(this);
	}

	public IDebugger getDebugger(String id)
	{
		return debuggers.get(id);
	}

	public IDebugger[] getDebuggers()
	{
		return debuggers.values().toArray(new IDebugger[0]);
	}

	public void addDebugger(DebuggerDescriptor desc)
	{
		debuggers.put(desc.getId(), desc);
	}
}
