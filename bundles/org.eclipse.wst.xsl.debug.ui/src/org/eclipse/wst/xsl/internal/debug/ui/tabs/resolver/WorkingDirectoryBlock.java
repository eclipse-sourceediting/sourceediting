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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.ResourceSelectionBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.ProcessorMessages;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;

public class WorkingDirectoryBlock extends ResourceSelectionBlock
{
	private String defaultWorkingDir;
	private Text defaultText;
	private Button useSpecificButton;

	public WorkingDirectoryBlock()
	{
		super(IResource.FOLDER, false, true, true);
	}

	@Override
	protected String getMessage(int type)
	{
		switch (type)
		{
			case ERROR_DIRECTORY_NOT_SPECIFIED:
				return ProcessorMessages.WorkingDirectoryBlock_DIRECTORY_NOT_SPECIFIED;
			case ERROR_DIRECTORY_DOES_NOT_EXIST:
				return ProcessorMessages.WorkingDirectoryBlock_DIRECTORY_DOES_NOT_EXIST;
			case GROUP_NAME:
				return getName();
			case USE_DEFAULT_RADIO:
				return ProcessorMessages.WorkingDirectoryBlock_DEFAULT_RADIO;
			case USE_OTHER_RADIO:
				return ProcessorMessages.WorkingDirectoryBlock_OTHER_RADIO;
			case DIRECTORY_DIALOG_MESSAGE:
				return ProcessorMessages.WorkingDirectoryBlock_DIALOG_MESSAGE;
			case WORKSPACE_DIALOG_MESSAGE:
				return ProcessorMessages.WorkingDirectoryBlock_WORKSPACE_DIALOG_MESSAGE;
			case VARIABLES_BUTTON:
				return ProcessorMessages.WorkingDirectoryBlock_VARIABLES_BUTTON;
			case FILE_SYSTEM_BUTTON:
				return ProcessorMessages.WorkingDirectoryBlock_FILE_SYSTEM_BUTTON;
			case WORKSPACE_BUTTON:
				return ProcessorMessages.WorkingDirectoryBlock_WORKSPACE_BUTTON;
			case WORKSPACE_DIALOG_TITLE:
				return ProcessorMessages.WorkingDirectoryBlock_WORKSPACE_DIALOG_TITLE;
		}
		return "" + type;
	}

	@Override
	protected void createCheckboxAndText(Composite parent)
	{
		Composite specificFileComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginHeight = 0;
		specificFileComp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		specificFileComp.setLayoutData(gd);

		useDefaultCheckButton = createRadioButton(specificFileComp, "Default:");
		useDefaultCheckButton.addSelectionListener(widgetListener);

		defaultText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		defaultText.setLayoutData(gd);
		defaultText.setFont(parent.getFont());
		defaultText.setEnabled(false);

		useSpecificButton = createRadioButton(specificFileComp, "Other:");

		resourceText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		resourceText.setLayoutData(gd);
		resourceText.setFont(parent.getFont());
		resourceText.addModifyListener(widgetListener);
	}

	@Override
	protected void setDefaultResource()
	{
	}

	public String getName()
	{
		return "Working Directory";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			setLaunchConfiguration(configuration);

			updateDefaultWorkingDir();

			outputFile = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR_WORKING_DIR, (String) null);
			boolean useDefault = outputFile == null;

			useDefaultCheckButton.setSelection(useDefault);
			useSpecificButton.setSelection(!useDefault);
			resourceText.setEnabled(!useDefault);

			if (useDefault)
				outputFile = "";

			updateResourceText(useDefault);
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	@Override
	protected void updateResourceText(boolean useDefault)
	{
		super.updateResourceText(useDefault);
		defaultText.setText(defaultWorkingDir);
	}

	public void updateDefaultWorkingDir()
	{
		try
		{
			ILaunchConfiguration config = getLaunchConfiguration();
			if (config != null)
			{
				String pathExpr = config.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
				if (pathExpr != null)
				{
					IPath path = getPath(pathExpr);
					if (path != null)
					{
						path = path.removeLastSegments(1);
						defaultWorkingDir = path.toPortableString();
						return;
					}
				}
			}
		}
		catch (CoreException ce)
		{
		}
		defaultWorkingDir = System.getProperty("user.dir");
	}

	private Path getPath(String inputFile)
	{
		Path path = null;
		if (inputFile.indexOf("${") >= 0)
		{
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try
			{
				manager.validateStringVariables(inputFile);
				path = new Path(manager.performStringSubstitution(inputFile));
			}
			catch (CoreException e)
			{
			}
		}
		else if (inputFile.length() > 0)
		{
			path = new Path(inputFile);
		}
		return path;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		boolean useDefault = useDefaultCheckButton.getSelection();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR, useDefault);
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR_WORKING_DIR, useDefault ? null : getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR, true);
	}

	@Override
	public boolean isValid(ILaunchConfiguration config)
	{
		required = useSpecificButton.getSelection();
		return super.isValid(config);
	}
}
