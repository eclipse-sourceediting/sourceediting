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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.preferences;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class ProcessorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	private InstalledProcessorsBlock processorsBlock;

	public ProcessorsPreferencePage()
	{
		super();
		// only used when page is shown programatically
		setTitle(Messages.ProcessorsPreferencePage_0);
		setDescription(Messages.ProcessorsPreferencePage_1);
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected Control createContents(Composite ancestor)
	{
		initializeDialogUnits(ancestor);

		noDefaultAndApplyButton();

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		processorsBlock = new InstalledProcessorsBlock();
		processorsBlock.createControl(ancestor);
		Control control = processorsBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp...

		initDefaultInstall();
		processorsBlock.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IProcessorInstall install = getCurrentDefaultProcessor();
				if (install == null)
				{
					setValid(false);
					setErrorMessage(Messages.ProcessorsPreferencePage_2);
				}
				else
				{
					setValid(true);
					setErrorMessage(null);
				}
			}
		});
		applyDialogFont(ancestor);
		return ancestor;
	}

	@Override
	public boolean performOk()
	{
		processorsBlock.saveColumnSettings();
		final boolean[] ok = new boolean[1];
		try
		{
			final IProcessorInstall[] installs = processorsBlock.getProcessors();
			final IProcessorInstall defaultProcessor = getCurrentDefaultProcessor();
			IRunnableWithProgress runnable = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					try
					{
						JAXPRuntime.saveProcessorPreferences(installs,defaultProcessor,monitor);
					}
					catch (CoreException e)
					{
						XSLDebugUIPlugin.log(e);
					}
					ok[0] = !monitor.isCanceled();
				}
			};
			XSLDebugUIPlugin.getDefault().getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		catch (InvocationTargetException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		catch (InterruptedException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		return ok[0];
	}

	private void initDefaultInstall()
	{
		IProcessorInstall realDefault = JAXPRuntime.getDefaultProcessor();
		if (realDefault != null)
		{
			IProcessorInstall[] installs = processorsBlock.getProcessors();
			for (IProcessorInstall fakeInstall : installs)
			{
				if (fakeInstall.getId().equals(realDefault.getId()))
				{
					verifyDefaultVM(fakeInstall);
					break;
				}
			}
		}
	}

	private void verifyDefaultVM(IProcessorInstall install)
	{
		if (install != null)
		{
			processorsBlock.setCheckedInstall(install);
		}
		else
		{
			processorsBlock.setCheckedInstall(null);
		}
	}

	private IProcessorInstall getCurrentDefaultProcessor()
	{
		return processorsBlock.getCheckedInstall();
	}

}
