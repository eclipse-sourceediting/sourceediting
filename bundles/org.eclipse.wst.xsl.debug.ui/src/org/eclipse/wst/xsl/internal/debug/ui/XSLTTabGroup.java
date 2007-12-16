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
package org.eclipse.wst.xsl.internal.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.XSLMainTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.output.OutputTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.XSLProcessorTab;

public class XSLTTabGroup extends AbstractLaunchConfigurationTabGroup
{
	public XSLTTabGroup()
	{
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		XSLMainTab main = new XSLMainTab();
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]
		{ main, new XSLProcessorTab(), new OutputTab(main),
		// new FOFormatterTab(),
				// new XSLFeaturesTab(),
				new JavaJRETab(), new JavaClasspathTab(), new SourceLookupTab(), new CommonTab() };
		setTabs(tabs);
	}
}
