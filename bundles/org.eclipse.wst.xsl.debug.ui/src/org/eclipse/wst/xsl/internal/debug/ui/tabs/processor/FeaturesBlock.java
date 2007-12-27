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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.preferences.FeaturesPreferencePage;
import org.eclipse.wst.xsl.launching.IFeature;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;
import org.eclipse.wst.xsl.launching.config.LaunchFeatures;

public class FeaturesBlock extends AbstractTableBlock
{
	private Table table;
	private TableViewer tViewer;
	private Text descriptionText;
	private IProcessorType processorType;
	private Button useFeaturesFromPreferencesRadio;
	private LaunchFeatures launchFeatures;
	private LaunchFeatures featuresFromPreferences;
	private Button changePreferences;
	private final Map<IProcessorType, LaunchFeatures> processorFeatures = new HashMap<IProcessorType, LaunchFeatures>();
	private Button useSpecificFeaturesRadio;

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier()
	{
		return XSLDebugUIConstants.FEATURES_LAUNCH_BLOCK;
	}

	@Override
	protected Table getTable()
	{
		return table;
	}

	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Group group = new Group(parent, SWT.NONE);
		group.setText(getName());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setFont(font);

		setControl(group);

		useFeaturesFromPreferencesRadio = new Button(group, SWT.RADIO);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		useFeaturesFromPreferencesRadio.setLayoutData(gd);
		useFeaturesFromPreferencesRadio.setText("Use features from preferences");
		useFeaturesFromPreferencesRadio.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				handleUseFeaturesFromPreferences(true);
				updateLaunchConfigurationDialog();
			}
		});

		changePreferences = new Button(group, SWT.PUSH);
		changePreferences.setText("Change Preferences...");
		gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		changePreferences.setLayoutData(gd);
		changePreferences.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IPreferencePage page = new FeaturesPreferencePage();
				XSLDebugUIPlugin.showPreferencePage("org.eclipse.wst.xsl.debug.ui.page1", page);
				// now refresh everything
				handleUseFeaturesFromPreferences(true);
				// tViewer.refresh();
			}
		});

		useSpecificFeaturesRadio = new Button(group, SWT.RADIO);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		useSpecificFeaturesRadio.setLayoutData(gd);
		useSpecificFeaturesRadio.setText("Use specific features");
		useSpecificFeaturesRadio.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				handleUseFeaturesFromPreferences(false);
				updateLaunchConfigurationDialog();
			}
		});

		// filler
		new Label(group, SWT.NONE);

		table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 450;
		gd.horizontalSpan = 2;
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn c1 = new TableColumn(table, SWT.NONE);
		c1.setWidth(150);
		c1.setResizable(true);
		c1.setText("Feature");

		TableColumn c2 = new TableColumn(table, SWT.NONE);
		c2.setWidth(50);
		c2.setResizable(true);
		c2.setText("Type");

		TableColumn c3 = new TableColumn(table, SWT.NONE);
		c3.setWidth(250);
		c3.setResizable(true);
		c3.setText("Value");

		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{

			private Set<?> typedFeatures;

			public Object[] getElements(Object inputElement)
			{
				return typedFeatures.toArray(new LaunchAttribute[0]);
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				typedFeatures = (Set<?>) newInput;
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
				LaunchAttribute tv = (LaunchAttribute) element;
				switch (columnIndex)
				{
					case 0:
						return removeURI(tv.uri);
					case 1:
						return tv.type;
					case 2:
						String value = tv.value;
						return value == null ? "" : value;
				}
				return "!!";
			}

			private String removeURI(String uri)
			{
				int index = uri.lastIndexOf('/');
				if (index > 0)
					return uri.substring(index + 1);
				return uri;
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
		{ "name", "type", "value" });
		tViewer.setCellModifier(new ICellModifier()
		{
			public boolean canModify(Object element, String property)
			{
				return "value".equals(property);
			}

			public Object getValue(Object element, String property)
			{
				LaunchAttribute tv = (LaunchAttribute) element;
				return tv.value == null ? "" : tv.value;
			}

			public void modify(Object element, String property, Object value)
			{
				Item item = (Item) element;
				LaunchAttribute tv = (LaunchAttribute) item.getData();
				if (value == null || "".equals(value))
					launchFeatures.removeFeature(tv.uri);
				else
					tv.setValue((String) value);
				tViewer.update(tv, null);
				updateLaunchConfigurationDialog();
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				LaunchAttribute a1 = (LaunchAttribute) e1;
				LaunchAttribute a2 = (LaunchAttribute) e2;
				return a1.uri.compareTo(a2.uri);
			}
		});

		TextCellEditor editor = new TextCellEditor(table);
		editor.setValidator(new ICellEditorValidator()
		{

			// TODO sort out
			public String isValid(Object value)
			{
				return null;
				// IStructuredSelection sel =
				// (IStructuredSelection)tViewer.getSelection();
				// TypedValue tv = (TypedValue)sel.getFirstElement();
				//			
				// IFeature feature = null;
				// IFeature[] features = processorType.getFeatures();
				// for (int i = 0; i < features.length; i++)
				// {
				// IFeature f = features[i];
				// if (f.getURI().equals(key))
				// {
				// feature = f;
				// break;
				// }
				// }
				// String valid = null;
				// IStatus validStatus = feature.validateValue((String)value);
				// if (validStatus != null && validStatus.getSeverity() ==
				// IStatus.ERROR)
				// {
				// valid = validStatus.getMessage();
				// }
				// return valid;
			}
		});

		CellEditor[] editors = new CellEditor[]
		{ null, null, editor };
		tViewer.setCellEditors(editors);
		tViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				LaunchAttribute tv = (LaunchAttribute) selection.getFirstElement();
				String text = null;
				if (tv != null)
				{
					IFeature feature = null;
					IFeature[] features = processorType.getFeatures();
					for (IFeature f : features)
					{
						if (f.getURI().equals(tv.uri))
						{
							feature = f;
							break;
						}
					}
					text = feature == null ? null : feature.getDescription();
				}
				descriptionText.setText(text == null ? "" : text);
			}
		});

		descriptionText = new Text(group, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 3;
		gd.heightHint = 50;
		descriptionText.setLayoutData(gd);

		restoreColumnSettings();
	}

	private void handleUseFeaturesFromPreferences(boolean selected)
	{
		changePreferences.setEnabled(selected);
		table.setEnabled(!selected);

		if (selected)
		{
			launchFeatures = initializeFeaturesFromPreferences();
		}
		else
		{
			launchFeatures = (LaunchFeatures) processorFeatures.get(processorType);

			if (launchFeatures == null)
			{// make a copy of the features for the processor type
				launchFeatures = new LaunchFeatures();
				processorFeatures.put(processorType, launchFeatures);
				for (int i = 0; i < processorType.getFeatures().length; i++)
				{
					IFeature feature = processorType.getFeatures()[i];
					LaunchAttribute att = new LaunchAttribute(feature.getURI(), feature.getType(), null);
					launchFeatures.addFeature(att);
				}
			}
		}
		tViewer.setInput(launchFeatures.getFeatures());
	}

	protected void setProcessorType(IProcessorType processorType)
	{
		if (processorType == this.processorType)
			return;

		// this.featuresFromPreferences = new LaunchFeatures();
		// IFeature[] features = processorType.getFeatures();
		// Map values = processorType.getFeatureValues();
		// for (int i = 0; i < features.length; i++)
		// {
		// IFeature feature = features[i];
		// featuresFromPreferences.addFeature(new
		// LaunchAttribute(feature.getURI(),feature.getType(),(String)values.get(feature.getURI())));
		// }
		//
		// if (table != null)
		// {
		// if (!table.getEnabled())
		// {
		// launchFeatures = featuresFromPreferences;
		// }
		// else
		// {
		// this.launchFeatures = new LaunchFeatures();
		// for (Iterator iter =
		// featuresFromPreferences.getFeatures().iterator(); iter.hasNext();)
		// {
		// LaunchAttribute att = (LaunchAttribute) iter.next();
		// att = new LaunchAttribute(att.uri,att.type,att.value);
		// launchFeatures.addFeature(att);
		// }
		// }
		// tViewer.setInput(launchFeatures.getFeatures());
		// }
		this.processorType = processorType;
		handleUseFeaturesFromPreferences(useFeaturesFromPreferencesRadio.getSelection());
	}

	public String getName()
	{
		return "Processor Features";
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, true);
	}

	private LaunchFeatures initializeFeaturesFromPreferences()
	{
		LaunchFeatures featuresFromPreferences = new LaunchFeatures();

		IFeature[] features = processorType.getFeatures();
		Map<?, ?> values = processorType.getFeatureValues();
		for (IFeature feature : features)
		{
			featuresFromPreferences.addFeature(new LaunchAttribute(feature.getURI(), feature.getType(), (String) values.get(feature.getURI())));
		}
		return featuresFromPreferences;
	}

	private void initializeFeaturesFromStorage(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchFeatures launchFeatures = new LaunchFeatures();

		IFeature[] features = processorType.getFeatures();
		for (IFeature feature : features)
		{
			launchFeatures.addFeature(new LaunchAttribute(feature.getURI(), feature.getType(), null));
		}

		String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_FEATURES, (String) null);
		if (s != null && s.length() > 0)
		{
			LaunchFeatures overrideFeatures = LaunchFeatures.fromXML(new ByteArrayInputStream(s.getBytes()));
			launchFeatures.getFeatures().removeAll(overrideFeatures.getFeatures());
			launchFeatures.getFeatures().addAll(overrideFeatures.getFeatures());
		}

		processorFeatures.put(processorType, launchFeatures);
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

			featuresFromPreferences = initializeFeaturesFromPreferences();
			initializeFeaturesFromStorage(configuration);

			boolean useFeaturesFromPreferences = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, true);

			useSpecificFeaturesRadio.setSelection(!useFeaturesFromPreferences);
			useFeaturesFromPreferencesRadio.setSelection(useFeaturesFromPreferences);
			handleUseFeaturesFromPreferences(useFeaturesFromPreferences);
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		boolean useDefault = useFeaturesFromPreferencesRadio.getSelection();
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, useDefault);
		try
		{
			configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_FEATURES, useDefault ? null : launchFeatures.toXML());
		}
		catch (ParserConfigurationException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		catch (IOException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		catch (TransformerException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}
}
