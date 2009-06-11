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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.JAXPDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class XSLProcessorTab2 extends XSLLaunchConfigurationTab
{
	private final ProcessorBlock processorBlock;
	private final AttributesBlock attributesBlock;
	private final OutputPropertiesBlock outputPropertiesBlock;
	private Image image;

	public XSLProcessorTab2()
	{
		attributesBlock = new AttributesBlock();
		outputPropertiesBlock = new OutputPropertiesBlock();
		processorBlock = new ProcessorBlock();
		
		setBlocks(new ILaunchConfigurationTab[]
		{ processorBlock , attributesBlock, outputPropertiesBlock });
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite comp = (Composite) getControl();

		processorBlock.createControl(comp);
		
		TabFolder tabFolder = new TabFolder(comp,SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		outputPropertiesBlock.createControl(tabFolder);		
		attributesBlock.createControl(tabFolder);		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		super.performApply(configuration);
		IProcessorInvoker invoker = JAXPRuntime.getProcessorInvokers()[0];
		configuration.setAttribute(JAXPLaunchConfigurationConstants.INVOKER_DESCRIPTOR, invoker.getId());
	}

	public String getName()
	{
		return Messages.getString("XSLProcessorTab.0"); //$NON-NLS-1$
	}
	
	@Override
	public String getId()
	{
		return "org.eclipse.wst.xsl.debug.ui.jaxp.tabs.processor"; //$NON-NLS-1$
	}

	@Override
	public Image getImage()
	{
		if (image == null)
		{
			ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(JAXPDebugUIPlugin.PLUGIN_ID, "icons/xslt_processor.gif"); //$NON-NLS-1$
			image = id.createImage();
		}
		return image;
	}

	@Override
	public void dispose()
	{
		if (image != null)
			image.dispose();
		super.dispose();
	}
}
