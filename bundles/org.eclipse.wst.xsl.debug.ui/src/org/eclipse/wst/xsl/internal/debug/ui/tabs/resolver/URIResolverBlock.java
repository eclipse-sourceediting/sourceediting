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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.resolver;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.ResourceSelectionBlock;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.ProcessorMessages;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;

public class URIResolverBlock extends ResourceSelectionBlock
{
	private Button stylesheetRelativeRadio;
	private Button otherRadio;
	private Button workingDirRelativeRadio;

	public URIResolverBlock()
	{
		super(IResource.FOLDER, true, true, true);
	}

	@Override
	protected String getMessage(int type)
	{
		switch (type)
		{
			case ERROR_DIRECTORY_NOT_SPECIFIED:
				return ProcessorMessages.URIResolverBlock_DIRECTORY_NOT_SPECIFIED;
			case ERROR_DIRECTORY_DOES_NOT_EXIST:
				return ProcessorMessages.URIResolverBlock_DIRECTORY_DOES_NOT_EXIST;
			case GROUP_NAME:
				return getName();
			case USE_DEFAULT_RADIO:
				return ProcessorMessages.URIResolverBlock_DEFAULT_RADIO;
			case USE_OTHER_RADIO:
				return ProcessorMessages.URIResolverBlock_OTHER_RADIO;
			case DIRECTORY_DIALOG_MESSAGE:
				return ProcessorMessages.URIResolverBlock_DIALOG_MESSAGE;
			case WORKSPACE_DIALOG_MESSAGE:
				return ProcessorMessages.URIResolverBlock_WORKSPACE_DIALOG_MESSAGE;
			case VARIABLES_BUTTON:
				return ProcessorMessages.URIResolverBlock_VARIABLES_BUTTON;
			case FILE_SYSTEM_BUTTON:
				return ProcessorMessages.URIResolverBlock_FILE_SYSTEM_BUTTON;
			case WORKSPACE_BUTTON:
				return ProcessorMessages.URIResolverBlock_WORKSPACE_BUTTON;
			case WORKSPACE_DIALOG_TITLE:
				return ProcessorMessages.URIResolverBlock_WORKSPACE_DIALOG_TITLE;
		}
		return "" + type;
	}

	@Override
	protected void setDefaultResource()
	{
	}

	public String getName()
	{
		return "URI Resolver";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
	}

	@Override
	protected void createCheckboxAndText(Composite parent)
	{
		useDefaultCheckButton = createCheckButton(parent, getMessage(USE_DEFAULT_RADIO));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		useDefaultCheckButton.setLayoutData(gd);
		useDefaultCheckButton.addSelectionListener(widgetListener);

		Composite specificFileComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginLeft = 5;
		layout.marginHeight = 0;
		specificFileComp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		specificFileComp.setLayoutData(gd);

		SelectionListener listener = new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				updateEnablement(useDefaultCheckButton.getSelection());
			}
		};

		workingDirRelativeRadio = createRadioButton(specificFileComp, "Relative to working directory");
		gd = new GridData();
		gd.horizontalSpan = 2;
		workingDirRelativeRadio.setLayoutData(gd);
		workingDirRelativeRadio.addSelectionListener(listener);

		stylesheetRelativeRadio = createRadioButton(specificFileComp, "Relative to (1st) stylesheet");
		gd = new GridData();
		gd.horizontalSpan = 2;
		stylesheetRelativeRadio.setLayoutData(gd);
		stylesheetRelativeRadio.addSelectionListener(listener);

		otherRadio = createRadioButton(specificFileComp, "Specific Location:");
		gd = new GridData();
		gd.horizontalSpan = 1;
		otherRadio.setLayoutData(gd);
		otherRadio.addSelectionListener(listener);

		resourceText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		resourceText.setLayoutData(gd);
		resourceText.setFont(parent.getFont());
		resourceText.addModifyListener(widgetListener);
	}

	@Override
	protected void updateResourceText(boolean useDefault)
	{
		if (useDefault)
		{
			resourceText.setText(defaultOutputFile == null ? "" : defaultOutputFile);
		}
		else
		{
			resourceText.setText(outputFile == null ? "" : outputFile);
		}
		updateEnablement(useDefault);
	}

	protected void updateEnablement(boolean useDefault)
	{
		otherRadio.setEnabled(!useDefault);
		stylesheetRelativeRadio.setEnabled(!useDefault);
		workingDirRelativeRadio.setEnabled(!useDefault);

		boolean otherEnabled = !useDefault && otherRadio.getSelection();
		resourceText.setEnabled(otherEnabled);
		fFileSystemButton.setEnabled(otherEnabled);
		fVariablesButton.setEnabled(otherEnabled);
		fWorkspaceButton.setEnabled(otherEnabled);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		if (workingDirRelativeRadio.getSelection())
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_TYPE, XSLLaunchConfigurationConstants.BASE_URI_WORKING_DIR_RELATIVE);
		else if (stylesheetRelativeRadio.getSelection())
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_TYPE, XSLLaunchConfigurationConstants.BASE_URI_STYLESHEET_RELATIVE);
		else if (otherRadio.getSelection())
		{
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_TYPE, XSLLaunchConfigurationConstants.BASE_URI_ABSOLUTE);
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_DIRECTORY, getText());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config)
	{
		config.setAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_DIRECTORY, (String) null);
	}
}
