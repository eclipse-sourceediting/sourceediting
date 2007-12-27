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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xsl.internal.debug.ui.ResourceSelectionBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.MainTabMessages;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;

public class OutputFileBlock extends ResourceSelectionBlock
{
	public static final String OUTPUT_METHOD_DEFAULT = "<Default>";
	public static final String OUTPUT_METHOD_XML = "xml";
	public static final String OUTPUT_METHOD_XHTML = "xhtml";
	public static final String OUTPUT_METHOD_HTML = "html";
	public static final String OUTPUT_METHOD_TEXT = "text";

	public static final String[] OUTPUT_METHODS = new String[]
	{ OUTPUT_METHOD_DEFAULT, OUTPUT_METHOD_XML, OUTPUT_METHOD_XHTML, OUTPUT_METHOD_HTML, OUTPUT_METHOD_TEXT };
	private Button openFileCheckButton;
	// private ComboViewer formatViewer;
	// private Combo formatCombo;
	private ComboViewer methodViewer;
	private String inputFilename;

	public OutputFileBlock()
	{
		super(IResource.FILE, true, true, false);
	}

	@Override
	protected String getMessage(int type)
	{
		switch (type)
		{
			case ERROR_DIRECTORY_NOT_SPECIFIED:
				return MainTabMessages.OutputFOFileBlock_DIRECTORY_NOT_SPECIFIED;
			case ERROR_DIRECTORY_DOES_NOT_EXIST:
				return MainTabMessages.OutputFOFileBlock_DIRECTORY_DOES_NOT_EXIST;
			case GROUP_NAME:
				return getName();
			case USE_DEFAULT_RADIO:
				return "Use default location";
			case USE_OTHER_RADIO:
				return MainTabMessages.OutputFOFileBlock_OTHER_RADIO;
			case DIRECTORY_DIALOG_MESSAGE:
				return MainTabMessages.OutputFOFileBlock_DIALOG_MESSAGE;
			case WORKSPACE_DIALOG_MESSAGE:
				return MainTabMessages.OutputFOFileBlock_WORKSPACE_DIALOG_MESSAGE;
			case VARIABLES_BUTTON:
				return MainTabMessages.OutputFOFileBlock_VARIABLES_BUTTON;
			case FILE_SYSTEM_BUTTON:
				return MainTabMessages.OutputFOFileBlock_FILE_SYSTEM_BUTTON;
			case WORKSPACE_BUTTON:
				return MainTabMessages.OutputFOFileBlock_WORKSPACE_BUTTON;
			case WORKSPACE_DIALOG_TITLE:
				return MainTabMessages.OutputFOFileBlock_WORKSPACE_DIALOG_TITLE;
		}
		return "" + type;
	}

	@Override
	protected void setDefaultResource()
	{}

	@Override
	protected void createContents(Composite parent)
	{
		// Composite comp = new Composite(parent,SWT.NONE);
		// GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		// gd.horizontalSpan = 2;
		// comp.setLayoutData(gd);
		// GridLayout layout = new GridLayout(2,true);
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		// comp.setLayout(layout);

		// createMethodCombo(comp);
		// createFormatCombo(comp);

		// methodViewer.setInput("");
		// formatViewer.setInput("");

		// methodViewer.setSelection(new StructuredSelection("xml"), true);
		// formatViewer.setSelection(new StructuredSelection("<none>"), true);

		fileLabel = "Location:";
		createCheckboxAndText(parent);
		createButtons(parent);
	}

	@Override
	protected void createButtons(Composite parent)
	{
		openFileCheckButton = createCheckButton(parent, "Open file on completion");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		openFileCheckButton.setLayoutData(gd);
		openFileCheckButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				updateLaunchConfigurationDialog();
			}
		});

		Composite buttonComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 1;
		buttonComp.setLayoutData(gd);
		buttonComp.setFont(parent.getFont());

		fWorkspaceButton = createPushButton(buttonComp, getMessage(WORKSPACE_BUTTON), null);
		fWorkspaceButton.addSelectionListener(widgetListener);

		fFileSystemButton = createPushButton(buttonComp, getMessage(FILE_SYSTEM_BUTTON), null);
		fFileSystemButton.addSelectionListener(widgetListener);

		fVariablesButton = createPushButton(buttonComp, getMessage(VARIABLES_BUTTON), null);
		fVariablesButton.addSelectionListener(widgetListener);
	}

	public String getName()
	{
		return "Output File";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			String outputMethod = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_METHOD, OUTPUT_METHODS[0]);
			// methodViewer.setSelection(new StructuredSelection(outputMethod),
			// true);

			inputFilename = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, "");
			// String renderTo =
			// configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_RENDER_TO,
			// "");
			updateDefaultOutputFile();

			boolean useDefault = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
			useDefaultCheckButton.setSelection(useDefault);

			outputFile = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILE, defaultOutputFile);

			updateResourceText(useDefault);

			boolean openFileOnCompletion = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
			openFileCheckButton.setSelection(openFileOnCompletion);
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		boolean useDefault = useDefaultCheckButton.getSelection();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, useDefault);

		String outputFile = resourceText.getText();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILE, outputFile);

		// String outputMethod =
		// (String)((IStructuredSelection)methodViewer.getSelection()).getFirstElement();
		// outputMethod = outputMethod == OUTPUT_METHODS[0] ? null :
		// outputMethod;
		// configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_METHOD,
		// outputMethod);

		boolean openFileOnCompletion = openFileCheckButton.getSelection();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, openFileOnCompletion);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILE, "");
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_METHOD, (String) null);
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
	}

	public void updateDefaultOutputFile()
	{
		String method = OUTPUT_METHOD_XML;
		if (methodViewer != null)
			method = (String) ((IStructuredSelection) methodViewer.getSelection()).getFirstElement();
		if (OUTPUT_METHOD_DEFAULT.equals(method))
			method = OUTPUT_METHOD_XML;

		String file = inputFilename;
		int index = inputFilename.lastIndexOf('.');
		if (index != -1)
			file = inputFilename.substring(0, index);
		file += ".out." + method;
		if (inputFilename.trim().endsWith("}"))
			file += "}";

		defaultOutputFile = file;

		updateResourceText(openFileCheckButton.getSelection());
	}
}
