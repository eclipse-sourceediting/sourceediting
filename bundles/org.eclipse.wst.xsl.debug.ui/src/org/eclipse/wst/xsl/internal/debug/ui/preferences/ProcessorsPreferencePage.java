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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.ProcessorMessages;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorJar;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class ProcessorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	private InstalledProcessorsBlock processorsBlock;

	public ProcessorsPreferencePage()
	{
		super();
		// only used when page is shown programatically
		setTitle("Java XSLT Processors");
		setDescription("Add, remove or edit XSLT processor definitions.\nBy default, the checked Processor is used for all transformations.");
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
					setErrorMessage("Select a default XSLT Processor");
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
		final boolean[] canceled = new boolean[]
		{ false };
		BusyIndicator.showWhile(null, new Runnable()
		{
			public void run()
			{
				IProcessorInstall defaultInstall = getCurrentDefaultProcessor();
				IProcessorInstall[] installs = processorsBlock.getProcessors();
				ProcessorsUpdater updater = new ProcessorsUpdater();
				if (!updater.updateProcessorSettings(installs, defaultInstall))
				{
					canceled[0] = true;
				}
			}
		});

		if (canceled[0])
		{
			return false;
		}

		IProcessorInstall[] standins = processorsBlock.getProcessors();
		// add the added processors:
		for (IProcessorInstall standin : standins)
		{
			if (getEquivalent(XSLTRuntime.getProcessors(), standin) == null)
				XSLTRuntime.getProcessorRegistry().addProcessor(standin);
		}

		// remove the removed processors + update the updated ones:
		for (int i = 0; i < XSLTRuntime.getProcessors().length; i++)
		{
			IProcessorInstall install = XSLTRuntime.getProcessors()[i];
			if (getEquivalent(standins, install) == null)
				XSLTRuntime.getProcessorRegistry().removeProcessor(i);
			else
			{
				IProcessorInstall standin = getEquivalent(standins, install);
				if (!standin.getLabel().equals(install.getLabel()))
					install.setLabel(standin.getLabel());
				if (!standin.getProcessorTypeId().equals(install.getProcessorTypeId()))
					install.setProcessorTypeId(standin.getProcessorTypeId());

				install.setDebuggerId(standin.hasDebugger() ? standin.getDebugger().getId() : null);
				install.setSupports(standin.getSupports());

				if (standin.getProcessorJars().length != install.getProcessorJars().length)
					install.setProcessorJars(standin.getProcessorJars());
				else
				{
					for (int j = 0; j < standin.getProcessorJars().length; j++)
					{
						IProcessorJar standinjar = standin.getProcessorJars()[j];
						IProcessorJar realjar = getEquivalent(install.getProcessorJars(), standinjar);
						if (realjar == null)
						{
							install.setProcessorJars(standin.getProcessorJars());
							break;
						}
					}
				}
			}
		}

		// set the new default if it changed:
		IProcessorInstall defaultInstallStandin = getCurrentDefaultProcessor();
		IProcessorInstall realDefault = null;
		for (int i = 0; i < XSLTRuntime.getProcessors().length; i++)
		{
			IProcessorInstall install = XSLTRuntime.getProcessors()[i];
			if (install.getId().equals(defaultInstallStandin.getId()))
			{
				realDefault = install;
				break;
			}
		}
		XSLTRuntime.getProcessorRegistry().setDefaultProcessor(realDefault);

		// save column widths
		processorsBlock.saveColumnSettings();

		return super.performOk();
	}

	private IProcessorInstall getEquivalent(IProcessorInstall[] installs, IProcessorInstall standin)
	{
		for (IProcessorInstall install : installs)
		{
			if (install.getId().equals(standin.getId()))
				return install;
		}
		return null;
	}

	private IProcessorJar getEquivalent(IProcessorJar[] jars, IProcessorJar standinJar)
	{
		for (IProcessorJar jar : jars)
		{
			if (jar.getPath().equals(standinJar.getPath()))
				return jar;
		}
		return null;
	}

	private void initDefaultInstall()
	{
		IProcessorInstall realDefault = XSLTRuntime.getDefaultProcessor();
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
