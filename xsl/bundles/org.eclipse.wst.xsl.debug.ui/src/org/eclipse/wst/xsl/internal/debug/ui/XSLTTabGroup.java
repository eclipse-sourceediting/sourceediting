/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.internal.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.XSLMainTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.output.OutputTab;

/**
 * The tab group for the XSL tabs.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLTTabGroup extends AbstractLaunchConfigurationTabGroup {
	/**
	 * Create a new instance of this.
	 */
	public XSLTTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		XSLMainTab main = new XSLMainTab();
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { main,
				new OutputTab(main) };
		tabs = addTabs(tabs, new ILaunchConfigurationTab[] {
				new SourceLookupTab(), new CommonTab() });

		setTabs(tabs);
	}

	private ILaunchConfigurationTab[] addTabs(ILaunchConfigurationTab[] tabs1,
			ILaunchConfigurationTab[] tabs2) {
		ILaunchConfigurationTab[] newTabs = new ILaunchConfigurationTab[tabs1.length
				+ tabs2.length];
		System.arraycopy(tabs1, 0, newTabs, 0, tabs1.length);
		System.arraycopy(tabs2, 0, newTabs, tabs1.length, tabs2.length);
		return newTabs;
	}

	@Override
	public void setTabs(ILaunchConfigurationTab[] tabs) {
		// TODO Auto-generated method stub
		super.setTabs(tabs);
	}
}
