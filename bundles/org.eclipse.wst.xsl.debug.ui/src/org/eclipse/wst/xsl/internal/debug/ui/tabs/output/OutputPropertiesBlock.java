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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.preferences.OutputPreferencePage;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.XSLMainTab;
import org.eclipse.wst.xsl.launching.IOutputProperty;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;
import org.eclipse.wst.xsl.launching.config.LaunchProperties;

public class OutputPropertiesBlock extends AbstractTableBlock
{
	private Table table;
	private TableViewer tViewer;
	private Text descriptionText;
	private IProcessorType processorType;
	private final Map typeProperties = new HashMap();
	private Button usePropertiesFromPreferencesRadio;
	private Button changePreferences;
	private Button useSpecificFeaturesRadio;
	private LaunchProperties launchFeatures;

	public OutputPropertiesBlock(XSLMainTab main)
	{
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier()
	{
		return XSLDebugUIConstants.OUTPUT_BLOCK;
	}

	@Override
	protected Table getTable()
	{
		return table;
	}

	public void createControl(Composite parent)
	{
		usePropertiesFromPreferencesRadio = new Button(parent, SWT.RADIO);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		usePropertiesFromPreferencesRadio.setLayoutData(gd);
		usePropertiesFromPreferencesRadio.setText("Use properties from preferences");
		usePropertiesFromPreferencesRadio.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				handleUsePropertiesFromPreferences(true);
				updateLaunchConfigurationDialog();
			}
		});

		changePreferences = new Button(parent, SWT.PUSH);
		changePreferences.setText("Change Preferences...");
		gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		changePreferences.setLayoutData(gd);
		changePreferences.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IPreferencePage page = new OutputPreferencePage();
				XSLDebugUIPlugin.showPreferencePage("org.eclipse.wst.xsl.debug.ui.output", page);
				handleUsePropertiesFromPreferences(true);
			}
		});

		useSpecificFeaturesRadio = new Button(parent, SWT.RADIO);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		useSpecificFeaturesRadio.setLayoutData(gd);
		useSpecificFeaturesRadio.setText("Use specific properties");
		useSpecificFeaturesRadio.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				handleUsePropertiesFromPreferences(false);
				updateLaunchConfigurationDialog();
			}
		});

		// filler
		new Label(parent, SWT.NONE);

		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		table.setLayoutData(gd);

		setControl(table);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn c1 = new TableColumn(table, SWT.NONE);
		c1.setWidth(150);
		c1.setResizable(true);
		c1.setText("Property");

		TableColumn c2 = new TableColumn(table, SWT.NONE);
		c2.setWidth(250);
		c2.setResizable(true);
		c2.setText("Value");

		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{

			public Object[] getElements(Object inputElement)
			{
				return launchFeatures.getProperties().keySet().toArray(new String[0]);
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{

			}
		});
		tViewer.setLabelProvider(new ITableLabelProvider()
		{

			public Image getColumnImage(Object element, int columnIndex)
			{
				return null;
			}

			public String getColumnText(Object element, int columnIndex)
			{
				String prop = (String) element;
				switch (columnIndex)
				{
					case 0:
						return prop;
					case 1:
						return launchFeatures.getProperty(prop);
				}
				return "!!";
			}

			public void addListener(ILabelProviderListener listener)
			{
			}

			public void dispose()
			{
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void removeListener(ILabelProviderListener listener)
			{
			}

		});
		tViewer.setColumnProperties(new String[]
		{ "property", "value" });
		tViewer.setCellModifier(new ICellModifier()
		{
			public boolean canModify(Object element, String property)
			{
				if (!"value".equals(property))
					return false;
				return true;
			}

			public Object getValue(Object element, String property)
			{
				String prop = (String) element;
				String value = launchFeatures.getProperty(prop);
				return value == null ? "" : value;
			}

			public void modify(Object element, String property, Object value)
			{
				Item item = (Item) element;
				String prop = (String) item.getData();
				if (value == null || "".equals(value))
					launchFeatures.removeProperty(prop);
				else
					launchFeatures.setProperty(prop, (String) value);
				tViewer.update(prop, null);
				updateLaunchConfigurationDialog();
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				String prop1 = (String) e1;
				String prop2 = (String) e2;
				return prop1.compareTo(prop2);
			}
		});
		tViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				String prop = (String) selection.getFirstElement();
				String text = null;
				if (prop != null)
				{
					IProcessorType[] types = XSLTRuntime.getProcessorTypes();
					outer: for (int i = 0; i < types.length; i++)
					{
						IProcessorType processorType = types[i];
						IOutputProperty[] property = processorType.getOutputProperties();
						for (int j = 0; j < property.length; j++)
						{
							IOutputProperty outputProperty = property[j];
							if (outputProperty.getKey().equals(prop))
							{
								text = outputProperty.getDescription();
								break outer;
							}
						}
					}
				}
				descriptionText.setText(text == null ? "" : text);
			}
		});

		TextCellEditor editor = new TextCellEditor(table);

		CellEditor[] editors = new CellEditor[]
		{ null, editor };
		tViewer.setCellEditors(editors);

		descriptionText = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 3;
		gd.heightHint = 50;
		descriptionText.setLayoutData(gd);

		// must come after input has been set
		restoreColumnSettings();
	}

	private void handleUsePropertiesFromPreferences(boolean selected)
	{
		changePreferences.setEnabled(selected);
		table.setEnabled(!selected);

		if (selected)
		{
			launchFeatures = initializeFeaturesFromPreferences();
		}
		else
		{
			launchFeatures = (LaunchProperties) typeProperties.get(processorType);

			if (launchFeatures == null)
			{// make a copy of the features for the processor type
				launchFeatures = new LaunchProperties();
				typeProperties.put(processorType, launchFeatures);
				for (int i = 0; i < processorType.getOutputProperties().length; i++)
				{
					IOutputProperty property = processorType.getOutputProperties()[i];
					launchFeatures.setProperty(property.getName(), null);
				}
			}
		}
		tViewer.setInput(launchFeatures);
	}

	public String getName()
	{
		return "Output Properties";
	}

	private LaunchProperties initializeFeaturesFromPreferences()
	{
		LaunchProperties preferences = new LaunchProperties();

		IProcessorType jreDefaultType = XSLTRuntime.getProcessorType(XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID);
		IOutputProperty[] features = jreDefaultType.getOutputProperties();
		Map values = jreDefaultType.getOutputPropertyValues();
		for (IOutputProperty feature : features)
		{
			String key = feature.getKey();
			preferences.setProperty(key, (String) values.get(key));
		}

		features = processorType.getOutputProperties();
		values = processorType.getOutputPropertyValues();
		for (IOutputProperty feature : features)
		{
			String key = feature.getKey();
			preferences.setProperty(key, (String) values.get(key));
		}

		return preferences;
	}

	private void initializeFeaturesFromStorage(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchProperties launchFeatures = new LaunchProperties();

		IProcessorType jreDefaultType = XSLTRuntime.getProcessorType(XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID);
		IOutputProperty[] defaultProps = jreDefaultType.getOutputProperties();
		for (IOutputProperty feature : defaultProps)
		{
			String key = feature.getKey();
			launchFeatures.setProperty(key, null);
		}
		IOutputProperty[] specificProps = processorType.getOutputProperties();
		for (IOutputProperty feature : specificProps)
		{
			String key = feature.getKey();
			launchFeatures.setProperty(key, null);
		}

		String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, (String) null);
		if (s != null && s.length() > 0)
		{
			LaunchProperties overrideFeatures = LaunchProperties.fromXML(new ByteArrayInputStream(s.getBytes()));
			IOutputProperty[] props = new IOutputProperty[specificProps.length + defaultProps.length];
			System.arraycopy(specificProps, 0, props, 0, specificProps.length);
			System.arraycopy(defaultProps, 0, props, specificProps.length, defaultProps.length);
			for (Iterator iterator = overrideFeatures.getProperties().keySet().iterator(); iterator.hasNext();)
			{
				String key = (String) iterator.next();
				launchFeatures.removeProperty(key);
				for (IOutputProperty prop : props)
				{
					if (prop.getKey().equals(key))
					{
						launchFeatures.setProperty(key, overrideFeatures.getProperty(key));
						break;
					}
				}
			}
		}

		typeProperties.put(processorType, launchFeatures);
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			String processorId = null;
			boolean useDefaultProcessor = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
			if (useDefaultProcessor)
				processorId = XSLTRuntime.getDefaultProcessor().getId();
			else
				processorId = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, XSLTRuntime.JRE_DEFAULT_PROCESSOR_ID);

			IProcessorInstall processor = XSLTRuntime.getProcessor(processorId);
			if (processor != null)
				processorType = processor.getProcessorType();
			else
				processorType = XSLTRuntime.getDefaultProcessor().getProcessorType();

			initializeFeaturesFromStorage(configuration);

			boolean useFeaturesFromPreferences = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_PROPERTIES_FROM_PREFERENCES, true);

			useSpecificFeaturesRadio.setSelection(!useFeaturesFromPreferences);
			usePropertiesFromPreferencesRadio.setSelection(useFeaturesFromPreferences);
			handleUsePropertiesFromPreferences(useFeaturesFromPreferences);
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_PROPERTIES_FROM_PREFERENCES, usePropertiesFromPreferencesRadio.getSelection());
		String xml;
		try
		{
			xml = launchFeatures.toXML();
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, xml);
		}
		catch (Exception e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_PROPERTIES_FROM_PREFERENCES, true);
	}
}
