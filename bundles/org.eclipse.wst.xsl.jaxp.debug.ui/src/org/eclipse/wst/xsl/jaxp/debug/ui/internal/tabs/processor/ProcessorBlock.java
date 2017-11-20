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
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.preferences.ProcessorsPreferencePage;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class ProcessorBlock extends AbstractLaunchConfigurationTab
{
	private Button usePreferencesRadio;
	private Button alterPreferencesButton;
	private Button overridePreferencesRadio;
	private Combo runCombo;
	private Button installedProcessorsButton;
	private ComboViewer runComboViewer;
	private ComboViewer factoryComboViewer;
	private ITransformerFactory currentFactory;

	public ProcessorBlock()
	{
	}

	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Composite group = new Composite(parent, SWT.NULL);
		setControl(group);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setFont(font);
//		group.setText(ProcessorMessages.ProcessorBlock_0);

		usePreferencesRadio = new Button(group, SWT.RADIO);
		usePreferencesRadio.setText(ProcessorMessages.ProcessorBlock_1);
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
		alterPreferencesButton.setText(ProcessorMessages.ProcessorBlock_2);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint = 150;
		alterPreferencesButton.setLayoutData(gd);
		alterPreferencesButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IPreferencePage page = new ProcessorsPreferencePage();
				showPrefPage("org.eclipse.wst.xslt.launching.ui.preferences.ProcessorPreferencePage", page); //$NON-NLS-1$
			}
		});

		overridePreferencesRadio = new Button(group, SWT.RADIO);
		overridePreferencesRadio.setText(ProcessorMessages.ProcessorBlock_4);
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
				showPrefPage("org.eclipse.wst.xslt.launching.ui.preferences.ProcessorPreferencePage", page); //$NON-NLS-1$
			}
		});

		Composite settingsComp = new Composite(group, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 2;
		settingsComp.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		gd.horizontalSpan = 2;
		settingsComp.setLayoutData(gd);
		settingsComp.setFont(font);

		Label label = new Label(settingsComp, SWT.NONE);
		label.setText(ProcessorMessages.ProcessorBlock_6);
		runCombo = new Combo(settingsComp, SWT.READ_ONLY | SWT.SINGLE);
		runCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runComboViewer = new ComboViewer(runCombo);
		runComboViewer.setContentProvider(new ComboContentProvider());
		runComboViewer.setLabelProvider(new ComboLabelProvider());
		runComboViewer.setInput(JAXPRuntime.getProcessors());
		runComboViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (!sel.isEmpty())
				{
					IProcessorInstall processor = (IProcessorInstall) sel.getFirstElement();
					
					if (processor.getProcessorType().getTransformerFactories().length > 1)
						factoryComboViewer.getCombo().setVisible(true);
					else
						factoryComboViewer.getCombo().setVisible(false);

					factoryComboViewer.setInput(processor.getProcessorType());
					
					boolean found = false;
					for (ITransformerFactory tf : processor.getProcessorType().getTransformerFactories())
					{
						if (tf.equals(currentFactory))
						{
							found = true;
							break;
						}
					}
					if (!found)
					{
						currentFactory = processor.getProcessorType().getDefaultTransformerFactory();
						if (currentFactory!=null)
							factoryComboViewer.setSelection(new StructuredSelection(currentFactory));
					}
				}
				updateLaunchConfigurationDialog();
			}
		});

		Combo factoryCombo = new Combo(settingsComp, SWT.READ_ONLY | SWT.SINGLE);
		factoryCombo.setLayoutData(new GridData(80,SWT.DEFAULT));
		factoryComboViewer = new ComboViewer(factoryCombo);
		factoryComboViewer.setContentProvider(new IStructuredContentProvider(){

			private IProcessorType type;
			
			public Object[] getElements(Object inputElement)
			{
				return type.getTransformerFactories();
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				this.type = (IProcessorType)newInput;
			}
			
		});
		factoryComboViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element)
			{
				ITransformerFactory f = (ITransformerFactory)element;
				return f.getName(); // + " - " + f.getFactoryClass();
			}
		});
		factoryComboViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				currentFactory = (ITransformerFactory)((IStructuredSelection)event.getSelection()).getFirstElement();
				updateLaunchConfigurationDialog();
			}
		});

