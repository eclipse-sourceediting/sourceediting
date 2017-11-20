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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.JAXPDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.launching.IOutputProperty;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.LaunchProperties;

public class OutputPropertiesBlock extends AbstractTableBlock
{
	private Table table;
	private TableViewer tViewer;
	private LaunchProperties properties;
	private Button removeButton;
	private Map<String,IOutputProperty> propertyUris = new HashMap<String,IOutputProperty>();
	
	public OutputPropertiesBlock()
	{
		for (IProcessorType type : JAXPRuntime.getProcessorTypes())
		{
			for (IOutputProperty property : type.getOutputProperties())
			{
				propertyUris.put(property.getURI(), property);
			}
		}
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier()
	{
		return JAXPDebugUIPlugin.PLUGIN_ID+"."+getClass().getCanonicalName(); //$NON-NLS-1$
	}

	@Override
	protected Table getTable()
	{
		return table;
	}

	public void createControl(Composite parent)
	{
		TabItem item = new TabItem((TabFolder)parent,SWT.NONE);
		item.setText(Messages.getString("OutputPropertiesBlock_0")); //$NON-NLS-1$
		
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		item.setControl(composite);
		
		table = new Table(composite,SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					performRemove();
				}
			}
		});

		
		TableColumn tc1 = new TableColumn(table,SWT.NONE);
		tc1.setText(Messages.getString("OutputPropertiesBlock_1")); //$NON-NLS-1$
		tc1.setWidth(350);
		tc1.setResizable(true);
		
		TableColumn tc2 = new TableColumn(table,SWT.NONE);
		tc2.setText(Messages.getString("OutputPropertiesBlock_2")); //$NON-NLS-1$
		tc2.setWidth(50);
		tc2.setResizable(true);
		
		Composite buttonComp = new Composite(composite,SWT.FILL);
		buttonComp.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		buttonComp.setLayout(gl);
		
		Button addButton = new Button(buttonComp,SWT.PUSH);
		addButton.setText(Messages.getString("OutputPropertiesBlock_3")); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		addButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
			public void widgetSelected(SelectionEvent e)
			{
				OutputPropertyDialog dialog = new OutputPropertyDialog(getShell(),properties);
				if (dialog.open() == Window.OK)
				{
					List<IOutputProperty> newProperties = dialog.getOutpuProperties();
					String first = null;
					for (IOutputProperty property : newProperties)
					{
						String att = property.getURI();
						if (first == null)
							first = att;
						properties.setProperty(property.getURI(), null);
					}
					if (newProperties.size() > 0)
					{
						tViewer.refresh();
						tViewer.setSelection(new StructuredSelection(first), true);
						tViewer.editElement(first, 1);
						updateLaunchConfigurationDialog();
					}
				}
			}
		});
		
		removeButton = new Button(buttonComp,SWT.PUSH);
		removeButton.setText(Messages.getString("OutputPropertiesBlock_4")); //$NON-NLS-1$
		removeButton.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		removeButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
			public void widgetSelected(SelectionEvent e)
			{
				performRemove();
			}
		});
		
		setControl(table);
		
		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{
			public Object[] getElements(Object inputElement)
			{
				return properties.getProperties().keySet().toArray(new String[0]);
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}
		});
		tViewer.setSorter(new ViewerSorter());
		tViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateRemoveButton();
			}
		});
		
		TableViewerColumn tvc1 = new TableViewerColumn(tViewer,tc1);
		tvc1.setLabelProvider(new CellLabelProvider(){
			@Override
			public void update(ViewerCell cell)
			{
				String tv = (String) cell.getElement();
				cell.setText(tv);
			}
			
			@Override
			public int getToolTipTimeDisplayed(Object object)
			{
				return 5000;
			}
			
			@Override
			public String getToolTipText(Object element)
			{
				String tv = (String) element;
				return propertyUris.get(tv).getDescription();
			}
			
		});
		
//		ColumnViewerToolTipSupport.enableFor(tViewer);
		
		TableViewerColumn tvc2 = new TableViewerColumn(tViewer,tc2);
		tvc2.setLabelProvider(new CellLabelProvider(){
			@Override
			public void update(ViewerCell cell)
			{
				String tv = (String) cell.getElement();
				String val = properties.getProperty(tv);
				cell.setText(val == null ? "" : val); //$NON-NLS-1$
			}
		});
		
		tvc2.setEditingSupport(new EditingSupport(tViewer){

			@Override
			protected boolean canEdit(Object element)
			{
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element)
			{
				return new TextCellEditor(table);
			}

			@Override
			protected Object getValue(Object element)
			{
				String tv = (String)element;
				String val = properties.getProperty(tv);
				return val == null ? "" : val; //$NON-NLS-1$
			}

			@Override
			protected void setValue(Object element, Object value)
			{
				String tv = (String)element;
				properties.setProperty(tv, (String)value);
				updateLaunchConfigurationDialog();
				tViewer.update(tv, null);
			}
			
		});
		
		restoreColumnSettings();
	}

	protected void updateRemoveButton()
	{
		removeButton.setEnabled(!tViewer.getSelection().isEmpty());
	}

	public String getName()
	{
		return Messages.getString("AttributesBlock.16"); //$NON-NLS-1$
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
//		LaunchProperties p = new LaunchProperties();
//		p.setProperty("indent", "yes");
//		try
//		{
//			String xml = p.toXML();
//			configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, xml);
//		}
//		catch (ParserConfigurationException e)
//		{
//			XSLDebugUIPlugin.log(e);
//		}
//		catch (IOException e)
//		{
//			XSLDebugUIPlugin.log(e);
//		}
//		catch (TransformerException e)
//		{
//			XSLDebugUIPlugin.log(e);
//		}
	}

	private void initializePropertiesFromStorage(ILaunchConfiguration configuration) throws CoreException
	{
		String s = configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, (String) null);
		if (s != null && s.length() > 0)
		{
			properties = LaunchProperties.fromXML(new ByteArrayInputStream(s.getBytes()));
		}
		else
		{
			properties = new LaunchProperties();
		}
		tViewer.setInput(properties);
		updateRemoveButton();
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			initializePropertiesFromStorage(configuration);
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		try
		{
			String xml = properties.toXML();
			configuration.setAttribute(JAXPLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, xml);
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

	private void performRemove()
	{
		IStructuredSelection sel = (IStructuredSelection)tViewer.getSelection();
		for (Iterator<String> iterator = sel.iterator(); iterator.hasNext();)
		{
			String att = iterator.next();
			properties.removeProperty(att);
		}
		tViewer.refresh();
		updateLaunchConfigurationDialog();
	}
}
