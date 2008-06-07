/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - bug 223557 - Added images contributed by Holger Voormann
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.processor;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xsl.debug.internal.util.XSLDebugPluginImages;
import org.eclipse.wst.xsl.debug.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;
import org.eclipse.wst.xsl.launching.IProcessorInvoker;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class XSLProcessorTab extends XSLLaunchConfigurationTab
{
	private final ProcessorBlock processorBlock;
	// private WorkingDirectoryBlock workingDirectoryBlock;
	// private URIResolverBlock2 featuresBlock;
	private final FeaturesBlock featuresBlock;

	public XSLProcessorTab()
	{
		featuresBlock = new FeaturesBlock();
		processorBlock = new ProcessorBlock(featuresBlock);
		setBlocks(new ILaunchConfigurationTab[]
		{ processorBlock, featuresBlock });
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite comp = (Composite) getControl();

		processorBlock.createControl(comp);
		featuresBlock.createControl(comp);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		super.performApply(configuration);
		// TODO make a new block to allow user to select the invoker
		IProcessorInvoker invoker = XSLTRuntime.getProcessorInvokers()[0];
		configuration.setAttribute(XSLLaunchConfigurationConstants.INVOKER_DESCRIPTOR, invoker.getId());
	}

	public String getName()
	{
		return Messages.getString("XSLProcessorTab.0"); //$NON-NLS-1$
	}

	@Override
	public Image getImage()
	{
		return XSLPluginImageHelper.getInstance().getImage(XSLDebugPluginImages.IMG_PROCESSOR_TAB);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
