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

import java.util.Properties;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.IOutputProperty;
import org.eclipse.wst.xsl.launching.IProcessorType;

public class OutputBlock extends AbstractTableBlock
{
	private Table table;
	private TableViewer tViewer;
	private Properties properties;

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
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		setControl(table);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn c1 = new TableColumn(table, SWT.NONE);
		c1.setWidth(450);
		c1.setResizable(true);
		c1.setText("Property");

		TableColumn c2 = new TableColumn(table, SWT.NONE);
		c2.setWidth(150);
		c2.setResizable(true);
		c2.setText("Value");

		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{

			private IOutputProperty[] allProperties;

			public Object[] getElements(Object inputElement)
			{
				return allProperties;
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				allProperties = (IOutputProperty[]) newInput;
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
				IOutputProperty prop = (IOutputProperty) element;
				switch (columnIndex)
				{
					case 0:
						return prop.getURI();
					case 1:
						return (String) properties.get(prop.getURI());
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
		{ "name", "value" });
		tViewer.setCellModifier(new ICellModifier()
		{
			public boolean canModify(Object element, String property)
			{
				return "value".equals(property);
			}

			public Object getValue(Object element, String property)
			{
				IOutputProperty prop = (IOutputProperty) element;
				String value = (String) properties.get(prop.getURI());
				return value == null ? "" : value;
			}

			public void modify(Object element, String property, Object value)
			{
				Item item = (Item) element;
				IOutputProperty prop = (IOutputProperty) item.getData();
				if (value == null || "".equals(value))
					properties.remove(prop.getURI());
				else
					properties.put(prop.getURI(), value);
				tViewer.update(prop, null);
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				IOutputProperty prop1 = (IOutputProperty) e1;
				IOutputProperty prop2 = (IOutputProperty) e2;
				return prop1.getURI().compareTo(prop2.getURI());
			}
		});

		TextCellEditor editor = new TextCellEditor(table);

		CellEditor[] editors = new CellEditor[]
		{ null, editor };
		tViewer.setCellEditors(editors);

		restoreColumnSettings();

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		tViewer.addSelectionChangedListener(listener);
	}

	public void refresh()
	{
		tViewer.refresh();
	}

	public void setOutputPropertyValues(IProcessorType type, Properties properties)
	{
		this.properties = properties;
	}

	public void setInput(IOutputProperty[] keys)
	{
		tViewer.setInput(keys);
	}

	public String getName()
	{
		return null;
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
	}

}
