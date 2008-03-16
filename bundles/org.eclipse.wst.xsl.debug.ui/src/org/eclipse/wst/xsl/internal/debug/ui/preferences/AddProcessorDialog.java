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
package org.eclipse.wst.xsl.internal.debug.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.InstallStandin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.ProcessorMessages;
import org.eclipse.wst.xsl.launching.IDebugger;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorJar;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class AddProcessorDialog extends StatusDialog
{
	private final InstallStandin standinProcessor;
	private final IProcessorType[] processorTypes;
	private IProcessorType selectedProcessorType;
	private ComboViewer processorTypeField;
	private ProcessorLibraryBlock fLibraryBlock;
	private Text processorNameField;
	private final IStatus[] fStati;
	private int fPrevIndex = -1;
	private final InstalledProcessorsBlock block;
	private boolean adding;
	private Button supportsVerson10Checkbox;
	private Button supportsVerson20Checkbox;

	public AddProcessorDialog(InstalledProcessorsBlock block, Shell parent, IProcessorType[] types, IProcessorInstall install)
	{
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.block = block;
		fStati = new IStatus[5];
		for (int i = 0; i < fStati.length; i++)
		{
			fStati[i] = Status.OK_STATUS;
		}
		processorTypes = types;
		selectedProcessorType = install != null ? install.getProcessorType() : types[0];
		InstallStandin standin = null;
		if (install == null)
		{
			adding = true;
			standin = new InstallStandin(XSLTRuntime.createUniqueProcessorId(selectedProcessorType), "", selectedProcessorType.getId(), null, new IProcessorJar[0]); //$NON-NLS-1$
		}
		else
		{
			standin = new InstallStandin(install);
		}
		standinProcessor = standin;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp...
	}

	protected void createDialogFields(Composite parent)
	{
		GridData gd;
		Label label;

		label = new Label(parent, SWT.NONE);
		label.setText(ProcessorMessages.AddProcessorDialog_processorName);
		processorNameField = new Text(parent, SWT.BORDER);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = convertWidthInCharsToPixels(50);
		gd.horizontalSpan = 2;
		processorNameField.setLayoutData(gd);

		label = new Label(parent, SWT.NONE);
		label.setText(ProcessorMessages.AddProcessorDialog_processorType);
		processorTypeField = new ComboViewer(parent, SWT.READ_ONLY);
		gd = new GridData();
		gd.horizontalSpan = 2;
		processorTypeField.getCombo().setLayoutData(gd);
		processorTypeField.setContentProvider(new IStructuredContentProvider()
		{

			private Object input;

			public Object[] getElements(Object inputElement)
			{
				return (IProcessorType[]) input;
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				input = newInput;
			}
		});
		processorTypeField.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ((IProcessorType) element).getLabel();
			}
		});

		label = new Label(parent, SWT.NONE);
		label.setText(ProcessorMessages.AddProcessorDialog_1);
		supportsVerson10Checkbox = new Button(parent, SWT.CHECK);
		gd = new GridData();
		gd.horizontalSpan = 2;
		supportsVerson10Checkbox.setLayoutData(gd);
		supportsVerson10Checkbox.setText("XSLT 1.0"); //$NON-NLS-1$

		label = new Label(parent, SWT.NONE);
		supportsVerson20Checkbox = new Button(parent, SWT.CHECK);
		gd = new GridData();
		gd.horizontalSpan = 2;
		supportsVerson20Checkbox.setLayoutData(gd);
		supportsVerson20Checkbox.setText("XSLT 2.0"); //$NON-NLS-1$
	}

	protected void createFieldListeners()
	{
		processorTypeField.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateProcessorType();
			}
		});

		processorNameField.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				setProcessorNameStatus(validateProcessorName());
				updateStatusLine();
			}
		});

		supportsVerson10Checkbox.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				setVersionStatus(validateVersionStatus());
				updateStatusLine();
			}
		});

		supportsVerson20Checkbox.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				setVersionStatus(validateVersionStatus());
				updateStatusLine();
			}
		});

	}

	protected String getProcessorName()
	{
		return processorNameField.getText();
	}

	@Override
	protected Control createDialogArea(Composite ancestor)
	{
		Composite parent = (Composite) super.createDialogArea(ancestor);
		((GridLayout) parent.getLayout()).numColumns = 3;
//		((GridData) parent.getLayoutData()).heightHint = 400;
//		((GridData) parent.getLayoutData()).widthHint = 400;

		createDialogFields(parent);

		Label l = new Label(parent, SWT.NONE);
		l.setText(ProcessorMessages.AddProcessorDialog_jars);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		l.setLayoutData(gd);

		fLibraryBlock = new ProcessorLibraryBlock(this);
		Control block = fLibraryBlock.createControl(parent);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		block.setLayoutData(gd);

		initializeFields();
		createFieldListeners();
		applyDialogFont(parent);
		
		return parent;
	}

	private void updateProcessorType()
	{
		int selIndex = processorTypeField.getCombo().getSelectionIndex();
		if (selIndex == fPrevIndex)
		{
			return;
		}
		fPrevIndex = selIndex;
		if (selIndex >= 0 && selIndex < processorTypes.length)
		{
			selectedProcessorType = processorTypes[selIndex];
		}
		fLibraryBlock.initializeFrom(standinProcessor, selectedProcessorType);
		updateStatusLine();
	}

	@Override
	public void create()
	{
		super.create();
		processorNameField.setFocus();
	}

	private void initializeFields()
	{
		processorTypeField.setInput(processorTypes);
		if (adding)
		{
			processorNameField.setText(""); //$NON-NLS-1$
			processorTypeField.setSelection(new StructuredSelection(processorTypes[0]));
			supportsVerson10Checkbox.setSelection(true);
			fLibraryBlock.initializeFrom(standinProcessor, selectedProcessorType);
		}
		else
		{
			processorTypeField.getCombo().setEnabled(false);
			processorTypeField.setSelection(new StructuredSelection(standinProcessor.getProcessorType()));
			processorNameField.setText(standinProcessor.getName());

			supportsVerson10Checkbox.setSelection(standinProcessor.supports("1.0")); //$NON-NLS-1$
			supportsVerson20Checkbox.setSelection(standinProcessor.supports("2.0")); //$NON-NLS-1$
			fLibraryBlock.initializeFrom(standinProcessor, selectedProcessorType);
		}
		setProcessorNameStatus(validateProcessorName());
		updateStatusLine();
	}

	private IStatus validateProcessorName()
	{
		IStatus status = Status.OK_STATUS;
		String name = processorNameField.getText();
		if (name == null || name.trim().length() == 0)
		{
			status = new Status(IStatus.INFO, XSLDebugUIPlugin.PLUGIN_ID, IStatus.OK, ProcessorMessages.AddProcessorDialog_enterName, null);
		}
		else
		{
			if (block.isDuplicateName(name) && (standinProcessor == null || !name.equals(standinProcessor.getName())))
			{
				status = new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, IStatus.OK, ProcessorMessages.AddProcessorDialog_duplicateName, null);
			}
			else
			{
				// IStatus s = ResourcesPlugin.getWorkspace().validateName(name,
				// IResource.FILE);
				// if (!s.isOK())
				// {
				// status.setError(MessageFormat.format(ProcessorMessages.AddProcessorDialog_Processor_name_must_be_a_valid_file_name,
				// new String[]
				// { s.getMessage() }));
				// }
			}
		}
		return status;
	}

	protected IStatus validateVersionStatus()
	{
		IStatus status = Status.OK_STATUS;
		if (!supportsVerson10Checkbox.getSelection() && !supportsVerson20Checkbox.getSelection())
			status = new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, IStatus.OK, ProcessorMessages.AddProcessorDialog_7, null);
		return status;
	}

	protected void updateStatusLine()
	{
		IStatus max = null;
		for (IStatus curr : fStati)
		{
			if (curr.matches(IStatus.ERROR))
			{
				updateStatus(curr);
				return;
			}
			if (max == null || curr.getSeverity() > max.getSeverity())
			{
				max = curr;
			}
		}
		updateStatus(max);
	}

	@Override
	protected void okPressed()
	{
		doOkPressed();
		super.okPressed();
	}

	private void doOkPressed()
	{
		if (adding)
		{
			setFieldValuesToProcessor(standinProcessor);
			block.processorAdded(standinProcessor);
		}
//		else
//			setFieldValuesToProcessor((ProcessorInstall)editedProcessor);
	}

	protected void setFieldValuesToProcessor(InstallStandin processor)
	{
		processor.setName(processorNameField.getText());
		processor.setProcessorTypeId(selectedProcessorType.getId());

		IDebugger[] debuggers = XSLTRuntime.getDebuggers();
		for (IDebugger element : debuggers)
		{
			if (element.getProcessorType().equals(selectedProcessorType))
				processor.setDebuggerId(element.getId());
		}

		String supports = ""; //$NON-NLS-1$
		if (supportsVerson10Checkbox.getSelection())
		{
			supports += "1.0"; //$NON-NLS-1$
			if (supportsVerson20Checkbox.getSelection())
				supports += ","; //$NON-NLS-1$
		}
		if (supportsVerson20Checkbox.getSelection())
			supports += "2.0"; //$NON-NLS-1$
		processor.setSupports(supports);
		fLibraryBlock.performApply(processor);
	}

	protected File getAbsoluteFileOrEmpty(String path)
	{
		if (path == null || path.length() == 0)
		{
			return new File(""); //$NON-NLS-1$
		}
		return new File(path).getAbsoluteFile();
	}

	private void setProcessorNameStatus(IStatus status)
	{
		fStati[0] = status;
	}

	protected IStatus getSystemLibraryStatus()
	{
		return fStati[3];
	}

	protected void setSystemLibraryStatus(IStatus status)
	{
		fStati[3] = status;
	}

	protected void setVersionStatus(IStatus status)
	{
		fStati[2] = status;
	}

	@Override
	protected void updateButtonsEnableState(IStatus status)
	{
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null && !ok.isDisposed())
			ok.setEnabled(status.getSeverity() == IStatus.OK);
	}

	@Override
	protected void setButtonLayoutData(Button button)
	{
		super.setButtonLayoutData(button);
	}

	protected String getDialogSettingsSectionName()
	{
		return "ADD_PROCESSOR_DIALOG_SECTION"; //$NON-NLS-1$
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings()
	{
//		IDialogSettings settings = XSLDebugUIPlugin.getDefault().getDialogSettings();
//		IDialogSettings section = settings.getSection(getDialogSettingsSectionName());
//		if (section == null)
//		{
//			section = settings.addNewSection(getDialogSettingsSectionName());
//		}
//		return section;
		return null;
	}
}
