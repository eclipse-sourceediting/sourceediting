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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.formatter;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.xsl.internal.debug.ui.ResourceSelectionBlock;

public class RendererConfigurationBlock extends ResourceSelectionBlock
{
	public String getName()
	{
		return "Configuration";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	@Override
	protected String getMessage(int type)
	{
		return "" + type;
	}

	@Override
	protected void setDefaultResource()
	{
	}
}