//		overridePreferencesButton = new Button(group,SWT.CHECK);
//		overridePreferencesButton.setText("Override preferences");
//		gd = new GridData(SWT.NONE,SWT.NONE,false,false);
//		overridePreferencesButton.setLayoutData(gd);
//		overridePreferencesButton.addSelectionListener(new SelectionListener(){
//			public void widgetDefaultSelected(SelectionEvent e)
//			{}
//			
//			public void widgetSelected(SelectionEvent e)
//			{
//				firePreferenceProcessorChanged(overridePreferencesButton.getSelection());
//			}
//		});
	}
	

	private void preferencesSelected()
	{
		alterPreferencesButton.setEnabled(true);
		installedProcessorsButton.setEnabled(false);
		runCombo.setEnabled(false);
		factoryComboViewer.getCombo().setEnabled(false);
		runComboViewer.setSelection(new StructuredSelection(getRunProcessorPreference()), true);
	}

	private void overrideSelected()
	{
		alterPreferencesButton.setEnabled(false);
		installedProcessorsButton.setEnabled(true);
		factoryComboViewer.getCombo().setEnabled(true);
		runCombo.setEnabled(true);
	}

	private void showPrefPage(String id, IPreferencePage page)
	{
		XSLDebugUIPlugin.showPreferencePage(id, page);
		// now refresh everything
		runComboViewer.setInput(JAXPRuntime.getProcessors());
		runComboViewer.setSelection(new StructuredSelection(getRunProcessorPreference()), true);
		// preferencesSelected();
	}

	public String getName()
	{
		return ProcessorMessages.ProcessorBlock_7;
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			boolean useDefaultProcessor = configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
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
				String runId = configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_PROCESSOR, (String) null);
				if (runId != null)
					runInstall = JAXPRuntime.getProcessor(runId);
				if (runInstall == null)
					runInstall = getRunProcessorPreference();
				runComboViewer.setSelection(new StructuredSelection(runInstall));
				

				String factoryId = configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_TRANSFORMER_FACTORY, (String) null);
				if (factoryId == null)
				{
					currentFactory = runInstall.getProcessorType().getDefaultTransformerFactory();
				}
				else
				{
					for (ITransformerFactory tf : runInstall.getProcessorType().getTransformerFactories())
					{
						if (tf.getFactoryClass().equals(factoryId))
						{
							currentFactory = tf;
							break;
						}
					}
				}
				
				if (currentFactory == null)
				{
					currentFactory = runInstall.getProcessorType().getDefaultTransformerFactory();
				}
				if (currentFactory != null)
				{
					factoryComboViewer.setSelection(new StructuredSelection(currentFactory), true);
				}
			}
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	private IProcessorInstall getRunProcessorPreference()
	{
		return JAXPRuntime.getDefaultProcessor();
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		boolean usePreferences = usePreferencesRadio.getSelection();
		configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, usePreferences);
		if (!usePreferences)
		{
			IProcessorInstall runprocessor = (IProcessorInstall) ((IStructuredSelection) runComboViewer.getSelection()).getFirstElement();
			configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_PROCESSOR, runprocessor.getId());
			configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_TRANSFORMER_FACTORY, currentFactory == null ? null : currentFactory.getFactoryClass());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
		configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_PROCESSOR, (String) null);
	}

	private static class ComboContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return JAXPRuntime.getProcessors();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	private static class ComboLabelProvider extends LabelProvider
	{
		@Override
		public String getText(Object element)
		{
			IProcessorInstall install = (IProcessorInstall) element;
			return install.getName();
		}
	}
}
