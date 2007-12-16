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
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;

public class FOFormatterTab extends XSLLaunchConfigurationTab
{
	private final XSLFOComboBlock xslfoBlock;
	private final RendererConfigurationBlock configBlock;

	public FOFormatterTab()
	{
		xslfoBlock = new XSLFOComboBlock();
		configBlock = new RendererConfigurationBlock();
		setBlocks(new ILaunchConfigurationTab[]
		{ configBlock });
	}

	private final IPropertyChangeListener fCheckListener = new IPropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			handleSelectionChanged();
		}
	};

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite comp = (Composite) getControl();

		xslfoBlock.createControl(comp);
		Control control = xslfoBlock.getControl();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		control.setLayoutData(gd);

		configBlock.createControl(comp);

		// useful bit of code if you want to display extra options when a
		// particular processor is chosen

		// Composite dynTabComp = new Composite(topComp, SWT.NONE);
		// dynTabComp.setFont(font);
		//		
		// setDynamicTabHolder(dynTabComp);
		// GridLayout tabHolderLayout = new GridLayout();
		// tabHolderLayout.marginHeight= 0;
		// tabHolderLayout.marginWidth= 0;
		// tabHolderLayout.numColumns = 1;
		// getDynamicTabHolder().setLayout(tabHolderLayout);
		// gd = new GridData(GridData.FILL_BOTH);
		// getDynamicTabHolder().setLayoutData(gd);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration)
	{
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	public String getName()
	{
		return "Formatter";
	}

	protected void handleSelectionChanged()
	{

		updateLaunchConfigurationDialog();
	}

}
