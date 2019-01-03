/*******************************************************************************
 * Copyright (c) 2007, 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.ProcessorInstall;

public class InstallStandin extends ProcessorInstall
{
	public InstallStandin(IProcessorInstall install)
	{
		super(install.getId(), install.getName(), install.getProcessorType().getId(), install.getProcessorJars(), install.getDebugger() != null ? install.getDebugger().getId() : null, install
				.getSupports(), install.isContributed());
	}

	public InstallStandin(String id, String name, String typeId, String debuggerId, IProcessorJar[] jars)
	{
		super(id, name, typeId, jars, debuggerId, "", false); //$NON-NLS-1$
	}
}
