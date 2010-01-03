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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.ResourceSelectionBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class OutputFileBlock extends ResourceSelectionBlock {
	private Button openFileCheckButton;
	private Button formatFileCheckButton;
	private String inputFilename;
	private Text fileNameText;
	private String defaultOutputFileName;
	private String outputFileName;

	public OutputFileBlock() {
		super(IResource.FOLDER, true, true, false);
	}

	@Override
	protected String getMessage(int type) {
		switch (type) {
		case ERROR_DIRECTORY_NOT_SPECIFIED:
			return Messages.OutputFOFileBlock_DIRECTORY_NOT_SPECIFIED;
		case ERROR_DIRECTORY_DOES_NOT_EXIST:
			return Messages.OutputFOFileBlock_DIRECTORY_DOES_NOT_EXIST;
		case GROUP_NAME:
			return getName();
		case USE_DEFAULT_RADIO:
			return Messages.OutputFileBlock_0;
		case USE_OTHER_RADIO:
			return Messages.OutputFOFileBlock_OTHER_RADIO;
		case DIRECTORY_DIALOG_MESSAGE:
			return Messages.OutputFOFileBlock_DIALOG_MESSAGE;
		case WORKSPACE_DIALOG_MESSAGE:
			return Messages.OutputFOFileBlock_WORKSPACE_DIALOG_MESSAGE;
		case VARIABLES_BUTTON:
			return Messages.OutputFOFileBlock_VARIABLES_BUTTON;
		case FILE_SYSTEM_BUTTON:
			return Messages.OutputFOFileBlock_FILE_SYSTEM_BUTTON;
		case WORKSPACE_BUTTON:
			return Messages.OutputFOFileBlock_WORKSPACE_BUTTON;
		case WORKSPACE_DIALOG_TITLE:
			return Messages.OutputFOFileBlock_WORKSPACE_DIALOG_TITLE;
		}
		return "" + type; //$NON-NLS-1$
	}

	@Override
	protected void createCheckboxAndText(Composite parent) {
		if (showDefault) {
			useDefaultCheckButton = createCheckButton(parent,
					getMessage(USE_DEFAULT_RADIO));
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 2;
			useDefaultCheckButton.setLayoutData(gd);
			useDefaultCheckButton.addSelectionListener(widgetListener);
		}

		Composite specificFileComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		if (showDefault)
			layout.marginLeft = 20;
		else
			layout.marginLeft = 0;
		layout.marginHeight = 0;
		specificFileComp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		specificFileComp.setLayoutData(gd);

		Label label = new Label(specificFileComp, SWT.NONE);
		label.setText(Messages.OutputFileBlock_1);

		fileNameText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = showDefault ? 1 : 2;
		fileNameText.setLayoutData(gd);
		fileNameText.setFont(parent.getFont());
		fileNameText.addModifyListener(widgetListener);

		if (showDefault) {
			label = new Label(specificFileComp, SWT.NONE);
			label.setText(Messages.OutputFileBlock_7);
		}

		resourceText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = showDefault ? 1 : 2;
		resourceText.setLayoutData(gd);
		resourceText.setFont(parent.getFont());
		resourceText.addModifyListener(widgetListener);
	}

	@Override
	protected void createButtons(Composite parent) {
		Composite checkComposite = new Composite(parent, SWT.NONE);
		checkComposite.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING));
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		checkComposite.setLayout(gl);

		openFileCheckButton = createCheckButton(checkComposite,
				Messages.OutputFileBlock_8);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		openFileCheckButton.setLayoutData(gd);
		openFileCheckButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (openFileCheckButton.getSelection()) {
					formatFileCheckButton.setEnabled(true);
					updateLaunchConfigurationDialog();
				} else {
					formatFileCheckButton.setEnabled(false);
					formatFileCheckButton.setSelection(false);
				}
			}
		});

		formatFileCheckButton = createCheckButton(checkComposite,
				Messages.OutputFileBlock_2);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		formatFileCheckButton.setLayoutData(gd);
		formatFileCheckButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		Composite buttonComp = new Composite(parent, SWT.TOP);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		buttonComp.setLayoutData(gd);
		buttonComp.setFont(parent.getFont());

		fWorkspaceButton = createPushButton(buttonComp,
				getMessage(WORKSPACE_BUTTON), null);
		fWorkspaceButton.addSelectionListener(widgetListener);

		fFileSystemButton = createPushButton(buttonComp,
				getMessage(FILE_SYSTEM_BUTTON), null);
		fFileSystemButton.addSelectionListener(widgetListener);

		fVariablesButton = createPushButton(buttonComp,
				getMessage(VARIABLES_BUTTON), null);
		fVariablesButton.addSelectionListener(widgetListener);
	}

	public String getName() {
		return Messages.OutputFileBlock_9;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			inputFilename = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, ""); //$NON-NLS-1$
			updateDefaultOutputFile();

			boolean useDefault = configuration
					.getAttribute(
							XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
							true);
			useDefaultCheckButton.setSelection(useDefault);

			outputFileName = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
					defaultOutputFileName);
			resource = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER,
					defaultResource);

			updateResourceText(useDefault);

			boolean openFileOnCompletion = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
			openFileCheckButton.setSelection(openFileOnCompletion);

			boolean formatFileOnCompletion = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_FORMAT_FILE, false);
			formatFileCheckButton.setSelection(formatFileOnCompletion);
		} catch (CoreException e) {
			XSLDebugUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		boolean useDefault = useDefaultCheckButton.getSelection();
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
				useDefault);

		String outputFile = resourceText.getText();
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER, outputFile);

		String outputFileName = fileNameText.getText();
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
				outputFileName);

		boolean openFileOnCompletion = openFileCheckButton.getSelection();
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OPEN_FILE,
				openFileOnCompletion);

		boolean formatFileOnCompletion = formatFileCheckButton.getSelection();
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_FORMAT_FILE,
				formatFileOnCompletion);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
				true);
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER,
				(String) null);
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
				(String) null);
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
		configuration.setAttribute(
				XSLLaunchConfigurationConstants.ATTR_FORMAT_FILE, false);
	}

	@Override
	protected void updateResourceText(boolean useDefault) {
		fileNameText.setEnabled(!useDefault);
		if (useDefault)
			fileNameText
					.setText(defaultOutputFileName == null ? "" : defaultOutputFileName); //$NON-NLS-1$
		else
			fileNameText.setText(outputFileName == null ? defaultOutputFileName
					: outputFileName);
		super.updateResourceText(useDefault);
	}

	private void updateDefaultOutputFile() {
		try {
			IPath path = XSLTRuntime
					.defaultOutputFileForInputFile(inputFilename);
			// determine whether this path exists in the workspace
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
					.findFilesForLocation(path);
			if (files.length > 0) {// inside workspace
				IPath p = new Path(files[0].getProject().getName());
				p.append(files[0].getParent().getProjectRelativePath());
				defaultResource = "${workspace_loc:/" + p.toString() + "}"; //$NON-NLS-1$//$NON-NLS-2$
			} else {// outside workspace
				IPath p = path.removeLastSegments(1);
				defaultResource = p.toOSString();
			}
			defaultOutputFileName = path.lastSegment();
		} catch (CoreException e) {
			// do nothing
		}
	}
}
