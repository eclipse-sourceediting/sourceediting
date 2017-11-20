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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.output;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.WorkingDirectoryBlock;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xsl.debug.internal.util.XSLDebugPluginImages;
import org.eclipse.wst.xsl.debug.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.XSLMainTab;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;

public class OutputTab extends XSLLaunchConfigurationTab {
	private final OutputFileBlock outputFileBlock;
	private final WorkingDirectoryBlock workingDirectoryBlock;

	public OutputTab(XSLMainTab main) {
		outputFileBlock = new OutputFileBlock();
		workingDirectoryBlock = new WorkingDirectoryBlock(
				XSLLaunchConfigurationConstants.ATTR_WORKING_DIR) {
			@Override
			protected IProject getProject(ILaunchConfiguration configuration)
					throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}
		};

		setBlocks(new ILaunchConfigurationTab[] { outputFileBlock,
				workingDirectoryBlock });
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite comp = (Composite) getControl();
		GridLayout layout = new GridLayout(1, false);
		comp.setLayout(layout);

		outputFileBlock.createControl(comp);

		workingDirectoryBlock.createControl(comp);

		// Group group = new Group(comp, SWT.NULL);
		// group.setText(Messages.OutputTab_0);
		// GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// group.setLayoutData(gd);
		// layout = new GridLayout(2, false);
		// group.setLayout(layout);

		// outputBlock.createControl(group);
	}

	@Override
	public String getId() {
		return "org.eclipse.wst.xsl.internal.debug.ui.tabs.output"; //$NON-NLS-1$
	}

	public String getName() {
		return Messages.OutputTab_1;
	}

	@Override
	public Image getImage() {
		return XSLPluginImageHelper.getInstance().getImage(
				XSLDebugPluginImages.IMG_OUTPUT_TAB);
	}
}
