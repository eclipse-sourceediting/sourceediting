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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.main;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AbstractParameterAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AddParameterAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.RemoveParameterAction;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class ParametersBlock extends AbstractTableBlock
{
	private ParameterViewer parametersViewer;
	private Button addParameterButton;
	private Button removeParameterButton;
	private final TransformsBlock transformsBlock;
	private final ISelectionChangedListener selectionListener = new ISelectionChangedListener()
	{
		public void selectionChanged(SelectionChangedEvent event)
		{
			IStructuredSelection stylesheetSelection = (IStructuredSelection) event.getSelection();
			if (stylesheetSelection.size() == 1)
				setTransform((LaunchTransform) stylesheetSelection.getFirstElement());
			else
				setTransform(null);
			updateEnabled();
		}
	};
	private Table fTable;

	public ParametersBlock(TransformsBlock transformsBlock)
	{
		super();
		this.transformsBlock = transformsBlock;
	}

	protected void setTransform(LaunchTransform transform)
	{
		parametersViewer.setInput(transform);
	}

	protected void updateEnabled()
	{
		IStructuredSelection stylesheetSelection = (IStructuredSelection) transformsBlock.getStylesheetViewer().getSelection();
		boolean enabled = stylesheetSelection.size() == 1;
		parametersViewer.getTable().setEnabled(enabled);
		addParameterButton.setEnabled(enabled);
		IStructuredSelection parametersSelection = (IStructuredSelection) parametersViewer.getSelection();
		removeParameterButton.setEnabled(enabled && !parametersSelection.isEmpty());
	}

	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Group group = new Group(parent, SWT.NONE);
		group.setText(getName());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setFont(font);

		setControl(group);

		fTable = new Table(group, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// data.heightHint = 100;
		fTable.setLayoutData(data);
		fTable.setFont(font);

		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		TableColumn column1 = new TableColumn(fTable, SWT.NONE);
		column1.setWidth(150);
		column1.setResizable(true);
		column1.setText("Name");
		column1.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// sortByName();
			}
		});

		TableColumn column2 = new TableColumn(fTable, SWT.NONE);
		column2.setWidth(50);
		column2.setResizable(true);
		column2.setText("Type");
		column2.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// sortByType();
			}
		});

		TableColumn column3 = new TableColumn(fTable, SWT.NONE);
		column3.setWidth(150);
		column3.setResizable(true);
		column3.setText("Value");
		column3.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// sortByType();
			}
		});

		parametersViewer = new ParameterViewer(fTable);
		parametersViewer.setLabelProvider(new ParametersLabelProvider());
		parametersViewer.setContentProvider(new ParametersContentProvider());
		parametersViewer.addParametersChangedListener(new IParametersChangedListener()
		{

			public void parametersChanged(ParameterViewer viewer)
			{
				updateLaunchConfigurationDialog();
			}
		});

		parametersViewer.setColumnProperties(new String[]
		{ "name", "type", "value" });
		final String[] types = new String[]
		{ LaunchAttribute.TYPE_STRING, LaunchAttribute.TYPE_BOOLEAN, LaunchAttribute.TYPE_INT, LaunchAttribute.TYPE_DOUBLE, LaunchAttribute.TYPE_FLOAT, LaunchAttribute.TYPE_OBJECT,
				LaunchAttribute.TYPE_CLASS, };
		ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(fTable, types, SWT.READ_ONLY | SWT.DROP_DOWN);
		TextCellEditor textEditor = new TextCellEditor(fTable);
		CellEditor[] editors = new CellEditor[]
		{ null, comboEditor, textEditor };
		parametersViewer.setCellEditors(editors);
		parametersViewer.setCellModifier(new ICellModifier()
		{
			public boolean canModify(Object element, String property)
			{
				return "type".equals(property) || "value".equals(property);
			}

			public Object getValue(Object element, String property)
			{
				LaunchAttribute att = (LaunchAttribute) element;
				if ("type".equals(property))
				{
					for (int i = 0; i < types.length; i++)
					{
						String type = types[i];
						if (type.equals(att.type))
							return new Integer(i);
					}
					return null;
				}
				return att.value == null ? "" : att.value;
			}

			public void modify(Object element, String property, Object value)
			{
				Item item = (Item) element;
				LaunchAttribute att = (LaunchAttribute) item.getData();
				if ("type".equals(property))
				{
					Integer v = (Integer) value;
					att.type = types[v.intValue()];
				}
				else if ("value".equals(property))
					att.value = (String) value;
				parametersViewer.update(att, null);
				updateLaunchConfigurationDialog();
			}
		});

		Composite parameterButtonComp = new Composite(group, SWT.NONE);
		GridLayout parameterButtonCompLayout = new GridLayout();
		parameterButtonCompLayout.marginHeight = 0;
		parameterButtonCompLayout.marginWidth = 0;
		parameterButtonComp.setLayout(parameterButtonCompLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		parameterButtonComp.setLayoutData(gd);
		parameterButtonComp.setFont(font);

		addParameterButton = createButton(parameterButtonComp, new AddParameterAction(parametersViewer));
		removeParameterButton = createButton(parameterButtonComp, new RemoveParameterAction(parametersViewer));

		transformsBlock.getStylesheetViewer().addSelectionChangedListener(selectionListener);

		restoreColumnSettings();
	}

	protected Button createButton(Composite pathButtonComp, AbstractParameterAction action)
	{
		Button button = createPushButton(pathButtonComp, action.getText(), null);
		action.setButton(button);
		return button;
	}

	@Override
	protected void setSortColumn(int column)
	{
		switch (column)
		{
		// case 1:
		// sortByName();
		// break;
		// case 2:
		// sortByType();
		// break;
		}
		super.setSortColumn(column);
	}

	@Override
	protected Table getTable()
	{
		return fTable;
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier()
	{
		return XSLDebugUIConstants.MAIN_PARAMATERS_BLOCK;
	}

	public String getName()
	{
		return "Transformation Parameters";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		// handled by the Tab
		updateEnabled();
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// handled by the Tab
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// handled by the Tab
	}

	@Override
	public void dispose()
	{
		if (transformsBlock.getStylesheetViewer() != null)
			transformsBlock.getStylesheetViewer().removeSelectionChangedListener(selectionListener);
		super.dispose();
	}
}
