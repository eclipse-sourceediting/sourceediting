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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.processor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.xsl.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.preferences.ProcessorsPreferencePage;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class ProcessorBlock extends AbstractLaunchConfigurationTab
{
	private Button usePreferencesRadio;
	private Button alterPreferencesButton;
	private Button overridePreferencesRadio;
	private Combo runCombo;
	private Button installedProcessorsButton;
	private ComboViewer runComboViewer;
	private final FeaturesBlock featuresBlock;

	public ProcessorBlock(FeaturesBlock featuresBlock)
	{
		this.featuresBlock = featuresBlock;
	}

	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Group group = new Group(parent, SWT.NULL);
		setControl(group);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setFont(font);
		group.setText("XSLT Processor");

		usePreferencesRadio = new Button(group, SWT.RADIO);
		usePreferencesRadio.setText("Use processor from preferences");
		usePreferencesRadio.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				preferencesSelected();
				updateLaunchConfigurationDialog();
			}
		});

		alterPreferencesButton = new Button(group, SWT.PUSH);
		alterPreferencesButton.setText("Change preferences...");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint = 150;
		alterPreferencesButton.setLayoutData(gd);
		alterPreferencesButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IPreferencePage page = new ProcessorsPreferencePage();
				showPrefPage("org.eclipse.wst.xslt.launching.ui.preferences.ProcessorPreferencePage", page);
			}
		});

		overridePreferencesRadio = new Button(group, SWT.RADIO);
		overridePreferencesRadio.setText("Use specific processor");
		overridePreferencesRadio.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				overrideSelected();
				updateLaunchConfigurationDialog();
			}
		});

		installedProcessorsButton = new Button(group, SWT.PUSH);
		installedProcessorsButton.setText(ProcessorMessages.ProcessorsComboBlock_1);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint = 150;
		installedProcessorsButton.setLayoutData(gd);
		installedProcessorsButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IPreferencePage page = new ProcessorsPreferencePage();
				showPrefPage("org.eclipse.wst.xslt.launching.ui.preferences.ProcessorPreferencePage", page);
			}
		});

		Composite settingsComp = new Composite(group, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		gl.marginHeight = 2;
		settingsComp.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		gd.horizontalSpan = 2;
		settingsComp.setLayoutData(gd);
		settingsComp.setFont(font);

		Label label = new Label(settingsComp, SWT.NONE);
		label.setText("Processor:");
		runCombo = new Combo(settingsComp, SWT.READ_ONLY | SWT.SINGLE);
		runCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runComboViewer = new ComboViewer(runCombo);
		runComboViewer.setContentProvider(new ComboContentProvider());
		runComboViewer.setLabelProvider(new ComboLabelProvider());
		runComboViewer.setInput(XSLTRuntime.getProcessors());
		runComboViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (!sel.isEmpty())
				{
					IProcessorInstall processor = (IProcessorInstall) sel.getFirstElement();
					featuresBlock.setProcessorType(processor.getProcessorType());
				}
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void preferencesSelected()
	{
		alterPreferencesButton.setEnabled(true);
		installedProcessorsButton.setEnabled(false);
		runCombo.setEnabled(false);
		runComboViewer.setSelection(new StructuredSelection(getRunProcessorPreference()), true);
	}

	private void overrideSelected()
	{
		alterPreferencesButton.setEnabled(false);
		installedProcessorsButton.setEnabled(true);
		runCombo.setEnabled(true);
	}

	private void showPrefPage(String id, IPreferencePage page)
	{
		XSLDebugUIPlugin.showPreferencePage(id, page);
		// now refresh everything
		runComboViewer.refresh();
		runComboViewer.setSelection(new StructuredSelection(getRunProcessorPreference()), true);
		// preferencesSelected();
	}

	public String getName()
	{
		return "Processor";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			boolean useDefaultProcessor = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
			if (useDefaultProcessor)
			{
				usePreferencesRadio.setSelection(true);
				overridePreferencesRadio.setSelection(false);
				preferencesSelected();
			}
			else
			{
				usePreferencesRadio.setSelection(false);
				overridePreferencesRadio.setSelection(true);
				overrideSelected();

				IProcessorInstall runInstall = null;
				String runId = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, (String) null);
				if (runId != null)
					runInstall = XSLTRuntime.getProcessor(runId);
				if (runInstall == null)
					runInstall = getRunProcessorPreference();
				runComboViewer.setSelection(new StructuredSelection(runInstall));

				// IDebugger debugInstall = null;
				// String debugId =
				// configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR_FOR_DEBUG,
				// (String)null);
				// if (debugId != null)
				// debugInstall = XSLTRuntime.getDebugger(debugId);
				// if (debugInstall == null)
				// debugInstall = runInstall.getDebugger();
				// if (debugInstall == null)
				// debugInstall = getDebugProcessorPreference();
				// debugComboViewer.setSelection(new
				// StructuredSelection(debugInstall));
			}
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	private IProcessorInstall getRunProcessorPreference()
	{
		return XSLTRuntime.getDefaultProcessor();
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		boolean usePreferences = usePreferencesRadio.getSelection();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, usePreferences);
		if (!usePreferences)
		{
			IProcessorInstall runprocessor = (IProcessorInstall) ((IStructuredSelection) runComboViewer.getSelection()).getFirstElement();
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, runprocessor.getId());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, (String) null);
	}

	private class ComboContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return XSLTRuntime.getProcessors();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	private class ComboLabelProvider extends LabelProvider
	{
		@Override
		public String getText(Object element)
		{
			IProcessorInstall install = (IProcessorInstall) element;
			return install.getLabel();
		}
	}
}
