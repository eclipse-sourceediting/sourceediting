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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;

public class ProcessorDetailsDialog extends Dialog
{
	private final IProcessorInstall install;

	public ProcessorDetailsDialog(Shell shell, IProcessorInstall install)
	{
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.install = install;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(ProcessorMessages.ProcessorDetailsDialog_Title);
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp...
	}

	@Override
	protected Control createDialogArea(Composite ancestor)
	{
		Composite parent = (Composite) super.createDialogArea(ancestor);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		// type
		createLabel(parent, ProcessorMessages.ProcessorDetailsDialog_installType);
		createLabel(parent, install.getProcessorType().getLabel());

		// name
		createLabel(parent, ProcessorMessages.ProcessorDetailsDialog_installName);
		createLabel(parent, install.getName());

		// jars
		Label label = createLabel(parent, ProcessorMessages.ProcessorDetailsDialog_installClasspath);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING);
		label.setLayoutData(gd);
		TableViewer libraryViewer = new TableViewer(parent);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 6;
		libraryViewer.getControl().setLayoutData(gd);
		libraryViewer.setContentProvider(new JarContentProvider());
		libraryViewer.setLabelProvider(new JarLabelProvider());
		libraryViewer.setInput(install);

		applyDialogFont(parent);
		return parent;
	}

	private Label createLabel(Composite parent, String text)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	/**
	 * Returns the name of the section that this dialog stores its settings in
	 * 
	 * @return String
	 */
	protected String getDialogSettingsSectionName()
	{
		return "XSL_DETAILS_DIALOG_SECTION"; //$NON-NLS-1$
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings()
	{
		IDialogSettings settings = XSLDebugUIPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(getDialogSettingsSectionName());
		if (section == null)
		{
			section = settings.addNewSection(getDialogSettingsSectionName());
		}
		return section;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

}
