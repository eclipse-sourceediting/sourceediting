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

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.IFeature;

public class FeaturesBlock extends AbstractTableBlock
{
	private Table table;
	private TableViewer tViewer;
	private Map<String, String> featureValues;
	private Text descriptionText;
	private DialogPage dialogPage;

	public FeaturesBlock(DialogPage dialogPage)
	{
		this.dialogPage = dialogPage;
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier()
	{
		return XSLDebugUIConstants.FEATURES_BLOCK;
	}

	@Override
	protected Table getTable()
	{
		return table;
	}

	public void createControl(Composite parent)
	{
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.widthHint = 450;
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn c1 = new TableColumn(table, SWT.NONE);
		c1.setWidth(150);
		c1.setResizable(true);
		c1.setText(Messages.getString("FeaturesBlock.0")); //$NON-NLS-1$

		TableColumn c2 = new TableColumn(table, SWT.NONE);
		c2.setWidth(100);
		c2.setResizable(true);
		c2.setText(Messages.getString("FeaturesBlock.1")); //$NON-NLS-1$

		TableColumn c3 = new TableColumn(table, SWT.NONE);
		c3.setWidth(200);
		c3.setResizable(true);
		c3.setText(Messages.getString("FeaturesBlock.2")); //$NON-NLS-1$

		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{

			private IFeature[] features;

			public Object[] getElements(Object inputElement)
			{
				return features;
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				features = (IFeature[]) newInput;
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
				IFeature feature = (IFeature) element;
				switch (columnIndex)
				{
					case 0:
						return removeURI(feature.getURI());
					case 1:
						return feature.getType();
					case 2:
						String value = (String) featureValues.get(feature.getURI());
						return value == null ? "" : value; //$NON-NLS-1$
				}
				return "!!"; //$NON-NLS-1$
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
		{ Messages.getString("FeaturesBlock.5"), Messages.getString("FeaturesBlock.6"), Messages.getString("FeaturesBlock.7") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final String[] valid = new String[]{null};
		tViewer.setCellModifier(new ICellModifier()
		{
			public boolean canModify(Object element, String property)
			{
				return Messages.getString("FeaturesBlock.8").equals(property); //$NON-NLS-1$
			}

			public Object getValue(Object element, String property)
			{
				IFeature feature = (IFeature) element;
				String value = (String) featureValues.get(feature.getURI());
				return value == null ? "" : value; //$NON-NLS-1$
			}

			public void modify(Object element, String property, Object value)
			{
				Item item = (Item) element;
				IFeature feature = (IFeature) item.getData();
				if (value == null || "".equals(value)) //$NON-NLS-1$
					featureValues.remove(feature.getURI());
				else
					featureValues.put(feature.getURI(), (String)value);				
				setErrorMessage(valid[0]);
				tViewer.update(feature, null);
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				IFeature feature1 = (IFeature) e1;
				IFeature feature2 = (IFeature) e2;
				return feature1.getURI().compareTo(feature2.getURI());
			}
		});

		TextCellEditor editor = new TextCellEditor(table);
		editor.setValidator(new ICellEditorValidator()
		{

			public String isValid(Object value)
			{
				IStructuredSelection sel = (IStructuredSelection) tViewer.getSelection();
				IFeature feature = (IFeature) sel.getFirstElement();
				IStatus validStatus = feature.validateValue((String) value);
				if (value == null || "".equals(value)) //$NON-NLS-1$
					valid[0] = null;
				else if (validStatus != null && validStatus.getSeverity() == IStatus.ERROR)
					valid[0] = validStatus.getMessage();
				else
					valid[0] = null;
				return valid[0];
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
				String text = null;
				if (selection != null && selection.getFirstElement() != null)
					text = ((IFeature) selection.getFirstElement()).getDescription();
				descriptionText.setText(text == null ? "" : text); //$NON-NLS-1$
			}
		});

		restoreColumnSettings();

		descriptionText = new Text(parent, SWT.WRAP | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.heightHint = 80;
		descriptionText.setLayoutData(gd);
	}

	protected void setInput(IFeature[] input)
	{
		tViewer.setInput(input);
	}

	protected void setFeatureValues(Map<String, String> map)
	{
		this.featureValues = map;
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
	
	@Override
	protected void setErrorMessage(String errorMessage)
	{
		dialogPage.setErrorMessage(errorMessage);
	}

}
