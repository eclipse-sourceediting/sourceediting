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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * The base class for XSL launch configuration tabs which allows a number of
 * 'blocks' to be added. Each block must itself fully implement
 * <code>ILaunchConfigurationTab</code>.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public abstract class XSLLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab {
	private ILaunchConfigurationTab[] blocks;

	protected void setBlocks(ILaunchConfigurationTab[] blocks) {
		this.blocks = blocks;
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setFont(parent.getFont());
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		comp.setLayout(layout);
		setControl(comp);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		for (ILaunchConfigurationTab element : blocks)
			element.setDefaults(configuration);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		for (ILaunchConfigurationTab element : blocks)
			element.initializeFrom(configuration);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		for (ILaunchConfigurationTab element : blocks)
			element.performApply(configuration);
	}

	@Override
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
		for (ILaunchConfigurationTab element : blocks)
			element.setLaunchConfigurationDialog(dialog);
	}

	@Override
	public void dispose() {
		for (ILaunchConfigurationTab element : blocks)
			element.dispose();
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// don't call initializeFrom
		// super.activated(workingCopy);
		for (ILaunchConfigurationTab element : blocks)
			element.activated(workingCopy);
	}

	// @Override
	// protected boolean isDirty()
	// {
	// for (ILaunchConfigurationTab element : blocks)
	// if (((XSLLaunchConfigurationTab)element).isDirty())
	// return true;
	// return super.isDirty();
	// }

	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		boolean valid = true;
		for (ILaunchConfigurationTab element : blocks)
			valid &= element.isValid(configuration);
		return valid;
	}

	@Override
	public String getErrorMessage() {
		String m = super.getErrorMessage();
		if (m == null) {
			for (ILaunchConfigurationTab element : blocks) {
				m = element.getErrorMessage();
				if (m != null)
					break;
			}
		}
		return m;
	}

	@Override
	public String getMessage() {
		String m = super.getMessage();
		if (m == null) {
			for (ILaunchConfigurationTab element : blocks) {
				m = element.getMessage();
				if (m != null)
					break;
			}
		}
		return m;
	}

}
